package test.misc;
/*

run with:

~/skripte/run_quick_test.sh

or

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
test.misc.TMP

 */

public class TMP {

    public static void main(String[] args) {
        testMethod2();
        for(int i = 0; i < 20000; i++) {
            testMethod();
        }
        for(long l = 1L<<60; l >= 0; l--) {
            testMethod();
        }
    }

    public static void testMethod() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void testMethod2() {
        System.out.println("***");
        System.out.println("***");
        System.out.println("***");
        System.out.println("Hello World!");
        System.out.println("***");
        System.out.println("***");
        System.out.println("***");
    }
}
