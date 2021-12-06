package test.arraycopy;

public class TestObjectCopy_aligned {

    private static final int LENGTH = 30000;

    static Object[] src = new Object[LENGTH];
    static Object[] dst = new Object[LENGTH];
    
    public static void main(String[] args) {
        try {
            long iterations = 0;
            iterations = Long.parseLong(args[0]);

            long start;
            long end;
            for (int i = 0; i < 5; i++) {
                start = System.currentTimeMillis();
                testMethod(iterations);
                end = System.currentTimeMillis();
                System.out.println((end - start) + " ms");
                Thread.sleep(100);
            }
            
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    public static void testMethod(long iterations) {
        for (long i = 0; i < iterations; i++) {
            src = new Object[LENGTH];
            dst = new Object[LENGTH];
            
            System.arraycopy(src, 0, dst, 0, LENGTH);
        }
    }
    
}
