package test.loom;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import java.util.concurrent.StructuredExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

public class TestDelayedIncrementWithExecutor {

    private static final long THRD_CNT = 1000 * 1000;
    private static final long SLEEP_TIME = 1000;
//    private static AtomicInteger i = new AtomicInteger();
    private static int i;

    public static void main(String[] args) {
        ThreadFactory fact =
                (args.length > 0 && args[0].equals("virtual"))
                ? Thread.ofVirtual().name("Test Delayed Increment #", 0).factory()
                        : Thread.ofPlatform().name("Test Delayed Increment #", 0).factory();
//        StructuredExecutor.open("IncTestExecutor", fact);

        log("Spinning up the threads");
        long start = System.currentTimeMillis();
        try (ExecutorService executor = Executors.newThreadPerTaskExecutor(fact)) {
            LongStream.range(0, THRD_CNT).forEach((long l) -> executor.submit(createTask(l)));
        }
        //log("i = " + i.get());
        log("i = " + i);
        log("Done! (" + (System.currentTimeMillis() - start) + "ms)");
    }

    private static Runnable createTask(long l) {
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
