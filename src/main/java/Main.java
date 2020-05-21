import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class Main {
    private static final ArrayList<URL> urls = new ArrayList<>();

    private static final String size = "5000";

    private static final URL france = Main.class.getResource("/data/" + size + "/France.csv");
    private static final URL italy = Main.class.getResource("/data/" + size + "/Italy.csv");
    private static final URL spain = Main.class.getResource("/data/" + size + "/Spain.csv");

    public static void init() {
        urls.clear();
        urls.add(france);
        urls.add(italy);
        urls.add(spain);
    }


    public static void main(String[] args) {
        System.out.println("Starting analysis");
        long startTime = System.nanoTime();
        init();
        covidTracer.CovidTracer covidTracer = new covidTracer.CovidTracer(urls);
        covidTracer.launchAnalysis();
        long endTime = System.nanoTime();
        System.out.println("Successful analysis");
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time : " +
                timeElapsed / 1000000 + " ms");
    }
}
