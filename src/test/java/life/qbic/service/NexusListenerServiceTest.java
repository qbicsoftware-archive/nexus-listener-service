package life.qbic.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;


/**
 * Unit tests for nexus-listenerService.
 */
public class NexusListenerServiceTest  {
    // TODO: write unit tests (you do not need to firstArtifact ToolExecutor, just firstArtifact the execute() and shutdown() methods of your service)

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    NexusListenerService nls;
    NexusListenerCommand nlc;

    @Before
    public void setUp(){
        nlc = new NexusListenerCommand();

        nlc.artifactType = new ArrayList<>();
        nlc.key = "123456789";
        nlc.port = 8080;
        nlc.url = "https://qbic-repo.am10.uni-tuebingen.de";
        nlc.outNonPortlet = "";
        nlc.outPortlet = "";

        nls = new NexusListenerService(nlc);


    }
    @Test
    public void testServiceExecution() {
        nls.execute();
        assertTrue(nls.getHttpServer().isAlive());
    }

    @Test
    public void testArtifactRelevant() throws ParseException{

        String payload = "{\"action\":\"CREATED\",\"component\":{\"name\":\"vaccine-designer-cli-portlet\"}}";
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(payload);

        assertEquals("portlet", nls.artifactNameRelevant(json));

        assertFalse(nls.actionTypRelevant(json));
    }

    @Test
    public void testBuildURL() throws Exception{

        String payload = "{\"timestamp\":\"2018-08-02T12:53:31.922+0000\",\"nodeId\":\"E72CE436-C789E185-BFF8B757-A30A899F-27751D2E\",\"initiator\":\"deployer/52.54.40.118\"," +
                "\"repositoryName\":\"maven-snapshots\",\"action\":\"UPDATED\",\"component\":{\"id\":\"4b378653591c67225df912359675c3c1\",\"format\":\"maven2\"," +
                "\"name\":\"vaccine-designer-portlet\",\"group\":\"life.qbic\",\"version\":\"1.0.0-20180802.125333-1\"}}";
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(payload);

        assertEquals("https://qbic-repo.am10.uni-tuebingen.de/repository/maven-snapshots/life/qbic/vaccine-designer-portlet/1.0.0-SNAPSHOT/vaccine-designer-portlet-1.0.0-20180802.125333-1.war",
                nlc.url+nls.buildURL(json,true)); //add nlc.url because method uses baseRepository variable that is not set within the test

    }

    @Test
    public void testPayloadContainsRepositoryName() throws Exception{

        expectedException.expect(life.qbic.exceptions.ApplicationException.class);
        expectedException.expectMessage("PARSE EXCEPTION: Missing Information about the repositoryName");

        String payload = "{\"timestamp\":\"2018-08-02T12:53:31.922+0000\",\"nodeId\":\"E72CE436-C789E185-BFF8B757-A30A899F-27751D2E\",\"initiator\":\"deployer/52.54.40.118\",\"action\":\"CREATED\",\"component\":{\"id\":\"4b378653591c67225df912359675c3c1\",\"format\":\"maven2\",\"name\":\"vaccine-designer-portlet\",\"group\":\"life.qbic\",\"version\":\"1.0.0-20180802.125333-1\"}}";
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(payload);
        nls.buildURL(json,true);

    }

    @Test
    public void testPayloadContainsComponent() throws Exception{

        expectedException.expect(life.qbic.exceptions.ApplicationException.class);
        expectedException.expectMessage("PARSE EXCEPTION: Missing Information about the component");

        String payload = "{\"timestamp\":\"2018-08-02T12:53:31.922+0000\",\"nodeId\":\"E72CE436-C789E185-BFF8B757-A30A899F-27751D2E\",\"initiator\":\"deployer/52.54.40.118\",\"repositoryName\":\"maven-snapshots\",\"action\":\"CREATED\"}";
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(payload);
        nls.buildURL(json,true);

    }

    @Test
    public void testPayloadContainsComponentName() throws Exception{

        expectedException.expect(life.qbic.exceptions.ApplicationException.class);
        expectedException.expectMessage("PARSE EXCEPTION: Missing Information about the components name");

        String payload = "{\"timestamp\":\"2018-08-02T12:53:31.922+0000\",\"nodeId\":\"E72CE436-C789E185-BFF8B757-A30A899F-27751D2E\",\"initiator\":\"deployer/52.54.40.118\"," +
                "\"repositoryName\":\"maven-snapshots\",\"action\":\"CREATED\",\"component\":{\"id\":\"4b378653591c67225df912359675c3c1\",\"format\":\"maven2\"," +
                "\"group\":\"life.qbic\",\"version\":\"1.0.0-20180802.125333-1\"}}";
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(payload);
        nls.buildURL(json,true);


    }

    @Test
    public void testPayloadContainsComponentVersion() throws Exception{

        expectedException.expect(life.qbic.exceptions.ApplicationException.class);
        expectedException.expectMessage("PARSE EXCEPTION: Missing Information about the components version");

        String payload = "{\"timestamp\":\"2018-08-02T12:53:31.922+0000\",\"nodeId\":\"E72CE436-C789E185-BFF8B757-A30A899F-27751D2E\",\"initiator\":\"deployer/52.54.40.118\"," +
                "\"repositoryName\":\"maven-snapshots\",\"action\":\"CREATED\",\"component\":{\"id\":\"4b378653591c67225df912359675c3c1\",\"format\":\"maven2\"," +
                "\"name\":\"vaccine-designer-portlet\",\"group\":\"life.qbic\"}}";
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(payload);
        nls.buildURL(json,true);


    }

    @Test
    public void testPayloadContainsComponentGroup() throws Exception{

        String payload = "{\"timestamp\":\"2018-08-02T12:53:31.922+0000\",\"nodeId\":\"E72CE436-C789E185-BFF8B757-A30A899F-27751D2E\",\"initiator\":\"deployer/52.54.40.118\"," +
                "\"repositoryName\":\"maven-snapshots\",\"action\":\"CREATED\",\"component\":{\"id\":\"4b378653591c67225df912359675c3c1\",\"format\":\"maven2\"," +
                "\"name\":\"vaccine-designer-portlet\",\"group\":\"life.qbic\",\"version\":\"1.0.0-20180802.125333-1\"}}";
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(payload);
        nls.buildURL(json,true);


    }


    @Test
    public void testHMAC() {

        String payload = "{\"timestamp\":\"2018-08-02T12:53:31.922+0000\",\"nodeId\":\"E72CE436-C789E185-BFF8B757-A30A899F-27751D2E\",\"initiator\":\"deployer/52.54.40.118\"," +
                "\"repositoryName\":\"maven-snapshots\",\"action\":\"UPDATED\",\"component\":{\"id\":\"4b378653591c67225df912359675c3c1\",\"format\":\"maven2\"," +
                "\"name\":\"vaccine-designer-portlet\",\"group\":\"life.qbic\",\"version\":\"1.0.0-20180802.125333-1\"}}";


        assertEquals("74525bdaf655faaa6fc0dc2ec6b56420679041ac", nls.hashKey(nlc.key,payload));

        payload = "{\"timestamp\":\"2018-08-02T12:53:31.922+0000\",\"nodeId\":\"E72CE436-C789E185-BFF8B757-A30A899F-27751D2E\",\"initiator\":\"deployer/52.54.40.118\"," +
                "\"repositoryName\":\"maven-snapshots\",\"action\":\"CREATED\",\"component\":{\"id\":\"4b378653591c67225df912359675c3c1\",\"format\":\"maven2\"," +
                "\"name\":\"vaccine-designer-portlet\",\"group\":\"life.qbic\",\"version\":\"1.0.0-20180802.125333-1\"}}";


        assertEquals("29e994b9bc5c6aed315e0e17ba19356ab0767adf", nls.hashKey(nlc.key,payload));



    }

    @Test
    public void testHMACwithWrongSignature() {

        String payload = "{\"timestamp\":\"2018-08-02T12:53:31.922+0000\",\"nodeId\":\"E72CE436-C789E185-BFF8B757-A30A899F-27751D2E\",\"initiator\":\"deployer/52.54.40.118\"," +
                "\"repositoryName\":\"maven-snapshots\",\"action\":\"CREATED\",\"component\":{\"id\":\"4b378653591c67225df912359675c3c1\",\"format\":\"maven2\"," +
                "\"name\":\"vaccine-designer-portlet\",\"group\":\"life.qbic\",\"version\":\"1.0.0-20180802.125333-1\"}}";

        assertNotSame("29e994bbc5c6aed315e0e17ba19356ab0767adf", nls.hashKey(nlc.key,payload));
    }

    @Test
    public void testHMACwithWrongPayload() {

        String payload = "{\"timestamp\":\"2018-08-02T12:53:31.922+0000\",\"nodeId\":\"E72CE436-C789E185-BFF8B757-A30A899F-27751D2E\",\"initiator\":\"deployer/52.54.40.118\"," +
                "\"repositoryName\":\"mvaen-snapshots\",\"action\":\"CREATED\",\"component\":{\"id\":\"4b378653591c67225df912359675c3c1\",\"format\":\"maven2\"," +
                "\"name\":\"vaccine-designer-portlet\",\"group\":\"life.qbic\",\"version\":\"1.0.0-20180802.125333-1\"}}";

        assertNotSame("29e994bbc5c6aed315e0e17ba19356ab0767adf", nls.hashKey(nlc.key,payload));
    }


    @Test
    public void testServiceShutdown() {
        nls.execute(); //execute a service before shutdown
        nls.shutdown();
        assertFalse(nls.getHttpServer().isAlive());
    }



}