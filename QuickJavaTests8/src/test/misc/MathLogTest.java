package test.misc;

public class MathLogTest {
    static double log_value = 17197;

    public static void main(String[] args) throws Exception {
        doTest();
    }

    public static double callMathLog(double lv) {
        return Math.log(lv);
    }
    
    public static void doTest() {
        System.out.println("interpreted:"+Math.log(log_value));
        System.out.println("compiled   :"+callMathLog(log_value));
    }
}
