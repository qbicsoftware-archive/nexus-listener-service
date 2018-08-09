package life.qbic.service;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static junit.framework.TestCase.assertTrue;

public class ClientTest {


    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    Client client;

    @Before
    public void setUp(){

        //http://media.einfachtierisch.de/thumbnail/600/390/media.einfachtierisch.de/images/2015/12/Katze-streicheln-Jakub-Zak-Shutterstock-305145185.jpg
        client = new Client("https://github.com/qbicsoftware/nexus-listener-service/blob/development/src/test/resources/vaccine-designer-portlet-1.0.0-20180802.133341-3.war");

    }

    @Test
    public void testDownloadFromUrl(){
        client.downloadFromURL();
        File file =  new File(client.getTmpFileName());
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
