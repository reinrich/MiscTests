package test.gc;

public class TestPromotionFailure {
    public Node nodesWithHashes;

    public static void main(String[] args) {
        TestPromotionFailure test = new TestPromotionFailure();

        // warmup: let all new Objects die. GC thinks nothing gets ever promoted
        System.out.println("WARMUP");
        test.run(true);
        System.out.println("WARMUP DONE");

        try {Thread.sleep(1000);} catch (InterruptedException e) { /* ign */ }
        // test: all objects survive and get promoted
        System.out.println("TESTRUN");
        test.run(false);
        System.out.println("TESTRUN DONE");
    }

    public static class Node {
        public Node next;
        public byte[] bytes;
        
        public Node(Node next, int byteSize) {
            this.next = next;
            this.bytes = new byte[byteSize];
        }
    }


    public void run(boolean warmup) {
        // reset
        nodesWithHashes = null;
        long checksum = 0L;
        int M = 1<<20;
        long warmupBytesToAllocate = 1024 * M;
        long bytesAllocated = 0;

        // Allocate!
        //
        // Warmup: allocate a lot and let each new node die immediately. GC stats will prognose that
        // nothing ever gets promoted.
        //
        // !Warmup: allocate and keep all new nodes alive until OOM. Before OOM we will get a promotion failure,
        // where markwords with hashcodes must be preserved. We collect all hashcodes into a checksum which
        // is verified afterwards.
        try {
            for (; !warmup || bytesAllocated < warmupBytesToAllocate; bytesAllocated += M) {
                nodesWithHashes = new Node(warmup ? null : nodesWithHashes, M);
                checksum += nodesWithHashes.hashCode();
            }
        } catch (OutOfMemoryError oom) { /* ignored */ }
        System.out.println("Allocated " + bytesAllocated / M + " MB");
        if (!warmup) {
            // now we check if markwords holding the hashcode were preserved during the promotion failure
            Node cur = nodesWithHashes;
            long checksum2 = 0L;
            while (cur != null) {
                checksum2 += cur.hashCode();
                cur = cur.next;
            }
            if (checksum != checksum2) {
                throw new Error();
            }
        }
    }
}
