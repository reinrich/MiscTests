package test.calls;

import test.classloading.DirectLeveledClassLoader;
import testlib.TestBase;

// Scenario: invokevirtual with known receiver type loaded by child classloader

public class CallVirtualFromOtherLoader_KnownReceiverType extends TestBase {

    public static void main(String[] args) {
        try {
            new CallVirtualFromOtherLoader_KnownReceiverType().runTest(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static interface TestInterface {
        public int call() throws Throwable;
    }

    // Final callee loaded by child loader
    public static class ClassB_LVL_2 {
        public int testMethod_statically_bound_callee_dontinline_dojit() {
            return 0;
        }
    }

    // Caller
    public static class ClassA_LVL_1 implements TestInterface {

        public int testMethod_dojit() throws Throwable {
            return getReceiver_dojit().testMethod_statically_bound_callee_dontinline_dojit();
        }

        @Override
        public int call() throws Throwable {
            return testMethod_dojit();
        }

        public ClassB_LVL_2 getReceiver_dojit() {
            return new ClassB_LVL_2();
        }
    }

    public void runTest(String[] args) throws Throwable {
        int checksum = 0;
        ClassLoader thisLoader = getClass().getClassLoader();
        log("CL: " + thisLoader);
        ClassLoader ldl = new DirectLeveledClassLoader(thisLoader, 2);
        Class<?> cls = ldl.loadClass(getClass().getName() + "$ClassA_LVL_1");
        TestInterface test = (TestInterface) cls.getDeclaredConstructor().newInstance();

        for (int i=0; i<30_000; i++) {
            checksum += test.call();
        }
        System.out.println("checksum:" + checksum);
    }
}
