package test.gc;

import java.util.concurrent.LinkedBlockingQueue;

import testlib.TestBase;

public class LPLinkedBlockingQueue extends TestBase implements LoadProducer {

    private int listLength;
    private int pauseMs;
    private LinkedBlockingQueue<PayLoad> queue;
    public static Thread daemon;

    static class PayLoad {
        public long l;
    }

    // argString: <length>:<alloc pause ms>
    public LPLinkedBlockingQueue(String argString) {
        log("###### setup " + getClass().getName());
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

    @Override
    public void run() {
        log("###### Starting " + getClass().getName());
        queue = new LinkedBlockingQueue<PayLoad>();

        while (true) {
            queue.add(new PayLoad());
            if (queue.size() < listLength) continue;
            queue.poll();
            if (pauseMs > 0) {
                try {
                    Thread.sleep(pauseMs);
                } catch (InterruptedException e) { /* ignore */ }
            }
        }
    }

    @Override
    public void runInBackground() {
        daemon = new Thread(this, "LPLinkedBlockingQueueThread");
        daemon.start();
    }
}
