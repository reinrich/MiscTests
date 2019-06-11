package test.methodhandles;

import java.lang.invoke.MethodHandle;

import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.*;

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
-cp /u/w/d/jtests/QuickJavaTests/bin
test.methodhandles.MHSimple

 */

public class MHSimple {

    public static void main(String[] args) {
        try {
            testCase_01();
          } catch (Throwable e) {
          throw new Error(e);
      }
    }

    public static void testCase_01() throws Throwable {
        MethodHandle mh_addr;
        try {
            mh_addr = publicLookup()
                    .findStatic(MHSimple.class, "addr", methodType(int.class, int.class, int.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new Error(e);
        }
        System.out.println("testCase_01:first call");
        int res = (int) testMethod_01(mh_addr, 1, 2);
        System.out.println("testCase_01:res:"+res);
        for(long i = (1L<<50); i > 0 ; i--) {
            res = (int) testMethod_01(mh_addr, 1, 2);
        }
        System.out.println("testCase_01:res:"+res);
    }

    public static int addr(int i, int j) {
        return i+j;
    }
    
    public static int testMethod_01(MethodHandle mh_addr, int i, int j) throws Throwable {
            return (int) mh_addr.invokeExact(i, j);
    }
    
}
