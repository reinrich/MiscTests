package test.sync;

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
test.sync.SyncBlock

 */

public class SyncBlock {

    public static int callCount;

    public static void main(String[] args) {
        Object lock = new Object();
        for (int i=0; i<30_000; i++) {
            dontinline_testMethod1(lock);
        }
    }

    public static void dontinline_testMethod1(Object lock) {
        synchronized (lock) {
            callCount++;
        }
    }
}
