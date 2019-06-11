package test.math;

public class IntArith {

    public static void main(String[] args) {
        int res = testCase8();
        System.out.println("### res == " + res);
        System.out.format("### res == %08x\n", res);
    }

    public static int testCase10() {
        for (int i = 1; i < 20_000; i++) { testMethod10(1, 2, 3, 4, 5, 6, 7, 8, 9, 10); }
        int i;
        int res;
        i = -1;
        res =                              testMethod10(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        return res;
    }
    public static int testMethod10(int x1, int x2, int x3, int x4, int x5, int x6,
            int x7, int x8, int x9, int x10) {
        dontinline_dummyMethod();
        return x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8 + x9 + x10;
    }

    public static int dontinline_dummyMethod() {
        return 42;
    }

    public static int testCase9() {
        for (int i = 1; i < 20_000; i++) { testMethod9(i); }
        int i;
        int res;
        i = -1;
        res =                              testMethod9(i);
        return res;
    }
    public static int testMethod9(int i) {
        return 80*i;
    }

    public static int testCase8() {
        for (int i = 1; i < 10_001; i++) { testMethod8(i, 3); }
        int i;
        int res;
        i = 200;
        res =                              testMethod8(i, 0);
        return res;
    }
    public static int testMethod8(int i, int j) {
        return i/j;
    }

    public static int testCase7() {
        for (int i = 1; i < 20_000; i++) { testMethod7(i, 3, 3); }
        int res;
        long start = System.currentTimeMillis();
        res =                              testMethod7(6, 500, 1<<20);
        long end = System.currentTimeMillis();
        System.out.println("### duration : " + (end-start));
        return res;
    }
    public static int testMethod7(int i, int j, int k) {
        int checksum = 0;
        for (int jj = 0; jj < j; jj++) {
            for (int kk = 0; kk < k; kk++) {
                checksum += kk++ / 2;
                checksum += kk++ / 2;
                checksum += kk++ / 2;
                checksum += kk++ / 2;
                checksum += kk++ / 2;
                checksum += kk++ / 2;
                checksum += kk++ / 2;
                checksum += kk++ / 2;
                checksum += kk++ / 2;
                checksum += kk++ / 2;
            }            
        }
        return checksum;
    }

    public static int testCase6() {
        for (int i = 1; i < 20_000; i++) { testMethod6(i); }
        int i;
        int res;
        i = -200;
        res =                              testMethod6(i);
        return res;
    }
    public static int testMethod6(int i) {
        return i/-2;
    }

    public static int testCase5() {
        for (int i = 1; i < 20_000; i++) { testMethod5(i, i); }
        int i,j;
        int res;
        i = 6; j = 3;
        res =                              testMethod5(i, j);
        System.out.println("i="+i+" j="+j);
        System.out.println("### res == " + res);
        System.out.format("### res == %08x\n", res);
        i = 6; j = 4;
        res =                              testMethod5(i, j);
        System.out.println("i="+i+" j="+j);
        System.out.println("### res == " + res);
        System.out.format("### res == %08x\n", res);
        i = -6; j = 4;
        res =                              testMethod5(i, j);
        System.out.println("i="+i+" j="+j);
        System.out.println("### res == " + res);
        System.out.format("### res == %08x\n", res);
        i = 0x80000000; j = -1;
        res =                              testMethod5(i, j);
        System.out.println("i="+i+" j="+j);
        System.out.println("### res == " + res);
        System.out.format("### res == %08x\n", res);
        return -1;
    }
    public static int testMethod5(int i, int j) {
        return i % j;
    }

    public static int testCase4() {
        for (int i = 1; i < 20_000; i++) { testMethod4(i, i); }
        int i,j;
        int res;
        i = 6; j = 3;
        res =                              testMethod4(i, j);
        System.out.println("i="+i+" j="+j);
        System.out.println("### res == " + res);
        System.out.format("### res == %08x\n", res);
        i = -6; j = 3;
        res =                              testMethod4(i, j);
        System.out.println("i="+i+" j="+j);
        System.out.println("### res == " + res);
        System.out.format("### res == %08x\n", res);
        i = 6; j = -3;
        res =                              testMethod4(i, j);
        System.out.println("i="+i+" j="+j);
        System.out.println("### res == " + res);
        System.out.format("### res == %08x\n", res);
        i = 0x80000000; j = -1;
        res =                              testMethod4(i, j);
        System.out.println("i="+i+" j="+j);
        System.out.println("### res == " + res);
        System.out.format("### res == %08x\n", res);
        return -1;
    }

    public static int testMethod4(int i, int j) {
        return i / j;
    }

    public static int testMethod3(int i, int j, int k, int l) {
        return i+j+k+l;
    }

    public static int testMethod2(int i, int j) {
        return i+j;
    }

    public static int testMethod1(int i) {
        return i+1;
    }

    public static int testMethod0(int i) {
        return i+16;
    }
    public static int testCase0() {
        for (int i = 1; i < 20_000; i++) { testMethod0(i); }
        int i,j;
        int res;
        i = -16;
        res =                              testMethod0(i);
        return res;
    }

}
