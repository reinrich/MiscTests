package test.gc;

import test.tools.NestedArrayList;
import test.tools.SparseListMap;

/**
 * {@code TestHeap} is used by GC tests to control the java heap.
 *
 */
public class TestHeapUseNestedArrayListsForObjects {
    
    private static final int KB = 1<<10;
    private static final int MB = 1<<20;

    private SparseListMap<NestedArrayList<HeapFiller>> allLiveObjects;
    private TestBaseOld test;
    private long heapCapacity;
    
    public TestHeapUseNestedArrayListsForObjects(TestBaseOld test) {
        allLiveObjects = new SparseListMap<NestedArrayList<HeapFiller>>();
        this.test = test;
        heapCapacity = Runtime.getRuntime().maxMemory();
        test.message();
        test.verboseMessage(1, "### Heap Capacity: " + (capacity()>>20) + " MB");
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
            NestedArrayList<HeapFiller> objectsInSizeClass = allLiveObjects.next();
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
        NestedArrayList<HeapFiller> objsInSizeClass = allLiveObjects.get(Integer.valueOf(size));
        objsInSizeClass = objsInSizeClass != null ? objsInSizeClass : new NestedArrayList<HeapFiller>();
        allLiveObjects.put(Integer.valueOf(size), objsInSizeClass);
        @SuppressWarnings("unused")
        byte[] reserved = new byte[4<<20];
        try {
            while (true) {
                HeapFiller hObj = new HeapFiller(size, referenceVsDataRatio);
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
        test.verboseMessage(1, allLiveObjects);
        long objCount = (long) ((capacity()*percentage - allocLiveBytes) / (float)size);
        test.verboseMessage(1, "allocating " + objCount + " objects with " + size + " bytes each");
        NestedArrayList<HeapFiller> objsInSizeClass = allLiveObjects.get(Integer.valueOf(size));
        objsInSizeClass = objsInSizeClass != null ? objsInSizeClass : new NestedArrayList<HeapFiller>();
        allLiveObjects.put(Integer.valueOf(size), objsInSizeClass);
        for (int i = 0; i < objCount; i++) {
            HeapFiller hObj = new HeapFiller(size, referenceVsDataRatio);
            test.verboseMessage(hObj);
            objsInSizeClass.add(hObj);
        }
        allocLiveBytes = liveBytes();
        allocLivePercentage = round((float)allocLiveBytes / (float)capacity() * 100);
        test.verboseMessage(1, allocLiveBytes/MB + " MB live objects allocated on heap (" + allocLivePercentage + "%)");
        test.verboseMessage(1, allLiveObjects);
        test.decMessageIndentation();
        return objCount;
    }

    public void printObjectsOfSize(int size) {
        NestedArrayList<HeapFiller> objs = allLiveObjects.get(size);

        for (int i = 0, s = objs.size(); i < s; i++) {
            test.verboseMessage(objs.get(i));
        }
    }

    public void dropEveryNthObjectOfSize(int nth, int offset, int size) {
        NestedArrayList<HeapFiller> objsInSizeClass = allLiveObjects.get(Integer.valueOf(size));
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
        NestedArrayList<HeapFiller> objs = allLiveObjects.get(size);
        for (int i = offset; i < objs.size()-1; i++) {
            if (--doRemoveCounter == 0) {
                objs.set(i, objs.removeLast()); // replace with last
                doRemoveCounter = nth;
            }
        }

        test.message("Objects now: " + objsInSizeClass.size());
        allocBytes = liveBytes();
        test.message(allocBytes/MB + " MB live objects allocated now (" + round((float)allocBytes/capacity()*100) + "%)");
        test.decMessageIndentation();
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
        long start = System.currentTimeMillis();
        System.gc();
        long end = System.currentTimeMillis();
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

    private float round(float f) {
        return Math.round(f*10)/10f;
    }
}
