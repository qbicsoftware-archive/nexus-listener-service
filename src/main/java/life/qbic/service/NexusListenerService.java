package life.qbic.service;


import life.qbic.cli.QBiCTool;
import life.qbic.exceptions.ApplicationException;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.io.IOException;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;


/**
 * Implementation of Nexus-Listener Service. Its command-line arguments are contained in instances of {@link NexusListenerCommand}.
 */
public class NexusListenerService extends QBiCTool<NexusListenerCommand> {

    private static final Logger LOG = LogManager.getLogger(NexusListenerService.class);
    private volatile NanoHTTPD httpServer;
    private final AtomicBoolean serverStarted;

    //Commandline Arguments
    private String baseRepo = "";
    private List<String> artifacts = new ArrayList();
    private String secretKey = "";
    private String url = "";
    private String outPortlet = "";
    private String outArtifacts = "";

    private boolean isPortlet = false;


    /**
     * Constructor.
     *
     * @param command an object that represents the parsed command-line arguments.
     */
    public NexusListenerService(final NexusListenerCommand command) {
        super(command);
        serverStarted = new AtomicBoolean(false);
    }

    @Override
    public void execute() {
        // get the parsed command-line arguments
        final NexusListenerCommand command = super.getCommand();
        baseRepo = command.url;
        secretKey = command.key;
        artifacts = command.artifactType;
        artifacts.add(command.firstArtifact);
        outArtifacts = command.outNonPortlet;
        outPortlet = command.outPortlet;


        httpServer = new NanoHTTPD(command.port) {
            @Override
            public Response serve(IHTTPSession session) {

                return processPOST(session);
            }
        };
        try {
            httpServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            serverStarted.set(true);
            LOG.info("Listening in port {}", command.port);
        } catch (IOException e) {
            throw new ApplicationException(String.format("Could not start http server using port %d", command.port), e);
        }

    }


    /**
     * This Method processes the POST-request of the Repository that notifies the server about changes
     *
     * @param session
     * @return
     */
    public Response processPOST(IHTTPSession session) {
        LOG.info("processing POST request");
        Map<String, String> files = new HashMap<String, String>();
        Map<String, String> headers;
        NanoHTTPD.Method method = session.getMethod();

        if (NanoHTTPD.Method.POST.equals(method)) {
            try {

                //need header information to get the signature (HMAC hashed value)
                headers = session.getHeaders();
                //need to do parseBody() in order to fill the map files with the body of the session
                session.parseBody(files);

                // TODO: can we get "postData" from HTTPSession.POST_DATA ???
                final String payload = files.get("postData");
                // payload can be turned into a JSON object


                //verify data: valid repository sends Post request?
                if (hashKey(secretKey, payload).equals(headers.get("x-nexus-webhook-signature"))) {

                    JSONObject jsonBody = parseJSON(payload);

                    if (artifactRelevant(jsonBody)) {

                        url = buildURL(jsonBody);

                        //download the file temporarily
                        Client client = new Client(url);
                        client.downloadFromURL();
                        //TODO here changed how fsh is called!!
                        //String fileName = client.getFileName()+"."+client.getFileFormat();

                        //move the temp file to desired outpath path
                        if(isPortlet){
                            FileSystemHandler fsh = new FileSystemHandler(client.getFilePath(),outPortlet,client.getFileName());
                        }else{
                            FileSystemHandler fsh = new FileSystemHandler(client.getFilePath(),outArtifacts,client.getFileName());

                        }

                        LOG.info("VERIFIED POST: the POST request has been successfully processed");
                    } else {
                        LOG.info("NOT RELEVANT: the changed artifact is not specified in the commandline");
                    }

                    return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "Ok"); // Or postParameter.
                } else {
                    LOG.error("UNVERIFIED DATA: the payload of the POST request cannot be validated (wrong payload or nexus-webhook-signature");

                }

            } catch (IOException ioe) {
                LOG.error("SERVER INTERNAL ERROR: IOException", ioe);
                return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            } catch (NanoHTTPD.ResponseException re) {
                LOG.error("NO RESPONSE FROM SERVER: ResponseException", re);
                return NanoHTTPD.newFixedLengthResponse(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
            } catch (ParseException pe) {
                LOG.error("ERROR WHILE PARSING BODY TO JSON: ParseException", pe);
                return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, pe.getMessage());
            }
        }
        LOG.info("NO POST: the request is no POST request and thus does not get processed");
        return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "Ok"); // Or postParameter.
    }


    /**
     * Method that verifies that the data was send by nexus
     *
     * @param secretKey, message, hash
     * @return
     */
    public String hashKey(String secretKey, String message) {
        String hmac = "";

        try {

            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");

            mac.init(secret);
            byte[] digest = mac.doFinal(message.getBytes());

            hmac = Hex.encodeHexString(digest);

        } catch (NoSuchAlgorithmException nsae) {
            LOG.error("NoSuchAlgorithmException " + nsae.getMessage());
        } catch (InvalidKeyException rk) {
            LOG.error("InvalidKeyException: " + rk.getMessage());
        }

        return hmac;
    }

    private JSONObject parseJSON(String toJSON) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(toJSON);

        LOG.info("PARSING: POST body to JSON");

        return json;
    }

    /**
     * This Method tests if the post request informs the user about an artifact that he has defined as relevant with the commandline option -t
     *
     * @param jsonBody
     * @return
     * @throws ParseException
     */
    private boolean artifactRelevant(JSONObject jsonBody) throws ParseException {

        //test if artifact type is relevant, is it specified with the commandline?
        String name = (parseJSON(jsonBody.get("component").toString()).get("name")).toString();
        String[] splitName = name.split("-");

        return artifacts.contains(splitName[splitName.length - 1]);
    }

    /**
     * Method that builds the URL of the updated repository
     *
     * @param body
     * @return
     */
    public String buildURL(JSONObject body) throws ParseException {


        //access the POST request's parameters set empty String as Default in order to better process errors:
        String repo = body.getOrDefault("repositoryName", "").toString();
        String comp = body.getOrDefault("component", "").toString();

        //Valid data?
        if (repo.equals("")) {
            LOG.error("PARSE EXCEPTION: Missing Information about the repositoryName");
            throw new ApplicationException("PARSE EXCEPTION: Missing Information about the repositoryName");
        }
        if (comp.equals("")) {
            LOG.error("PARSE EXCEPTION: Missing Information about the component");
            throw new ApplicationException("PARSE EXCEPTION: Missing Information about the component "); //test if component is empty before accessing/parsing it in a later
        }

        JSONObject component = parseJSON(comp); //need to access sub-elements of component!
        String name = component.getOrDefault("name", "").toString();
        String group = component.getOrDefault("group", "").toString().replace(".", "/");
        String version = component.getOrDefault("version", "").toString();
        String assetVersion = version;

        //Valid data?
        if (name.equals("")) {
            LOG.error("PARSE EXCEPTION: Missing Information about the components name");
            throw new ApplicationException("PARSE EXCEPTION: Missing Information about the components name");
        }
        if (group.equals("")) {
            LOG.error("PARSE EXCEPTION: Missing Information about the group");
            throw new ApplicationException("PARSE EXCEPTION: Missing Information about the group");
        }
        if (version.equals("")) {
            LOG.error("PARSE EXCEPTION: Missing Information about the components version");
            throw new ApplicationException("PARSE EXCEPTION: Missing Information about the components version");
        }

        //if payload was fine build URL:
        String asset = buildAsset(name, assetVersion);

        //process version, if snapshot process version for url (but not for asset specification!
        if (repo.contains("snapshot")) {
            version = version.split("-")[0] + "-SNAPSHOT"; //raw version
        }

        String url = baseRepo + "/repository/" + repo + "/" + group + "/" + name + "/" + version + "/" + asset; //need asset specification?
        LOG.info(url);


        return url;
    }


    /**
     * Method that specifies the asset from the updated repository, which contains either the .war or the .jar file
     *
     * @param
     * @return
     */
    private String buildAsset(String name, String version) {

        String format = "";

        if (name.contains("portlet")) {
            format = ".war";
            isPortlet = true;
        } else {
            format = ".jar";
        }

        return name + "-" + version + format;
    }


    @Override
    public void shutdown() {
        LOG.info("Shutting down");
        if (serverStarted.get()) {
            httpServer.stop();
        }
    }

    //###############GETTER-Methods ####################################################################################

    public String getUrl() {
        return url;
    }

    public List<String> getArtifacts() {
        return artifacts;
    }

    public NanoHTTPD getHttpServer() {
        return httpServer;
    }
}