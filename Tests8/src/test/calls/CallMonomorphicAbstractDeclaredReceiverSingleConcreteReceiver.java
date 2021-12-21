package test.calls;

import testlib.TestBase;

// Monomorphic Virtual Call
// Declared (static) receiver is abstract.
// Single concrete receiver.

public class CallMonomorphicAbstractDeclaredReceiverSingleConcreteReceiver extends TestBase {

    public static void main(String[] args) {
        new CallMonomorphicAbstractDeclaredReceiverSingleConcreteReceiver().runTest(args);
    }

    public static abstract class DeclaredReceiver {
        public abstract int testMethod_callee();
    }

    public static class ConcreteReceiver extends DeclaredReceiver {
        @Override
        public int testMethod_callee() {
            return 0;
        }
    }

    public byte[] dummy;
    public void runTest(String[] args) {
        DeclaredReceiver recv = new ConcreteReceiver();
        int checksum = 0;
        for (int i=0; i<30_000; i++) {
            checksum += testMethod_dojit(recv);
        }
        System.out.println("checksum:" + checksum);
        waitForEnter("Press Enter to start GC Load");
        for (int i=0; i<30_000; i++) {
            checksum += testMethod_dojit(recv);
        }
    }

    public static int testMethod_dojit(DeclaredReceiver receiver) {
        return receiver.testMethod_callee();
    }
}
