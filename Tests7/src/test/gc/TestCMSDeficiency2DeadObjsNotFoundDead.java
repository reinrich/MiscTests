package test.gc;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import testlib.TestBase;

public class TestCMSDeficiency2DeadObjsNotFoundDead extends TestBase {

    public static final boolean TRIGGER_GC_BY_CALLING_SYSTEM_GC = false;
    public static final long DELAY_BEFORE_DROPPING_STRONG_REF_TO_WEAK_REFERENT_MS = 20*1000;


    public static void main(String[] args) {
        new TestCMSDeficiency2DeadObjsNotFoundDead().run();
    }

    private ReferenceQueue<Object> referenceQueue;
    private WeakReference<Old> weakReferenceToOld;
    public volatile Old old; // null most of the time

    public void run() {
        try {
            GCLoadProducer gcLoad = null;
            createReferences();
            old = weakReferenceToOld.get();

            log("### Starting thread that produces GC load");
            TestGCOptions opts = TestGCOptions.getInstance();
            opts.busy_wait_iterations = 1<<14;
            gcLoad = new GCLoadProducer(opts);
            gcLoad.runInBackground();
            gcLoad.waitUntilReady2go();

            Thread.sleep(10000); // wait until old gets promoted
            renewYoungAndPollRefQueue();
            gcLoad.stopAllocating();
            gcLoad.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void renewYoungAndPollRefQueue() throws InterruptedException {
        boolean oldDied = false;
        do {
            Reference<? extends Object> theReference = referenceQueue.poll();
            if (theReference != null) {
                log("### old object died!!!: " + theReference);
                oldDied = true;
            } else {
                log("### renewing new object");
                old = weakReferenceToOld.get();
                if (old == null) {
                    log("### old object died!!!: " + theReference);
                    oldDied = true;
                } else {
                    old.young = new Young(old);
                    old = null;
                }
            }
            Thread.sleep(200);
        } while(!oldDied);
    }

    private void createReferences() {
        referenceQueue = new ReferenceQueue<>();
        Old oldWeakReferent = new Old();
        Young young = new Young(oldWeakReferent);
        oldWeakReferent.young = young;
        weakReferenceToOld = new WeakReference<>(oldWeakReferent, referenceQueue);

        log(weakReferenceToOld);
    }

    public static class Old {
        public Young young;
    }

    public static class Young {
        public Object old;
        public Young(Object old) {
            this.old = old;
        }
    }
}
