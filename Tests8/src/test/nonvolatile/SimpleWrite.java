package test.nonvolatile;

public class SimpleWrite {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int checksum;
        volatileField = 1;
        nonVolatileField = 1;

        // warmup
        checksum = 0;
        for (int i = 1 ; i < 100000 ; i++) {
            checksum = writeVolatile(checksum);
        }
        System.out.println("checksum == " + checksum);

        // test
        checksum = 0;
        for (int i = 1 ; i < 100000 ; i++) {
            checksum = writeVolatile(checksum);
        }
        System.out.println("checksum == " + checksum);
    }

    public static  int volatileField;
    public static          int nonVolatileField;
    
    public static int writeVolatile(int checksum) {
        volatileField = checksum;
        int res = volatileField + checksum;

        nonVolatileField = checksum;
        res += nonVolatileField + checksum;
        return res ;
    }

}
