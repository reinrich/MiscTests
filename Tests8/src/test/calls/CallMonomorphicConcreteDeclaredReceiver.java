package test.calls;

import testlib.TestBase;

// Monomorphic Virtual Call
// Declared (static) receiver is concrete and not overridden

public class CallMonomorphicConcreteDeclaredReceiver extends TestBase {

    public static void main(String[] args) {
        new CallMonomorphicConcreteDeclaredReceiver().runTest(args);
    }

    public static class DeclaredReceiver {

        public int testMethod_callee() {
            return 0;
        }

    }

    public byte[] dummy;
    public void runTest(String[] args) {
        DeclaredReceiver recv = new DeclaredReceiver();
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
