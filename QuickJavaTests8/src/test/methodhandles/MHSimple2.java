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
-cp /u/w/d/jtests/QuickJavaTests/bin
test.methodhandles.MHSimple2

 */

public class MHSimple2 {

    public static void main(String[] args) {
        try {
            testCase_01();
          } catch (Throwable e) {
          throw new Error(e);
      }
    }

    public static void testCase_01() throws Throwable {
        MethodHandle mh_testMethod_useManyRegs;
        try {
            mh_testMethod_useManyRegs = publicLookup()
                    .findStatic(MHSimple2.class, "testMethod_useManyRegs", methodType(int.class, int.class, int.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new Error(e);
        }
        System.out.println("testCase_01:first call");
        int res = (int) testMethod_01(mh_testMethod_useManyRegs, 100, 2);
        System.out.println("testCase_01:res:"+res);
        for(long i = (1L<<20); i > 0 ; i--) {
            res = (int) testMethod_01(mh_testMethod_useManyRegs, 100, 2);
        }
        System.out.println("testCase_01:res:"+res);
    }

    public static int testMethod_useManyRegs(int i, int j) {
        int v01 = i % j;
        int v02 = (v01 << 3) % j;
        int v03 = (v02 << 3) % j;
        int v04 = (v03 << 3) % j;
        int v05 = (v04 << 3) % j;
        int v06 = (v05 << 3) % j;
        int v07 = (v06 << 3) % j;
        int v08 = (v07 << 3) % j;
        int v09 = (v08 << 3) % j;
        int v10 = (v09 << 3) % j;
        int v11 = (v10 << 3) % j;
        int v12 = (v11 << 3) % j;
        int v13 = (v12 << 3) % j;
        int v14 = (v13 << 3) % j;
        int v15 = (v14 << 3) % j;
        int v16 = (v15 << 3) % j;
        int v17 = (v16 << 3) % j;
        int v18 = (v17 << 3) % j;
        int v19 = (v18 << 3) % j;
        int v20 = (v19 << 3) % j;
        int v21 = (v20 << 3) % j;
        int v22 = (v21 << 3) % j;
        int v23 = (v22 << 3) % j;
        int v24 = (v23 << 3) % j;
        int v25 = (v24 << 3) % j;
        int v26 = (v25 << 3) % j;
        int v27 = (v26 << 3) % j;
        int v28 = (v27 << 3) % j;
        int v29 = (v28 << 3) % j;
        int checksum = 0;
        for (int k = 0; k < 10; k++) {
            checksum += v01+v02+v03+v04+v05+v06+v07+v08+v09+v10+
                    v11+v12+v13+v14+v15+v16+v17+v18+v19+v20+
                    v21+v22+v23+v24+v25+v26+v27+v28+v29;
            v01 = (v29 << 3) % j;
            v02 = (v01 << 3) % j;
            v03 = (v02 << 3) % j;
            v04 = (v03 << 3) % j;
            v05 = (v04 << 3) % j;
            v06 = (v05 << 3) % j;
            v07 = (v06 << 3) % j;
            v08 = (v07 << 3) % j;
            v09 = (v08 << 3) % j;
            v10 = (v09 << 3) % j;
            v11 = (v10 << 3) % j;
            v12 = (v11 << 3) % j;
            v13 = (v12 << 3) % j;
            v14 = (v13 << 3) % j;
            v15 = (v14 << 3) % j;
            v16 = (v15 << 3) % j;
            v17 = (v16 << 3) % j;
            v18 = (v17 << 3) % j;
            v19 = (v18 << 3) % j;
            v20 = (v19 << 3) % j;
            v21 = (v20 << 3) % j;
            v22 = (v21 << 3) % j;
            v23 = (v22 << 3) % j;
            v24 = (v23 << 3) % j;
            v25 = (v24 << 3) % j;
            v26 = (v25 << 3) % j;
            v27 = (v26 << 3) % j;
            v28 = (v27 << 3) % j;
            v29 = (v28 << 3) % j;
        }
        return checksum;
    }
    
    public static int testMethod_01(MethodHandle mh_testMethod_useManyRegs, int i, int j) throws Throwable {
            return (int) mh_testMethod_useManyRegs.invokeExact(i, j);
    }
    
}
