package covidTracer;

import dto.Chain;
import dto.PeopleHashMap;
import dto.Person;
import dto.Tree;
import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.stat.regression.UpdatingMultipleLinearRegression;
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

            // Update Part
            min_top_chain = 0;
            if (top_1_chain != null) {
                top_1_chain.getRoot().getTree_in().updateTree(new_person.getDiagnosed_ts());
                Tree t = top_1_chain.getRoot().getTree_in();
                min_top_chain = t.getWeightOfChainEndingWith(top_1_chain.getEnd());
            }
            if (top_2_chain != null) {
                top_2_chain.getRoot().getTree_in().updateTree(new_person.getDiagnosed_ts());
                Tree t = top_2_chain.getRoot().getTree_in();
                min_top_chain = Integer.min(min_top_chain, t.getWeightOfChainEndingWith(top_2_chain.getEnd()));
            }
            if (top_3_chain != null) {
                top_3_chain.getRoot().getTree_in().updateTree(new_person.getDiagnosed_ts());
                Tree t = top_3_chain.getRoot().getTree_in();
                min_top_chain = Integer.min(min_top_chain, t.getWeightOfChainEndingWith(top_3_chain.getEnd()));
            }

            top_1_chain = top_2_chain = top_3_chain = null;

            // Update trees
            List<Tree> new_trees = new ArrayList<>();
            ListIterator<Tree> treeIterator = trees.listIterator();
            while (treeIterator.hasNext()) {
                Tree t = treeIterator.next();

                // Only for trees which have a potential
                if (t.getPotential_top_chain_weight() + 10 >= min_top_chain) {

                    // Add every person in waiting list
                    ListIterator<Person> personIterator = t.getWaitingList().listIterator();
                    while (personIterator.hasNext()) {
                        Person personToAdd = personIterator.next();
                        Person contamined_by = PeopleHashMap.getPersonWithId(personToAdd.getContaminated_by_id());

                        if (contamined_by == null) {
                            new_trees.add(new Tree(personToAdd));
                        } else {
                            Tree tree_to_update = contamined_by.getTree_in();
                            tree_to_update.updateTree(personToAdd.getDiagnosed_ts());

                            if (contamined_by.getWeight() == 0) {
                                new_trees.add(new Tree(personToAdd));
                            } else {
                                tree_to_update.addPersonToTree(personToAdd, contamined_by);
                            }
                        }
                        personIterator.remove();
                    }
                    t.updateTree(new_person.getDiagnosed_ts());

                    if (t.getChains().isEmpty())
                        treeIterator.remove();

                    // Update top chains
                    Chain[] top_tree_chains = t.getTop_chains();
                    updateTopChains(top_tree_chains[0]);
                    updateTopChains(top_tree_chains[1]);
                    updateTopChains(top_tree_chains[2]);
                }
                if (new_person.getDiagnosed_ts() - t.getLast_update() > 1209600) {
                    t.deleteTree();
                    treeIterator.remove();
                }
            }
            trees.addAll(new_trees);

            // Add person to HashMap
            PeopleHashMap.addPersonToMap(new_person);

            Tree tree_modified = null;
            // If the person is contaminated by someone unknown
            if (new_person.getContaminated_by_id() == -1) {
                tree_modified = new Tree(new_person);
                trees.add(tree_modified);
            } else {

                // If we found the person who contaminated the new person
                Person contaminated_by = PeopleHashMap.getPersonWithId(new_person.getContaminated_by_id());

                // We have 3 cases here :
                // First, IF the contaminator does not exist : we create a tree
                // ELSE, IF the person exist (so he has a weight != 0), then IF the tree has a potential to be in the top 3,
                // we add him in the tree, OTHERWISE we had him iun the waiting list
                // ELSE, IF the contaminator exist but he is not in a tree (so he is in a waiting list of a tree),
                // we need to had the new_person in the waiting list

                if (contaminated_by == null) {
                    tree_modified = new Tree(new_person);
                    trees.add(tree_modified);
                } else if (contaminated_by.isIn_the_tree()) {
                    // If the tree has no potential to be in top 3, we put the new person in the waiting list,
                    // otherwise we add him in the tree
                    tree_modified = contaminated_by.getTree_in();
                    if (tree_modified.getPotential_top_chain_weight() + 10 >= min_top_chain) {
                        tree_modified.addPersonToTree(new_person, contaminated_by);
                    } else {
                        tree_modified.addPersonToWaiting(new_person);
                    }
                } else if (!contaminated_by.isIn_the_tree()) {
                    tree_modified = contaminated_by.getTree_in();
                    tree_modified.addPersonToWaiting(new_person);
                }
            }

            // Update top chains only with tree modified
            if (tree_modified != null) {
                Chain[] top_tree_chains = tree_modified.getTop_chains();
                for (Chain c : top_tree_chains) {
                    if (c != null) {
                        if (!c.equals(top_1_chain) && !c.equals(top_2_chain) && !c.equals(top_3_chain))
                            updateTopChains(c);
                        else
                            sortTopChains();
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

            writer.write(sb.toString() + "\n"); // Remove trim() for comparing with Arnette's results
        }

    }


    public void updateTopChains(Chain c) {
        if (c == null) return;
        // If the chain is already in the list
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


    private void sortTopChains() {
        if (top_2_chain != null) {
            if (top_2_chain.compareTo(top_1_chain) > 0) {
                Chain temp = top_1_chain;
                top_1_chain = top_2_chain;
                top_2_chain = temp;
            } else if (top_3_chain != null) {
                if (top_3_chain.compareTo(top_2_chain) > 0) {
                    if (top_3_chain.compareTo(top_1_chain) > 0) {
                        Chain temp = top_1_chain;
                        top_1_chain = top_3_chain;
                        top_3_chain = top_2_chain;
                        top_2_chain = temp;
                    } else {
                        Chain temp = top_2_chain;
                        top_2_chain = top_3_chain;
                        top_3_chain = temp;
                    }
                }
            }
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

