package test.gc;

import testlib.tools.NestedArrayList;
import test.classloading.ClassGenerator;
import test.classloading.SimpleClassLoader;
import testlib.TestBase;
import testlib.gc.JavaHeap;
import testlib.gc.MetaSpaceLoadProducerOptions;

//
// Continuously allocates the following type of class loaders, which in turn load classes:
//
//     Immortal class loaders: Allocated in the setup phase. Each immortal loader remains reachable during
//     the whole test.
//
//     Mortal class loader: after reaching MORTAL_OBJ_HEAP_OCCUPANCY older mortal class loader are replaced by newly
//     created ones, so that older ones can die.
//
//     Short lived class loaders: are allocated without keeping the reference.
//

public class MetaSpaceLoadProducer extends TestBase implements Runnable {

    private NestedArrayList<ClassLoader> immortals;
    private NestedArrayList<ClassLoader> mortals;

    private byte[][] immortalClassesBytes;
    private byte[][] mortalClassesBytes;
    private byte[][] shortLivedClassesBytes;

    private JavaHeap heap;

    private volatile boolean shouldContinueToAllocate;
    private MetaSpaceLoadProducerOptions opts;

    public volatile long checksum; // result of busy wait calculation

    private boolean ready2go;
    private Thread backGroundThread;

    public MetaSpaceLoadProducer(MetaSpaceLoadProducerOptions opts) {
        super(opts.trc_level);
        this.opts = opts;
        heap = new JavaHeap(this);
    }

    @Override
    public void run() {
        try {
            shouldContinueToAllocate = true;
            setUp(); // load all types until immortals are full
            signalReady2Go();
            continouslyAllocateObjects();
        } catch (Exception e) {
            signalReady2Go(); // maybe somebody is waiting
        }
    }

    public void runInBackground() {
        backGroundThread = new Thread(this, "MetaSpaceLoadProducer");
        backGroundThread.start();
    }

    public void stopAllocating() {
        shouldContinueToAllocate = false;
    }

    private byte[][] generateClassBytes(String type, int classesPerLoader, int methodsPerClass) throws Exception {
        byte[][] classesBytes = new byte[classesPerLoader][];
        ClassGenerator clsGen = new ClassGenerator("test.generate."+type, this);
        log("## generating " + classesPerLoader + " byte arrays used as template for " + type + " classes");
        for (int i=0; i<classesBytes.length; i++) {
            classesBytes[i] = clsGen.generateClass("DummyClass"+i, methodsPerClass);
        }
        return classesBytes;
    }

    /**
     * Load all types until immortals are full
     * @throws Exception 
     */
    private void setUp() throws Exception {
        immortals = new NestedArrayList<>();
        mortals = new NestedArrayList<>();

        long moIdx = 0;

        log("###### Set-up: generate class templates");
        logIncInd();
        immortalClassesBytes   = generateClassBytes("immortal",    opts.classes_per_immortal_loader,    opts.methods_per_class);
        mortalClassesBytes     = generateClassBytes("mortal",      opts.classes_per_mortal_loader,      opts.methods_per_class);
        shortLivedClassesBytes = generateClassBytes("short_lived", opts.classes_per_short_lived_loader, opts.methods_per_class);
        logDecInd();

        log("###### Set-up: allocate all types until immortals are there");
        logIncInd();
        log(heap);
        while(immortals.size() < opts.immortal_loader_count || mortals.size() < opts.mortal_loader_count) {
            // allocate 100 objects according to the ALLOC_PERCENTAGES
            int immoToAlloc = immortals.size() >= opts.immortal_loader_count ? 0 : opts.alloc_classes_per_cycle_immortal;
            int moToAlloc = opts.alloc_classes_per_cycle_mortal;
            int shortToAlloc = opts.alloc_classes_per_cycle_short_lived;

            while(moToAlloc+immoToAlloc+shortToAlloc > 0) {
                if (immoToAlloc > 0) {
                    immoToAlloc -= opts.classes_per_immortal_loader;
                    immortals.add(allocImmortal());
                }
                if (moToAlloc > 0) {
                    moToAlloc -= opts.classes_per_mortal_loader;
                    mortals.set(moIdx++, allocMortal());
                    if (moIdx == opts.mortal_loader_count) moIdx = 0;
                }
                if (shortToAlloc > 0) {
                    shortToAlloc -= opts.classes_per_short_lived_loader;
                    allocShortlived(); // allocated and instantly dropped
                }
            }
        }
        log("allocated " + immortals.size() + " immortalObjs (" + immortals + ")");
        heap.gc();
        log(heap);
        logDecInd();
        log();
    }

    private void continouslyAllocateObjects() throws Exception {
        long moIdx = 0;

        log("###### Continously load classes using the mortal and short lived loaders");
        logIncInd();
        log(heap);
        while(shouldContinueToAllocate) {
            // allocate 100-immortal_count classloaders according to the ALLOC_PERCENTAGES
            int moToAlloc = opts.alloc_classes_per_cycle_mortal;
            int shortToAlloc = opts.alloc_classes_per_cycle_short_lived;
            int totalClassesLoaded = 0;

            long timeBefore = System.currentTimeMillis();

            while(moToAlloc+shortToAlloc > 0) {
                if (moToAlloc > 0) {
                    moToAlloc -= opts.classes_per_mortal_loader;
                    mortals.set(moIdx++, allocMortal());
                    totalClassesLoaded += opts.classes_per_mortal_loader;
                    if (moIdx == opts.mortal_loader_count) moIdx = 0;
                }
                if (shortToAlloc > 0) {
                    shortToAlloc -= opts.classes_per_short_lived_loader;
                    @SuppressWarnings("unused")
                    ClassLoader tmp = allocShortlived(); // allocated and instantly dropped
                    totalClassesLoaded += opts.classes_per_short_lived_loader;
                }
            }

            long duration = System.currentTimeMillis() - timeBefore;
            long minDuration = Math.round((float)totalClassesLoaded / opts.class_allocation_rate_per_ms);
            log("## loaded " + totalClassesLoaded + " classes in " + duration + "ms ("+ minDuration + "ms minDuration)");
            if (minDuration > duration+1) {
                log(3, "sleeping " + (minDuration-duration) + "ms");
                Thread.sleep(minDuration-duration);
            }
        }
        log(heap);
        logDecInd();
        log();
    }

    private ClassLoader allocImmortal() {
        SimpleClassLoader loader = new SimpleClassLoader();
        for (int i=0; i<opts.classes_per_immortal_loader; i++) {
            loader.myDefineClass(null, immortalClassesBytes[i]);
        }
        return loader;
    }

    private ClassLoader allocMortal() {
        SimpleClassLoader loader = new SimpleClassLoader();
        for (int i=0; i<opts.classes_per_mortal_loader; i++) {
            loader.myDefineClass(null, mortalClassesBytes[i]);
        }
        return loader;
    }

    private ClassLoader allocShortlived() {
        SimpleClassLoader loader = new SimpleClassLoader();
        for (int i=0; i<opts.classes_per_short_lived_loader; i++) {
            loader.myDefineClass(null, shortLivedClassesBytes[i]);
        }
        return loader;
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
