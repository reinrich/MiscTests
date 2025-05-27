package test.gc;

import java.lang.reflect.Constructor;

import test.gc.MetaSpaceLoadProducerOptions.MSTestType;
import test.gc.ReferenceProcessorLoadProducerOptions.RefTestType;
import test.gc.TestGCOptions.HumTestType;
import test.gc.TestGCOptions.TestType;
import testlib.TestBase;;

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

    private static LoadProducer loadProducer;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: unexpected number of commandline arguments: " + args.length);
            printUsageAndExit();
        }

        // Read test types
        TestType tt = TestType.valueOf(args[0]);
        MSTestType mstt   = MSTestType.MS_NONE;
        HumTestType humtt = HumTestType.HUM_NONE;
        RefTestType reftt = RefTestType.REF_NONE;

        for (int i = 1; i < args.length; i++) {
            try {
                mstt = MSTestType.valueOf(args[i]);
                continue;
            } catch (IllegalArgumentException e) { /* ignored */}
            try {
                humtt = HumTestType.valueOf(args[i]);
                continue;
            } catch (IllegalArgumentException e) { /* ignored */}
            try {
                reftt = RefTestType.valueOf(args[i]);
                continue;
            } catch (IllegalArgumentException e) { /* ignored */}

            // Must be the name of a LoadProducer class
            createLoadProducer(args[i], ((i + 1) < args.length) ? args[i + 1] : null);
            i++;
        }
        System.out.println("Using the following test types:");
        System.out.println("    " + tt);
        System.out.println("    " + mstt);
        System.out.println("    " + humtt);
        System.out.println("    " + reftt);
        System.out.println();

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
        new GCLoadProducer(exOpts, () -> finishedObjectGraphBuildupCallback()).run();
    }

    private static void finishedObjectGraphBuildupCallback() {
        if (loadProducer == null) return;
        loadProducer.runInBackground();
    }

    static void createLoadProducer(String className, String args) {
        boolean retried = false;
        do {
            try {
                Class<?> clazz = Class.forName(className);
                Constructor<?> constructor = clazz.getConstructor(String.class);
                loadProducer = (LoadProducer) constructor.newInstance(args);
            } catch (Throwable t) {
                if (retried) {
                    throw new Error (t);
                }
                retried = true;
                className = "test.gc." + className;
                System.out.println("Retrying with package prepended: " + className);
            }
        } while(loadProducer == null);
    }

    private static void printUsageAndExit() {
        System.err.println();
        System.err.println("Usage: " + TestGCWithClassloadingWithOpts.class + " TestType [AuxTestType ...]");
        System.err.println();
        System.err.println("   TestType := ");
        for (TestType tt : TestType.values()) {
            System.err.println("      " + tt);
        }
        System.err.println();
        System.err.println("   AuxTestType := MSTestType | HumTestType | j.l.r.ReferenceType ");
        for (MSTestType tt : MSTestType.values()) {
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
