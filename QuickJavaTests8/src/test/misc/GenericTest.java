package test.misc;

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
test.misc.GenericTest

 */

public class GenericTest {
    
    public static void main(String[] args) {
        long res_J = 0;
        long res_I = 0;
        Object obj = new Object();
        RRSub sub = new RRSub();
//        testMethod_1(obj);
        for (int i = 0;  i < 10_009; i++) {
            testMethod_1(sub);
        }
//        testMethod_1(obj);
        System.out.println("res_J:"+res_J);
        System.out.println("res_I:"+res_I);
    }

    public static interface RRInterf {}
    public static class RRSuper {}
    public static class RRSub extends RRSuper implements RRInterf {}
    
    public static RRInterf testMethod_1(Object sub) {
//        try {
            return (RRInterf) sub;
//        } catch (ClassCastException cce) {
//            reportFailure(sub);
//            return null;
//        }
    }

    private static void reportFailure(Object sub) {
        System.out.println("CCE: " + sub);
    }
    
}
