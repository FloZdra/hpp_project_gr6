package covidTracer;

import dto.Chain;
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
    static boolean writeThreadAlive = true;

    static Thread readThread;
    static Thread addThread;
    static Thread writeThread;

    List<URL> url_files;
    PrintWriter writer = null;
    List<Tree> trees = new ArrayList<>();

    private Thread addNewPersonThread;
    private Thread writeFileThread;

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
                fileReader.closeFile(); // TODO mark file as ended if it is not necessary to continue reading
                // But in our case, a new patient can be added even if the file was empty at start
            }
        }

        BlockingQueue<Person> blockingQueueRead = new LinkedBlockingDeque<>(10);
        BlockingQueue<ArrayList<Tree>> blockingQueueWrite = new LinkedBlockingDeque<>(10);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // TODO optimize parameters (not necessary to be global)
        readNewPersonRunnable readNewPersonRunnable = new readNewPersonRunnable(blockingQueueRead, initialList, fileReaders, parser);
        addNewPersonRunnable addNewPersonRunnable = new addNewPersonRunnable(blockingQueueRead, blockingQueueWrite, readThreadAlive);
        writeFileRunnable writeFileRunnable = new writeFileRunnable(writer, blockingQueueWrite, addThreadAlive);

        readThread = new Thread(readNewPersonRunnable);
        addThread = new Thread(addNewPersonRunnable);
        writeThread = new Thread(writeFileRunnable);

        //readThreadAlive = readThread.isAlive();
        //addThreadAlive = addThread.isAlive();
        //writeThreadAlive = writeThread.isAlive();

        //executor.execute(readNewPersonRunnable);
        //executor.execute(addNewPersonRunnable);
        //executor.execute(writeFileRunnable);
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
        boolean end = false;
        do {
            System.out.println("Read new person started");
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
            System.out.println("Read new person ended");
        }
        while (!end);
        System.out.println("Read has terminated");
    }
}

class addNewPersonRunnable implements Runnable {
    BlockingQueue<Person> blockingQueueRead;
    BlockingQueue<ArrayList<Tree>> blockingQueueWrite;
    List<Tree> trees;
    boolean readerIsAlive;

    public addNewPersonRunnable(BlockingQueue<Person> blockingQueueRead, BlockingQueue<ArrayList<Tree>> blockingQueueWrite, boolean readerIsAlive) {
        this.blockingQueueRead = blockingQueueRead;
        this.blockingQueueWrite = blockingQueueWrite;
        this.trees = new ArrayList<>();
        this.readerIsAlive = readerIsAlive;
    }

    public void run() {
        addNewPerson();
    }

    public void addNewPerson() {
        while () {
            System.out.println("State of reader" + readerIsAlive);
            System.out.println("addNewPerson thread started size queue " + blockingQueueRead.size());
            try {
                Person new_person = blockingQueueRead.take();

                // Recalculate every score of every chain
                ListIterator<Tree> iterator = trees.listIterator();
                while (iterator.hasNext()) {
                    Tree t = iterator.next();
                    t.updateChains(new_person.getDiagnosed_ts());
                    if (t.getRoot() == null)
                        iterator.remove();
                }

                // If the person is contaminated by someone unknown
                if (new_person.getContaminated_by_id() == -1) {
                    trees.add(new Tree(new_person));
                } else {
                    // If we found the person who contaminated the new person
                    boolean added = false;
                    for (Tree t : trees) {
                        if (t.addPerson(new_person, null)) {
                            added = true;
                            break;
                        }
                    }
                    // If we didn't find him, we create a new tree where the person will be the root
                    if (!added) {
                        trees.add(new Tree(new_person));
                    }
                }
                blockingQueueWrite.put(new ArrayList<>(trees));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("addNewPerson thread Ended");
        }
    }
}

class writeFileRunnable implements Runnable {
    PrintWriter writer;
    BlockingQueue<ArrayList<Tree>> blockingQueueWrite;

    List<Tree> trees;
    boolean adderIsAlive;

    public writeFileRunnable(PrintWriter printWriter, BlockingQueue<ArrayList<Tree>> blockingQueueWrite, boolean adderIsAlive) {
        this.blockingQueueWrite = blockingQueueWrite;
        this.writer = printWriter;
        this.adderIsAlive = adderIsAlive;
    }

    public void run() {
        writePersonToFile();
    }

    public void writePersonToFile() {
        while (adderIsAlive) {
            System.out.println("State of adder" + adderIsAlive);
            System.out.println("Write person threat started size queue " + blockingQueueWrite.size());
            if (writer != null) {
                try {
                    trees = blockingQueueWrite.take();
                    System.out.println(trees.size());
                    // Get all the chains
                    List<Chain> global_chains = new ArrayList<>();
                    for (Tree t : trees) {
                        global_chains.addAll(t.getChains());
                    }

                    //TODO ADD TO THREAD
                    global_chains.sort(Collections.reverseOrder());

                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < 3 && i < global_chains.size(); i++)
                        sb.append(global_chains.get(i).toString());

                    writer.write(sb.toString().trim() + "\n"); // Remove trim() for comparing with Arnette's results

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Write person thread Ended");
            }
        }
    }
}

