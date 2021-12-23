package test.ref;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.LinkedList;

import test.JavaHeap;
import test.TestBase;

//
// Allocates reference types and checks how many System.gc() it takes to get rid of them
//


public class TestWeakPhantomFinalizer extends TestBase {
    
    public static final boolean EXPLICITELY_CEAR_REFERENCE = false;

    private JavaHeap heap;

    enum REF_TYPE {
        FINALIZER,
        WEAK_REF,
        WEAK_REF_WITH_QUEUE,
        PHANTOM_REF
    }

    public static void main(String[] args) {
        try {
//            REF_TYPE[] ref_types = REF_TYPE.values();
            REF_TYPE[] ref_types = {REF_TYPE.PHANTOM_REF};
            TestWeakPhantomFinalizer test = new TestWeakPhantomFinalizer();
            for (int i = 0; i < ref_types.length; i++) {
                test.run(ref_types[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    public TestWeakPhantomFinalizer() {
        heap = new JavaHeap(this);
    }

    public void run(REF_TYPE ref_type) throws Exception {
        log("### Testing " + ref_type);
        logIncInd();

        int gcCount = 1;
        System.gc();
        long allocatedBefore = heap.allocatedBytes();
        log(allocatedBefore/K + "K allocated before test");
        RefHolder ref = new RefHolder(ref_type);

        long allocatedNow = heap.allocatedBytes();
        log(allocatedNow/K + "K allocated now");
        do {
            log();
            log(gcCount + ". call to System.gc()");
            gcCount++;
            System.gc();
            allocatedNow = heap.allocatedBytes();
            log(allocatedNow/K + "K allocated now");
            log("sleeping...");
            ref.checkRef();
            Thread.sleep(100);
        } while(allocatedNow > allocatedBefore + 10*K);
        logDecInd();
        log();
    }

    private class RefHolder {

        private REF_TYPE refType;
        private Reference<Object> ref;
        private ReferenceQueue<Object> refQ;
        private boolean alreadyPrinted;

        public RefHolder(REF_TYPE refType) {
            this.refType = refType;
            Object payLoad = createPayload(10*M);

            switch (refType) {
            case FINALIZER:
                new ClassWithFinalizer(payLoad);
                break;
            case WEAK_REF:
                ref = new WeakReference<Object>(payLoad);
                break;
            case WEAK_REF_WITH_QUEUE:
                refQ = new ReferenceQueue<>();
                ref = new WeakReference<>(payLoad, refQ);
                break;
            case PHANTOM_REF:
                refQ = new ReferenceQueue<>();
                ref = new PhantomReference<Object>(payLoad, refQ);
                break;
            default:
                TODO();
            }
            if (ref != null) {
                log("new Reference object: " + ref);
            }
        }

        private Object createPayload(int size) {
            final int chunk_size = 1*K;
            LinkedList<byte[]> list = new LinkedList<>();
            while(size > 0) {
                list.add(new byte[chunk_size]);
                size -= chunk_size;
            }
            return new Payload(list);
        }

        public void checkRef() throws Exception {
            switch (refType) {
            case FINALIZER:
                break;
            case WEAK_REF_WITH_QUEUE:
            case PHANTOM_REF:
                for(Reference<? extends Object> x; (x = refQ.poll()) != null ; ) {
                    log("refQ -> x = " + x);
                    log("x.get() -> " + x.get());
                    log("x.referent (reflective) -> " + getReferentUsingReflection(x));
                    if (EXPLICITELY_CEAR_REFERENCE) {
                        logIncInd();
                        log("explicitly clearing the referent");
                        logDecInd();
                        x.clear();
                    }
                }
            case WEAK_REF:
                if (ref.get() == null && !alreadyPrinted) {
                    alreadyPrinted = true;
                    log("ref.get() -> null");
                }
                break;
            default:
                TODO();
            }
        }

        private Object getReferentUsingReflection(Reference<? extends Object> x) throws Exception {
            Field f = Reference.class.getDeclaredField("referent");
            f.setAccessible(true);
            return f.get(x);
        }
    }

    private class ClassWithFinalizer {

        @SuppressWarnings("unused")
        private Object payLoad;

        public ClassWithFinalizer(Object payLoad) {
            this.payLoad = payLoad;
        }

        protected void finalize() throws Throwable {
            log("Finalizer was called");
        }
    }

    // Purpose: overwrite toString
    private static class Payload {

        private LinkedList<byte[]> payload;

        public Payload(LinkedList<byte[]> list) {
            this.payload = list;
        }
        
        public String toString() {
            return "Payload: Linked List with " + payload.size() + " Elements of byte["+ payload.getFirst().length +"]";
        }
    }
}
