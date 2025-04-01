package test.calls;

import testlib.TestBase;

public class CallStatic extends TestBase {

    public static void main(String[] args) {
        new CallStatic().runTest();
    }

    public byte[] dummy;
    @Override
    public void runTest() {
        int checksum = 0;
        for (int i=0; i<30_000; i++) {
            checksum += testMethod_dojit();
        }
        System.out.println("checksum:" + checksum);
        waitForEnter("Press Enter to start GC Load");
        for (int i=0; i<30_000; i++) {
            checksum += testMethod_dojit();
        }
    }

    public static int testMethod_dojit() {
        return testMethod_static_callee_dontinline();
    }

    public static int testMethod_static_callee_dontinline() {
        return 0;
    }
}
