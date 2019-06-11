package test.controlflow;

import java.util.HashMap;

public class DiamondWithStores {

    /**
     * @param args
     */
    public static void main(String[] args) {
        long checksum;
                
        // warmup
        checksum = 0;
        nonVolatileField = 0;
        long vals[] = new long[2];
        for(int i = 0; i < 100000; i++) {
            checksum = testMethod((i&1) == 0, vals, checksum);
        }
        System.out.println("checksum: " + checksum);
    }

    static long nonVolatileField;
    static long testMethod(boolean cond, long vals[], long checksum) {
        long res = vals[0] + vals[1]; // get checks out of the ctrl flow diamond
        if (cond) {
            vals[0] = checksum;
        } else {
            vals[1] = checksum;
        }
        res = vals[0];
        return res;
    }

}
