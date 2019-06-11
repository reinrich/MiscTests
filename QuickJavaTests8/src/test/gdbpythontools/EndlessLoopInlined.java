package test.gdbpythontools;

public class EndlessLoopInlined {

    private static volatile long incr;
    private static long totalSum;

    /**
     * @param args
     */
    public static void main(String[] args) {
        // warmup
        doTest(8);
        doTest(8);
        doTest(8);

        // endless loop
        System.out.println("Entering endless loop");
        doTest(1L<<60);
}

    public static void doTest(long innerLoopIterations) {
        for (int i = 0; i < 1<<16; i++) {
            inlined1(innerLoopIterations);
        }
    }

    public static void inlined1(long innerLoopIterations) {
        inlined2(innerLoopIterations);
    }

    private static void inlined2(long innerLoopIterations) {
        inlined3(innerLoopIterations);
    }

    private static void inlined3(long innerLoopIterations) {
        long sum = 0;
        while (innerLoopIterations-- > 0) {
            sum += incr;
        }
          
        totalSum = sum;
    }

}
