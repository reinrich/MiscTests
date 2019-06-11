package test.controlflow;
/*

./sapjvm_8/bin/java
-XX:+ClientCompiler
-XX:+PrintLIR
-XX:-PrintIR
-XX:+TraceCompilerOracle
-XX:+PrintCompilation
-XX:CICompilerCount=1
-Xbatch
'-XX:CompileCommand=compileonlywithinlining,*::testMethod*'
'-XX:CompileCommand=dontinline,*::dontinline*'
-cp
/u/w/d/jtests/QuickJavaTests/bin
test.controlflow.ControlFlow

 */

public class ControlFlow {

    public static void main(String[] args) {
        for(int i = 0; i < 20000; i++) {
            testMethod(true, 1, 2);
        }
    }

    public static long testMethod(boolean cond, long left, long right) {
        return cond ? left : right;
    }

}
