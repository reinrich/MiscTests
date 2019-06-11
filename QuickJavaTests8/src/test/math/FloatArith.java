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
test.math.FloatArith

 */

public class FloatArith {
    
    public static void main(String[] args) {
        float res=0f;
        boolean res_boolean = false;
        for (int i=0; i<30_000; i++) {
            res_boolean = testMethod2(10.0f, 4.5f);
        }
        System.out.println("### res : " + res);
        System.out.println("### res_boolean : " + res_boolean);
    }

    public static boolean testMethod2(float f, float g) {
        dontinline_trigger_spilling();
        return f > g;
    }

    public static void dontinline_trigger_spilling() {
        // empty
    }

    public static float testMethod1(float f, float g) {
        return f%g;
    }
}
