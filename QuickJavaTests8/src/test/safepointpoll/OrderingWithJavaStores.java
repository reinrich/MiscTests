package test.safepointpoll;

import java.util.HashMap;

public class OrderingWithJavaStores {

    /**
     * @param args
     */
    public static void main(String[] args) {
        long checksum;
        
        // warmup
        checksum = 0;
        nonVolatileField = 0;
        for(int i = 0; i < 6000; i++) {
            checksum = testMethod(100000, 1, checksum);
        }
        System.out.println("checksum: " + checksum);
    }

    static long nonVolatileField;
    static long testMethod(long start, long decr, long checksum) {
        for (long l = start; l > 0; l -= decr) {
            checksum++;
        }
        return checksum;
    }

}
