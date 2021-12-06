package test.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import testlib.TestBase;
import testlib.gc.GCLoadProducer;
import testlib.gc.TestGCOptions;
import test.gc.TestGCWithOpts;

public class TestWeakBehindSoftReference extends TestBase {

    public static final boolean TRIGGER_GC_BY_CALLING_SYSTEM_GC = false;
    public static final long DELAY_BEFORE_DROPPING_STRONG_REF_TO_WEAK_REFERENT_MS = 20*1000;


    public static void main(String[] args) {
        new TestWeakBehindSoftReference().run();
    }

    private ReferenceQueue<Object> referenceQueue;;
    private Object weakReferent;
    private SoftReference<WeakReference<Object>> softReference;

    public void run() {
        try {
            GCLoadProducer gcLoad = null;
//            SoftReference<WeakReference<Object>> softReference;
            softReference = createReferences();
            if (!TRIGGER_GC_BY_CALLING_SYSTEM_GC) {
                // keep weakReferent strong reachable until we are sure that it was promoted into the
                // old generation.
                weakReferent = softReference.get().get();

                msg("### Starting thread that produces GC load");
                TestGCOptions opts = new TestGCOptions();
                opts.busy_wait_iterations = 1<<18;
                gcLoad = new GCLoadProducer(opts);
                gcLoad.runInBackground();
                gcLoad.waitUntilReady2go();
            }
            checkIfWeakReferentGetsGCed();
            if (gcLoad != null) {
                gcLoad.stopAllocating();
                gcLoad.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void checkIfWeakReferentGetsGCed() throws InterruptedException {
        int referenceCount = 1;
        long timeStampStart = System.currentTimeMillis();

        do {
            if (TRIGGER_GC_BY_CALLING_SYSTEM_GC) {
                msg("System.gc()");
                System.gc();
            } else if (weakReferent != null &&
                    System.currentTimeMillis() - timeStampStart > DELAY_BEFORE_DROPPING_STRONG_REF_TO_WEAK_REFERENT_MS) {
                msg("### Clearing strong ref to weakReferent");
                weakReferent = null;
            }
            Reference<? extends Object> theReference = referenceQueue.poll();
            if (theReference != null) {
                msg("### from referenceQueue: " + theReference);
                referenceCount--;
            }
            Thread.sleep(500);
        } while(referenceCount > 0);
    }

    private SoftReference<WeakReference<Object>> createReferences() {
        referenceQueue = new ReferenceQueue<>();
        Object weakReferent = new Object();
        WeakReference<Object> weakReference = new WeakReference<Object>(weakReferent, referenceQueue);
        SoftReference<WeakReference<Object>> softReference = new SoftReference<WeakReference<Object>>(weakReference, referenceQueue);

        msg(weakReference);
        msg(weakReferent);
        msg(softReference);

        return softReference;
    }
}
