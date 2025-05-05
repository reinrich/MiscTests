package test.calls;

import test.classloading.DirectClassLoader;
import testlib.TestBase;

// Monomorphic Virtual Call
// Declared (static) receiver is concrete.
// Single concrete receiver.
// nmethod with opt virt. call ist unloaded when concrete method is unloaded
// Check inline cache clearing of caller.

public class CallMonomorphicMakeUnloadedReceiver2 extends TestBase {

    public static void main(String[] args) {
        new CallMonomorphicMakeUnloadedReceiver2().runTest();
    }

    public static class DeclaredReceiver {
        public int testMethod_callee() {
            return 0;
        }
    }

    public static class ConcreteReceiverR1 extends DeclaredReceiver {
        @Override
        public int testMethod_callee() {
            return 0;
        }
    }

    @Override
    public void runTest() {
        try {
            runTest(getClass().getName() + "$ConcreteReceiverR1");
            runTest(getClass().getName() + "$ConcreteReceiverR1");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    public void runTest(String receiverClassName) throws Throwable {
        waitForEnter("Press Enter to to start test with '" + receiverClassName + "' as receiver");
        DirectClassLoader loader = new DirectClassLoader(getClass().getClassLoader());
        DeclaredReceiver recv = (DeclaredReceiver) loader.newInstance(receiverClassName);
        int checksum = 0;
        for (int i=0; i<30_000; i++) {
            checksum += testMethod_callercaller_dojit(recv);
        }
        System.out.println("checksum:" + checksum);
        waitForEnter("Press Enter to call System.GC()");
        loader = null;
        recv = null;
        System.gc();
        System.gc();
        System.gc();
    }

    public static int testMethod_callercaller_dojit(DeclaredReceiver receiver) {
        return testMethod_opt_virt_call_dojit(receiver);
    }

    public static int testMethod_opt_virt_call_dojit(DeclaredReceiver receiver) {
        return receiver.testMethod_callee();
    }
}
