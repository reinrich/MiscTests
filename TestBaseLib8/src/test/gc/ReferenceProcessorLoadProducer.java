package test.gc;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;

import testlib.TestBase;
import testlib.gc.ReferenceProcessorLoadProducerOptions;

//
// Produce load for the reference processor
//

public class ReferenceProcessorLoadProducer extends TestBase implements Runnable {

    private ReferenceProcessorLoadProducerOptions opts;
    private Thread backGroundThread;
    private boolean ready2go;

    public static interface KeepAlive<E> {
        public boolean add(E e);
        public int size();
        public void clearAt(int idx);
        public void setAt(int idxCleared, E e);
    }

    // NOTE: referents will be traced before weak references if using ArrayListKeepAlive
    @SuppressWarnings("serial")
    public static class ArrayListKeepAlive<E> extends ArrayList<E> implements KeepAlive<E> {
        @Override
        public boolean add(E e) {
            return super.add(e);
        }

        @Override
        public void clearAt(int idx) {
            set(idx, null);
        }

        @Override
        public void setAt(int idx, E e) {
            set(idx, e);
        }
    }
    public static class SimpleLinkedList<E> implements KeepAlive<E> {
        public static class SimpleLinedListNode {
            public Object element;
            public SimpleLinedListNode next;

            public SimpleLinedListNode(Object element, SimpleLinedListNode next) {
                this.element = element;
                this.next = next;
            }
        }

        public SimpleLinedListNode head;
        public SimpleLinedListNode tail;
        private int count;

        public boolean add(E element) {
            SimpleLinedListNode newNode = new SimpleLinedListNode(element, null);
            if (tail == null) {
                head = newNode;
            } else {
                tail.next = newNode;
            }
            tail = newNode;
            count++;
            return true;
        }

        @Override
        public int size() {
            return count;
        }

        @Override
        public void clearAt(int idx) {
            setAt(idx, null);
        }

        @Override
        public void setAt(int idx, E e) {
            if (idx >= 0 && idx < count) {
                SimpleLinedListNode cur = head;
                long curIdx = 0;
                while (idx != curIdx++) {
                    cur = cur.next;
                }
                cur.element = e;
            } else {
                throw new IndexOutOfBoundsException("Index " + idx + " is out of bounds.");
            }
        }
    }

    private KeepAlive<SoftReference<Object>> softRefs;
    private KeepAlive<Object> referents;
    private ReferenceQueue<Object> queue;

    public static class SoftReferenceWithId<T> extends SoftReference<T> {
        public int id;

        public SoftReferenceWithId(int id, T referent) {
            super(referent);
            this.id = id;
        }

        public SoftReferenceWithId(int id, T referent, ReferenceQueue<T> queue) {
            super(referent, queue);
            this.id = id;
        }
    }
    public ReferenceProcessorLoadProducer(ReferenceProcessorLoadProducerOptions opts) {
        super(opts.trc_level);
        this.opts = opts;
    }

    @Override
    public void run() {
        try {
            setUp();
            signalReady2Go();
            Thread.sleep(30000);
            switch (opts.testType) {
            case REF_FEW_SOFTREFERENCES_STRONG_REACHABLE:
                msg("###### Drop referents keepalive");
                referents = null;
                Thread.sleep(30000);
                msg("###### Terminated");
                break;
            case REF_LOW_SOFTREFERENCES_TURNOVER:
                dropStrongRefsAndReplaceClearedSoftReferences();
            default:
                msg("Unhandled RP test type: " + opts.testType);
                Thread.dumpStack();
                System.exit(1);
                break;
            }
        } catch (Exception e) {
            signalReady2Go(); // maybe somebody is waiting
        }
    }

    public void runInBackground() {
        backGroundThread = new Thread(this, "ReferenceProcessorLoadProducer");
        backGroundThread.start();
    }

    private void setUp() throws Exception {
        msg("###### Set-up");
        createKeepAlive();
        msgIncInd();
        queue = new ReferenceQueue<Object>();
        for (int i = 0; i < opts.soft_refs_count; i++) {
            Object referent = new Object();
            SoftReference<Object> reference = new SoftReferenceWithId<Object>(i, referent, queue);
            referents.add(referent);
            softRefs.add(reference);
        }
        msgDecInd();
        msg("###### Set-up Done");
    }

    private void createKeepAlive() {
        switch (opts.testType) {
        case REF_FEW_SOFTREFERENCES_STRONG_REACHABLE:
            referents = new SimpleLinkedList<Object>();
            softRefs  = new SimpleLinkedList<SoftReference<Object>>();
            break;
        case REF_LOW_SOFTREFERENCES_TURNOVER:
            referents = new ArrayListKeepAlive<Object>();
            softRefs  = new ArrayListKeepAlive<SoftReference<Object>>();
            break;
        default:
            msg("Unhandled RP test type: " + opts.testType);
            Thread.dumpStack();
            System.exit(1);
            break;
        }
    }

    private void dropStrongRefsAndReplaceClearedSoftReferences() {
        int idxStrongRef = 0;
        while (true) {
            // Drop strong references to a batch of soft reference referents
            for(int i = opts.soft_refs_clearing_batch_size; i > 0; idxStrongRef++, i--) {
                if (idxStrongRef >= referents.size()) {
                    idxStrongRef = 0;
                }
                msg(4, "Clearing strong reference at index " + idxStrongRef);
                referents.clearAt(idxStrongRef);
            }
            // Wait for notification about cleared soft references and create new
            // SoftReferences
            for(int i = opts.soft_refs_clearing_batch_size; i > 0; i--) {
                SoftReferenceWithId<Object> reference = null;
                try {
                    reference = (SoftReferenceWithId<Object>) queue.remove();
                    int idxCleared = reference.id;
                    msg(4, "Soft reference at index " + idxCleared + " was cleared. Recreate!");
                    Object referent = new Object();
                    reference = new SoftReferenceWithId<Object>(idxCleared, referent, queue);
                    referents.setAt(idxCleared, referent);
                    softRefs.setAt(idxCleared, reference);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void waitUntilReady2go() {
        while (!ready2go) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                // ignored
            }
        }
    }

    private synchronized void signalReady2Go() {
        ready2go = true;
        this.notifyAll();
    }

    public void join() throws InterruptedException {
        if (backGroundThread != null) {
            backGroundThread.join();
        }
    }

    @Override
    public void msg(Object msg) {
        super.msg("[" + getClass().getCanonicalName() + "]" + msg);
    }


    @Override
    public void msg(int lvl, Object msg) {
        super.msg(lvl, "[" + getClass().getCanonicalName() + "]" + msg);
    }
}
