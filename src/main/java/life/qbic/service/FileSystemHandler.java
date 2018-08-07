package life.qbic.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.plaf.metal.MetalLookAndFeel;
import java.io.File;

/**
 * This class is responsible for moving the the changed artifact to the desired location e.g. to the liferay filesystem in order to update the portal
 */
public class FileSystemHandler {

    private static final Logger LOG = LogManager.getLogger(FileSystemHandler.class);

    private String tempPath;
    private String outPath;

    public FileSystemHandler(String tempPath, String outPath, String fileName){
        this.tempPath = tempPath;
        this.outPath = outPath;

        File artifactFile = new File(tempPath);
        LOG.info(artifactFile.getPath());
        LOG.info(outPath+"/"+fileName);

        artifactFile.renameTo(new File(outPath+"/"+fileName));

    }

    public String getTempPath() {
        return tempPath;
    }

    public String getOutPath() {
        return outPath;
    }
}
