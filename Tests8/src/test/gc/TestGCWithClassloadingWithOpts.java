package test.gc;

import testlib.TestBase;
import testlib.gc.GCLoadProducer;
import testlib.gc.MetaSpaceLoadProducerOptions;
import testlib.gc.TestGCOptions;
import testlib.gc.MetaSpaceLoadProducerOptions.MSTestType;
import testlib.gc.ReferenceProcessorLoadProducerOptions;
import testlib.gc.ReferenceProcessorLoadProducerOptions.RefTestType;
import testlib.gc.TestGCOptions.TestType;
import testlib.gc.TestGCOptions.HumTestType;;

//
// Test Description
//
// Test continuously allocates the following type of java objects:
//
//     Immortal objects: Allocated in the build-up phase. Each allocated object remains reachable during
//     the whole test.
//
//     Mortal objects: after reaching MORTAL_OBJ_HEAP_OCCUPANCY older mortal objects are replaced by newly
//     created ones, so that older ones can die.
//
//     Short lived objects: are allocated without keeping the reference.
//

public class TestGCWithClassloadingWithOpts extends TestBase {

    static byte[][] humonguous_objs;

    private static MetaSpaceLoadProducer msLoadProducer;

    private static ReferenceProcessorLoadProducer rpLoadProducer;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: unexpected number of commandline arguments: " + args.length);
            printUsageAndExit();
        }

        // Read test types
        TestType tt = TestType.valueOf(args[0]);
        MSTestType mstt   = args.length >= 1 ? MSTestType.valueOf(args[1]) : MSTestType.MS_NONE;
        HumTestType humtt = args.length >= 2 ? HumTestType.valueOf(args[2]) : HumTestType.HUM_NONE;
        RefTestType reftt     = args.length >= 3 ? RefTestType.valueOf(args[3]) : RefTestType.REF_NONE;

        // MetaSpace load
        if (mstt != MSTestType.MS_NONE) {
            MetaSpaceLoadProducerOptions msOpts = new MetaSpaceLoadProducerOptions(mstt);
            msOpts.trc_level = 3;
            msLoadProducer = new MetaSpaceLoadProducer(msOpts);
            msLoadProducer.runInBackground();
        }

        // Reference processor load
        if (reftt != RefTestType.REF_NONE) {
            ReferenceProcessorLoadProducerOptions rpOpts = new ReferenceProcessorLoadProducerOptions(reftt);
            rpOpts.trc_level = 3;
            rpLoadProducer = new ReferenceProcessorLoadProducer(rpOpts);
            rpLoadProducer.runInBackground();
        }

        // Java heap load
        TestGCOptions exOpts = new TestGCOptions(tt, humtt);
        new GCLoadProducer(exOpts).run();
    }

    private static void printUsageAndExit() {
        System.err.println();
        System.err.println("Usage: " + TestGCWithClassloadingWithOpts.class + " TestType [MSTestType] [HumTestType] [j.l.r.ReferenceType]");
        System.err.println();
        System.err.println("   TestType := ");
        for (TestType tt : TestType.values()) {
            System.err.println("      " + tt);
        }
        System.err.println();
        System.err.println("   MSTestType := ");
        for (MSTestType tt : MSTestType.values()) {
            System.err.println("      " + tt);
        }
        System.err.println();
        System.err.println("   HumTestType := ");
        for (HumTestType tt : HumTestType.values()) {
            System.err.println("      " + tt);
        }
        System.err.println();
        System.err.println("   j.l.r.ReferenceType := ");
        for (RefTestType tt : RefTestType.values()) {
            System.err.println("      " + tt);
        }
        System.err.println();
        System.exit(1);
    }
}
