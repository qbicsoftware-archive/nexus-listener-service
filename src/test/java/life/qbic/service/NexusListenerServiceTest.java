package life.qbic.service;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


/**
 * Unit tests for nexus-listenerService.
 */
public class NexusListenerServiceTest  {
    // TODO: write unit tests (you do not need to test ToolExecutor, just test the execute() and shutdown() methods of your service)

    NexusListenerService nls;
    NexusListenerCommand nlc;

    @Before
    public void setUp() throws Exception{
        nlc = new NexusListenerCommand();

        //nlc.artifact_type = "";
        nlc.key = "";
        nlc.port = 8080;
        nlc.url = "";
        nlc.outNonPortlet = "";
        nlc.outPortlet = "";

        nls = new NexusListenerService(nlc);

    }
    @Test
    public void testServiceExecution() {
        //need to trigger execute before?
        nls.execute();
        assertTrue(nls.getHttpServer().isAlive());

        // --> woanders hin assertNotNull(nls.getArtifacts()); test ob commandline String artifacts richtig in Liste übersetzt wird Picocli?

    }

    @Test
    public void testPOSTProcessing() {
        //TODO: test with data!
    }

    @Test
    public void testBuildURL() {
        assertEquals(".....",nls.getUrl());
    }

    @Test
    public void testHMAC() {
        //TODO: generate Key, test if key equals the build URL
       // assertEquals("",nls.buildURL());
    }

    @Test
    public void testServiceShutdown() {
        nls.shutdown();
        assertFalse(nls.getHttpServer().isAlive());
    }



}