package life.qbic.service;

import life.qbic.exceptions.ApplicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
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
    private String filePath;

    private static final Logger LOG = LogManager.getLogger(Client.class);


    public Client(String url){
        this.url = url;
        filterName(url);
    }

    public void downloadFromURL(){

        try{
            URL website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());

            File tempFile = File.createTempFile(fileName, fileFormat);
            filePath = tempFile.getPath();
            fileName = tempFile.getName(); //TODO her changed file name!
            LOG.info("client file name "+fileName);

            FileOutputStream fos = new FileOutputStream(filePath); //path to where the file is written
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE); //write

        } catch (IOException e) {
            LOG.error("ERROR WHILE DOWNLOADING FILE: IOException "+e);
            throw new ApplicationException("ERROR WHILE DOWNLOADING FILE: IOException");

        }

    }

    //This Method retrieves the artifact name from the URL in order to provide proper naming of the download file
    private void filterName(String url){

        String[] urlSplit = url.split("/");
        String name = urlSplit[urlSplit.length -1];

        String[] nameSplit = name.split("\\.");

        fileFormat = nameSplit[nameSplit.length -1];
        fileName = name.split(fileFormat)[0];

    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileFormat() {
        return fileFormat;
    }
}
