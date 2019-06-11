package test.misc;

public class TEST_OPT_SUCC {

    public static Object escape1;
    public static Object escape2;

    public static void main(String[] args_ignored) {
        try {
            for (int i = 50_000; i > 0; i--) {
                testMethod_dontinline();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static final Integer[] SRC = new Integer[4096];
    public static void testMethod_dontinline() throws Exception {
        Object[] clone = SRC.clone();
        // Load L below is executed speculatively at this point from src without range check.
        // The result is put into the OopMap of the allocation in the next line.
        // If src.length is 0 then the loaded value is no heap reference and GC crashes.
        escape1 = new Object();
        if (SRC.length > 4) {
            escape2 = clone[4]; // Load L
        }
    }
}