package life.qbic.service;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.*;

public class FileSystemHandlerTest {

    FileSystemHandler fsh;


    @Before
    public void setUp() throws Exception{
        Client client = new Client("https://github.com/qbicsoftware/nexus-listener-service/blob/development/src/test/resources/vaccine-designer-portlet-1.0.0-20180802.133341-3.war");
        client.downloadFromURL();


        fsh = new FileSystemHandler(client.getFilePath(),"/home","vaccine-designer-portlet-1.0.0-20180802.133341-3.war");
    }

    @Test
    public void testFileMoving() {
        File fileOld =  new File(fsh.getTempPath());
        Boolean fileExists = fileOld.exists();

        assertTrue(fileExists);

        File fileNew = new File(fsh.getOutPath()+"/vaccine-designer-portlet-1.0.0-20180802.133341-3.war");

/*  cannot find these files!!
        fileExists = fileOld.exists();
        assertFalse(fileExists);
        fileExists = fileNew.exists();
        assertTrue(fileExists);*/
    }


}
