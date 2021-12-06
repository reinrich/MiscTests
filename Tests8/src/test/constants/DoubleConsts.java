package test.constants;

/*
 * ./sapjvm_8/bin/java -XX:+PrintOptoAssembly -XX:+TraceCompilerOracle -XX:+PrintCompilation -XX:CICompilerCount=1 -Xbatch -cp /u/w/d/jtests/QuickJavaTests/bin '-XX:CompileCommand=compileonly,*::testMethod*' test.constants.DoubleConsts
 */

import java.util.HashMap;

public class DoubleConsts {

    /**
     * @param args
     */
    public static void main(String[] args) {
        double checksum;
        
        // warmup
        checksum = 0;
        for(int i = 0; i < 100000; i++) {
            checksum += testMethod(i);
        }
        System.out.println("checksum: " + checksum);
    }

    static double testMethod(int i) {
        return 47.11D;
    }

}
