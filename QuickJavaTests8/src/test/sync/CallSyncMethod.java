package test.sync;

public class CallSyncMethod {

    private static long callCount;
    private volatile static boolean _doWait;
    private static CallSyncMethod recvr;

    public static void main(String[] args) {
        int iterations = 1 << 16;
        long checksum = 0;
        recvr = new CallSyncMethod();
        
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
        callCount++;
        return i+j+k;
    }

}
