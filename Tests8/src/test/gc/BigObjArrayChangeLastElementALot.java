package test.gc;

public class BigObjArrayChangeLastElementALot {

    public static final int K = 1024;
    public static final int M = 1024*K;
    public static final int G = 1024*M;

    public static final int BIG_ARRY_SIZE = 100*M;
    public static final int TMPS_UNTIL_CHANGE = 100;
    public static final int TMP_OBJ_SIZE = 1*K;

    public static final int MAGIC = 4711;

    public static byte[] blackHole;

    public static void main(String[] args) {
        MyInt[] bigArray = new MyInt[BIG_ARRY_SIZE];
        while (true) {
            doChange(bigArray);
            for (int i = 0; i < TMPS_UNTIL_CHANGE; i++) {
                blackHole = new byte[TMP_OBJ_SIZE];
                check_dontinline(bigArray);
            }
        }
    }

    public static void doChange(MyInt[] bigArray) {
        bigArray[BIG_ARRY_SIZE - 1] = new MyInt();
    }

    private static void check_dontinline(MyInt[] bigArray) {
        if (bigArray[BIG_ARRY_SIZE - 1].val != MAGIC) {
            throw new Error("MAGIC not found!!!!");
        }
    }

    public static class MyInt {
        public int val = MAGIC;
    }
}
