package test.gc;

// Tests parallel processing of large object arrays.
// See https://bugs.openjdk.java.net/browse/JDK-8151101

public class TestLargeObjectArray {

    private static final long MB = 1 << 20;
    private int arraySize;
    private int treeDepth;
    private int iterations;
    private LittleTree[] arrayOfSmallTrees;

    public static void main(String[] args) {
        TestLargeObjectArray test = new TestLargeObjectArray();
        test.parseCmdLine(args);
        test.buildLargeArrayOfSmallTrees();
        test.run();
    }

    private void run() {
        while (iterations-- > 0) {
            long start = System.currentTimeMillis();
            message("Calling System.gc()");
            System.gc();
            long end = System.currentTimeMillis();
            heapStats();
            message("Full GC duration: " + (end - start) + " ms");
        }
    }

    public void buildLargeArrayOfSmallTrees() {
        try {
            message("Building large array with " + arraySize + " elements of small trees with depth " + treeDepth);
            heapStats();
            arrayOfSmallTrees = new LittleTree[arraySize];
            for (int i = 0; i < arraySize; i++) {
                arrayOfSmallTrees[i] = LittleTree.build(treeDepth);
            }
            message("done");
            heapStats();
        } catch (OutOfMemoryError oom) {
            arrayOfSmallTrees = null;
            message("OutOfMemory: please reduce array size or tree depth");
            System.exit(1);
        }
    }

    private void heapStats() {
        long free = Runtime.getRuntime().freeMemory();
        long max = Runtime.getRuntime().maxMemory();
        float freePercentage = round((float) free / (float) max * 100);
        message("Heap stats: free:" + free / MB + " MB (" + freePercentage + " %)" + "  max:" + max / MB + " MB");
    }

    private float round(float f) {
        return Math.round(f * 10) / 10f;
    }

    private void parseCmdLine(String[] args) {
        if (args.length != 3) {
            message();
            message("usage: java " + TestLargeObjectArray.class.getName()
                    + " <test iterations, e.g. 20> <large array size, e.g. 50000> <small tree depth, e.g. 4>");
            message();
            System.exit(1);
        }
        int i = 0;
        iterations = Integer.parseInt(args[i++]);
        arraySize = Integer.parseInt(args[i++]);
        treeDepth = Integer.parseInt(args[i++]);
    }

    private void message(String msg) {
        System.out.println(msg);
    }

    private void message() {
        System.out.println();
    }
}

class LittleTree {

    private static final int ARITY = 6;
    @SuppressWarnings("unused")
    private LittleTree[] subtrees;

    public LittleTree(LittleTree[] trees) {
        this.subtrees = trees;
    }

    public static LittleTree build(int treeDepth) {
        if (treeDepth == 0) {
            return null;
        }

        // recursion
        LittleTree[] subtrees = new LittleTree[ARITY];
        for (int i = 0; i < ARITY; i++) {
            subtrees[i] = build(treeDepth - 1);
        }
        return new LittleTree(subtrees);
    }
}
