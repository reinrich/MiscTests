package test.sync;

public class SyncMethod {

    private static long callCount;
    private volatile static boolean _doWait;
//    public static Class<SyncMethod> lock = SyncMethod.class;
    public static SyncMethod lock;

    public static void main(String[] args) {
        int iterations = 1 << 16;
        long checksum = 0;
        
        lock = new SyncMethod();
        
//        _doWait = true;
//        waitForGdb();
//        synchronized (lock) {
//            try {
//                lock.wait(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            for (long i = 0; i < iterations; i++) {
//                checksum += testMethod1(1, 2, 3);
                checksum += lock.testMethod2(1, 2, 3);
            }
//        }
        System.out.println("checksum : "+checksum);
        System.out.println("callCount: "+callCount);
    }

    private static void waitForGdb() {
        System.out.println("Waiting for gdb...");
        while(_doWait);
    }

    public synchronized long testMethod2(long i, long j, long k) {
        callCount++;
        return i+j+k;
    }

    public static synchronized long testMethod1(long i, long j, long k) {
        callCount++;
        return i+j+k;
    }

}
