package life.qbic.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.*;

public class FileSystemHandlerTest {

    FileSystemHandler fsh;
    //private static final Logger LOG = LogManager.getLogger(FileSystemHandlerTest.class);

    @Before
    public void setUp(){
        //http://media.einfachtierisch.de/thumbnail/600/390/media.einfachtierisch.de/images/2015/12/Katze-streicheln-Jakub-Zak-Shutterstock-305145185.jpg

        Client client = new Client("https://github.com/qbicsoftware/nexus-listener-service/blob/development/src/test/resources/vaccine-designer-portlet-1.0.0-20180802.133341-3.war");
        client.downloadFromURL();

        fsh = new FileSystemHandler(client.getFilePath(),"/home/jefo",client.getFileName()+client.getFileFormat());

    }

    @Test
    public void testFileMoving() throws IOException {
        File fileOld =  new File(fsh.getTempPath());
        Boolean fileExists = fileOld.exists();

        assertTrue(fileExists);
        fsh.move();

        File fileNew = new File(fsh.getOutPath());

        assertFalse(fileOld.exists());
        assertTrue(fileNew.exists());

        fileNew.delete();
        assertFalse(fileNew.exists());
    }


}
