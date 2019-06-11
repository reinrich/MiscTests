package test.misc;
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
test.misc.StringBufferGetCharsTest

 */

public class StringBufferGetCharsTest {

    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer("Sample Text");
        char[] dst0 = new char[0];
        char[] dst5 = new char[5];
        System.out.println("### warm-up");
        for(int i = 0; i < 20000; i++) {
            testMethod(sb, dst5);
        }
        System.out.println("### test run");
        testMethod(sb, dst0);
    }

    public static void testMethod(StringBuffer sb, char[] dst) {
        sb.getChars(0, 3, dst, 1);
    }

}
