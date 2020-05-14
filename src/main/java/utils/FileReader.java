package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Open, Read and Close a file Line by Line
 * help from : https://www.baeldung.com/java-read-lines-large-file
 */
public class FileReader {

    private InputStream inputStream = null;
    private Scanner sc = null;
    private String country;

    public void openFile(URL path) {
        this.country = FilenameUtils.getBaseName(path.getPath());
        try {
            inputStream = path.openStream();
            sc = new Scanner(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("File not found");
            e.printStackTrace();
        }
    }

    public String readLine() {
        if (sc.hasNextLine()) {
            return sc.nextLine();
        } else {
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                try {
                    throw sc.ioException();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // end of file
            return "";
        }
    }

    public void closeFile() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                System.err.println("Cannot close file");
                e.printStackTrace();
            }
        }
        if (sc != null) {
            sc.close();
        }
    }

    // Generated functions

    public String getCountry() {
        return country;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public Scanner getSc() {
        return sc;
    }
}
