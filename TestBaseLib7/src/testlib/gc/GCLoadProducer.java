package testlib.gc;

import testlib.tools.NestedArrayList;
import testlib.TestBase;

//
// Continuously allocates the following type of java objects:
//
//     Immortal objects: Allocated in the build-up phase. Each allocated object remains reachable during
//     the whole test.
//
//     Mortal objects: after reaching MORTAL_OBJ_HEAP_OCCUPANCY older mortal objects are replaced by newly
//     created ones, so that older ones can die.
//
//     Short lived objects: are allocated without keeping the reference.
//

public class GCLoadProducer extends TestBase implements Runnable {

    private static final int CHECK_HUM_INTERVAL = 3000; // allocations
    private NestedArrayList<byte[]> immortalObjs;
    private NestedArrayList<byte[]> mortalObjs;
    private NestedArrayList<byte[]> humObjs;

    private JavaHeap heap;

    private volatile boolean shouldContinueToAllocate;
    private TestGCOptions opts;

    public volatile long checksum; // result of busy wait calculation

    private boolean ready2go;
    private Thread backGroundThread;

    public GCLoadProducer(TestGCOptions opts) {
        super(opts.trc_level);
        this.opts = opts;
        heap = new JavaHeap(this);
    }

    @Override
    public void run() {
        shouldContinueToAllocate = true;
        buildUpObjectGraph();
        signalReady2Go();
        continouslyAllocateObjects();
    }

    public void runInBackground() {
        backGroundThread = new Thread(this, "GCLoadProducer");
        backGroundThread.start();
    }

    public void stopAllocating() {
        shouldContinueToAllocate = false;
    }

    private void buildUpObjectGraph() {
        immortalObjs = new NestedArrayList<>();
        mortalObjs = new NestedArrayList<>();
        humObjs = new NestedArrayList<>();
        long moIdx = 0;

        msg("###### Build-up of object graph");
        msgIncInd();
        msg(heap);
        while(immortalObjs.size() < opts.immortal_obj_count || mortalObjs.size() < opts.mortal_obj_count || humObjs.size() < opts.hum_obj_count) {
            // allocate 100 objects according to the ALLOC_PERCENTAGES
            int immoToAlloc = immortalObjs.size() >= opts.immortal_obj_count ? 0 : opts.alloc_percentage_immortal;
            int humToAlloc = humObjs.size() >= opts.hum_obj_count ? 0 : 1;
            int moToAlloc = opts.alloc_percentage_mortal;
            int shortToAlloc = opts.alloc_percentage_short_lived;

            while(moToAlloc+immoToAlloc+shortToAlloc > 0) {
                if (immoToAlloc > 0) {
                    immoToAlloc--;
                    immortalObjs.add(allocImmortalObject());
                }
                if (humToAlloc > 0) {
                    humToAlloc--;
                    humObjs.add(allocHumongousObject());
                }
                if (moToAlloc > 0) {
                    moToAlloc--;
                    mortalObjs.set(moIdx++, allocMortalObject());
                    if (moIdx == opts.mortal_obj_count) moIdx = 0;
                }
                if (shortToAlloc > 0) {
                    shortToAlloc--;
                    allocImmortalObject(); // allocated and instantly dropped
                }
            }
        }
        msg("allocated " + immortalObjs.size() + " immortalObjs (" + immortalObjs + ")");
        heap.gc();
        msg(heap);
        msgDecInd();
        msg();
    }

    private void continouslyAllocateObjects() {
        long moIdx = 0;
        long humIdx = 0;

        msg("###### Continously allocating objects");
        msgIncInd();
        msg(heap);

        int allocationsSinceLastHumongousAlloc = 0;
        boolean doAllocHumongous = opts.hum_obj_count > 0;
        while(shouldContinueToAllocate) {
            // allocate 100-immortal_obj_count objects according to the ALLOC_PERCENTAGES
            int moToAlloc = opts.alloc_percentage_mortal;
            int shortToAlloc = opts.alloc_percentage_short_lived;

            // busy wait for gc to do its work
            long res = 0;
            for(long i = opts.busy_wait_iterations; i > 0; i--) {
                res += i & 42;
            }
            checksum += res;

            if (doAllocHumongous) {
                allocationsSinceLastHumongousAlloc += moToAlloc+shortToAlloc;
                if (allocationsSinceLastHumongousAlloc > opts.alloc_interval_humongous) {
                    allocationsSinceLastHumongousAlloc = 0;
                    humObjs.set(humIdx++, allocHumongousObject());
                    if (humIdx == opts.hum_obj_count) humIdx = 0;
                }
            }

            while(moToAlloc+shortToAlloc > 0) {
                if (moToAlloc > 0) {
                    moToAlloc--;
                    mortalObjs.set(moIdx++, allocMortalObject());
                    if (moIdx == opts.mortal_obj_count) moIdx = 0;
                }
                if (shortToAlloc > 0) {
                    shortToAlloc--;
                    @SuppressWarnings("unused")
                    byte[] tmp = allocMortalObject(); // allocated and instantly dropped
                }
            }
        }
        msg(heap);
        msgDecInd();
        msg();
    }

    private byte[] allocImmortalObject() {
        return new byte[opts.immortal_obj_size_bytes-opts.obj_header_size_bytes];
    }

    private byte[] allocHumongousObject() {
        return new byte[opts.hum_obj_size_bytes-opts.obj_header_size_bytes];
    }

    private byte[] allocMortalObject() {
        byte[] result = new byte[opts.mortal_obj_size_bytes-opts.obj_header_size_bytes];
        result.hashCode();
        return result;
    }

    public synchronized void waitUntilReady2go() {
        while (!ready2go) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                // ignored
            }
        }
    }

    private synchronized void signalReady2Go() {
        ready2go = true;
        this.notifyAll();
    }

    public void join() throws InterruptedException {
        if (backGroundThread != null) {
            backGroundThread.join();
        }
    }
}
