package covidTracer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import utils.FileReader;

import java.net.URL;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CovidTracerTest {

    private static final ArrayList<URL> urls = new ArrayList<>();

    private void covidTracerTest(URL output_test) {

        CovidTracer covidTracer = new CovidTracer(urls);
        covidTracer.launchAnalysis();

        FileReader fr_expected = new FileReader(), fr_actual = new FileReader();

        fr_expected.openFile(output_test);
        fr_actual.openFile(getClass().getResource("/output_generated/output.csv"));

        int line = 1;
        while (fr_expected.getSc().hasNextLine() || fr_actual.getSc().hasNextLine()) {
            assertEquals(fr_expected.readLine(), fr_actual.readLine(), "Error line " + line);
            line++;
        }

        fr_expected.closeFile();
        fr_actual.closeFile();
    }

    @BeforeEach
    void cleanUrls() {
        urls.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Test 1")
    void covidTracerTest1() {

        urls.add(getClass().getResource("/input_test/test1/France.csv"));
        urls.add(getClass().getResource("/input_test/test1/Italy.csv"));
        urls.add(getClass().getResource("/input_test/test1/Spain.csv"));

        URL output_test = getClass().getResource("/output_test/test1.csv");
        covidTracerTest(output_test);
    }

    @Test
    @Order(2)
    @DisplayName("Test 2")
    void covidTracerTest2() {

        urls.add(getClass().getResource("/input_test/test2/France.csv"));

        URL output_test = getClass().getResource("/output_test/test2.csv");
        covidTracerTest(output_test);
    }

    @Test
    @Order(3)
    @DisplayName("Test 3")
    void covidTracerTest3() {

        urls.add(getClass().getResource("/input_test/test3/France.csv"));

        URL output_test = getClass().getResource("/output_test/test3.csv");

        covidTracerTest(output_test);
    }

    @Test
    @Order(4)
    @DisplayName("Test 4")
    void covidTracerTest4() {

        urls.add(getClass().getResource("/input_test/test4/France.csv"));

        URL output_test = getClass().getResource("/output_test/test4.csv");

        covidTracerTest(output_test);
    }

    @Test
    @Order(5)
    @DisplayName("Test 5")
    void covidTracerTest5() {

        urls.add(getClass().getResource("/input_test/test5/France.csv"));

        URL output_test = getClass().getResource("/output_test/test5.csv");

        covidTracerTest(output_test);
    }

    @Test
    @Order(6)
    @DisplayName("Test 6")
    void covidTracerTest6() {

        urls.add(getClass().getResource("/input_test/test6/France.csv"));
        urls.add(getClass().getResource("/input_test/test6/Italy.csv"));
        urls.add(getClass().getResource("/input_test/test6/Spain.csv"));

        URL output_test = getClass().getResource("/output_test/test6.csv");

        covidTracerTest(output_test);
    }

    @Test
    @Order(7)
    @DisplayName("Test 7")
    void covidTracerTest7() {

        urls.add(getClass().getResource("/input_test/test7/France.csv"));

        URL output_test = getClass().getResource("/output_test/test7.csv");

        covidTracerTest(output_test);
    }

    @Test
    @Order(8)
    @DisplayName("Test 8")
    void covidTracerTest8() {

        urls.add(getClass().getResource("/input_test/test8/France.csv"));

        URL output_test = getClass().getResource("/output_test/test8.csv");

        covidTracerTest(output_test);
    }

    @Test
    @Order(9)
    @DisplayName("Test 9")
    void covidTracerTest9() {

        urls.add(getClass().getResource("/input_test/test9/France.csv"));

        URL output_test = getClass().getResource("/output_test/test9.csv");

        covidTracerTest(output_test);
    }

    @Test
    @Order(10)
    @DisplayName("Test 10")
    void covidTracerTest10() {

        urls.add(getClass().getResource("/input_test/test10/France.csv"));

        URL output_test = getClass().getResource("/output_test/test10.csv");

        covidTracerTest(output_test);
    }

    @Test
    @Order(11)
    @DisplayName("Test 11")
    void covidTracerTest11() {

        urls.add(getClass().getResource("/input_test/test11/France.csv"));
        urls.add(getClass().getResource("/input_test/test11/Italy.csv"));
        urls.add(getClass().getResource("/input_test/test11/Spain.csv"));

        URL output_test = getClass().getResource("/output_test/test11.csv");

        covidTracerTest(output_test);
    }
}