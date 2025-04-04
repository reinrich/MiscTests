package test.virtualthread;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import testlib.TestBase;

public class PreemptWait extends TestBase {

    public static final int THREAD_COUNT = 1000;
    public static final int WAIT_CALLS = 100;
    public static final int WAIT_CALLS_POST_WARM_UP = 3;
    public static final long WAIT_MS_WARMUP = Duration.ofMillis(10).toMillis();;
    public static final long WAIT_MS_POST_WARMUP = Duration.ofSeconds(5).toMillis();

    public static void main(String[] args) {
        PreemptWait test = new PreemptWait();
        test.run();
    }

    static AtomicInteger activeThreads;

    void run() {
        activeThreads = new AtomicInteger(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread.ofPlatform().start(() -> dontinline_testMethod(WAIT_CALLS, WAIT_MS_WARMUP));
        }
        waitForThreads();
        log("Warmup done.");

        activeThreads = new AtomicInteger(1);
        Thread.ofVirtual().start(() -> dontinline_testMethod(WAIT_CALLS_POST_WARM_UP, WAIT_MS_WARMUP));
        waitForThreads();
        log("Test run done.");
    }

    private void waitForThreads() {
        while (activeThreads.get() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void dontinline_testMethod(int waitCalls, long waitMsWarmup) {
        for (int i = 0; i < waitCalls; i++) {
            dontinline_doWait(waitMsWarmup);
        }
        activeThreads.decrementAndGet();
    }

    void dontinline_doWait(long waitMsWarmup) {
        Object lock = Thread.currentThread();
        synchronized (lock) {
            try {
                lock.wait(waitMsWarmup);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
