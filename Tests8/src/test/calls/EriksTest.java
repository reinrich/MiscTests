package test.calls;

import test.classloading.DirectClassLoader;
import testlib.TestBase;

// I do recall there were also racing call issues here. I'll try to remember the details
// of the race. I think it involved loading a new class A extending a class B that previously had a single
// concrete method of class C, while concurrently unloading C.
// There was something about the dependency checking that was weird there. When that class loads,
// it is still true that there is a single concrete method, and if memory serves right, we do *not* trigger
// deoptimization of thenmethod, because it has not turned megamorphic. But when the target of the
// call is an nmethod, we would catch that in the nmethod entry barrier, and when the target is interpreted,
// we would either observe the old Method*, and catch that it's dead in the c2i adapter, or observe
// the NULL, in which case we catch that also in the c2i adapter, triggering re-resolution of the call.


public class EriksTest extends TestBase {

    public static void main(String[] args) {
        new EriksTest().runTest();
    }

    @Override
    public void runTest() {
        try {
            runTest(getClass().getName() + "$ClassC_WithSingleConcrete");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static abstract class DeclaredReceiver {
        public abstract int testMethod_callee();
    }

    public static class ClassC_WithSingleConcrete extends DeclaredReceiver {
        @Override
        public int testMethod_callee() {
            return 0;
        }
    }

    public static class ClassB {
        public static int testMethod_dojit(DeclaredReceiver receiver) {
            return receiver.testMethod_callee();
        }
    }

    public static class ClassA extends ClassB {
    }

    public void runTest(String receiverClassName) throws Throwable {
        waitForEnter("Press Enter to to start test with '" + receiverClassName + "' as receiver");
        DirectClassLoader loader = new DirectClassLoader(getClass().getClassLoader());
        DeclaredReceiver recv = (DeclaredReceiver) loader.newInstance(receiverClassName);
        int checksum = 0;
        for (int i=0; i<30_000; i++) {
            checksum += ClassB.testMethod_dojit(recv);
        }
        System.out.println("checksum:" + checksum);
        waitForEnter("Press Enter to call System.GC()");
        loader = null;
        recv = null;
        System.gc();
        System.gc();
        System.gc();
    }

}
