package test.misc;

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
test.misc.TestDeoptC1MethForUnloadedVolatileField

 */

public class TestDeoptC1MethForUnloadedVolatileField {
    
    public static void main(String[] args) {
        for (int i = 0; i < 10_003; i++) {
            testMethod01(i > 10_001);
        }
    }

    public static class UnloadedClass {
        public static volatile long volatileField=43;
    }
    
    public static long testMethod01(boolean accessUnloadedVolatileField) {
        if (accessUnloadedVolatileField) {
            return UnloadedClass.volatileField;
        }
        return -1;
    }

}
