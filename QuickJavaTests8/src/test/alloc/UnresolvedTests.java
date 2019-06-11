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
test.alloc.UnresolvedOopConstantTest

 */

public class UnresolvedTests {
    
    public static void main(String[] args) {
        long res_J = 0;
        long res_I = 0;
        for (int i = 0;  i < 10_009; i++) {
//            res_J = testMethod_1(i > 10_005);
//            res_I = testMethod_2(i > 10_005);
//            res_J = testMethod_3(i > 10_005);
//            res_I = testMethod_4(i > 10_005);
            
            if (i > 10_005) setup_5();
            res_J = testMethod_5(i > 10_005);
        }
        System.out.println("res_J:"+res_J);
        System.out.println("res_I:"+res_I);
    }

    public static UnresolvedClass c;

    public static void setup_5() {
        c = new UnresolvedClass();
    }
    public static long testMethod_5(boolean doIt) {
        if (doIt) {
            return c.instance_fourty2_J;
        }
        return 41;
    }
    
    public static int testMethod_4(boolean doIt) {
        if (doIt) {
            UnresolvedClass.fourty2w_I = 42;
            return UnresolvedClass.fourty2w_I;
        }
        return 41;
    }
    
    public static long testMethod_3(boolean doIt) {
        if (doIt) {
            UnresolvedClass.fourty2w_J = 42L;
            return UnresolvedClass.fourty2w_J;
        }
        return 41;
    }
    
    public static int testMethod_2(boolean doIt) {
        if (doIt) {
            return UnresolvedClass.fourty2_I;
        }
        return 41;
    }
    public static long testMethod_1(boolean doIt) {
        if (doIt) {
            return UnresolvedClass.fourty2_J;
        }
        return 41;
    }
    
    public static Object testMethod_0(boolean doAlloc) {
        if (doAlloc)
            return new UnresolvedClass();
        return null;
    }
    
    
}
