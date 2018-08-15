package life.qbic.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is responsible for moving the the changed artifact to the desired location e.g. to the liferay filesystem in order to update the portal
 */
public class FileSystemHandler {

    private static final Logger LOG = LogManager.getLogger(FileSystemHandler.class);

    private String tempPath;
    private String outPath;

    public FileSystemHandler(String tempPath, String out, String fileName){
        String fileSeparator = System.getProperty("file.separator");

        this.tempPath = tempPath;
        this.outPath = out + fileSeparator + fileName;

    }

    /**
     * This method moves the temporary file created with the client to the desired output path specified with the commandline argument
     * @throws IOException
     */
    public void move(){

        try{
        Path artifactFile = Paths.get(tempPath);
        Files.setPosixFilePermissions(artifactFile, java.nio.file.attribute.PosixFilePermissions.fromString("rw-rw-rw-"));

        Path movedFile = Paths.get(outPath);

        Files.move(artifactFile,movedFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        Files.setPosixFilePermissions(movedFile, java.nio.file.attribute.PosixFilePermissions.fromString("rw-rw-rw-"));

        LOG.info("FILE MOVED: the downloaded file is now moved to the desired path");

        }catch (IOException io){
            //delete tmp file if cannot be moved
            new File(tempPath).delete();

            LOG.error("ERROR WHILE MOVING FILE: IOException "+io.getMessage());
        }
    }


    public String getTempPath() {
        return tempPath;
    }

    public String getOutPath() {
        return outPath;
    }
}
