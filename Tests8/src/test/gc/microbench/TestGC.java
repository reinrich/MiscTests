package test.gc.microbench;

import test.gc.HeapFiller;
import test.gc.HeapFillerConfig;
import test.gc.TestBaseOld;
import test.gc.TestHeap;

/*
./sapjvm_8/bin/java
-Xms1g -Xmx1g 
-XX:G1HeapRegionSize=1m -XX:+UseG1GC
-cp /s/d/jtests/QuickJavaTests/bin
test.gc.microbench.TestGC
 */


/**
 * TODO
 * 
 * @author d
 */
public class TestGC extends TestBaseOld {
    private static final int KB = 1<<10;
    private static final int MB = 1<<20;
    private static final int HEAP_FILLER_SIZE = 100*KB;
    private static final float TARGET_OCCUPANCY = 0.1F;
    // ratio between bytes used to encode references and bytes used for non reference data
    private static final float REF_VS_DATA_RATIO = 0.5F;
    private static final int NTH_TO_DROP = 4;

    public TestGC(TestOptions opts) {
        super(opts.verboseLevel, opts.fullGcCount, opts);
    }

    public static void main(String[] args) {
        TestOptions opts = new TestOptions();
        opts.verboseLevel = 1;
        parseCmdLine(args, opts);
        TestGC test = new TestGC(opts);
        TestHeap heap = new TestHeap(test);

        HeapFillerConfig hfConfig = new HeapFillerConfig();
        hfConfig.smallObjectsPerHeapFiller = 3;
        hfConfig.referenceVsDataRatio = REF_VS_DATA_RATIO;
        HeapFiller.config(test, hfConfig);

        test.message();
        test.message("### Starting test");
        if (opts.humongousObjsSizeTotalMB > 0) {
            heap.createHumongousObjects();
        }

        heap.fillWithObjectsOfSize(HEAP_FILLER_SIZE, REF_VS_DATA_RATIO, TARGET_OCCUPANCY);
        test.message("heap depth: " + heap.depth(HEAP_FILLER_SIZE));
        heap.replaceEveryNthObjectOfSize(NTH_TO_DROP, HEAP_FILLER_SIZE, REF_VS_DATA_RATIO,
                opts.allocRateKbPer100MilliSec  );
    }
    
    private static void parseCmdLine(String[] args, TestOptions opts) {
        if (args.length != 1) {
            System.out.println();
            System.out.println("usage: java " + TestGC.class.getName() + " <alloc rate in kb / 100ms>");
            System.out.println("   Properties:");
            System.out.println("      ");
            System.out.println("      -Dtest.humongous_objs_size_kb=<humongous objs size / KB>");
            System.out.println("      -Dtest.total_humongous_objs_size_mb=<total humongous objs size / MB>");
            System.out.println("      -Dtest.humongous_objs_per_sec=<humongous objs / s>");
            System.out.println("      ");
            System.exit(1);
        }
        opts.allocRateKbPer100MilliSec = Integer.parseInt(args[0]);
        opts.humongousObjsSizeKB = Integer.parseInt(System.getProperty("test.humongous_objs_size_kb", "1"));
        opts.humongousObjsSizeTotalMB = Integer.parseInt(System.getProperty("test.total_humongous_objs_size_mb", "0"));
        opts.humongousObjsPerSec = Integer.parseInt(System.getProperty("test.humongous_objs_per_sec", "0"));
    }

    @Override
    public float outlierFactor() {
        return 1000f;
    }
}
