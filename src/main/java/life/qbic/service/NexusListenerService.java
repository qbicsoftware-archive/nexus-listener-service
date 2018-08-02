package life.qbic.service;


import life.qbic.cli.QBiCTool;
import life.qbic.exceptions.ApplicationException;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;




/**
 * Implementation of Nexus-Listener Service. Its command-line arguments are contained in instances of {@link NexusListenerCommand}.
 */
public class NexusListenerService extends QBiCTool<NexusListenerCommand> {

    private static final Logger LOG = LogManager.getLogger(NexusListenerService.class);
    private volatile NanoHTTPD httpServer;
	private final AtomicBoolean serverStarted;
	private String baseRepo = "";
    private List<String> artifacts = new ArrayList<String>();
	//private boolean snapshot = false;
	private String secretKey = "";
    private String url = "";



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


		//fillArtifactList(command.artifact_type.split("\\s"));


         httpServer = new NanoHTTPD(command.port) {
            @Override
            public Response serve(IHTTPSession session) {

				return processPOST(session);
        }};
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
	 * @param session
	 * @return
	 */
    public Response processPOST(IHTTPSession session){
    	LOG.info("processing POST request");
		Map<String, String> files = new HashMap<String, String>();
		Map<String,String> headers;
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
                if(hashKey(secretKey, payload, headers.get("x-nexus-webhook-signature"))){
                    //TODO: check if toString() serves JSON.stringify(req.body) as suggested by sonatype

                    if(artifacts.contains(files.get("component:name"))){

                        url = buildURL(files);

                        LOG.info("Verified POST request");
                    } else{
                        LOG.info("Change not relevant");
                    }

                    return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT,"Ok"); // Or postParameter.
                }

			} catch (IOException ioe) {
				LOG.error("SERVER INTERNAL ERROR: IOException"+ioe.getMessage());
				return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
			} catch (NanoHTTPD.ResponseException re) {
				LOG.error(re.getMessage());
				return NanoHTTPD.newFixedLengthResponse(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
			}
		}
        LOG.info("Data not verified");
        return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT,"Ok"); // Or postParameter.
	}


    /**
     * Method that verifies that the data was send by nexus
     * @param secretKey, message, hash
     * @return
     */
	 public boolean hashKey(String secretKey, String message, String hash) {

        try{

            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");

            mac.init(secret);
            byte[] digest = mac.doFinal(message.getBytes());

            String hmac = Hex.encodeHexString(digest);

            if(hmac == hash){

                return true;
            }

        } catch (NoSuchAlgorithmException nsae) {
            LOG.error("NoSuchAlgorithmException "+nsae.getMessage());
        } catch (InvalidKeyException rk) {
            LOG.error("InvalidKeyException: "+rk.getMessage());
        }

         return false;
     }

    /**
     * Method that builds the URL of the updated repository
     * @param body
     * @return
     */
     public String buildURL(Map<String, String> body){

         //access the POST request's parameters:
         String repo = body.get("repositoryName");
         String name = body.get("component:name");
         String group = body.get("component:group");
         String assetVersion = body.get("component:version");
         String version = body.get("component:version");
         //String id = body.get("component:id");

         //String postParameter = session.getParms().get("parameter"); the same???
         String asset = buildAsset(repo,assetVersion);

         //process version, if snapshot process version for url (but not for asset specification!
         if(version.contains("snapshot")){
             version = version.split("-")[0]+"-SNAPSHOT"; //raw version
         }

         String url =  baseRepo+"/repository/"+repo+"/"+group+"/"+name+"/"+version+"/"+asset; //need asset specification?

        return url;
     }

    /**
     * Method that specifies the asset from the updated repository, which contains either the .war or the .jar file
     * @param
     * @return
     */
    private String buildAsset(String name, String version){

        String format = "";

        if(name.contains("-portlet")){
            format = ".war";
        }else{
            format = ".jar";
        }

        return name+"-"+version+format;
    }

/*    *//**
     * Method that parses the artifact from the commandline
     * @param elem
     *//*
    private void fillArtifactList(String[] elem){

        for(int j = 0; j < elem.length; j++){
            artifacts.add(elem[j]);
        }
    }*/

		@Override
    public void shutdown() {
		LOG.info("Shutting down");
		if (serverStarted.get()) {
			httpServer.stop();
		}
    }

    //

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