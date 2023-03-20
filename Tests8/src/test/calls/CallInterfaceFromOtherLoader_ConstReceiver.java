package test.calls;

import test.classloading.DirectLeveledClassLoader;
import testlib.TestBase;

// Example of an "optimized virtual call" where the path from the caller nmethod
// to the holder oop of the callee is a little longer. In this case there is
// only one path by traversing the constant receiver object.
//
// - invokeinterface with constant receiver in ClassA_LVL_1::testMethod_dojit
//
// - The call is optimized to be a static call based on the constness of the receiver
//
// - ClassC_LVL_3 overrides the target method. This prevents CHA based optimization.
//
// - The receiver class ClassB_LVL_3 is loaded by a child classloader.
//
public class CallInterfaceFromOtherLoader_ConstReceiver extends TestBase {

    public static void main(String[] args) {
        try {
            new CallInterfaceFromOtherLoader_ConstReceiver().runTest(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    // Callee loaded by child loader
    public static class ClassB_LVL_3 implements RecvInterface {
        @Override
        public int testMethod_statically_bound_callee_dontinline_dojit() {
            return 2;
        }
    }

    // Override target method. This prevents CHA based optimization
    public static class ClassC_LVL_3 extends ClassB_LVL_3 {
        @Override
        public int testMethod_statically_bound_callee_dontinline_dojit() {
            throw new Error("Should not reach here");
        }
    }

    public static interface RecvInterface {
        public int testMethod_statically_bound_callee_dontinline_dojit();
    }

    // Decouple Caller/Callee
    public static class Factory_LVL_2 {
        public static RecvInterface getReceiver() {
            new ClassC_LVL_3();              // load class that overrides target method
            return new ClassB_LVL_3();
        }
    }

    // Caller
    public static class ClassA_LVL_1 implements Runnable {
        static final RecvInterface constReceiver = Factory_LVL_2.getReceiver();

        public void testMethod_dojit() {
            // Receiver is a constant
            constReceiver.testMethod_statically_bound_callee_dontinline_dojit();
        }

        @Override
        public void run() {
            testMethod_dojit();
        }
    }

    public void runTest(String[] args) throws Throwable {
        ClassLoader thisLoader = getClass().getClassLoader();
        log("CL: " + thisLoader);
        ClassLoader ldl = new DirectLeveledClassLoader(thisLoader, 3);
        Class<?> cls = ldl.loadClass(getClass().getName() + "$ClassA_LVL_1");
        Runnable test = (Runnable) cls.getDeclaredConstructor().newInstance();

        for (int i=0; i<30_000; i++) {
            test.run();
        }
    }
}
