package test.math;

public class LongArith {

    public static void main(String[] args) {
        long res = testCase9();
        System.out.println("### res == " + res);
        System.out.format("### res == %016X\n", res);
    }
 
    public static long testCase10() {
        for (long i = 1; i < 20_000; i++) { testMethod10(i); }
        long i;
        long res;
        i = -200;
        res =                              testMethod10(i);
        return res;
    }
    public static long testMethod10(long i) {
        return i*3;
    }

    public static long testCase9() {
        for (long i = 1; i < 10002; i++) { testMethod9(i, i); }
        long i;
        long res;
        i = Long.MIN_VALUE;
        res =                              testMethod9(i, 0);
        return res;
    }
    public static long testMethod9(long i, long j) {
        return i%j;
    }

    public static long testCase8() {
        for (int i = 1; i < 20_000; i++) { 
            try {
                testMethod8(i);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        long i;
        long res;
        i = -200;
        res =                              testMethod8(i);
        return res;
    }
    public static long testMethod8(long i) {
        return i/-2;
    }

    
    public static long testCase7() {
        for (long i = 1; i < 20_000; i++) { testMethod7(i, i); }
        return                              testMethod7(4, 2);
    }

    public static long testCase6() {
        for (long i = 0; i < 20_000; i++) { testMethod6(i); }
        return                              testMethod6(1);
    }

    public static long testCase5() {
        for (long i = 0; i < 20_000; i++) { testMethod5(i); }
        return                              testMethod5(0);
    }

    public static long testCase4() {
        for (long i = 0; i < 20_000; i++) { testMethod4(i); }
        return                              testMethod4(0);
    }

    public static long testCase3() {
        for (long i = 0; i < 20_000; i++) { testMethod3(i, i, i, i); }
        return                              testMethod3(1, 2, 1, 2);
    }

    public static long testCase1() {
        for (long i = 0; i < 20_000; i++) { testMethod1(i); }
        return                              testMethod1(0);
    }

    
    public static long testMethod7(long i, long j) {
        return i / j;
    }

    public static long testMethod6(long i) {
        return -i;
    }

    public static long testMethod5(long i) {
//      return i-0x123456789L;
      return i-0x7fff_ffffL;
//        return i-0x000000089L;
    }

    public static long testMethod4(long i) {
        return i+0x123456789L;
//      return i+0x023456789L;
//        return i+0x000000089L;
    }

    public static long testMethod3(long i, long j, long k, long l) {
        return i+j+k+l;
    }

    public static long testMethod2(long i, long j) {
        return i+j;
    }

    public static long testMethod1(long i) {
        return i+1L;
    }
}
