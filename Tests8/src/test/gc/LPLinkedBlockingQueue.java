package test.gc;

import java.util.concurrent.LinkedBlockingQueue;

import testlib.TestBase;

public class LPLinkedBlockingQueue extends TestBase implements LoadProducer {

    private int listLength;
    private int pauseMs;
    private LinkedBlockingQueue<PayLoad> queue;
    public static Thread producerThread;

    static class PayLoad {
        public long l;
    }

    // argString: <length>:<alloc pause ms>
    public LPLinkedBlockingQueue(String argString) {
        log0("setup " + getClass().getName());
        try {
            String[] aArgs = argString.split(":");
            String lenStr = aArgs[0];
            String pauseMs = aArgs.length > 1 ? aArgs[1] : "0";
            int aLen = lenStr.length();
            int scale = 1;
            if (lenStr.charAt(aLen - 1) == 'k') {
                scale = 1000;
                aLen--;
            } else if (lenStr.charAt(aLen - 1) == 'm') {
                scale = 1000 * 1000;
                aLen--;
            }
            this.listLength = scale * Integer.parseInt(lenStr.substring(0, aLen));
            this.pauseMs = Integer.parseInt(pauseMs);
        } catch (Exception e) {
            throw new Error(e);
        }
        String listLengthStr;
        if (listLength > 1000) {
            listLengthStr = String.valueOf(listLength / 1000) + "k";
        } else {
            listLengthStr = String.valueOf(listLength);
        }
        log("    " + listLengthStr + " elements");
        log("    " + pauseMs + " ms pause per iteration");
    }

    public class Consumer implements Runnable {
        @Override
        public void run() {
            log0("Started Consumer");
            LinkedBlockingQueue<PayLoad> q = queue;
            while (true) {
                q.poll();
                if (pauseMs > 0) {
                    try {
                        Thread.sleep(pauseMs);
                    } catch (InterruptedException e) { /* ignore */ }
                }
            }
        }
    }

    @Override
    public void run() {
        log0("Started Producer");
        queue = new LinkedBlockingQueue<PayLoad>();

        // Fill queue
        while (queue.size() < listLength) {
            queue.add(new PayLoad());
        }

        // Start consumer thread
        Thread consumer = new Thread(new Consumer(), "LPLinkedBlockingQueueThread Consumer");
        consumer.start();

        // Continuously add more items in queue
        while (true) {
            queue.add(new PayLoad());
            if (pauseMs > 0) {
                try {
                    Thread.sleep(pauseMs);
                } catch (InterruptedException e) { /* ignore */ }
            }
        }
    }

    @Override
    public void runInBackground() {
        producerThread = new Thread(this, "LPLinkedBlockingQueueThread Producer");
        producerThread.start();
    }
}
