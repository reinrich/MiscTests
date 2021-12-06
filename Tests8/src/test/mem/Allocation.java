package test.mem;

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
test.mem.Allocation

 */

public class Allocation {

    public static class MemoryOperandHolder {
        int theMemoryOperand;
    }

    public static volatile int volatileIntField;
    
    public static void main(String[] args) {
        MemoryOperandHolder mem = new MemoryOperandHolder();
        for (int i = 0; i < 50_000; i++) {
            testMethod0();
            testMethod1();
            testMethod2();
        }
    }

    // multinewarray
    public static Object testMethod2() {
        return new int[1][2][3][2][3];
    }

    public static Object testMethod1() {
        return new int[1][2][3];
    }

    // multinewarray
    public static void testMethod0() {
    }
}
