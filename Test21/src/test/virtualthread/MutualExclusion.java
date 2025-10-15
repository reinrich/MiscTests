package test.virtualthread;

import testlib.TestBase;

public class MutualExclusion  extends TestBase {

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 100_000; i++) {
                dontinline_testMutualExclusion(1, 0);
            }
            System.out.println("WARMUPDONE");
            for (int i = 0; i < 100_000; i++) {
                dontinline_testMutualExclusion(0, Integer.parseInt(args[0]));
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    static void dontinline_testMutualExclusion(int nPlatformThreads, int nVirtualThreads) throws Exception {
        class Counter {
            int count;
            synchronized void dontinline_increment() {
                count++;
                Thread.yield();
            }
        }
        var counter = new Counter();
        int nThreads = nPlatformThreads + nVirtualThreads;
        var threads = new Thread[nThreads];
        int index = 0;
        for (int i = 0; i < nPlatformThreads; i++) {
            threads[index] = Thread.ofPlatform()
                    .name("platform-" + index)
                    .unstarted(counter::dontinline_increment);
            index++;
        }
        for (int i = 0; i < nVirtualThreads; i++) {
            threads[index] = Thread.ofVirtual()
                    .name("virtual-" + index)
                    .unstarted(counter::dontinline_increment);
            index++;
        }
        // start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        // wait for all threads to terminate
        for (Thread thread : threads) {
            thread.join();
        }
        if (nThreads != counter.count) {
            throw new Error();
        }
    }

}
