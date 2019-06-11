package test.controlflow;

/*

./sapjvm_8/bin/java
-XX:-C2Tiers
-XX:+TieredCompilation
-XX:+PrintLIR
-XX:+PrintIR
-XX:+TraceCompilerOracle
-XX:+PrintCompilation
-XX:CICompilerCount=1
-Xbatch
'-XX:CompileCommand=compileonlywithinlining,*::testMethod*'
-XX:-TieredCompilation
-cp /u/w/d/jtests/QuickJavaTests/bin
test.controlflow.SimpleLoop

 */

public class SimpleLoop {

    public static void main(String[] args) {
        long iterations = 50000;
        for (int i = 0; i < 2; i++) {
            testMethod_01(iterations);
        }
    }

    public static void testMethod_01(long iterations) {
        for (int i = 0; i < iterations; i++) {
            // empty
        }
    }

}
