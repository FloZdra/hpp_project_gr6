import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import utils.FileReader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CovidTracerTest {

    private static final ArrayList<URL> urls = new ArrayList<>();
    private static CovidTracer covidTracer;

    @Test
    @Order(1)
    @DisplayName("Test 2")

    void covidTracerTest2() throws MalformedURLException {
        URL france = Main.class.getResource("/input_test/test2/France.csv");
        urls.add(france);
        covidTracer = new CovidTracer(urls);
        covidTracer.launchAnalysis();
        FileReader fr1 = new FileReader();
        fr1.openFile(Main.class.getResource("/output_test/test2.csv"));
        FileReader fr2 = new FileReader();
        fr2.openFile(new URL("src/main/resources/output_generated/output.csv"));
        try {
            while(fr1.getSc().hasNextLine() || fr2.getSc().hasNextLine()) {
                assertEquals(fr1.readLine(), fr2.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test 3")

    void covidTracerTest3() throws MalformedURLException {
        URL france = france = new URL("src/main/resources/input_test/test3/France.csv");
        urls.add(france);
        covidTracer = new CovidTracer(urls);
        covidTracer.launchAnalysis();
        FileReader fr1 = new FileReader();
        fr1.openFile(Main.class.getResource("/output_test/test3.csv"));
        FileReader fr2 = new FileReader();
        fr2.openFile(new URL("src/main/resources/output_generated/output.csv"));
        try {
            while(fr1.getSc().hasNextLine() || fr2.getSc().hasNextLine()) {
                assertEquals(fr1.readLine(), fr2.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        fr1.closeFile();
        fr2.closeFile();
    }

    @Test
    @Order(3)
    @DisplayName("Test 4")

    void covidTracerTest4() {
        URL france = Main.class.getResource("/input_test/test4/France.csv");
        urls.add(france);
        covidTracer = new CovidTracer(urls);
        covidTracer.launchAnalysis();
        FileReader fr1 = new FileReader();
        fr1.openFile(Main.class.getResource("/output_test/test4.csv"));
        FileReader fr2 = new FileReader();
        fr2.openFile(Main.class.getResource("/output_generated/output.csv"));
        try {
            while(fr1.getSc().hasNextLine() || fr2.getSc().hasNextLine()) {
                assertEquals(fr1.readLine(), fr2.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        fr1.closeFile();
        fr2.closeFile();
    }

    @Test
    @Order(4)
    @DisplayName("Test 5")

    void covidTracerTest5() {
        URL france = Main.class.getResource("/input_test/test5/France.csv");
        urls.add(france);
        covidTracer = new CovidTracer(urls);
        covidTracer.launchAnalysis();
        FileReader fr1 = new FileReader();
        fr1.openFile(Main.class.getResource("/output_test/test5.csv"));
        FileReader fr2 = new FileReader();
        fr2.openFile(Main.class.getResource("/output_generated/output.csv"));
        try {
            while(fr1.getSc().hasNextLine() || fr2.getSc().hasNextLine()) {
                assertEquals(fr1.readLine(), fr2.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        fr1.closeFile();
        fr2.closeFile();
    }

    @Test
    @Order(5)
    @DisplayName("Test 6")

    void covidTracerTest6() {
        URL france = Main.class.getResource("/input_test/test6/France.csv");
        URL italy = Main.class.getResource("/input_test/test6/Italy.csv");
        URL spain = Main.class.getResource("/input_test/test6/Spain.csv");
        urls.add(france);
        urls.add(italy);
        urls.add(spain);
        covidTracer = new CovidTracer(urls);
        covidTracer.launchAnalysis();
        FileReader fr1 = new FileReader();
        fr1.openFile(Main.class.getResource("/output_test/test6.csv"));
        FileReader fr2 = new FileReader();
        fr2.openFile(Main.class.getResource("/output_generated/output.csv"));
        try {
            while(fr1.getSc().hasNextLine() || fr2.getSc().hasNextLine()) {
                assertEquals(fr1.readLine(), fr2.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        fr1.closeFile();
        fr2.closeFile();
    }

    @Test
    @Order(6)
    @DisplayName("Test 7")

    void covidTracerTest7() {
        URL france = Main.class.getResource("/input_test/test7/France.csv");
        urls.add(france);
        covidTracer = new CovidTracer(urls);
        covidTracer.launchAnalysis();
        FileReader fr1 = new FileReader();
        fr1.openFile(Main.class.getResource("/output_test/test7.csv"));
        FileReader fr2 = new FileReader();
        fr2.openFile(Main.class.getResource("/output_generated/output.csv"));
        try {
            while(fr1.getSc().hasNextLine() || fr2.getSc().hasNextLine()) {
                assertEquals(fr1.readLine(), fr2.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        fr1.closeFile();
        fr2.closeFile();
    }

    @Test
    @Order(7)
    @DisplayName("Test 8")

    void covidTracerTest8() {
        URL france = Main.class.getResource("/input_test/test8/France.csv");
        urls.add(france);
        covidTracer = new CovidTracer(urls);
        covidTracer.launchAnalysis();
        FileReader fr1 = new FileReader();
        fr1.openFile(Main.class.getResource("/output_test/test8.csv"));
        FileReader fr2 = new FileReader();
        fr2.openFile(Main.class.getResource("/output_generated/output.csv"));
        try {
            while(fr1.getSc().hasNextLine() || fr2.getSc().hasNextLine()) {
                assertEquals(fr1.readLine(), fr2.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        fr1.closeFile();
        fr2.closeFile();
    }

    @Test
    @Order(8)
    @DisplayName("Test 9")

    void covidTracerTest9() {
        URL france = Main.class.getResource("/input_test/test9/France.csv");
        urls.add(france);
        covidTracer = new CovidTracer(urls);
        covidTracer.launchAnalysis();
        FileReader fr1 = new FileReader();
        fr1.openFile(Main.class.getResource("/output_test/test9.csv"));
        FileReader fr2 = new FileReader();
        fr2.openFile(Main.class.getResource("/output_generated/output.csv"));
        try {
            while(fr1.getSc().hasNextLine() || fr2.getSc().hasNextLine()) {
                assertEquals(fr1.readLine(), fr2.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        fr1.closeFile();
        fr2.closeFile();
    }
}