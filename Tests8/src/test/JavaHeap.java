package test;

import static test.TestBase.*;

public class JavaHeap {

    public final static long MAX_JAVA_HEAP_BYTES = Runtime.getRuntime().maxMemory();
    
    private Runtime rt;
    private TestBaseOld test;

    public JavaHeap(TestBaseOld test) {
        this.test = test;
        this.rt = Runtime.getRuntime();;
    }

    public long allocatedBytes() {
        return rt.totalMemory() - rt.freeMemory();
    }

    public long freeReservedBytes() {
        return rt.maxMemory() - allocatedBytes();
    }

    public float occupancy() {
        return (float)allocatedBytes() / (float)MAX_JAVA_HEAP_BYTES;
    }

    public float liveOccupancy() {
        gc();
        return occupancy();
    }

    public void gc() {
        test.log(2, "calling System.gc()");
        long start = test.trcActive(3) ? System.currentTimeMillis() : 0;
        System.gc();
        long end = test.trcActive(3) ? System.currentTimeMillis() : 0;
        if (test.trcActive(3)) {
            test.log("System.gc() duration " + (end-start) + "ms");
        }
    }

    public boolean occupancyBelow(float limitPercentage) {
        return occupancy() < limitPercentage || liveOccupancy() < limitPercentage;
    }
    
    public String toString() {
        return "heap: " + rt.maxMemory()/M + "MB reserved | " + rt.totalMemory()/M + " MB committed | " + allocatedBytes()/M +
                " MB allocated | " + rt.freeMemory()/M + " MB committed free | "
                + freeReservedBytes()/M + " MB reserved free | " + TestBaseOld.rndF(100*occupancy()) + "% occupancy";
    }
}
