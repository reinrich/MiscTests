package test.arraycopy;

public class TestObjectCopy {

    private static final int LENGTH = 300000;

    public static void main(String[] args) {
        try {
            long iterations = 0;
            iterations = Long.parseLong(args[0]);

            Object[] src = new Object[LENGTH];
            Object[] dst = new Object[LENGTH];

            long start;
            long end;
            for (int i = 0; i < 5; i++) {
                start = System.currentTimeMillis();
                testMethod(iterations, src, 0, dst, 0, LENGTH);
                end = System.currentTimeMillis();
                System.out.println((end - start) + " ms");
                Thread.sleep(100);
            }
            
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    public static void testMethod(long iterations, Object[] src, int srcPos, Object[] dest, int destPos, int length) {
        for (long i = 0; i < iterations; i++) {
            System.arraycopy(src, srcPos, dest, destPos, length);
        }
    }
    
}
