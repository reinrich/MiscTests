package test.misc;

/*

./sapjvm_8/bin/java
-XX:-ClientCompiler
-XX:+PrintLIR
-XX:+PrintIR
-XX:+TraceCompilerOracle
-XX:+PrintCompilation
-XX:CICompilerCount=1
-Xbatch
'-XX:CompileCommand=compileonlywithinlining,*::testMethod*'
'-XX:CompileCommand=dontinline,*::dontinline*'
-XX:-TieredCompilation
-XX:-UseOnStackReplacement
-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n
-cp
/u/w/d/jtests/QuickJavaTests/bin
test.misc.FloatInDoubleAccessedByDebugger

 */

public class FloatInDoubleAccessedByDebugger {

    public static volatile long dummy;
    private static float fourty2 = 42f;

    public static void main(String[] args) {
        for (int i = 0; i < 20_000; i++) {
            testMethod(10);
        }
        System.out.println("### attach debugger now!");
        testMethod(1L<<60);
    }

    private static float testMethod(long l) {
        float res = fourty2 ;
        while (l-- > 0) {
            dummy++;
        }
        return res;
    }

}
