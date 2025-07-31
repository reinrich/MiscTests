package test.gc;

import testlib.tools.NestedArrayList;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

import javax.management.NotificationEmitter;
import javax.management.openmbean.CompositeData;

import com.sun.management.GarbageCollectionNotificationInfo;

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

@SuppressWarnings("restriction")
public class GCLoadProducer extends TestBase implements Runnable {

    private static final int CHECK_HUM_INTERVAL = 3000; // allocations
    private NestedArrayList<ImmortalObject> immortalObjs;
    private NestedArrayList<MortalObject> mortalObjs;
    private NestedArrayList<MortalObject> shortLived;   // survive a few young gcs but get never promoted to old
    private NestedArrayList<byte[]> humObjs;

    private JavaHeap heap;

    private volatile boolean shouldContinueToAllocate;
    private TestGCOptions opts;

    public volatile long checksum; // result of busy wait calculation

    private boolean ready2go;
    private Thread backGroundThread;
    private Runnable objectGraphCompleteCallback;

    public GCLoadProducer(TestGCOptions opts, Runnable objectGraphCompleteCallback) {
        this.opts = opts;
        this.objectGraphCompleteCallback = objectGraphCompleteCallback;
        heap = new JavaHeap(this);
    }

    @Override
    public void run() {
        log("Running with the following options: ");
        opts.printOn(this);
        shouldContinueToAllocate = true;
        registerGCListener();
        buildUpObjectGraph();
        signalReady2Go();
        continouslyAllocateObjects();
    }

    private void registerGCListener() {
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (gcBean instanceof NotificationEmitter) {
                NotificationEmitter emitter = (NotificationEmitter) gcBean;

                emitter.addNotificationListener((notification, handback) -> {
                    if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                        CompositeData cd = (CompositeData) notification.getUserData();
                        GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from(cd);

                        if (!isYoungGC(info.getGcName())) {
                            log0(String.format("GC: %-20s | Cause: %-20s | Duration: %4d ms%n",
                                    info.getGcName(), info.getGcCause(), info.getGcInfo().getDuration()));
                        }
                    }
                }, null, null);
            }
        }
    }

    private boolean isYoungGC(String gcName) {
        switch (gcName) {
        case "ParNew":
            return true;
        default:
            return false;
        }
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

        log0("Build-up of object graph");
        logIncInd();
        log(heap);
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
                    allocMortalShortObject(); // allocated and instantly dropped
                }
            }
        }
        log("allocated " + immortalObjs.size() + " immortalObjs (" + immortalObjs + ")");
        heap.gc();
        log(heap);
        logDecInd();
        log();
        log0("Running objectGraphCompleteCallback");
        objectGraphCompleteCallback.run();
    }

    private void continouslyAllocateObjects() {
        long moIdx = 0;
        long humIdx = 0;

        log0("Continously allocating objects");
        logIncInd();
        log(heap);

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
                    MortalObject tmp = allocMortalShortObject(); // allocated and instantly dropped
                }
            }
        }
        log(heap);
        logDecInd();
        log();
    }

    int curImmoCount;
    int curImmoWithFinalizerCount;
    private ImmortalObject allocImmortalObject() {
        ImmortalObject result = null;
        int c1 = curImmoCount;
        int c2 = curImmoWithFinalizerCount;
        if ((c1 + c2) == 0) {
            c2 = curImmoWithFinalizerCount = opts.alloc_percentage_immortal_with_finalizer;
            c1 = curImmoCount = 100 - c2;
        }
        int byteSize = opts.immortal_obj_size_bytes-opts.obj_header_size_bytes;
        if (c1 > 0 && ((c1 & 1) == 0 || c2 == 0)) {
            curImmoCount = c1 - 1;
            result = new ImmortalObject(new byte[byteSize]);
        } else if (c2 > 0) {
            curImmoWithFinalizerCount = c2 - 1;
            result = new ImmortalObjectWithFinalizer(new byte[byteSize]);
        } else {
            throwNegativeCountError(c1, c2);
        }
        return result;
    }

    private byte[] allocHumongousObject() {
        return new byte[opts.hum_obj_size_bytes-opts.obj_header_size_bytes];
    }

    int curMoCount;
    int curMoWithFinalizerCount;
    private MortalObject allocMortalObject() {
        MortalObject result = null;
        int c1 = curMoCount;
        int c2 = curMoWithFinalizerCount;
        if ((c1 + c2) == 0) {
            c2 = curMoWithFinalizerCount = opts.alloc_percentage_mortal_with_finalizer;
            c1 = curMoCount = 100 - c2;
        }
        int byteSize = opts.mortal_obj_size_bytes-opts.obj_header_size_bytes;
        if (c1 > 0 && ((c1 & 1) == 0 || c2 == 0)) {
            curMoCount = c1 - 1;
            result = new MortalObject(new byte[byteSize]);
        } else if (c2 > 0) {
            curMoWithFinalizerCount = c2 - 1;
            result = new MortalObjectWithFinalizer(new byte[byteSize]);
        } else {
            throwNegativeCountError(c1, c2);
        }
        result.hashCode();
        return result;
    }

    int curMoShortCount;
    int curMoShortWithFinalizerCount;
    private MortalObject allocMortalShortObject() {
        MortalObject result = null;
        int c1 = curMoShortCount;
        int c2 = curMoShortWithFinalizerCount;
        if ((c1 + c2) == 0) {
            c2 = curMoShortWithFinalizerCount = opts.alloc_percentage_short_lived_with_finalizer;
            c1 = curMoShortCount = 100 - c2;
        }
        int byteSize = opts.mortal_obj_size_bytes-opts.obj_header_size_bytes;
        if (c1 > 0 && ((c1 & 1) == 0 || c2 == 0)) {
            curMoShortCount = c1 - 1;
            result = new MortalObject(new byte[byteSize]);
        } else if (c2 > 0) {
            curMoShortWithFinalizerCount = c2 - 1;
            result = new MortalObjectWithFinalizer(new byte[byteSize]);
        } else {
            throwNegativeCountError(c1, c2);
        }
        result.hashCode();
        return result;
    }

    private void throwNegativeCountError(int c1, int c2) {
        throw new Error("Negative count: c1 = " + c1 + "  c2 = " + c2);
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
