package test.calls;

import testlib.TestBase;

// Monomorphic Virtual Call
// Declared (static) receiver is concrete and not overridden

public class CallMonomorphicConcreteDeclaredReceiver extends TestBase {

    public static void main(String[] args) {
        new CallMonomorphicConcreteDeclaredReceiver().runTest(args);
    }

    public static class DeclaredReceiver {

        public int dontinline_testMethod_callee() {
            return 0;
        }

    }

    public byte[] dummy;
    public void runTest(String[] args) {
        DeclaredReceiver recv = new DeclaredReceiver();
        int checksum = 0;
        for (int i=0; i<30_000; i++) {
            checksum += dontinline_testMethod_dojit(recv);
        }
        System.out.println("checksum:" + checksum);
    }

    public static int dontinline_testMethod_dojit(DeclaredReceiver receiver) {
        return receiver.dontinline_testMethod_callee();
    }
}
