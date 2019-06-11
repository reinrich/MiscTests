package test.calls;

//import com.sap.jvm.impl.testing.JIT;

public class CallStaticSyncNativeMethod {

    private static long callCount;
    private volatile static boolean _doWait;

    public static void main(String[] args) {
        int iterations = 1 << 10;
        long checksum = 0;
        
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
        return 0;
//        return JIT.mSsSrJpaC0();
    }

}
