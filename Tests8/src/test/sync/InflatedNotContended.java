package test.sync;

public class InflatedNotContended {

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
    }

    public static void dontinline_dojit_testMethod() {
        synchronized (testLockInstance) {
            dontinline_inflate();
        }
    }

    private static void dontinline_inflate() {
        if (warmupDone) {
            // get lock inflated by calling wait()
            try {
                testLockInstance.wait(1);
            } catch (InterruptedException e) { /* ignored */ }
        }
    }

}
