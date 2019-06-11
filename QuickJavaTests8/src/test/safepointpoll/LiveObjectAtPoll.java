package test.safepointpoll;

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
-XX:-TieredCompilation
-cp
/u/w/d/jtests/QuickJavaTests/bin
test.safepointpoll.LiveObjectAtPoll

*/

public class LiveObjectAtPoll {

    public static void main(String[] args) {
        donjit_callTestMethod();
    }

    public static void donjit_callTestMethod() {
        int iterations = 3;
        for(int i = 0; i < 20_000; i++) {
            if (i == 11_000) {
                Thread tt = new Thread(() -> {
                    System.err.println("### produce GCs!!!");
                    while (true) {new Object();}
                });
                tt.start();
                iterations = 1_000_000;
            }
            if (testMethod_1(iterations) != 42) {
                throw new Error();
            }
        }
    }

    // ordinary instance
    public static int testMethod_1(long iters) {
        Integer i1 = new Integer(42);
        for(long i=iters; i>=0; i--) {
            // safepoint poll
        }
        return i1.intValue();
    }

}
