package test.controlflow;

import java.util.HashMap;

public class LoopWithIndepRead {

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
        long res = 0;
        for (long l = start; l > 0; l -= decr) {
            res += nonVolatileField;
        }
        return checksum;
    }

}
