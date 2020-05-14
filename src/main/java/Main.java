import dto.Chain;
import dto.Person;
import dto.Tree;
import java.util.*;
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
  
  public void algorithm() {
    
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

        // UNIT TEST 9
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, 1583071200, -1));
        people.add(new Person(2, 1583676000, 1));
        people.add(new Person(3, 1583679600, 2));
        people.add(new Person(4, 1584280800, 3));
        people.add(new Person(5, 1584284400, 4));
        people.add(new Person(6, 1585501200, 4));


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


            Collections.sort(global_chains, Collections.reverseOrder());
            for (Chain c : global_chains) System.out.print(c.toString());
            System.out.print('\n');
        }

        System.out.println("\nFin du programme");
  }
}
