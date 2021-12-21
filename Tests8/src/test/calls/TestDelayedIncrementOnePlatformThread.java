package test.calls;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.StructuredExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

// /opt/jdks/jdk-18-loom/bin/java
// --enable-preview
// --source 18
// -Djdk.defaultScheduler.parallelism=1
// ~/priv/git/reinrich/MiscTests/Tests/src/test/loom/TestDelayedIncrementOnePlatformThread.java

public class TestDelayedIncrementOnePlatformThread {

    private static final int THRD_CNT = 1000 * 1000;
    private static final long SLEEP_TIME = 1000;
//    private static AtomicInteger i = new AtomicInteger();
    private static int i;

    public static void main(String[] args) {
        try {
            log("Spinning up the threads");
            long start = System.currentTimeMillis();
            Thread last = null;
            for (int i = 0; i < THRD_CNT; i++) {
                last  = Thread.startVirtualThread(createTask(i));
            }
            //log("i = " + i.get());
            last.join();
            log("i = " + i);
            log("Done! (" + (System.currentTimeMillis() - start) + "ms)");
        } catch (Throwable t) { /*IGN*/ }
    }

    private static Runnable createTask(int l) {
        return () -> {
            try { Thread.sleep(SLEEP_TIME); } catch (Throwable t) { /*ign*/ }
            if (l == 0) { log(Thread.currentThread().getName()); Thread.dumpStack(); }
            //i.incrementAndGet();
            i++;
        };
    }

    private static void log(String s) {
        System.out.println(s);
    }

}
