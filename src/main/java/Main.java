import dto.Person;
import utils.FileReader;
import utils.Parser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final ArrayList<URL> urls = new ArrayList<>();

    private static final URL france = Main.class.getResource("/data/20/France.csv");
    private static final URL italy = Main.class.getResource("/data/20/Italy.csv");
    private static final URL spain = Main.class.getResource("/data/20/Spain.csv");

    public static void main(String[] args) {
        System.out.println("test");
        Person myPerson = new Person(1, 1, 1);
        try {
            analyseFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main function of project.
     * Create output result in new file.
     *
     * @throws IOException
     */
    public static void analyseFiles() throws IOException {
        Parser parser = new Parser();

        urls.add(france);
        urls.add(italy);
        urls.add(spain);

        List<FileReader> fileReaders = new ArrayList<>();

        // Open each file
        for (URL country_url : urls) {
            FileReader fileReader = new FileReader();
            fileReaders.add(fileReader);
            fileReader.openFile(country_url);
        }

        // Read each file and construct trees
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // ! At the moment the lecture is File by File !
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        for (FileReader fileReader : fileReaders) {

            boolean end = false;
            do {
                String read_string = fileReader.readLine();
                if (!read_string.equals("")) {
                    Person read_person = parser.parseLine(read_string);

                    // Add person to tree
                    // ...
                } else {
                    // end of file
                    end = true;
                    fileReader.closeFile();
                }
            } while (!end);
        }
    }
}
