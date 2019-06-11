package test.misc;

import testlib.TestBase;

public class GcHeapDumpWithHumongousObjects extends TestBase implements Runnable {

    public static final long G = 1L<<30;
    public static final long M = 1L<<20;
    public static final long K = 1L<<10;

    public static void main(String[] args) {
        GcHeapDumpWithHumongousObjects test = new GcHeapDumpWithHumongousObjects();
        test.run();
    }

    public double[][] humongousObjs;

    @Override
    public void run() {
        humongousObjs = new double[10][];
        long heapSize = 4*G;
        long g1HeapRegionSize = 2*M;
        long targetArraySize = g1HeapRegionSize + 100*K;
        int numElements = (int) (targetArraySize / 8);
        
        for (int i = 0; i < humongousObjs.length; i++) {
            humongousObjs[i] = new double[numElements];
        }
//        System.gc();
        // produce some unreachable humongous objects
        for (int i = 0; i < humongousObjs.length; i++) {
            if ((i&1) == 0) {
                humongousObjs[i] = null;
            }
        }

        int i=0;
        waitForEnter("--- trigger heap dump now! ---");
    }

}
