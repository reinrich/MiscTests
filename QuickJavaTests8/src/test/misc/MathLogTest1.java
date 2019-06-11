package test.misc;

public class MathLogTest1 {
    static double log_value = 17197;

    public static void main(String[] args) throws Exception {
        doTest();
    }

    public static double callMathLog(double lv) {
        return Math.log(lv);
    }
    
    public static void doTest() {
        System.out.println("java.lang.Math.log("+log_value+"):"+Math.log(log_value));
        System.out.println("java.lang.StrictMath.log("+log_value+"):"+StrictMath.log(log_value));
    }
}
