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

//        // UNIT TEST 2
//        List<Person> people = new ArrayList<>();
//        people.add(new Person(1, 1584540000, -1));
//        people.add(new Person(3, 1584558000, 1));
//        people.add(new Person(2, 1584712800, -1));
//        people.add(new Person(4, 1585324800, -1));
//        people.add(new Person(7, 1585357200, 1));

//        // UNIT TEST 3
//        List<Person> people = new ArrayList<>();
//        people.add(new Person(1, 1584540000, -1));
//        people.add(new Person(2, 1584540000, -1));

//        // UNIT TEST 4
//        List<Person> people = new ArrayList<>();
//        people.add(new Person(1, 1584489600, -1));
//        people.add(new Person(2, 1584576000, 1));
//        people.add(new Person(3, 1584662400, -1));
//        people.add(new Person(4, 1584748800, -1));
//        people.add(new Person(5, 1585267200, 3));
//        people.add(new Person(6, 1585440000, 4));
//        people.add(new Person(7, 1586390400, 5));
//        people.add(new Person(8, 1586908800, 6));

//        // UNIT TEST 9
//        List<Person> people = new ArrayList<>();
//        people.add(new Person(1, 1583071200, -1));
//        people.add(new Person(2, 1583676000, 1));
//        people.add(new Person(3, 1583679600, 2));
//        people.add(new Person(4, 1584280800, 3));
//        people.add(new Person(5, 1584284400, 4));
//        people.add(new Person(6, 1585501200, 4));

        List<Tree> trees = new ArrayList<>();
        List<Chain> global_chains = new ArrayList<>();

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


            try (PrintWriter writer = new PrintWriter(new File(Main.class.getResource("output_test").getPath()+"/output.csv"))) {

                StringBuilder sb = new StringBuilder();


                for (Chain c : global_chains) {
                    sb.append(c.toString());
                    //System.out.print(c.toString());
                }
                sb.append('\n');
                writer.write(sb.toString());
            }catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("\nFin du programme");
    }


}

