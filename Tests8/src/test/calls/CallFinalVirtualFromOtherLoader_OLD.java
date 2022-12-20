package test.calls;

import test.classloading.LeveledDirectClassLoader;
import testlib.TestBase;

// Scenario: invokevirtual with final target with other classloader

public class CallFinalVirtualFromOtherLoader_OLD extends TestBase {

    public static void main(String[] args) {
        try {
            new CallFinalVirtualFromOtherLoader_OLD().runTest(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static interface TestInterface {
        public int function() throws Throwable;
        public void setReceiver();
        public void clearReceiver();
    }

    // Final callee loaded in non-delegating loader
    public static class ClassB_LOAD_AT_LEVEL_1 {
        public final int testMethod_statically_bound_callee_dontinline_dojit() {
            return 0;
        }
    }

    // Caller
    public static class ClassA_LOAD_AT_LEVEL_0 implements TestInterface {

        public ClassB_LOAD_AT_LEVEL_1 callReceiver;

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
        public void setReceiver() {
            callReceiver = new ClassB_LOAD_AT_LEVEL_1();
        }

        @Override
        public void clearReceiver() {
            callReceiver = null;
        }
    }

    public static boolean doCall;

    enum TestVariant {
        C1_WITH_LAZY_LOAD, // Callsite is C1 compiled when the declared/static receiver was not yet loaded -> vanilla virtual call
        EAGER_LOAD,        // Callsite is C2 compiled when the declared/static receiver was already loaded -> optimized virtual call
    }

    public TestVariant variant = TestVariant.EAGER_LOAD;

    public void runTest(String[] args) throws Throwable {
        int checksum = 0;
        LeveledDirectClassLoader ldl = new LeveledDirectClassLoader(getClass().getClassLoader(), 2);
        TestInterface test = (TestInterface) ldl.newInstance(getClass().getName() + "$ClassA_LOAD_AT_LEVEL_0");
        if (variant == TestVariant.EAGER_LOAD) {
            doCall = true;
            test.setReceiver();
        }
        for (int i=0; i<30_000; i++) {
            checksum += test.function();
        }
        System.out.println("checksum:" + checksum);
        if (variant == TestVariant.C1_WITH_LAZY_LOAD) {
            waitForEnter("Press Enter load LEVEL_1 class and make statically bound call");
            doCall = true;
            test.setReceiver();
            log("Calling test function");
            checksum += test.function();
            log("DONE: Calling test function");
        }
    }
}
