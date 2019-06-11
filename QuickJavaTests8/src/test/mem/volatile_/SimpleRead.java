package test.mem.volatile_;

public class SimpleRead {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int checksum;
        volatileField = 1;

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

    public static volatile int volatileField;
    
    public static int readVolatile(int checksum) {
        return volatileField + checksum;
    }

}
