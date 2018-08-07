package life.qbic.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ClientTest {


    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    Client client;
    NexusListenerService nls;
    NexusListenerCommand nlc;

    @Before
    public void setUp() throws Exception{
 /*       nlc = new NexusListenerCommand();

        nlc.artifactType = new ArrayList<>();
        nlc.key = "123456789";
        nlc.port = 8080;
        nlc.url = "https://qbic-repo.am10.uni-tuebingen.de";
        nlc.outNonPortlet = "";
        nlc.outPortlet = "";

        nls = new NexusListenerService(nlc);*/
        client = new Client("https://github.com/qbicsoftware/nexus-listener-service/blob/development/src/test/resources/vaccine-designer-portlet-1.0.0-20180802.133341-3.war");

    }

    @Test
    public void testDownloadFromUrl(){
        client.downloadFromURL();
        File file =  new File(client.getFilePath());
        Boolean fileExists = file.exists();

        assertTrue(fileExists);

        file.delete();
        assertTrue("File does not exist.", fileExists);
    }

    @Test
    public void testDownloadFails(){
        client = new Client("https://github.com/qbicsoftware/nexus-listener-service/blob/development/src/test/resources/vaccine-designer-portlet-1.0.0.war");

        expectedException.expect(life.qbic.exceptions.ApplicationException.class);
        expectedException.expectMessage("ERROR WHILE DOWNLOADING FILE: IOException");

        client.downloadFromURL();
    }

}
