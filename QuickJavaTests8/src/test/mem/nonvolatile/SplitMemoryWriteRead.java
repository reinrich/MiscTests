package test.mem.nonvolatile;

public class SplitMemoryWriteRead {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int checksum;
        field1 = 1;
        field2 = 1;

        // warmup
        checksum = 0;
        for (int i = 1 ; i < 100000 ; i++) {
            checksum = testMethod(checksum);
        }
        System.out.println("checksum == " + checksum);

        // test
        checksum = 0;
        for (int i = 1 ; i < 100000 ; i++) {
            checksum = testMethod(checksum);
        }
        System.out.println("checksum == " + checksum);
    }

    public static int field1;
    public static int field2;
    
    public static int testMethod(int checksum) {
        field1 = checksum;
        int res = field1 + checksum;

        field2 = checksum;
        res += field2 + checksum;
        return res;
    }

}
