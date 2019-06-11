package test.volatile_;

public class SimpleReadLoop {

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
        for (int i = 0; i < 100; i++) {
            checksum += volatileField + checksum;
        }
        return checksum;
    }

}
