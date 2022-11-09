package test.calls;

import testlib.TestBase;

// Monomorphic Virtual Call
// Declared (static) receiver is concrete and not overridden

public class CallMonomorphicCallSiteMethodDelegatingToSuper extends TestBase {

    public static void main(String[] args) {
        new CallMonomorphicCallSiteMethodDelegatingToSuper().runTest(args);
    }

    public static class DeclaredReceiver {
        public int testMethod_callee() {
            return 0;
        }
    }

    public static class DynamicReceiver extends DeclaredReceiver {
        @Override
        public int testMethod_callee() {
            return super.testMethod_callee();
        }
    }

    public void runTest(String[] args) {
        DeclaredReceiver recv[] = new DeclaredReceiver[2];
        recv[0] = new DeclaredReceiver();
        recv[1] = new DynamicReceiver();
        int checksum = 0;
        for (int i=0; i<30_000; i++) {
            checksum += testMethod_dojit(recv[1]);
        }
        System.out.println("checksum:" + checksum);
    }

    public static int testMethod_dojit(DeclaredReceiver receiver) {
        return receiver.testMethod_callee();
    }
}
