package test.calls;

import test.classloading.DirectLeveledClassLoader;
import testlib.TestBase;

// Scenario: invokevirtual with constant receiver type loaded by child classloader

public class CallVirtualFromOtherLoader_ConstReceiver extends TestBase {

    public static void main(String[] args) {
        try {
            new CallVirtualFromOtherLoader_ConstReceiver().runTest(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    // Callee loaded by child loader
    public static class ClassB_LVL_2 extends CalleeBase {
        public int testMethod_statically_bound_callee_dontinline_dojit() {
            return 2;
        }
    }

    public static class CalleeBase {
        public int testMethod_statically_bound_callee_dontinline_dojit() {
            return 1;
        }
    }

    // Decouple Caller/Callee
    public static class Factory_LVL_1 {
        public static CalleeBase getReceiver() {
            return new ClassB_LVL_2();
        }
    }


    // Caller
    public static class ClassA_LVL_1 implements Runnable {

        public static final CalleeBase receiver = Factory_LVL_1.getReceiver();

        public void testMethod_dojit() {
            // Receiver is a constant
            receiver.testMethod_statically_bound_callee_dontinline_dojit();
        }

        @Override
        public void run() {
            testMethod_dojit();
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
        Runnable test = (Runnable) cls.getDeclaredConstructor().newInstance();

        for (int i=0; i<30_000; i++) {
            test.run();
        }
        System.out.println("checksum:" + checksum);
    }
}
