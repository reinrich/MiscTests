package test.mem.nonvolatile;

public class SimpleReadLoop {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int checksum;
        nonVolatileField = 1;

        // warmup
        checksum = 0;
        for (int i = 1 ; i < 100000 ; i++) {
            checksum = readVolatile(checksum);
        }
        System.out.println("checksum == " + checksum);

        // test
        checksum = 0;
        for (int i = 1 ; i < 100000 ; i++) {
            checksum = readVolatile(checksum);
        }
        System.out.println("checksum == " + checksum);
    }

    public static int nonVolatileField;
    
    public static int readVolatile(int checksum) {
        for (int i = 0; i < 100; i++) {
            checksum += nonVolatileField + checksum;
        }
        return checksum;
    }

}
