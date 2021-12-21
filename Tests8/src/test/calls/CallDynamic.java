package test.calls;

import testlib.TestBase;

public class CallDynamic extends TestBase {

    public static class ReceiverBase {
        public int getVal() { return 0; }
        public int getExtraVal() { return 0; }
    }

    public static class Receiver2 extends ReceiverBase {
        public int getVal() { return 1; }
        public int getExtraVal() { return 1; }
    }

    public static class Receiver3 extends Receiver2 {
        public int getVal() { return -1; }
        public int getExtraVal() { return -1; }
    }

    public static void main(String[] args) {
        new CallDynamic().runTest(args);
    }

    public byte[] dummy;
    public void runTest(String[] args) {
        ReceiverBase receiver1 = new ReceiverBase();
        ReceiverBase receiver2 = new Receiver2();
        ReceiverBase receiver3 = new Receiver3();
        int checksum = 0;
        for (int i=0; i<30_000; i++) {
            checksum += testMethod_dojit(receiver1);
            checksum += testMethod_dojit(receiver2);
            checksum += testMethod_dojit(receiver3);
        }
        System.out.println("checksum:" + checksum);
        waitForEnter("Press Enter to start GC Load");
        while(true) {
            dummy = new byte[10000];
        }
    }

    public static class IntWrapper {
        public int val;
    }

    static final IntWrapper wrapper = new IntWrapper();
    static final IntWrapper wrapper2 = new IntWrapper();
    public static int testMethod_dojit(ReceiverBase receiver) {
        return receiver.getVal() + wrapper.val;
    }

    public static int testMethod_dojit2(ReceiverBase receiver) {
        return receiver.getVal() + receiver.getExtraVal() +
                wrapper.val + wrapper2.val;
    }
}
