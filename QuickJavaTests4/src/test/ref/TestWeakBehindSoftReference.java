package test.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import test.TestBase;

public class TestWeakBehindSoftReference extends TestBase {

    public static void main(String[] args) {
        new TestWeakBehindSoftReference().run();
    }

    private ReferenceQueue referenceQueue;

    private void run() {
        try {
            SoftReference softReference = createReferences();
            checkIfWeakReferentGetsGCed();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void checkIfWeakReferentGetsGCed() throws InterruptedException {
        int referenceCount = 1;
        trcMsg();
        trcMsg("### Triggering GCs to see if the WeakReference is cleared.");
        trcIncInd();

        do {
            trcMsg("System.gc()");
            System.gc();
            Reference theReference = referenceQueue.poll();
            if (theReference != null) {
                trcMsg("from referenceQueue: " + theReference);
                referenceCount--;
            }
            Thread.sleep(500);
        } while(referenceCount > 0);
    }

    private SoftReference createReferences() {
        trcMsg("### Creating SoftReference with WeakReference as referent");
        referenceQueue = new ReferenceQueue();
        Object weakReferent = new Object();
        WeakReference weakReference = new WeakReference(weakReferent, referenceQueue);
        SoftReference softReference = new SoftReference(weakReference, referenceQueue);
        
        trcMsg(weakReference);
        trcMsg(weakReferent);
        trcMsg(softReference);
        
        return softReference;
    }
}
