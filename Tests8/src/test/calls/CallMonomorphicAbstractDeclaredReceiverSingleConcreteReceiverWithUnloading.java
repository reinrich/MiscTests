package test.calls;

import test.classloading.DirectClassLoader;
import testlib.TestBase;

// Monomorphic Virtual Call
// Declared (static) receiver is abstract.
// Single concrete receiver.

public class CallMonomorphicAbstractDeclaredReceiverSingleConcreteReceiverWithUnloading extends TestBase {

    public static void main(String[] args) {
        new CallMonomorphicAbstractDeclaredReceiverSingleConcreteReceiverWithUnloading().runTest();
    }

    public static abstract class DeclaredReceiver {
        public abstract int testMethod_callee();
    }

    public static class ConcreteReceiverR1 extends DeclaredReceiver {
        @Override
        public int testMethod_callee() {
            return 0;
        }
    }

    public static class ConcreteReceiverR2 extends DeclaredReceiver {
        @Override
        public int testMethod_callee() {
            return 0;
        }
    }

    public void runTest() {
        try {
            runTest(getClass().getName() + "$ConcreteReceiverR1");
            runTest(getClass().getName() + "$ConcreteReceiverR2");
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
            checksum += testMethod_dojit(recv);
        }
        System.out.println("checksum:" + checksum);
        waitForEnter("Press Enter to call System.GC()");
        loader = null;
        recv = null;
        System.gc();
        System.gc();
        System.gc();
    }

    public static int testMethod_dojit(DeclaredReceiver receiver) {
        return receiver.testMethod_callee();
    }
}
