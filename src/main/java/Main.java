import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final ArrayList<URL> urls = new ArrayList<>();

    private static final URL france = Main.class.getResource("/data/5000/France.csv");
    private static final URL italy = Main.class.getResource("/data/5000/Italy.csv");
    private static final URL spain = Main.class.getResource("/data/5000/Spain.csv");

    public static void main(String[] args) {


        List<URL> files = new ArrayList<>();
        urls.add(france);
        urls.add(italy);
        urls.add(spain);
        CovidTracer covidTracer = new CovidTracer(urls);

        covidTracer.launchAnalysis();


    }


}
