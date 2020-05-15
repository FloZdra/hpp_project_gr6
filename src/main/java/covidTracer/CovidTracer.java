package covidTracer;

import dto.Chain;
import dto.PeopleHashMap;
import dto.Person;
import dto.Tree;
import org.apache.commons.io.IOUtils;
import utils.FileReader;
import utils.Parser;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CovidTracer {

    List<URL> url_files;
    PrintWriter writer = null;

    List<Tree> trees = new ArrayList<>();
    List<Chain> global_chains = new ArrayList<>();

    public CovidTracer(List<URL> urls) {
        url_files = urls;
    }

    /**
     * Main function of program
     **/
    public void launchAnalysis() {

        // Open the file we will enter results in
        try {
            writer = new PrintWriter(new File(getClass().getResource("/output_generated/output.csv").getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start analysing files
        analyseFiles();

        writer.close();

        // Copy output file from target to src
        copyFiles();
    }

    /**
     * Main function of project.
     * Create output result in new file.
     */
    public void analyseFiles() {
        Parser parser = new Parser();
        List<FileReader> fileReaders = new ArrayList<>();

        // Open each file
        for (URL country_url : url_files) {
            FileReader fileReader = new FileReader();
            fileReaders.add(fileReader);
            fileReader.openFile(country_url);
        }

        // Read each file and construct trees
        List<Person> initialList = new ArrayList<>();
        for (FileReader fileReader : fileReaders) {
            String read_string = fileReader.readLine();
            if (!read_string.equals("")) {
                Person read_person = parser.parseLine(fileReader.getCountry(), read_string);
                // Add person to tree
                initialList.add(read_person);
            } else {
                fileReader.closeFile(); // TODO mark file as ended
            }
        }

        boolean end;
        do {

            // Find the file with the oldest person
            Person next_person = initialList.get(0);
            for (Person person : initialList) {
                if (person.getDiagnosed_ts() < next_person.getDiagnosed_ts()) {
                    next_person = person;
                }
            }

            // Add the new person into the memory, and start the process
            addNewPerson(next_person.clone());

            int index_next_person = initialList.indexOf(next_person);

            String read_string = fileReaders.get(index_next_person).readLine();

            if (!read_string.equals("")) {
                initialList.set(index_next_person, parser.parseLine(fileReaders.get(index_next_person).getCountry(), read_string));
            } else {
                fileReaders.remove(fileReaders.get(initialList.indexOf(next_person)));
                initialList.remove(next_person);
            }

            end = fileReaders.isEmpty();
        }
        while (!end);
    }

    public void addNewPerson(Person new_person) {

        if (writer != null) {

            // Recalculate every score of every chain
            ListIterator<Tree> iterator = trees.listIterator();
            while (iterator.hasNext()) {
                Tree t = iterator.next();
                t.updateChains(new_person.getDiagnosed_ts());
                if (t.getRoot() == null)
                    iterator.remove();
            }

            // Add person to HashMap
            PeopleHashMap.addPersonToMap(new_person);

            // If the person is contaminated by someone unknown
            if (new_person.getContaminated_by_id() == -1) {
                trees.add(new Tree(new_person));
            } else {

                // If we found the person who contaminated the new person
                Person contaminated_by = PeopleHashMap.getPersonWithId(new_person.getContaminated_by_id());
                if (contaminated_by == null || contaminated_by.getWeight() == 0) {
                    trees.add(new Tree(new_person));
                } else {
                    // Get the tree where tu put the new_person
                    contaminated_by.getTree_in().addPersonWithHashMap(new_person, contaminated_by);
                }
            }

            // Get 3 top chains
            Chain top_1_chain = null;
            Chain top_2_chain = null;
            Chain top_3_chain = null;

            for (Tree t : trees) {
                for (Chain c : t.getChains()) {
                    if (top_1_chain == null || c.compareTo(top_1_chain) > 0) {
                        top_3_chain = top_2_chain;
                        top_2_chain = top_1_chain;
                        top_1_chain = c;
                    } else if (top_2_chain == null || c.compareTo(top_2_chain) > 0) {
                        top_3_chain = top_2_chain;
                        top_2_chain = c;
                    } else if (top_3_chain == null || c.compareTo(top_3_chain) > 0) {
                        top_3_chain = c;
                    }
                }
            }
            StringBuilder sb = new StringBuilder();

            if (top_1_chain != null)
                sb.append(top_1_chain.toString());
            if (top_2_chain != null)
                sb.append(top_2_chain.toString());
            if (top_3_chain != null)
                sb.append(top_3_chain.toString());

            sb.deleteCharAt(sb.length() - 1);

            // Write results in file

            writer.write(sb.toString() + "\n"); // Remove trim() for comparing with Arnette's results
        }
    }

    public void copyFiles() {

        // Copy generated output from target to src/main/resources
        try {
            InputStream inputStream = getClass().getResource("/output_generated/output.csv").openStream();
            FileOutputStream fileOS = new FileOutputStream("src/main/resources/output_generated/output.csv");
            int i = IOUtils.copy(inputStream, fileOS);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

