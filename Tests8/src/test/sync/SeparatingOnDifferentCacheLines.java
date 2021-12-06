package test.sync;

import java.util.concurrent.atomic.AtomicInteger;

public class SeparatingOnDifferentCacheLines extends Thread {

    private static Object[] locks = new Object[16]; 
    private static AtomicInteger running = new AtomicInteger();

    public SeparatingOnDifferentCacheLines(int i) {
        lockIdx = i;
    }

    public static void main(String[] args) {
        allocateStuff(); // widen tlab
        System.out.println("------------------ Force GC ------------------");
        System.gc();
        allocateLocks();

        for(int lockIdx=1; lockIdx<6; lockIdx++) {
            test(lockIdx);
        }
        System.out.println("-----------------------------------");
        for(int lockIdx=1; lockIdx<6; lockIdx++) {
            test(lockIdx);
        }
        System.out.println("-----------------------------------");
        
        for(int lockIdx=1; lockIdx<locks.length; lockIdx++) {
            test(lockIdx);
            test(lockIdx);
            test(lockIdx);
        }
        System.out.println("-----------------------------------");
    }

    private static void allocateLocks() {
        for(int i = 0; i < locks.length; i++) {
            locks[i] = new Object();
        }
    }

    private static void allocateStuff() {
        for (int i = 0 ; i < 1<<20; i++) {
            byte[] dummy = new byte[100];
        }
    }

    private int lockIdx;

    public void run() {
        Object theLock = locks[lockIdx];
        running.incrementAndGet();
        while (running.get() != 2);
        long start = System.currentTimeMillis();
        for (int i = 1 << 25; i>0; i--) {
            synchronized (theLock) {
              // empty   
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("   " + (end - start) + "ms");
        running.decrementAndGet();
    }
    
    private static void test(int lockIdx) {
        System.out.println("Test with " + (lockIdx*2)*8 + " bytes between locks");
        
        Thread t1 = new SeparatingOnDifferentCacheLines(0);
        Thread t2 = new SeparatingOnDifferentCacheLines(lockIdx);
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
    }
    
}
