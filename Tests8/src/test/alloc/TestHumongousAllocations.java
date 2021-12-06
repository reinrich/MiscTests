package test.alloc;

public class TestHumongousAllocations {

    public static Object res;

    public static void main(String[] args) {
        int i = 10000;
        while (i-- > 0) {
            int len = i == 1 ? 1000*1000 : 3;
            res = allocLongArray(len);
            res = allocLong1Array(len);
            res = allocLong2Array(len);
        }
    }

    public static long[] allocLongArray(int count) {
        return new long[count];
    }

    public static long[] allocLong1Array(int count) {
        return new long[count];
    }

    public static long[] allocLong2Array(int count) {
        return new long[count];
    }

}
