package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

/**
 * Open, Read and Close a file Line by Line
 * help from : https://www.baeldung.com/java-read-lines-large-file
 */
public class FileReader {

    private InputStream inputStream = null;
    private Scanner sc = null;

    void openFile(URL path) {
        try {
            inputStream = path.openStream();
            sc = new Scanner(inputStream, "UTF-8");
        } catch (IOException e) {
            System.err.println("File not found");
            e.printStackTrace();
        }
    }

    String readLine() throws IOException {
        if (sc.hasNextLine()) {
            String line = sc.nextLine();
            return line;
        } else {
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
            // end of file
            return "";
        }
    }

    void closeFile() {
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

}
