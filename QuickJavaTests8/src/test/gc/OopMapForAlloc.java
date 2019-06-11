package test.gc;

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
test.gc.OopMapForAlloc

*/

public class OopMapForAlloc {

    public static void main(String[] args) {
        donjit_callTestMethod();
    }

    public static void donjit_callTestMethod() {
        int iterations = 3;
        for(int i = 0; i < 10_100; i++) {
            if (i > 10_000) {
                System.err.println("### produce GCs!!!");
                iterations = 1_000_000;
            }
            if (testMethod_1(iterations) != (41+42)) {
                throw new Error();
            }
        }
    }

    // type array
    public static int testMethod_1(int iters) {
        int[] arr2  = new int[42];
        arr2[0] = 41;
        int[] arr = new int[3];;
        for(int i=iters; i>=0; i--) {
            arr = new int[3];
            arr[0] = 42;
        }
        return arr[0] + arr2[0];
    }

    // ordinary instance
    public static int testMethod_2(int iters) {
        Integer i1 = new Integer(41);
        Integer i2 = new Integer(42);
        for(int i=iters; i>=0; i--) {
            i2 = new Integer(42);
        }
        return i1.intValue() + i2.intValue();
    }
}
