package test.calls;

import test.classloading.DirectLeveledClassLoader;
import testlib.TestBase;

// Scenario: invokevirtual with final target loaded by child classloader

public class CallFinalVirtualFromOtherLoader extends TestBase {

    public static void main(String[] args) {
        try {
            new CallFinalVirtualFromOtherLoader().runTest(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static interface TestInterface {
        public int call() throws Throwable;
        public void setReceiver();
        public void clearReceiver();
    }

    // Final callee loaded by child loader
    public static class ClassB_LVL_2 {
        public final int testMethod_statically_bound_callee_dontinline_dojit() {
            return 0;
        }
    }

    // Caller
    public static class ClassA_LVL_1 implements TestInterface {

        public ClassB_LVL_2 callReceiver;

        public int testMethod_dojit() throws Throwable {
            int res = 0;
            if (doCall) {
                callReceiver.testMethod_statically_bound_callee_dontinline_dojit();
            }
            return res;
        }

        @Override
        public int call() throws Throwable {
            return testMethod_dojit();
        }

        @Override
        public void setReceiver() {
            callReceiver = new ClassB_LVL_2();
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
        ClassLoader thisLoader = getClass().getClassLoader();
        log("CL: " + thisLoader);
        ClassLoader ldl = new DirectLeveledClassLoader(thisLoader, 2);
        Class<?> cls = ldl.loadClass(getClass().getName() + "$ClassA_LVL_1");
        TestInterface test = (TestInterface) cls.getDeclaredConstructor().newInstance();
        if (variant == TestVariant.EAGER_LOAD) {
            doCall = true;
            test.setReceiver();
        }
        for (int i=0; i<30_000; i++) {
            checksum += test.call();
        }
        System.out.println("checksum:" + checksum);
        if (variant == TestVariant.C1_WITH_LAZY_LOAD) {
            waitForEnter("Press Enter load LEVEL_1 class and make statically bound call");
            doCall = true;
            test.setReceiver();
            log("Calling test function");
            checksum += test.call();
            log("DONE: Calling test function");
        }
    }
}
