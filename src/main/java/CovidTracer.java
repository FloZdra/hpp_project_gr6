import dto.Chain;
import dto.Person;
import dto.Tree;
import utils.FileReader;
import utils.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class CovidTracer {
    List<URL> url_files;

    public CovidTracer(List<URL> urls) {
        url_files = urls;
    }

    /**
     * Main function of program
     **/
    public void launchAnalysis() {
        List<Person> people = new ArrayList<>();

        try {
            analyseFiles(people);
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        algorithm(people);
    }

    /**
     * Main function of project.
     * Create output result in new file.
     *
     * @throws IOException
     */
    public void analyseFiles(List<Person> people) throws IOException {
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

            Person cloned_person = next_person.clone();
            people.add(cloned_person);

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

    public void algorithm(List<Person> people) {

        List<Tree> trees = new ArrayList<>();
        List<Chain> global_chains = new ArrayList<>();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new File("src/main/resources/output_generated/" + "output.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (Person p : people) {
            // Clear all the chains
            global_chains.clear();

            // Recalculate every score of every chain
            ListIterator<Tree> iterator = trees.listIterator();
            while (iterator.hasNext()) {
                Tree t = iterator.next();
                t.updateChains(p.getDiagnosed_ts());
                if (t.getRoot() == null)
                    iterator.remove();
            }

            // If the person is contaminated by someone unknown
            if (p.getContaminated_by_id() == -1) {
                trees.add(new Tree(p));
            } else {
                // If we found the person who contaminated the new person
                boolean added = false;
                for (Tree t : trees) {
                    if (t.addPerson(p, null)) {
                        added = true;
                        break;
                    }
                }
                // If we didn't find him, we create a new tree where the person will be the root
                if (!added) {
                    trees.add(new Tree(p));
                }
            }


            for (Tree t : trees) {
                global_chains.addAll(t.getChains());
            }


            global_chains.sort(Collections.reverseOrder());
            List<Chain> chains_to_print = global_chains.subList(0, Math.min(global_chains.size(), 3));

            StringBuilder sb = new StringBuilder();

            for (Chain c : chains_to_print) {
                sb.append(c.toString());
            }
            writer.write(sb.toString().trim() + "\n");
        }

        writer.close();

        System.out.println("\nFin du programme");
    }


}

