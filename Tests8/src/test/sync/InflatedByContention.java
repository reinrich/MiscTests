package test.sync;

// Inflate in other thread.
// See if obj is removed from LockStack

public class InflatedByContention {

    public static class TestLock {}

    public static TestLock testLockInstance = new TestLock();
    public static boolean warmupDone;

    public static void main(String[] args) {
        int iterations = 100*1000;
        for (int i = 0; i < iterations; i++) {
            dontinline_dojit_testMethod();
        }
        warmupDone = true;
        dontinline_dojit_testMethod();
        log("Sleeping forever");
        try {
            Thread.sleep(1000*1000);
        } catch (InterruptedException e) { /* ignored */ }
    }

    public static void dontinline_dojit_testMethod() {
        synchronized (testLockInstance) {
            dontinline_inflate();
        }
    }

    private static void dontinline_inflate() {
        if (warmupDone) {
            Thread t = new Thread(() -> {
                synchronized (testLockInstance) {
                    // empty
                }
            }, "Other Test Thread");
            t.start();
            log("Started other thread");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) { /* ignored */ }
            log("leaving synchronized block");
        }
    }

    private static void log(String msg) {
        System.out.println("### " + msg);
    }

}
