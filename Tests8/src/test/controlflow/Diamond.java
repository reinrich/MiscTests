package test.controlflow;

import java.util.HashMap;

public class Diamond {

    /**
     * @param args
     */
    public static void main(String[] args) {
        long checksum;
        
        // warmup
        checksum = 0;
        nonVolatileField = 0;
        for(int i = 0; i < 100000; i++) {
            checksum = testMethod((i&1) == 0,checksum);
        }
        System.out.println("checksum: " + checksum);
    }

    static long nonVolatileField;
    static long testMethod(boolean cond, long checksum) {
        long res;
        if (cond) {
            res = checksum+1;
        } else {
            res = checksum+2;
        }
        return res;
    }

}
