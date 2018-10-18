package life.qbic.service;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static junit.framework.TestCase.assertTrue;

public class ClientTest {

  private final static String VALID_URL = ClientTest.class.getResource("/vaccine-designer-portlet-1.0.0-20180802.133341-3.war").toExternalForm();
  public static final String INVALID_URL = "https://github.com/qbicsoftware/does-not-exist-portlet.war";


  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  Client client;

  @Before
  public void setUp() {
    client = new Client(VALID_URL);
  }

  @Test
  public void testDownloadFromUrl() {
    client.downloadFromURL();
    File file = new File(client.getTmpFilePath());
    Boolean fileExists = file.exists();

    assertTrue(fileExists);

    file.delete();
    assertTrue("File does not exist.", fileExists);
  }

  @Test
  public void testDownloadFails() {
    client = new Client(INVALID_URL);

    expectedException.expect(life.qbic.exceptions.ApplicationException.class);
    expectedException.expectMessage("ERROR WHILE DOWNLOADING FILE: IOException");

    client.downloadFromURL();
  }

  @Test
  public void testFileNotFoundException() {
    System.setProperty("user.dir", "/usr/bin/");
    client.downloadFromURL();
  }


}
