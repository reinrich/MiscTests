package test.uncommontrap;

public class TestObjectCopy {

    private static final int LENGTH = 200;

    public static void main(String[] args) {
        Object[] src = new Object[LENGTH];
        Object[] dst = new Object[LENGTH];

        System.out.println("\n\n\n        STARTED\n\n\n");
        for (long i = 0; i < (1L<<62); i++) {
            testMethod_dontinline(src, 0, dst, 0, LENGTH);
        }
        System.out.println("\n\n\n        FINISHED\n\n\n");
    }

    public static void testMethod_dontinline(Object[] src, int srcPos, Object[] dest, int destPos, int length) {
//        for (long i = 0; i < iterations; i++) {
//            System.arraycopy(src, srcPos, dest, destPos, length);
//        }
        System.arraycopy(src, srcPos, dest, destPos, length);
    }
    
}
