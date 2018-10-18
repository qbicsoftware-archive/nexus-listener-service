package life.qbic.service;

import life.qbic.exceptions.ApplicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * This Class is responsible for downloading the changed files from the repository to a temp location in order to make it available for the class FileSystemHandler
 */
public class Client {

    private String url;
    private String fileName;
    private String fileFormat;
    private String tmpFilePath;

    private static final Logger LOG = LogManager.getLogger(Client.class);


    public Client(String url){
        this.url = url;
        filterName(url);
    }

    public void downloadFromURL(){

        try{
            final URL website = new URL(url);
            LOG.info("Downloading from {}", url);
            final ReadableByteChannel rbc = Channels.newChannel(website.openStream());

            final File tempFile = File.createTempFile(fileName,fileFormat);

            final FileOutputStream fos = new FileOutputStream(tempFile); //path to where the file is written
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE); //write
            tmpFilePath = tempFile.getAbsolutePath();

            LOG.info("DOWNLOAD: the file was downloaded to {}", tmpFilePath);
            fos.close();

        } catch (IOException e) {
            LOG.error("ERROR WHILE DOWNLOADING FILE: IOException ", e);
            throw new ApplicationException("ERROR WHILE DOWNLOADING FILE: IOException");

        }

    }

    //This Method retrieves the artifact name from the URL in order to provide proper naming of the download file
    private void filterName(String url){

        String[] splitName = url.split("/");
        String name = splitName[splitName.length -1];
        int index = 0;

        for(int i = name.length() -1 ;  0 < i; i--){
            //find first dot from behind
            if(name.charAt(i) == '.'){
                index = i;
                break;
            }
        }

        fileName = name.substring(0,index);
        fileFormat = name.substring(index);

    }

    public String getTmpFilePath() {
        return tmpFilePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileFormat() {
        return fileFormat;
    }
}
