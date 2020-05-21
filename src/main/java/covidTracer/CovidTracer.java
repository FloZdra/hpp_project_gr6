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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class CovidTracer {
    static boolean readThreadAlive = true;
    static boolean addThreadAlive = true;

    static Thread readThread;
    static Thread addThread;
    static Thread writeThread;

    List<URL> url_files;
    PrintWriter writer = null;

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
                fileReader.closeFile();
            }
        }

        BlockingQueue<Person> blockingQueueRead = new LinkedBlockingDeque<>(5000);
        BlockingQueue<String> blockingQueueWrite = new LinkedBlockingDeque<>(5000);
        //ExecutorService executor = Executors.newFixedThreadPool(3);

        // TODO optimize parameters (not necessary to be global)
        readNewPersonRunnable readNewPersonRunnable = new readNewPersonRunnable(blockingQueueRead, initialList, fileReaders, parser);
        addNewPersonRunnable addNewPersonRunnable = new addNewPersonRunnable(blockingQueueRead, blockingQueueWrite, readThreadAlive);
        writeFileRunnable writeFileRunnable = new writeFileRunnable(writer, blockingQueueWrite, addThreadAlive);

        readThread = new Thread(readNewPersonRunnable);
        addThread = new Thread(addNewPersonRunnable);
        writeThread = new Thread(writeFileRunnable);

        addThread.setPriority(10);

        readThread.start();
        addThread.start();
        writeThread.start();
        try {
            readThread.join();
            addThread.join();
            writeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

class readNewPersonRunnable implements Runnable {
    BlockingQueue<Person> blockingQueueRead;
    List<Person> initialList;
    List<FileReader> fileReaders;
    Parser parser;

    public readNewPersonRunnable(BlockingQueue<Person> blockingQueueRead, List<Person> initialList, List<FileReader> fileReaders, Parser parser) {
        this.blockingQueueRead = blockingQueueRead;
        this.initialList = initialList;
        this.fileReaders = fileReaders;
        this.parser = parser;
    }

    public void run() {
        readNewPerson();
    }

    public void readNewPerson() {
        boolean end;
        do {
            // System.out.println("Read new person started");
            // Add a new person to the queue
            try {
                // Find the file with the oldest person
                Person next_person = initialList.stream()
                        .sorted(Comparator.comparing(Person::getDiagnosed_ts))
                        .collect(Collectors.toList()).get(0);

                blockingQueueRead.put(next_person.clone());

                int index_next_person = initialList.indexOf(next_person);
                String read_string = fileReaders.get(index_next_person).readLine();

                if (!read_string.equals("")) {
                    initialList.set(index_next_person, parser.parseLine(fileReaders.get(index_next_person).getCountry(), read_string));
                } else {
                    fileReaders.remove(fileReaders.get(initialList.indexOf(next_person)));
                    initialList.remove(next_person);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            end = fileReaders.isEmpty();

            //System.out.println("Read new person ended");
            //System.out.println("Blocking queue read contains : " + blockingQueueRead.size());
        }
        while (!end);
        //System.out.println("Read has terminated");
    }
}

class addNewPersonRunnable implements Runnable {
    BlockingQueue<Person> blockingQueueRead;
    BlockingQueue<String> blockingQueueWrite;
    boolean readerIsAlive;

    List<Tree> trees;
    Chain top_1_chain = null;
    Chain top_2_chain = null;
    Chain top_3_chain = null;
    int min_top_chain = 0;

    public addNewPersonRunnable(BlockingQueue<Person> blockingQueueRead, BlockingQueue<String> blockingQueueWrite, boolean readerIsAlive) {
        this.blockingQueueRead = blockingQueueRead;
        this.blockingQueueWrite = blockingQueueWrite;
        this.trees = new ArrayList<>();
        this.readerIsAlive = readerIsAlive;
    }

    public void run() {
        addNewPerson();
    }

    public void addNewPerson() {
        int i = 1;
        while (CovidTracer.readThread.isAlive() || !blockingQueueRead.isEmpty()) {
            //System.out.println("Add New Person thread started");
            if (!blockingQueueRead.isEmpty()) {
                try {
                    Person new_person = blockingQueueRead.take();

                    if (i % 5000 == 0) {
                        System.out.println(i);
                    }
                    i++;

                    // Flo part
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
                        }
                        if (new_person.getDiagnosed_ts() - t.getLast_update() > 1209600) {
                            t.deleteTree();
                            treeIterator.remove();
                        }
                    }
                    trees.addAll(new_trees);

                    // Add person to HashMap
                    PeopleHashMap.addPersonToMap(new_person);

                    // If the person is contaminated by someone unknown
                    if (new_person.getContaminated_by_id() == -1) {
                        trees.add(new Tree(new_person));
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
                        }
                    }

                    top_1_chain = top_2_chain = top_3_chain = null;

                    for (Tree t : trees) {
                        if (t.getLast_update() == new_person.getDiagnosed_ts()) {
                            Chain[] top_tree_chains = t.getTop_chains();
                            updateTopChains(top_tree_chains[0]);
                            updateTopChains(top_tree_chains[1]);
                            updateTopChains(top_tree_chains[2]);
                        }
                    }
                    StringBuilder sb = new StringBuilder();

                    if (top_1_chain != null)
                        sb.append(top_1_chain.toString());
                    if (top_2_chain != null)
                        sb.append(top_2_chain.toString());
                    if (top_3_chain != null)
                        sb.append(top_3_chain.toString());


                    blockingQueueWrite.put(sb.toString());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println("Add New Person thread Ended");
            //System.out.println("Blocking queue write contains : " + blockingQueueWrite.size());
        }
    }

    public void updateTopChains(Chain c) {
        if (c == null) return;
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

class writeFileRunnable implements Runnable {
    PrintWriter writer;
    BlockingQueue<String> blockingQueueWrite;

    boolean adderIsAlive;

    public writeFileRunnable(PrintWriter printWriter, BlockingQueue<String> blockingQueueWrite, boolean adderIsAlive) {
        this.blockingQueueWrite = blockingQueueWrite;
        this.writer = printWriter;
        this.adderIsAlive = adderIsAlive;
    }

    public void run() {
        writePersonToFile();
    }

    public void writePersonToFile() {
        while (CovidTracer.addThread.isAlive() || !blockingQueueWrite.isEmpty()) {
            //System.out.println("Write person threat started");
            if (!blockingQueueWrite.isEmpty()) {
                try {
                    String sb = blockingQueueWrite.take();
                    writer.write(sb.trim() + "\n"); // Remove trim() for comparing with Arnette's results
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println("Write person thread Ended");
        }
    }
}

