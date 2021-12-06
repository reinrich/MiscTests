package test.math;

/*

./sapjvm_8/bin/java
-XX:+ClientCompiler
-XX:+PrintLIR
-XX:+PrintIR
-XX:+TraceCompilerOracle
-XX:+PrintCompilation
-XX:CICompilerCount=1
-Xbatch
'-XX:CompileCommand=compileonlywithinlining,*::testMethod*'
'-XX:CompileCommand=dontinline,*::dontinline*'
-XX:-TieredCompilation
-cp
/u/w/d/jtests/QuickJavaTests/bin
test.math.DoubleArith

 */

public class DoubleArith {
    
    private static int _l1;
    private static int _l2;

    public static void main(String[] args) {
        double res=0f;
        boolean res_boolean = false;
        for (int i=0; i<30_000; i++) {
//            res = testMethod2(5.0D, 3.0D);
//            res_boolean = testMethod3(5.0D, 3.0D);
            res_boolean = testMethod4();
        }
        System.out.println("### res : " + res);
        System.out.println("### res_boolean : " + res_boolean);
    }

    public static boolean testMethod4() {
        int l1 = _l1;
        int l2 = _l2;
        dontinline_trigger_spilling();
        return l1 > l2;
    }
    public static boolean testMethod3(double f, double g) {
        dontinline_trigger_spilling();
        return f > g;
    }
    public static void dontinline_trigger_spilling() {
        // empty
    }

    public static double testMethod2(double f, double g) {
        return java.lang.Math.sin(f);
    }

    public static double testMethod1(double f, double g) {
        return f*g;
    }    
}
