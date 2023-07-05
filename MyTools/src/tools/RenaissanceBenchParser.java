package tools;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

// Read data from Renaissance benchmark output.
// Ignore all but the last `AFTER_WARMUP_ITERATIONS` iterations.
// Result of an iteration: (1) sum of young pauses (2) benchmark duration
// Result of run: geonmean of the results of the last `AFTER_WARMUP_ITERATIONS` iterations.

public class RenaissanceBenchParser {

    private static final int LOG_LVL = 2;

    // E.g. ====== akka-uct (concurrency) [default], iteration 0 started ======
    private static final Pattern PATTERN_START = Pattern.compile("=+ (\\S+) .* iteration (\\d+) started.*");
    // E.g. ====== akka-uct (concurrency) [default], iteration 0 completed (16549.855 ms) ======
    private static final Pattern PATTERN_COMPLETED = Pattern.compile("=+ \\S+ .* iteration \\d+ completed \\(([0-9.]+) ms\\).*");
    // E.g. [402.640s][info][gc     ] GC(7698) Pause Young (Allocation Failure) 1698M->1039M(1922M) 8.108ms
    private static final Pattern PATTERN_YOUNG_GC = Pattern.compile(".*Pause Young.* ([0-9.]+)ms");
    private static final int AFTER_WARMUP_ITERATIONS = 5; // last N iterations score

    private static BenchIteration[] iterations = new BenchIteration[AFTER_WARMUP_ITERATIONS];
    private static String curName;
    private static BenchIteration currentIteration;
    private static int currentIdx = 0;

    public static void main(String[] files) {
        for (String fileName : files) {
            processFile(fileName);
        }
    }


    private static void processFile(String fileName) {
        log(1, "Processing " + fileName);
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            currentIdx = 0;
            stream.forEach(RenaissanceBenchParser::parseOneLine);
        } catch (IOException e) {
            throw new Error(e);
        }
        double durationGeoMean = 1D;
        double gcPauseGeoMean = 1D;
        for (int i = 0; i < AFTER_WARMUP_ITERATIONS; i++) {
            BenchIteration iter = iterations[(currentIdx + i) % AFTER_WARMUP_ITERATIONS];
            log(2, "iteration:" + iter.iteration + " duration:" + iter.duration + " gcpause:" + round(iter.youngGCPausesMs, 1));
            durationGeoMean *= iter.duration;
            gcPauseGeoMean *= iter.youngGCPausesMs;
        }
        durationGeoMean = Math.pow(durationGeoMean, 1.0d/AFTER_WARMUP_ITERATIONS);
        gcPauseGeoMean = Math.pow(gcPauseGeoMean, 1.0d/AFTER_WARMUP_ITERATIONS);
        logResult(curName+ "\t" + round(durationGeoMean, 1) + "\t" + round(gcPauseGeoMean, 1));
    }



    static class BenchIteration {
        int iteration;
        double duration;
        double youngGCPausesMs;
    }

    static void parseOneLine(String line) {
        Matcher m;
        log(3, "line: " + line);
        if (currentIteration == null) {
            if ((m = PATTERN_START.matcher(line)).find()) {
                currentIteration = iterations[currentIdx ] = new BenchIteration();
                currentIdx = ++currentIdx >= AFTER_WARMUP_ITERATIONS ? 0 : currentIdx;
                curName = m.group(1);
                currentIteration.iteration = Integer.parseInt(m.group(2));
                log(2, "Found start line: name:" + curName + " iteration:" + currentIteration.iteration);
            }
        } else if ((m = PATTERN_YOUNG_GC.matcher(line)).find()) {
            double pause = Double.parseDouble(m.group(1));
            currentIteration.youngGCPausesMs += pause;
            log(3, "Found young gc line: pause:" + pause + "ms sum:" + currentIteration.youngGCPausesMs);

        } else if ((m = PATTERN_COMPLETED.matcher(line)).find()) {
            currentIteration.duration = Double.parseDouble(m.group(1));
            log(2, "Found completed line: duration:" + currentIteration.duration + "ms");
            currentIteration = null;
        }
    }

    private static void logResult(String r) {
        System.out.println(r);
    }

    private static void log(int lvl, String msg) {
        if (lvl <= LOG_LVL) {
            System.err.println(msg);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
