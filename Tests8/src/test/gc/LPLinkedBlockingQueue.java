package test.gc;

import java.util.concurrent.LinkedBlockingQueue;

import testlib.TestBase;

public class LPLinkedBlockingQueue extends TestBase implements LoadProducer {

    private int listLength;
    private LinkedBlockingQueue<PayLoad> queue;

    static class PayLoad {
        public long l;
    }

    @Override
    public void runInBackground(String args) {
        log("###### setup " + getClass().getName());
        try {
            int aLen = args.length();
            int scale = 1;
            if (args.charAt(aLen - 1) == 'k') {
                scale = 1000;
                aLen--;
            } else if (args.charAt(aLen - 1) == 'm') {
                scale = 1000 * 1000;
                aLen--;
            }
            listLength = scale * Integer.parseInt(args.substring(0, aLen));
        } catch (Exception e) {
            throw new Error(e);
        }

        String listLengthStr;
        if (listLength > 1000) {
            listLengthStr = String.valueOf(listLength / 1000) + "k";
        } else {
            listLengthStr = String.valueOf(listLength);
        }
        log("###### building queue with " + listLengthStr + " elements");
        queue = new LinkedBlockingQueue<PayLoad>();
        for (int i = 0; i < listLength; i++) {
            queue.add(new PayLoad());
        }
        log("###### setup " + getClass().getName() + " DONE");
    }
}
