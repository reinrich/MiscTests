package test.gc.microbench;

import java.io.File;
import java.io.IOException;

import test.gc.PromotionMethod;
import test.gc.TestBaseOld;
import test.gc.TestHeapBasedOnTreeSet;
import test.gc.TestHeap;

/*
./sapjvm_8/bin/java
-Xms1g -Xmx1g 
-XX:G1HeapRegionSize=1m -XX:+UseG1GC
-cp /u/w/d/jtests/QuickJavaTests/bin
test.gc.microbench.BenchFullGC
 */


/**
 * @author d
 *
 *         (1) Allocated from heap until {@link #TARGET_OCCUPANCY} is reached.
 *         This is done by creating {@link test.gc.HeapFiller} instances of
 *         {@link #HEAP_FILLER_SIZE}. The {@link test.gc.HeapFiller} instances
 *         construct a tree structure such that the ratio between references and
 *         non-reference data is {@link #REF_VS_DATA_RATIO}.
 * 
 *         (2) All objects are promoted to the old generation by calling
 *         {@code System.gc()}.
 * 
 *         (3) Every {@link #NTH_TO_DROP} HeapFiller is dropped and becomes
 *         garbage
 * 
 *         (4) Trigger a full GC by calling {@code System.gc()} and measure
 *         duration.
 */
public class BenchFullGC extends TestBaseOld {
    private static final int KB = 1<<10;
    private static final int MB = 1<<20;
    private static final int HEAP_FILLER_SIZE = 100*KB;
    private static final float TARGET_OCCUPANCY = 0.7F;
    // ratio between bytes used to encode references and bytes used for non reference data
    private static final float REF_VS_DATA_RATIO = 0.5F;
    private static final int NTH_TO_DROP = 4;
    private static final float OUTLIER_FACTOR = 1.5f;

    public BenchFullGC(TestOptions opts) {
        super(opts.verboseLevel, opts.fullGcCount);
    }

    public static void main(String[] args) {
        TestOptions opts = new TestOptions();
        opts.verboseLevel = 1;
        parseCmdLine(args, opts);
        BenchFullGC test = new BenchFullGC(opts);
        TestHeap heap = new TestHeap(test);

        test.message();
        test.message("### Starting measurements");

        long minPromotionDuration = Long.MAX_VALUE/2 - 1;
        long minBenchmarkGCDuration = Long.MAX_VALUE/2 - 1;
        for (int i = 1; i <= opts.fullGcCount; i++) {
            test.message();
            test.message("Measurement " + i);
            test.incMessageIndentation();

            heap.fillWithObjectsOfSize(HEAP_FILLER_SIZE, REF_VS_DATA_RATIO, TARGET_OCCUPANCY);
            test.message("heap depth: " + heap.depth(HEAP_FILLER_SIZE));
            long promDuration = heap.promoteAllObjects(PromotionMethod.SYSTEM_GC);
            if (promDuration > OUTLIER_FACTOR*minPromotionDuration) {
                test.messageNoIndent("!!! WARNING: promoting objects took suspiciously long");
            }
            minPromotionDuration = Math.min(minPromotionDuration, promDuration);
            heap.dropEveryNthObjectOfSize(NTH_TO_DROP, (i-1) % NTH_TO_DROP, HEAP_FILLER_SIZE);

            test.message("### Measure FullGC");
            long benchmarkGCDuration = heap.fullGC("benchmark measurement", true);
            if (benchmarkGCDuration > OUTLIER_FACTOR*minBenchmarkGCDuration && i > 5 /* 5 iterations warm-up */) {
                test.messageNoIndent("!!! WARNING: benchmark GC took suspiciously long");
                try {
                    (new File("./dostop")).createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(1);
            }
            minBenchmarkGCDuration = Math.min(minBenchmarkGCDuration, benchmarkGCDuration);
            test.decMessageIndentation();
        }
        test.printResults();
    }

    private static void parseCmdLine(String[] args, TestOptions opts) {
        if (args.length != 1) {
            System.out.println();
            System.out.println("usage: java " + BenchFullGC.class.getName() + " <#full gcs to trigger>");
            System.out.println();
            System.exit(1);
        }
        opts.fullGcCount = Integer.parseInt(args[0]);
    }

    @Override
    public float outlierFactor() {
        return OUTLIER_FACTOR;
    }
    
}
