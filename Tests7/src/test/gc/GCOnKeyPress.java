package test.gc;

import testlib.TestBase;
import testlib.gc.GCLoadProducer;
import testlib.gc.JavaHeap;
import testlib.gc.TestGCOptions;

public class GCOnKeyPress extends TestBase {

    private TestGCOptions opts;

    public static void main(String[] args) {
        TestGCOptions gcOpts = new TestGCOptions();
        initOpts(gcOpts);
        new GCOnKeyPress(gcOpts).runTest();
    }

    // copy and adapt as needed
    public static void initOpts(TestGCOptions gcOpts) {
        gcOpts.trc_level = 3;
        gcOpts.obj_header_size_bytes = 16;
        gcOpts.immortal_obj_heap_occupancy = 0.15f; // changed
        gcOpts.immortal_obj_size_bytes = 1*K;
        gcOpts.immortal_obj_heap_occupancy_bytes = (long) (gcOpts.immortal_obj_heap_occupancy * JavaHeap.MAX_JAVA_HEAP_BYTES);
        gcOpts.immortal_obj_count = gcOpts.immortal_obj_heap_occupancy_bytes / gcOpts.immortal_obj_size_bytes;
        gcOpts.mortal_obj_heap_occupancy = 0.30f;
        gcOpts.mortal_obj_size_bytes = 256;
        gcOpts.mortal_obj_heap_occupancy_bytes = (long) (gcOpts.mortal_obj_heap_occupancy * JavaHeap.MAX_JAVA_HEAP_BYTES);
        gcOpts.mortal_obj_count = gcOpts.mortal_obj_heap_occupancy_bytes / gcOpts.mortal_obj_size_bytes;
        gcOpts.alloc_percentage_immortal = 20;
        gcOpts.alloc_percentage_mortal   = 20; // changed
        gcOpts.alloc_percentage_short_lived = 100-(gcOpts.alloc_percentage_immortal+gcOpts.alloc_percentage_mortal);
    }

    public GCOnKeyPress(TestGCOptions gcOpts) {
        super(gcOpts.trc_level);
        this.opts = gcOpts;
    }

    public void runTest() {
        log("Running with the following options: ");
        opts.printOn(this);
        GCLoadProducer gcLoad = new GCLoadProducer(opts);
        gcLoad.runInBackground();
        gcLoad.waitUntilReady2go();
        while (true) {
            waitForEnter("-- press key to trigger System.gc --");
            log("### Calling System.gc()");
            System.gc();
        }
    }

}
