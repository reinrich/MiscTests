package test.gc;

import java.util.concurrent.LinkedBlockingQueue;

import testlib.Tracing;

// 2 Threads put into a LinkedBlockingQueue and take from it.
// They block if the capacity or minimum occupancy is reached.
// A pause time between 2 operations in the same thread can be specified.
public class LPLinkedBlockingQueue implements Tracing, LoadProducer {

    private static final long STATS_INTERVAL_MS = 10000;
    private final int capacity;
    private final int minOcc;
    private final int pauseMs;
    private final LinkedBlockingQueue<PayLoad> queue;
    public static Thread producerThread;

    static class PayLoad {
        public long l;
    }

    public LPLinkedBlockingQueue() {
        String usage = "Usage: " + getClass().getName() + " <capacity, e.g. 4m>[,<pause ms, 0 dflt>[,<min. occupancy %, e.g. 90>]]";
        System.err.println();
        System.err.println(usage);
        System.err.println();
        System.err.println("Example: " + getClass().getName() + " 4m,10,90");
        System.err.println();
        System.err.println();
        throw new Error(usage);
    }

    public LPLinkedBlockingQueue(String capacity, String pauseMs, String minOccPerc) {
        log();
        log0("setup " + getClass().getName());
        try {
            int capaStrLen = capacity.length();
            int scale = 1;
            if (capacity.charAt(capaStrLen - 1) == 'k') {
                scale = 1000;
                capaStrLen--;
            } else if (capacity.charAt(capaStrLen - 1) == 'm') {
                scale = 1000 * 1000;
                capaStrLen--;
            }
            this.capacity = scale * Integer.parseInt(capacity.substring(0, capaStrLen));
            this.minOcc = (int) (this.capacity * Float.valueOf(minOccPerc) / 100f);
            this.pauseMs = Integer.parseInt(pauseMs);
            this.queue = new LinkedBlockingQueue<PayLoad>(this.capacity);
        } catch (Exception e) {
            throw new Error(e);
        }
        logIncInd();
        log(humanReadable(this.capacity) + " elements capacity (producer blocks when reached)");
        log(humanReadable(this.minOcc) + " elements min. occupancy (consumer blocks when reached)");
        log(pauseMs + " ms pause per operation");
        logDecInd();
        log();
    }

    // argString: <capacity>,<min. occu. perc.>,<alloc pause ms>
    // Capacity can have suffix m or k.
    public LPLinkedBlockingQueue(String argString) {
        this(argString.split(","));
    }

    public LPLinkedBlockingQueue(String[] args) {
        this(args[0] /*capacity*/,
             args.length > 1 ? args[1] : "0" /*pauseMs*/,
             args.length > 2 ? args[2] : "90" /*minOccPerc*/);
    }

    public class Consumer implements Runnable {
        @Override
        public void run() {
            log0("Started Consumer");
            while (true) {
                take();
                pause();
            }
        }

        private void take() {
            try {
                if (queue.size() > minOcc)
                    queue.take();
            } catch (InterruptedException e) { /* ignore */ }
        }
    }

    @Override
    public void run() {
        log0("Started Producer");

        // Fill queue
        while (queue.size() < capacity) {
            put(new PayLoad());
        }

        // Start consumer thread
        Thread consumer = new Thread(new Consumer(), "LPLinkedBlockingQueueThread Consumer");
        consumer.start();

        // Continuously add more items in queue
        long lastStatsMillis = System.currentTimeMillis();
        int opsUntilStatsCheck = 100;
        while (true) {
            if (opsUntilStatsCheck-- < 0) {
                opsUntilStatsCheck = 100;
                long now = System.currentTimeMillis();
                if ((now - lastStatsMillis) > STATS_INTERVAL_MS) {
                    lastStatsMillis = now;
                    printStats();
                }
            }
            put(new PayLoad());
            pause();
        }
    }

    /** Add new item to queue. Block if capacity exhausted */
    private void put(PayLoad payLoad) {
        try {
            queue.put(payLoad);
        } catch (InterruptedException e) { /* ignore */ }
    }

    private void pause() {
        if (pauseMs > 0) {
            try {
                Thread.sleep(pauseMs);
            } catch (InterruptedException e) { /* ignore */ }
        }
    }

    @Override
    public void runInBackground() {
        producerThread = new Thread(this, "LPLinkedBlockingQueueThread Producer");
        producerThread.start();
    }

    private void printStats() {
        log0("Queue size: " + humanReadable(queue.size()));
    }
}
