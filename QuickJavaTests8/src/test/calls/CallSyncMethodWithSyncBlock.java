package test.calls;

public class CallSyncMethodWithSyncBlock {

    private static long callCount;
    private volatile static boolean _doWait;
    private static CallSyncMethodWithSyncBlock recvr;

    public static void main(String[] args) {
        int iterations = 1 << 16;
        long checksum = 0;
        recvr = new CallSyncMethodWithSyncBlock();
        
//        _doWait = true;
        waitForGdb();
        for (long i = 0; i < iterations; i++) {
            checksum += callTestMethod();
        }
        System.out.println("checksum : "+checksum);
        System.out.println("callCount: "+callCount);
    }

    private static void waitForGdb() {
        while(_doWait);
    }

    private static long callTestMethod() {
        return recvr.testMethod(1, 2, 3);
    }

    private synchronized long testMethod(long i, long j, long k) {
        synchronized (recvr) {
            callCount++;
        }
        return i+j+k;
    }

}
