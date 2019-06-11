package test.calls;

/*

./sapjvm_8/bin/java
-XX:+ClientCompiler
-XX:+PrintLIR
-XX:+PrintIR
-XX:+TraceCompilerOracle
-XX:+PrintCompilation
-Xbatch
-XX:-Inline
-XX:CICompilerCount=1
'-XX:CompileCommand=compileonlywithinlining,*::testMethod*'
-XX:-TieredCompilation
-cp
/u/w/d/jtests/QuickJavaTests/bin
test.calls.CallWithICCache

*/

public class CallWithICCache {

    public static void main(String[] args) {
        CallWithICCache receiver = new CallWithICCache();
        for (int i=0; i<30_000; i++) {
            testMethod1(receiver);
        }
    }

    public static void testMethod1(CallWithICCache receiver) {
        receiver.testMethod1_callee();
    }

    private int dummyCounter;

    public void testMethod1_callee() {
        dummyCounter++;
    }

}
