package test.misc;

public class TEST_PROB {

    public static class Case0 {

        public static void main(String[] args_ignored) {
            try {
                int[] src = new int[10];

                // Warm-up
                for (long i = 1L<<20; i > 0; i--) {
                    comp_testMethod_dontinline(src, true, true, 10);
                    comp_testMethod_dontinline(src, true, false, 10);
                    comp_testMethod_dontinline(src, false, false, 10);
                }
                src[9] = 42;
                final int v = comp_testMethod_dontinline(src, true, true, 1);
                if (v != 42) {
                    throw new RuntimeException("Incorrect return value " + v);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                System.exit(1);
            }
        }

        public static int comp_testMethod_dontinline(int[] src, boolean flag1, boolean flag2, int stop) {
            int v = 0;
            int j = 1;
            for (; j < 10; j++) {
                for (int i = 0; i < 2; i++) {

                }
            }
            int[] dst = new int[10];
            for (int i = 0; i < stop; i ++) {
                if (flag1) {
                    System.arraycopy(src, 0, dst, 0, j);
                    v = dst[9];
                    if (flag2) {
                        src[9] = 0x42;
                    }
                }
            }
            return v;
        }
    }

    // Clone statt ArrayCopy
    public static class Case1 {

        public static void main(String[] args_ignored) {
            try {
                int[] src = new int[10];

                // Warm-up
                for (long i = 1L<<20; i > 0; i--) {
                    comp_testMethod_dontinline(src, true, true, 10);
                    comp_testMethod_dontinline(src, true, false, 10);
                    comp_testMethod_dontinline(src, false, false, 10);
                }
                src[9] = 42;
                final int v = comp_testMethod_dontinline(src, true, true, 1);
                if (v != 42) {
                    throw new RuntimeException("Incorrect return value " + v);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                System.exit(1);
            }
        }

        public static int comp_testMethod_dontinline(int[] src, boolean flag1, boolean flag2, int stop) {
            int v = 0;
            int j = 1;
            for (; j < 10; j++) {
                for (int i = 0; i < 2; i++) {

                }
            }
            int[] dst; ////////////////////// = new int[10];
            for (int i = 0; i < stop; i ++) {
                if (flag1) {
                    dst = src.clone();                    // Use clone instead of arraycopy
                    v = dst[9];
                    if (flag2) {
                        src[9] = 0x42;
                    }
                }
            }
            return v;
        }
    }

    // Clone statt ArrayCopy; eliminated loops
    public static class Case2 {

        public static void main(String[] args_ignored) {
            try {
                int[] src = new int[10];

                // Warm-up
                for (long i = 1L<<20; i > 0; i--) {
                    comp_testMethod_dontinline(src, true, true, 10);
                    comp_testMethod_dontinline(src, true, false, 10);
                    comp_testMethod_dontinline(src, false, false, 10);
                }
                src[9] = 42;
                final int v = comp_testMethod_dontinline(src, true, true, 1);
                if (v != 42) {
                    throw new RuntimeException("Incorrect return value " + v);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                System.exit(1);
            }
        }

        public static int comp_testMethod_dontinline(int[] src, boolean flag1, boolean flag2, int stop) {
            int v = 0;
            int[] dst; ////////////////////// = new int[10];
            for (int i = 0; i < stop; i ++) {
                dst = src.clone();                    // Use clone instead of arraycopy
                v = dst[9];
                if (flag2) {
                    src[9] = 0x42;
                }
            }
            return v;
        }
    }

    // Simple Loop mit Phi: TODO
    public static class Case3 {

        public static void main(String[] args_ignored) {
            try {
                int[] src = new int[10];

                // Warm-up
                for (long i = 1L<<20; i > 0; i--) {
//                    comp_testMethod_dontinline(42);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                System.exit(1);
            }
        }

        public static int comp_testMethod_dontinline(int vIn, int stop) {
            int v = 0;
            for (int i = 0; i < stop; i++) {
                
            }
            return v;
        }
    }
 
    // Keep both memory and control of the original cannot work as
    // the optimized load would return the value stored by S below.
    public static class Case4 {
        public static void main(String[] args_ignored) {
            try {
                int[] a = {41, 41, 41, 41, 41, };

                // Warm-up
                for (int i = 50_000; i > 0; i--) {
                    a[4] = 41;
                    int res = testMethod_dontinline(a);
                    if (res != 41) {
                        throw new RuntimeException("Unxpected return value " + res);
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
                System.exit(1);
            }
        }

        public static int testMethod_dontinline(int[] src) {
            int[] clone = src.clone();
            src[4] = 42;                         // Store S
            return clone[4];
        }
    }
}
