package test.alloc;

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
test.alloc.AllocTest
 
 */

public class AllocTest {

    public static void main(String[] args) {
        donjit_callTestMethod();
    }

    public static void donjit_callTestMethod() {
        for(int i = 0; i < 10_003; i++) {
            testMethod_1(1, 1, 1);
        }
    }

    // alloc type array
    public static int testMethod_1(int i1, int i2, int i3) {
        int[] arr = new int[3];
        return i1+i2+i3;
    }

    // alloc type array
    public static int testMethod_2(int i1, int i2, int i3) {
        Integer o1 = new Integer(3);
        return i1+i2+i3;
    }

    public static class XYZ {
        int x, y, z;
    }
    
    // alloc type array
    public static int testMethod_3(int i1, int i2, int i3) {
        XYZ o1 = new XYZ();
        return i1+i2+i3;
    }

    public static class SomeLongs {
        long l1, l2, l3;
    }

    // alloc type array
    public static int testMethod_4(int i1, int i2, int i3) {
        SomeLongs o1 = new SomeLongs();
        return i1+i2+i3;
    }

}
