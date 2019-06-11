package test.uncommontrap;

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
test.arraycopy.TestArrayCopy

 */

public class TestArrayCopy {
    
    public static class SubTestArrayCopy extends TestArrayCopy {
        
    }

    public static void main(String[] args) {
        long[] src1 = new long[100];
        long[] dst1 = new long[100];
        Object[] src2 = new Object[100];
        init2(src2);
        TestArrayCopy[] dst2 = new TestArrayCopy[100];
        SubTestArrayCopy[] src3 = new SubTestArrayCopy[100];
        init3(src3);
        TestArrayCopy[] dst3 = new TestArrayCopy[100];
        for (int i=0; i < 10_003; i++) {
            testMethod1(dst1, src1);
//            testMethod2(dst2, src2, 100);
//            testMethod3(dst3, src3, 50);
        }
    }
    
    public static void init3(Object[] src3) {
        for (int i=0; i < 100; i++) {
            src3[i] = new SubTestArrayCopy();
        }
    }

    public static Object testMethod3(Object[] dst, Object src[], int length) {
        System.arraycopy(src, 0, dst, 1, length);
        return dst;
    }

    public static void init2(Object[] src2) {
        for (int i=0; i < 100; i++) {
            src2[i] = new TestArrayCopy();
        }
    }

    public static Object testMethod2(Object dst, Object src, int length) {
      System.arraycopy(src, 0, dst, 0, length);
      return dst;
  }

    public static long[] testMethod1(long[] dst, long[] src) {
//      dst = new long[src.length];
      System.arraycopy(src, 0, dst, 0, src.length);
      return dst;
  }

}
