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
/s/d/jtests/QuickJavaTests/bin
test.XXXX.TestTemplate

 */

public class TestTemplate {

    public static void main(String[] args) {
        for(int i = 0; i < 20000; i++) {
            testMethod();
        }
    }

    public static void testMethod() {
        // TODO Auto-generated method stub
    }

}
