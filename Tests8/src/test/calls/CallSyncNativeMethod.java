package test.calls;

//import com.sap.jvm.impl.testing.JIT;

public class CallSyncNativeMethod {

    private static long callCount;
    private volatile static boolean _doWait;
//    private static JIT rcvr;
    private static CallSyncNativeMethod testObj;

    public static void main(String[] args) {
        int iterations = 1 << 10;
        long checksum = 0;
//        rcvr = null; // new JIT();
        testObj = new CallSyncNativeMethod();
        
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

    // called by JIT.mIsSrJpLLaI0()
    // see //bas2/.../testing/JIT.cpp
    public static long callback() {
        return 1;
    }
    
    private static long callTestMethod() {
        long res=1;
        try {
//            res = rcvr.mIsSrJpLLaI0(testObj, "callback");
            System.exit(1);
        } catch (Exception e) {
            // JIT.mIsSrJpLLaI0() always throws an exception
        }
        return res;
    }

}
