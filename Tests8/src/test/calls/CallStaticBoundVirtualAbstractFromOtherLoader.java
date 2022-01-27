package test.calls;

import test.classloading.LeveledDirectClassLoader;
import testlib.TestBase;

public class CallStaticBoundVirtualAbstractFromOtherLoader extends TestBase {

    public static void main(String[] args) {
        try {
            new CallStaticBoundVirtualAbstractFromOtherLoader().runTest(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static interface TestInterface {
        public int function() throws Throwable;
        void setReceiver(ClassB_Abstract r);
        public void clearReceiver();
    }

    public static abstract class ClassB_Abstract {
        public abstract int testMethod_statically_bound_callee_dontinline_dojit();
    }

    // Static Callee
    public static class ClassB_LOAD_AT_LEVEL_1 extends ClassB_Abstract {
        public final int testMethod_statically_bound_callee_dontinline_dojit() {
            return 0;
        }
    }

    // Static Caller
    public static class ClassA_LOAD_AT_LEVEL_0 implements TestInterface {

        public ClassB_Abstract callReceiver;

        public int testMethod_dojit() throws Throwable {
            int res = 0;
            if (doCall) {
                callReceiver.testMethod_statically_bound_callee_dontinline_dojit();
            }
            return res;
        }

        @Override
        public int function() throws Throwable {
            return testMethod_dojit();
        }

        @Override
        public void setReceiver(ClassB_Abstract r) {
            callReceiver = r;
        }

        @Override
        public void clearReceiver() {
            callReceiver = null;
        }
    }

    public static boolean doCall;

    public void runTest(String[] args) throws Throwable {
        int checksum = 0;
        LeveledDirectClassLoader ldl = new LeveledDirectClassLoader(getClass().getClassLoader(), 2);
        TestInterface test = (TestInterface) ldl.newInstance(getClass().getName() + "$ClassA_LOAD_AT_LEVEL_0");
        for (int i=0; i<30_000; i++) {
            checksum += test.function();
        }
        System.out.println("checksum:" + checksum);
        waitForEnter("Press Enter load LEVEL_1 class and make statically bound call");
        doCall = true;
        test.setReceiver((ClassB_Abstract)ldl.newInstance(getClass().getName() + "$ClassB_LOAD_AT_LEVEL_1"));
        checksum += test.function();
        ldl.clearLoaderFromLevel(1);
        test.clearReceiver();
//        ldl = null;
//        test = null;
        System.gc();
        System.gc();
        System.gc();
    }
}
