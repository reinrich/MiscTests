package test.safepointpoll;

public class EmptyLoopWithSafepoint {

    /**
     * @param args
     */
    public static void main(String[] args) {
        long checksum;
        
        // warmup
        checksum = 0;
        nonVolatileField = 0;
        for(int i = 0; i < 100000; i++) {
            checksum = dontinline_testMethod(100, 1, checksum);
        }
        System.out.println("checksum: " + checksum);
    }

    static long nonVolatileField;
    static long dontinline_testMethod(long start, long decr, long checksum) {
        for (long l = start; l > 0; l -= decr) {
            checksum++;
        }
        return checksum;
    }

}
