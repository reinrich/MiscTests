package test.gc;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import testlib.Tracing;

// TODO: add description
public class LPWeakBehindFinalRef implements Tracing, LoadProducer {

    private final int pauseMs;
    private final int finalRefsCount;
    private final WeakBehindFinal[] finalRefs;
    public static Thread producerThread;

    static class PayLoad {
        public long l;
    }

    public LPWeakBehindFinalRef() {
        String usage = "Usage: " + getClass().getName() + " <final refs count, e.g. 4m>,<pause ms>";
        System.err.println();
        System.err.println(usage);
        System.err.println();
        System.err.println("Example: " + getClass().getName() + " 1k,100");
        System.err.println();
        System.err.println();
        throw new Error(usage);
    }

    public LPWeakBehindFinalRef(String finalRefsCount, String pauseMs) {
        log();
        log0("setup " + getClass().getName());
        try {
            int countStrLength = finalRefsCount.length();
            int scale = 1;
            if (finalRefsCount.charAt(countStrLength - 1) == 'k') {
                scale = 1000;
                countStrLength--;
            } else if (finalRefsCount.charAt(countStrLength - 1) == 'm') {
                scale = 1000 * 1000;
                countStrLength--;
            }
            this.finalRefsCount = scale * Integer.parseInt(finalRefsCount.substring(0, countStrLength));
            this.pauseMs = Integer.parseInt(pauseMs);
            this.finalRefs = new WeakBehindFinal[this.finalRefsCount];
        } catch (Exception e) {
            throw new Error(e);
        }
        logIncInd();
        log(humanReadable(this.finalRefsCount) + " elements capacity (producer blocks when reached)");
        log(pauseMs + " ms pause per operation");
        logDecInd();
        log();
    }

    // argString: <final refs count>,<pause ms>
    // Count can have suffix m or k.
    public LPWeakBehindFinalRef(String argString) {
        this(argString.split(","));
    }

    public LPWeakBehindFinalRef(String[] args) {
        this(args[0] /*capacity*/, args[1] /*pauseMs*/);
    }

    static class WeakBehindFinal implements Tracing {

        static final SimpleDateFormat FMT = new SimpleDateFormat("HH:mm:s.SSS");

        private WeakReference<String> allocatedWeak;

        private String allocatedStrong;

        WeakBehindFinal() {
             Date now = new Date(System.currentTimeMillis());
             int hash = hashCode();
             allocatedStrong = "allocated(" + hash + "): " + FMT.format(now);
             allocatedWeak = new WeakReference<String>(allocatedStrong);
        }

        @Override
        protected void finalize() throws Throwable {
            Date now = new Date(System.currentTimeMillis());
            int hash = hashCode();
            String deallocated = "deallocated(" + hash + "): " + FMT.format(now);
            log(allocatedWeak.get() + " | " + deallocated);
        }

        public void removeStrongRef() {
            allocatedStrong = null;
        }
    }

    @Override
    public void run() {
        log0("Started Producer");

        for (int i = finalRefsCount - 1; i >= 0; i--) {
            finalRefs[i] = new WeakBehindFinal();
        }

        int idx = 0;
        while (true) {
            finalRefs[idx].removeStrongRef();
            finalRefs[idx] = new WeakBehindFinal();
            idx++;
            if (idx == finalRefsCount) {
                idx = 0;
            }
            pause();
        }
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
        producerThread = new Thread(this, getClass().getName() + " Producer");
        producerThread.start();
    }
}
