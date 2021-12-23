package test.gc;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import testlib.TestBase;

public class TestGCHistoryReferenceProcessing extends TestBase {

    private static final int  K = 1 <<10;
    private static final long M = 1L<<20;
    private static final long G = 1L<<30;

    private static final int  REFERENT_BYTES       =  1*K;
    private static final long REFERENT_BYTES_TOTAL = 500*M;
    private static final ReferenceQueue<? super byte[]> QUEUE = new ReferenceQueue<byte[]>();

    private static ArrayList<Reference<byte[]>> refs;
    private static ArrayList<byte[]> referents;

    private static ArrayList<FinalizableObject> finalizableRefs;

    enum ReferenceType {
        Soft,
        Weak,
        Final,
        Phantom,
        None
    }
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Error: unexpected number of commandline arguments: " + args.length);
            System.err.println();
            System.err.println("Usage: " + TestGCHistoryReferenceProcessing.class + " ReferenceType");
            System.err.println();
            System.err.println("   <ReferenceType> := ");
            for (ReferenceType tt : ReferenceType.values()) {
                System.err.println("      " + tt);
            }
            System.err.println();
            System.exit(1);
        }
        ReferenceType rt = ReferenceType.valueOf(args[0]);
        TestGCHistoryReferenceProcessing test = new TestGCHistoryReferenceProcessing();
        if (rt == ReferenceType.Final) {
            test.createFinalizableObjects();
        } else {
            test.createReferenceObjects(rt);
        }
        test.clearStrongReferences();
        test.triggerGC();
        if (rt == ReferenceType.Soft) {
            test.consumeAllMemory();
        }
    }

    private void createReferenceObjects(ReferenceType rt) {
        log("Creating " + rt + " reference objects");
        refs = new ArrayList<Reference<byte[]>>();
        referents = new ArrayList<byte[]>();
        for (long allocatedBytes = 0; allocatedBytes < REFERENT_BYTES_TOTAL; allocatedBytes += REFERENT_BYTES) {
            byte[] referent = new byte[REFERENT_BYTES];
            Reference<byte[]> reference = null;
            switch (rt) {
            case Soft:
                reference = new SoftReference<byte[]>(referent, QUEUE);
                break;
            case Weak:
                reference = new WeakReference<byte[]>(referent, QUEUE);
                break;
            case Phantom:
                reference = new PhantomReference<byte[]>(referent, QUEUE);
                break;

            default:
                break;
            }
            refs.add(reference);
            referents.add(referent);
        }
        log("Created " + refs.size() + " j.l.r.Reference instances");
    }

    static class FinalizableObject {
        public static volatile int counter;
        Reference<byte[]> reference;

        public FinalizableObject() {
            byte[] referent = new byte[REFERENT_BYTES];
            reference = new WeakReference<byte[]>(referent);//, QUEUE);
        }

        @Override
        protected void finalize() {
            counter++;
        }
    }
    private void createFinalizableObjects() {
        log("Creating finalizable objects");
        finalizableRefs = new ArrayList<FinalizableObject>();
        for (long allocatedBytes = 0; allocatedBytes < REFERENT_BYTES_TOTAL; allocatedBytes += REFERENT_BYTES) {
            FinalizableObject finalizable = new FinalizableObject();
            finalizableRefs.add(finalizable);
        }
        log("Created " + finalizableRefs.size() + " FinalizableObject instances");
    }

    private void clearStrongReferences() {
        log("Creating reference objects");
        if (referents != null) referents.clear();
        if (finalizableRefs != null) finalizableRefs.clear();
    }

    private void triggerGC() {
        log("Calling System.gc()");
        System.gc();
    }

    static class LinkedList {
        LinkedList l;
        public long[] array;
        public LinkedList(LinkedList l, int size) {
            this.array = size > 0 ? new long[size] : null;
            this.l = l;
        }
    }

    public LinkedList consumedMemory;

    public void consumeAllMemory() {
        log("consume all memory");
        int size = 128 * 1024 * 1024;
        while(true) {
            try {
                while(true) {
                    consumedMemory = new LinkedList(consumedMemory, size);
                }
            } catch(OutOfMemoryError oom) {
                if (size == 0) break;
            }
            size = size / 2;
        }
    }
}
