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
import java.util.*;

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

        List<Long> find_person = new ArrayList<>();
        List<Long> add_person = new ArrayList<>();
        List<Long> find_next_person = new ArrayList<>();
        long begin;

        boolean end;
        do {


            begin = System.nanoTime();

            // Find the file with the oldest person
            Person next_person = initialList.get(0);
            for (Person person : initialList) {
                if (person.getDiagnosed_ts() < next_person.getDiagnosed_ts()) {
                    next_person = person;
                }
            }

            find_person.add(System.nanoTime() - begin);
            begin = System.nanoTime();


            // Add the new person into the memory, and start the process
            addNewPerson(next_person.clone());


            add_person.add(System.nanoTime() - begin);
            begin = System.nanoTime();

            int index_next_person = initialList.indexOf(next_person);

            String read_string = fileReaders.get(index_next_person).readLine();

            find_next_person.add(System.nanoTime() - begin);

            if (!read_string.equals("")) {
                initialList.set(index_next_person, parser.parseLine(fileReaders.get(index_next_person).getCountry(), read_string));
            } else {
                fileReaders.remove(fileReaders.get(initialList.indexOf(next_person)));
                initialList.remove(next_person);
            }

            end = fileReaders.isEmpty();

        }
        while (!end);

        OptionalDouble avg_find_person = find_person.stream().mapToDouble(x -> x).average();
        OptionalDouble avg_add_person = add_person.stream().mapToDouble(x -> x).average();
        OptionalDouble avg_find_next_person = find_next_person.stream().mapToDouble(x -> x).average();

//        System.out.println("Average find person : " + avg_find_person.toString());
//        System.out.println("Average add person : " + avg_add_person.toString());
//        System.out.println("Average find next person : " + avg_find_next_person.toString());


    }

    public void addNewPerson(Person new_person) {

        if (writer != null) {

            long update_time;
            long add_person_to_hashmap;
            long add_person_to_tree;
            long get_chains;
            long writing;

            update_time = System.nanoTime();

            // Recalculate every score of every chain
            ListIterator<Tree> iterator = trees.listIterator();
            while (iterator.hasNext()) {
                Tree t = iterator.next();
                t.updateChains(new_person.getDiagnosed_ts());
                if (t.getRoot() == null)
                    iterator.remove();
            }

            add_person_to_hashmap = System.nanoTime();

            // Add person to HashMap
            PeopleHashMap.addPersonToMap(new_person);

            add_person_to_tree = System.nanoTime();

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

            get_chains = System.nanoTime();

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

            sb.deleteCharAt(sb.length() - 1); // Remove this line for comparing with Arnette's results

//            // Get all the chains
//            List<Chain> global_chains = new ArrayList<>();
//            for (Tree t : trees) {
//                global_chains.addAll(t.getChains());
//            }
//
//            global_chains.sort(Collections.reverseOrder());
//
//            StringBuilder sb = new StringBuilder();
//
//            for (int i = 0; i < 3 && i < global_chains.size(); i++)
//                sb.append(global_chains.get(i).toString());

            writing = System.nanoTime();

            writer.write(sb.toString() + "\n"); // Remove trim() for comparing with Arnette's results

            long end = System.nanoTime();

            update_time = add_person_to_hashmap - update_time;
            add_person_to_hashmap = add_person_to_tree - add_person_to_hashmap;
            add_person_to_tree = get_chains - add_person_to_tree;
            get_chains = writing - get_chains;
            writing = end - writing;

            System.out.print("Update time : " + update_time + "\t\t");
            System.out.print("Add person hashmap time : " + add_person_to_hashmap + "\t\t");
            System.out.print("Add person tree time : " + add_person_to_tree + "\t\t");
            System.out.print("Get chains time : " + get_chains + "\t\t");
            System.out.print("Writing time : " + writing + "\n");


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

