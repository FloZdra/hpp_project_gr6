package benchmark;

import covidTracer.CovidTracer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@State(Scope.Benchmark) //@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
public class BenchmarkCovidTracer {
    private final ArrayList<URL> urls = new ArrayList<>();

    @Param({"20", "5000"})//@Param({"20", "5000", "1000000"})
    private String size;

    @Setup
    public void init() {
        urls.clear();
        URL france = getClass().getResource("/data/" + size + "/France.csv");
        URL italy = getClass().getResource("/data/" + size + "/Italy.csv");
        URL spain = getClass().getResource("/data/" + size + "/Spain.csv");
        urls.add(france);
        urls.add(italy);
        urls.add(spain);
    }

    @Benchmark // ! avoid calls that return nothing
    public int benchmarkCovidTracer() {
        CovidTracer covidTracer = new CovidTracer(urls);
        covidTracer.launchAnalysis();
        return 1;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkCovidTracer.class.getSimpleName())
                //.forks(1)
                .build();

        new Runner(opt).run();
    }
}
