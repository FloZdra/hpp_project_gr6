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

    Chain top_1_chain = null;
    Chain top_2_chain = null;
    Chain top_3_chain = null;

    int min_top_chain = 0;

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

        int i = 1;

        boolean end;
        do {
//            if (i % 5000 == 0) {
//                System.out.println(i);
//                OptionalDouble up = PeopleHashMap.update_time.stream().mapToDouble(x -> x).average();
//                OptionalDouble add_p_h = PeopleHashMap.add_person_to_hashmap.stream().mapToDouble(x -> x).average();
//                OptionalDouble add_p_t = PeopleHashMap.add_person_to_tree.stream().mapToDouble(x -> x).average();
//                OptionalDouble get_c = PeopleHashMap.get_chains.stream().mapToDouble(x -> x).average();
//                OptionalDouble write = PeopleHashMap.writing.stream().mapToDouble(x -> x).average();
//
//                System.out.print("Update time : " + up.getAsDouble() + "\t\t");
//                System.out.print("Add person hashmap time : " + add_p_h.getAsDouble() + "\t\t");
//                System.out.print("Add person tree time : " + add_p_t.getAsDouble() + "\t\t");
//                System.out.print("Get chains time : " + get_c.getAsDouble() + "\t\t");
//                System.out.print("Writing time : " + write.getAsDouble() + "\n");
//
//                PeopleHashMap.update_time.clear();
//                PeopleHashMap.add_person_to_hashmap.clear();
//                PeopleHashMap.add_person_to_tree.clear();
//                PeopleHashMap.get_chains.clear();
//                PeopleHashMap.writing.clear();
//            }
//
//            i++;

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

//        OptionalDouble up = PeopleHashMap.update_time.stream().mapToDouble(x -> x).average();
//        OptionalDouble add_p_h = PeopleHashMap.add_person_to_hashmap.stream().mapToDouble(x -> x).average();
//        OptionalDouble add_p_t = PeopleHashMap.add_person_to_tree.stream().mapToDouble(x -> x).average();
//        OptionalDouble get_c = PeopleHashMap.get_chains.stream().mapToDouble(x -> x).average();
//        OptionalDouble write = PeopleHashMap.writing.stream().mapToDouble(x -> x).average();
//
//        System.out.print("Update time : " + up.getAsDouble() + "\t\t");
//        System.out.print("Add person hashmap time : " + add_p_h.getAsDouble() + "\t\t");
//        System.out.print("Add person tree time : " + add_p_t.getAsDouble() + "\t\t");
//        System.out.print("Get chains time : " + get_c.getAsDouble() + "\t\t");
//        System.out.print("Writing time : " + write.getAsDouble() + "\n");

    }

    public void addNewPerson(Person new_person) {

        if (writer != null) {

            long update_time;
            long add_person_to_hashmap;
            long add_person_to_tree;
            long get_chains;
            long writing;

            update_time = System.nanoTime();

            boolean optimize = true;
            if (optimize) {
                // Update Part
                // Optimized
                min_top_chain = 0;
                if (top_1_chain != null) {
                    top_1_chain.getRoot().getTree_in().updateTree(new_person.getDiagnosed_ts());
                    PeopleHashMap.nb_update++;
                    Tree t = top_1_chain.getRoot().getTree_in();
                    min_top_chain = t.getWeightOfChainEndingWith(top_1_chain.getEnd());
                }
                if (top_2_chain != null) {
                    top_2_chain.getRoot().getTree_in().updateTree(new_person.getDiagnosed_ts());
                    PeopleHashMap.nb_update++;
                    Tree t = top_2_chain.getRoot().getTree_in();
                    min_top_chain = Integer.min(min_top_chain, t.getWeightOfChainEndingWith(top_2_chain.getEnd()));
                }
                if (top_3_chain != null) {
                    top_3_chain.getRoot().getTree_in().updateTree(new_person.getDiagnosed_ts());
                    PeopleHashMap.nb_update++;
                    Tree t = top_3_chain.getRoot().getTree_in();
                    min_top_chain = Integer.min(min_top_chain, t.getWeightOfChainEndingWith(top_3_chain.getEnd()));
                }

                // For every tree
                List<Tree> new_trees = new ArrayList<>();
                ListIterator<Tree> treeIterator = trees.listIterator();
                while (treeIterator.hasNext()) {
                    Tree t = treeIterator.next();
                    if (t.getPotential_top_chain_weight() + 10 >= min_top_chain) {

                        //System.out.println("Min top chain : " + min_top_chain);

                        // Add every person in waiting list
                        ListIterator<Person> personIterator = t.getPeopleToAdd().listIterator();
                        while (personIterator.hasNext()) {
                            Person personToAdd = personIterator.next();
                            Person contamined_by = PeopleHashMap.getPersonWithId(personToAdd.getContaminated_by_id());

                            if (contamined_by == null) {
                                new_trees.add(new Tree(personToAdd));
                            } else {
                                Tree tree_to_update = contamined_by.getTree_in();
                                tree_to_update.updateTree(personToAdd.getDiagnosed_ts());
                                PeopleHashMap.nb_update++;

                                if (contamined_by.getWeight() == 0) {
                                    new_trees.add(new Tree(personToAdd));
                                } else {
                                    tree_to_update.addPersonToTree(personToAdd, contamined_by);
                                }
                            }
                            personIterator.remove();
                        }

                        t.updateTree(new_person.getDiagnosed_ts());
                        PeopleHashMap.nb_update++;

                        if (t.getChains().isEmpty())
                            treeIterator.remove();
                    } else {
                        // System.out.println("Dodge update!");
                    }
                    if (new_person.getDiagnosed_ts() - t.getLast_update() > 1209600) {
                        t.deleteTree();
                        treeIterator.remove();
                    }
                }
                trees.addAll(new_trees);

//            PeopleHashMap.iteration++;
//            System.out.println("Iterations : " + PeopleHashMap.iteration + "\tUpdate : " + PeopleHashMap.nb_update);
//            PeopleHashMap.nb_update = 0;

            } else {
                // No optimization
                // Recalculate every score of every chain
                ListIterator<Tree> iterator = trees.listIterator();

                // For every tree
                while (iterator.hasNext()) {
                    Tree t = iterator.next();
                    t.updateTree(new_person.getDiagnosed_ts());
                    PeopleHashMap.nb_update++;
                    if (t.getChains().isEmpty())
                        iterator.remove();
                }
//            PeopleHashMap.iteration++;
//            System.out.println("Iterations : " + PeopleHashMap.iteration + "\tUpdate : " + PeopleHashMap.nb_update);
//            PeopleHashMap.nb_update = 0;
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

                // We have 3 cases here :
                // First, if the contaminator does not exist : we create a tree
                // Else, if the person exist, is in a tree, and has a weight different of zero,
                // we add him in the tree, else we create a tree
                // Else, if the contaminator exist but he is in a waiting list of a tree, he is not in the tree yet
                // so we need to had the new_person in the waiting list or

                if (contaminated_by == null) {
                    trees.add(new Tree(new_person));
                } else if (contaminated_by.isIn_the_tree()) {
                    // If the tree has no potential to be in top 3, we put the new person in the waiting list,
                    // otherwise we add him in the tree
                    Tree tree_to_add = contaminated_by.getTree_in();
                    if (tree_to_add.getPotential_top_chain_weight() + 10 >= min_top_chain) {
                        tree_to_add.addPersonToTree(new_person, contaminated_by);
                    } else {
                        tree_to_add.addPersonToWaiting(new_person);
                    }
                } else if (!contaminated_by.isIn_the_tree()) {
                    Tree tree_to_add = contaminated_by.getTree_in();
                    tree_to_add.addPersonToWaiting(new_person);
                } else {
                    System.out.println("We missed a case");
                }
            }

            get_chains = System.nanoTime();

            top_1_chain = top_2_chain = top_3_chain = null;

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

            PeopleHashMap.update_time.add(update_time);
            PeopleHashMap.add_person_to_hashmap.add(add_person_to_hashmap);
            PeopleHashMap.add_person_to_tree.add(add_person_to_tree);
            PeopleHashMap.get_chains.add(get_chains);
            PeopleHashMap.writing.add(writing);
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

