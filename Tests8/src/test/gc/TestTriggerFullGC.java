package test.gc;

import testlib.TestBase;

public class TestTriggerFullGC extends TestBase implements Runnable {

    public static void main(String[] args) {
        TestTriggerFullGC test = new TestTriggerFullGC();
        test.run();
    }

    @Override
    public void run() {
        while (true) {
            try {
                consumeAllMemory();
            } catch (Throwable e) {
                consumedMemory = null;
                msg("Caught OutOfMemoryError");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) { }
            }
        }
    }

    static class LinkedList {
        LinkedList l;
        public long[] array;
        public LinkedList(LinkedList l, int size) {
            this.array = new long[size];
            this.l = l;
        }
    }

    public LinkedList consumedMemory;

    public void consumeAllMemory() {
        msg("consume all memory");
        int size = 128 * 1024 * 1024;
        while(size > 0) {
            try {
                while(true) {
                    consumedMemory = new LinkedList(consumedMemory, size);
                }
            } catch(OutOfMemoryError oom) {
            }
            size = size / 2;
        }
    }
}
