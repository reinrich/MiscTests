package test.misc;
/*

run with:

~/skripte/run_quick_test.sh

or

./sapjvm_8/bin/java
-XX:+TraceCompilerOracle
-XX:+PrintCompilation
-XX:CICompilerCount=1
-Xbatch
'-XX:CompileCommand=compileonlywithinlining,*::testMethod*'
'-XX:CompileCommand=dontinline,*::dontinline*'
-cp
/u/w/d/jtests/QuickJavaTests/bin
test.XXXX.TestTemplate

 */

public class TestMPTCompilation {

    public static int dummy;

    public static void main(String[] args) {
        TestMPTCompilation test = new TestMPTCompilation();
        for(int i = 0; i < 100000; i++) {
            test.testMethod();
        }
    }

    public void testMethod() {
        dummy += dummy % 17;
    }

}
