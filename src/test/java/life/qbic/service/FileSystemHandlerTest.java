package life.qbic.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.*;

public class FileSystemHandlerTest {

  private static final Logger LOG = LogManager.getLogger(FileSystemHandlerTest.class);

  FileSystemHandler fsh;
  Client client;

  @Before
  public void setUp() {
    client = new Client(
        FileSystemHandlerTest.class.getResource("/vaccine-designer-portlet-1.0.0-20180802.133341-3.war").toExternalForm());
    client.downloadFromURL();

    fsh = new FileSystemHandler(client.getTmpFilePath(), System.getProperty("java.io.tmpdir"),
        client.getFileName() + client.getFileFormat());
  }

  @After
  public void cleanUp() throws IOException {
    Files.deleteIfExists(Paths.get(client.getTmpFilePath()));
    Files.deleteIfExists(Paths.get(fsh.getOutPath()));
  }

  @Test
  public void testFileMoving() {
    File fileOld = new File(fsh.getTempPath());
    Boolean fileExists = fileOld.exists();
    //LOG.info(fsh.getTempPath()+" "+fsh.getOutPath());

    assertTrue(fileExists);
    fsh.move();

    File fileNew = new File(fsh.getOutPath());
    LOG.info(fileNew.getAbsolutePath());

    assertFalse(fileOld.exists());
    assertTrue(fileNew.exists());

    fileNew.delete();
    assertFalse(fileNew.exists());
  }


}
