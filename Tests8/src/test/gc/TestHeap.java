package test.gc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import test.gc.microbench.TestOptions;
import test.tools.SparseListMap;

/**
 * {@code TestHeap} is used by GC tests to control the java heap.
 *
 */
public class TestHeap {
    
    private static final int KB = 1<<10;
    private static final int MB = 1<<20;

    private SparseListMap<ArrayList<HeapFiller>> allLiveObjects;
    private TestBaseOld test;
    private long heapCapacity;
    private SimpleDateFormat tsFormatter;
    
    public TestHeap(TestBaseOld test) {
        allLiveObjects = new SparseListMap<ArrayList<HeapFiller>>();
        this.test = test;
        heapCapacity = Runtime.getRuntime().maxMemory();
        test.message();
        test.verboseMessage(1, "### Heap Capacity: " + (capacity()>>20) + " MB");
        tsFormatter = new SimpleDateFormat("HH:mm:ss.S");
    }

    public long capacity() {
        return heapCapacity;
    }
    
    public long allocatedBytes() {
        return capacity() - Runtime.getRuntime().freeMemory();
    }

    private float allocatedBytesPercentage() {
        return round((float)allocatedBytes()/capacity()*100);
    }

    private long liveBytes() {
        long result = 0;
        allLiveObjects.resetIterator();
        while (allLiveObjects.hasNext()) {
            ArrayList<HeapFiller> objectsInSizeClass = allLiveObjects.next();
            for (int i = 0, n = objectsInSizeClass.size(); i < n; i++) {
                result += objectsInSizeClass.get(i).getSize();
            }
        }
        return result;
    }

    /**
     * Uses {@link HeapFiller} to allocate objects of the given size until the heap is exhausted.
     * {@code HeapFiller} is wraps a {@code long} array of the given size, which it newly allocates.
     * 
     * @param size Size of objects
     * @return Number of {@code HeapFiller} created
     */
    public long fillWithObjectsOfSize(int size) {
        return fillWithObjectsOfSize(size, 0.0F);
    }

    /**
     * Uses {@link HeapFiller} to allocate from the heap until it is exhausted.
     * {@code HeapFiller} builds a tree structure where the ratio between references and non-references is {@code referenceVsDataRatio}
     * 
     * @param size Size of {@code HeapFiller} to be constructed
     * @param referenceVsDataRatio Ratio between references and non-references in the {@code HeapFiller}
     * @return Number of {@code HeapFiller} created
     */
    public long fillWithObjectsOfSize(int size, float referenceVsDataRatio) {
        test.message();
        test.message("### Filling heap with objects of size " + size + " bytes");
        if (referenceVsDataRatio != 0) {
            test.message("### Ratio references : other data = " + referenceVsDataRatio);
        }
        ArrayList<HeapFiller> objsInSizeClass = allLiveObjects.get(Integer.valueOf(size));
        objsInSizeClass = objsInSizeClass != null ? objsInSizeClass : new ArrayList<HeapFiller>();
        allLiveObjects.put(Integer.valueOf(size), objsInSizeClass);
        @SuppressWarnings("unused")
        byte[] reserved = new byte[4<<20];
        try {
            while (true) {
                HeapFiller hObj = new HeapFiller(size);
                test.verboseMessage(hObj);
                objsInSizeClass.add(hObj);
            }
        } catch (OutOfMemoryError e) {
            reserved = null; // free the reserved memory to avoid subsequent OOMs
        }
        return objsInSizeClass.size();
    }

    /**
     * Uses {@link HeapFiller} to allocate from the heap until it is occupied to the given percentage.
     * {@code HeapFiller} builds a tree structure where the ratio between references and non-references is {@code referenceVsDataRatio}
     * 
     * @param size Size of {@code HeapFiller} to be constructed
     * @param referenceVsDataRatio Ratio between references and non-references in the {@code HeapFiller}
     * @param percentage The percentage to which the heap is supposed to be occupied.
     * @return Number of {@code HeapFiller} created
     */
    public long fillWithObjectsOfSize(int size, float referenceVsDataRatio, float percentage) {
        long allocLiveBytes = liveBytes();
        float allocLivePercentage = round((float)allocLiveBytes / (float)capacity() * 100);
        test.message();
        test.message("### Filling heap up to " + percentage*100 + "% (" + (long)(capacity()*percentage / MB) + " MB) with objects of size " + size + " bytes");
        if (referenceVsDataRatio != 0) {
            test.message("### Ratio references : other data = " + referenceVsDataRatio);
        }
        test.incMessageIndentation();
        
        test.verboseMessage(1, allocLiveBytes/MB + " MB live objects allocated on heap (" + allocLivePercentage + "%)");
        long objCount = (long) ((capacity()*percentage - allocLiveBytes) / (float)size);
        test.verboseMessage(1, "allocating " + objCount + " objects with " + size + " bytes each");
        ArrayList<HeapFiller> objsInSizeClass = allLiveObjects.get(Integer.valueOf(size));
        objsInSizeClass = objsInSizeClass != null ? objsInSizeClass : new ArrayList<HeapFiller>();
        allLiveObjects.put(Integer.valueOf(size), objsInSizeClass);
        for (int i = 0; i < objCount; i++) {
            HeapFiller hObj = new HeapFiller(size);
            test.verboseMessage(hObj);
            objsInSizeClass.add(hObj);
        }
        allocLiveBytes = liveBytes();
        allocLivePercentage = round((float)allocLiveBytes / (float)capacity() * 100);
        test.verboseMessage(1, allocLiveBytes/MB + " MB live objects allocated on heap (" + allocLivePercentage + "%)");
        test.decMessageIndentation();
        return objCount;
    }

    public void printObjectsOfSize(int size) {
        ArrayList<HeapFiller> objs = allLiveObjects.get(size);

        for (int i = 0, s = objs.size(); i < s; i++) {
            test.verboseMessage(objs.get(i));
        }
    }

    public void dropEveryNthObjectOfSize(int nth, int offset, int size) {
        ArrayList<HeapFiller> objsInSizeClass = allLiveObjects.get(Integer.valueOf(size));
        String nthStr;
        switch (nth) {
        case 2:
            nthStr = "2nd";
            break;
        case 3:
            nthStr = "3rd";
            break;

        default:
            nthStr = nth+"th";
        }
        test.message("Dropping every " + nthStr + " object of size class " + size/KB + " KB beginning at offset " + offset);
        test.incMessageIndentation();
        test.message("Objects before: " + objsInSizeClass.size());
        long allocBytes = liveBytes();
        test.message(allocBytes/MB + " MB live objects allocated before (" + round((float)allocBytes/capacity()*100) + "%)");

        int doRemoveCounter = 1;
        ArrayList<HeapFiller> objs = allLiveObjects.get(size);
        for (int i = offset; i < objs.size()-1; i++) {
            if (--doRemoveCounter == 0) {
                objs.set(i, objs.remove(objs.size()-1)); // replace with last
                doRemoveCounter = nth;
            }
        }

        test.message("Objects now: " + objsInSizeClass.size());
        allocBytes = liveBytes();
        test.message(allocBytes/MB + " MB live objects allocated now (" + round((float)allocBytes/capacity()*100) + "%)");
        test.decMessageIndentation();
    }

    public void replaceEveryNthObjectOfSize(int nth, int size, float referenceVsDataRatio, int allocRateKbPer100MilliSec) {
        String nthStr;
        switch (nth) {
        case 2:
            nthStr = "2nd";
            break;
        case 3:
            nthStr = "3rd";
            break;

        default:
            nthStr = nth+"th";
        }
        test.message("Replacing every " + nthStr + " object of size class " + size/KB + " KB");
        test.incMessageIndentation();

        int offset = 0;
        int sleepCounter = 0;
        ArrayList<HeapFiller> objs = allLiveObjects.get(size);
        while (true) {
            int doRemoveCounter = 1;
            long bytesAllocated = 0;
            long start = System.currentTimeMillis();
            for (int i = offset; i < objs.size()-1; i++) {
                if (--doRemoveCounter == 0) {
                    HeapFiller hf = new HeapFiller(size);
                    bytesAllocated += hf.getSize();
                    if (bytesAllocated/KB > allocRateKbPer100MilliSec) {
                        bytesAllocated = 0;
                        long now = System.currentTimeMillis();
                        long sleepTime = 100 - (now - start);
                        if (sleepTime > 0) {
                            test.message("S" + sleepTime + "ms ", sleepCounter++ % 10 == 0/*newline*/);
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            start = System.currentTimeMillis();
                        }
                    }
                    objs.set(i, hf);
                    doRemoveCounter = nth;
                }
            }
            if (++offset == nth) {
                offset = 0;
            }
        }
    }

    public void dropAllObjs() {
        allLiveObjects.clear();
    }

    /**
     * Triggers a full GC by calling {@code System.gc()}, measures the duration and adds it to the test results if desired.
     */
    public long fullGC(String comment, boolean addToTestResults) {
        test.message("triggering full GC by calling System.gc(): " + comment);
        test.incMessageIndentation();
        test.message("before GC: allocated:" + allocatedBytes()/MB + " MB (" + allocatedBytesPercentage() + "%)");
        test.message(tsFormatter.format(new Date())+": GC start");
        long start = System.currentTimeMillis();
        System.gc();
        long end = System.currentTimeMillis();
        test.message(tsFormatter.format(new Date())+": GC end");
        long duration = end - start;
        if (addToTestResults) {
            test.addResult(duration);
        }
        test.message("after GC: allocated:" + allocatedBytes()/MB + " MB (" + allocatedBytesPercentage() + "%)");
        test.message("FullGC duration: " + duration + " ms");
        test.decMessageIndentation();
        return duration;
    }

    /**
     * Promotes objects to the old generation using the given {@link PromotionMethod}
     */
    public long promoteAllObjects(PromotionMethod method) {
        // trigger young GCs
        // assumption: less than half the heap is youngen
        long result = 0;
        switch (method) {
        case SYSTEM_GC:
            result = fullGC("promote all objects to old generation", false);
            break;

        case ALLOC_TEMP_OBJECTS_ALOT:
            test.verboseMessage(1, "triggering young GCs to promote objects");
            int size = 1000;
            long count = capacity() / 2 / size;
            for (int ii = 0; ii < 10; ii++) {
                for (int jj = 0; jj < count; jj++) {
                    @SuppressWarnings("unused")
                    byte[] dummy = new byte[size];
                }
            }
            break;

        default:
            break;
        }
        return result;
    }

    public void createHumongousObjects() {
        TestOptions opts = test.opts;
        test.incMessageIndentation();
        long humSizeTotal = (long)opts.humongousObjsSizeTotalMB * MB;
        long humSize      = (long)opts.humongousObjsSizeKB      * KB;
        int humObjCount = (int)(humSizeTotal / humSize);
        test.message("allocating humongous obects (size : " + opts.humongousObjsSizeKB + " KB" +
                "  total : "+opts.humongousObjsSizeTotalMB +" MB)" +
                "  count : " + humObjCount +
                "  alloc rate : " + opts.humongousObjsPerSec + " objs/s ("
                + test.round(opts.humongousObjsPerSec * opts.humongousObjsSizeKB * KB / (float)MB, 2) + " MB/s)");
        byte[][] humObjs = new byte[humObjCount][];
        final int sleepTime = Math.max(1, opts.humongousObjsPerSec == 0 ? 60000 : (1000 / opts.humongousObjsPerSec));

        for(int i = 0; i < humObjCount; i++) {
            humObjs[i] = new byte[opts.humongousObjsSizeKB*KB];
        }

        // start thread that creates new humongous objs replacing old ones
        Runnable run = () -> {
            while(true) {
                int idx = (int) (Math.random() * humObjCount);
                byte[] hobj = opts.humongousObjsPerSec == 0 ? null : new byte[opts.humongousObjsSizeKB*KB];
                humObjs[idx] = hobj;
                try {
                    Thread.sleep(sleepTime);
                    System.out.print("H");
                } catch (Exception e) { /*ignore*/ }
            }
        };
        
        Thread humongousObjThread = new Thread(run, "Humongous Obj. Creator");
        humongousObjThread.start();
    }

    private float round(float f) {
        return Math.round(f*10)/10f;
    }

    public int depth(int heapFillerSize) {
        ArrayList<HeapFiller> hf = allLiveObjects.get(heapFillerSize);
        return hf.get(0).depth();
    }
}
