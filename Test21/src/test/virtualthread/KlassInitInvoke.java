package test.virtualthread;

import java.util.concurrent.CountDownLatch;

import testlib.TestBase;

public class KlassInitInvoke  extends TestBase {
//    private static final int MAX_VTHREAD_COUNT = 8 * Runtime.getRuntime().availableProcessors();
    private static int MAX_VTHREAD_COUNT;

    private static final CountDownLatch finishInvokeStatic1 = new CountDownLatch(1);

    public static void main(String[] args) {
        try {
            MAX_VTHREAD_COUNT = Integer.parseInt(args[0]);
            System.out.println("MAX_VTHREAD_COUNT:" + MAX_VTHREAD_COUNT);
            testReleaseAtKlassInitInvokeStatic1();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    static void testReleaseAtKlassInitInvokeStatic1() throws Exception {
        class TestClass {
            static {
                try {
                    finishInvokeStatic1.await();
                } catch (InterruptedException e) {}
            }
            static void m() {
            }
        }

        Thread[] vthreads = new Thread[MAX_VTHREAD_COUNT];
        CountDownLatch[] started = new CountDownLatch[MAX_VTHREAD_COUNT];
        for (int i = 0; i < MAX_VTHREAD_COUNT; i++) {
            final int id = i;
            started[i] = new CountDownLatch(1);
            vthreads[i] = Thread.ofVirtual().start(() -> {
                started[id].countDown();
                TestClass.m();
            });
        }
        for (int i = 0; i < MAX_VTHREAD_COUNT; i++) {
            started[i].await();
            await(vthreads[i], Thread.State.WAITING);
        }

        finishInvokeStatic1.countDown();
        for (int i = 0; i < MAX_VTHREAD_COUNT; i++) {
            vthreads[i].join();
        }
    }

    /**
     * Waits for the given thread to reach a given state.
     */
    private static void await(Thread thread, Thread.State expectedState) throws InterruptedException {
        Thread.State state = thread.getState();
        while (state != expectedState) {
            Thread.sleep(10);
            state = thread.getState();
        }
    }
}
