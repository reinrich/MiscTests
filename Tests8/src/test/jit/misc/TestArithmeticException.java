package test.jit.misc;

public class TestArithmeticException {
    
    public static void main(String[] args) {
        driver10();
    }

    public static void driver0() {
        long intCompact = 1;
        long val=2;
        for (int i = 100*1000; i > 0; i--) {
            dontinline_testMethod(intCompact, val);
        }
    }

    public static void driver4() {
        long val=10L + Integer.MAX_VALUE;
        long intCompact = (int) val;
        int exCount=0;
        for (int i = 100*1000; i > 0; i--) {
            try {
                dontinline_testMethod(intCompact, val);
            } catch (ArithmeticException e) {
                exCount++;
            }
        }
        System.out.println("### exCount:"+exCount);
    }

    public static void driver10() {
        int exCount=0;
        for (int i = 100*1000; i > 0; i--) {
            try {
                long val=10L + Integer.MAX_VALUE;
                long intCompact = (int) val;
                dontinline_testMethod(intCompact, val);
            } catch (ArithmeticException e) {
                exCount++;
            }
            try {
                long val=-10L - Integer.MIN_VALUE;
                long intCompact = (int) val;
                dontinline_testMethod(intCompact, val);
            } catch (ArithmeticException e) {
                exCount++;
            }
        }
        System.out.println("### driver10: exCount:"+exCount);
    }

    public static int dontinline_testMethod(long intCompact, long val) {
        int asInt = (int)val;
        if (asInt != val) {
            asInt = val>Integer.MAX_VALUE ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            if (intCompact != 0)
                throw new ArithmeticException(asInt>0 ? "Underflow":"Overflow");
        }
        return asInt;
    }
}
