package test.calls;

import test.classloading.LeveledDirectClassLoader;
import testlib.TestBase;

public class CallStaticFromOtherLoader extends TestBase {

    public static void main(String[] args) {
        try {
            new CallStaticFromOtherLoader().runTest(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static interface IntFunction {
        public int function() throws Throwable;
    }

    // Static Callee
    public static class ClassB_LVL_1 {
        public static int testMethod_static_callee_dontinline() {
            return 0;
        }
    }

    // Static Caller
    public static class ClassA_LVL_0 implements IntFunction {
        public int testMethod_dojit() throws Throwable {
            return ClassB_LVL_1.testMethod_static_callee_dontinline();
        }

        @Override
        public int function() throws Throwable {
            return testMethod_dojit();
        }
    }

    public void runTest(String[] args) throws Throwable {
        int checksum = 0;
        LeveledDirectClassLoader ldl = new LeveledDirectClassLoader(getClass().getClassLoader(), 2);
        IntFunction f = (IntFunction) ldl.newInstance(getClass().getName() + "$ClassA_LVL_0");
        for (int i=0; i<30_000; i++) {
            checksum += f.function();
        }
        System.out.println("checksum:" + checksum);
        waitForEnter("Press Enter to start GC Load");
        System.gc();
        System.gc();
        System.gc();
        for (int i=0; i<30_000; i++) {
            checksum += f.function();
        }
    }
}
