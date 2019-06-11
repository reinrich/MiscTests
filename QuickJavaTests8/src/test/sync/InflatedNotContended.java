package test.sync;
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
test.sync.InflatedNotContended

 */

public class InflatedNotContended {

    public static void main(String[] args) {
        InflatedNotContended lock = new InflatedNotContended();
        for(int i = 0; i < 20000; i++) {
            if ((i%100) == 0) {
                lock.dontinline_inflate();
            }
            lock.testMethod();
        }
    }

    public synchronized void testMethod() {
//        dontinline_inflate();
    }

    private synchronized void dontinline_inflate() {
        // get lock inflated by calling wait()
        try {
            wait(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }            
    }

}
