package test.gc.humongous;

import test.gc.TestBaseOld;
import test.gc.TestHeapBasedOnTreeSet;

/*
./sapjvm_8/bin/java
-Xms1g -Xmx1g 
-XX:G1HeapRegionSize=1m -XX:+UseG1GC
-cp /u/w/d/jtests/QuickJavaTests/bin
test.gc.humongous.TestPinningOfHumongousObjects
 */
public class TestPinningOfHumongousObjects extends TestBaseOld {
    private static final int MB = 1<<20;
    private static final int REGION_SIZE = 1*MB; // -XX:G1HeapRegionSize=1m
    private static final int HUMONGOUS_SIZE_1 = 10 * REGION_SIZE;
    private static final int HUMONGOUS_SIZE_2 = HUMONGOUS_SIZE_1 + 1*REGION_SIZE;
    private static final int HUMONGOUS_SIZE_3 = HUMONGOUS_SIZE_2 + REGION_SIZE;

    public TestPinningOfHumongousObjects(int verboseLevel) {
        super(verboseLevel);
    }

    public static void main(String[] args) {
        int verboseMessageLevel = 3;
        TestPinningOfHumongousObjects test = new TestPinningOfHumongousObjects(verboseMessageLevel);
        TestHeapBasedOnTreeSet heap = new TestHeapBasedOnTreeSet(test);
        long objCount;
        int size;
        
        size = HUMONGOUS_SIZE_1;
        objCount = heap.fillWithObjectsOfSize(size);
        test.message("Allocated " + objCount + " objects");
        heap.dropEveryNthObjectOfSize(2, size);
        System.gc();
        heap.printObjectsOfSize(size);
        
        size = HUMONGOUS_SIZE_2;
        objCount = heap.fillWithObjectsOfSize(size);
        test.message("Allocated " + objCount + " objects");
        heap.dropEveryNthObjectOfSize(2, size);
        System.gc();
        heap.printObjectsOfSize(size);

        size = HUMONGOUS_SIZE_3;
        objCount = heap.fillWithObjectsOfSize(size);
        test.message("Allocated " + objCount + " objects");
        heap.dropEveryNthObjectOfSize(2, size);
        System.gc();
        heap.printObjectsOfSize(size);
    }
    
}
