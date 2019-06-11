package test.calls;

// ./sapjvm_8/bin/java -XX:+PrintInlining -XX:-UseCompressedOops -XX:-TieredCompilation -XX:+PrintIdeal -XX:-PrintAssembly -XX:+PrintOptoAssembly -XX:+TraceCompilerOracle -XX:+PrintCompilation -XX:CICompilerCount=1 -Xbatch -cp /u/w/d/jtests/QuickJavaTests/bin -XX:CompileCommand=exclude,*::dontjit* test.calls.CallLongParameters

public class CallLongParameters {

    public static String strHallo = "Hallo";
    public static volatile boolean volFalse = false;
    public static Long[] someLongs = new Long[64];

    public static void main(String[] args) {
        for(int i = 0; i < 100_000; i++) {
            testMethod1();
        }
    }

    public static long testMethod1() {
        return dontjit_testMethodLongParams(1L, 2L, 3L);
    }

    public static long dontjit_testMethodLongParams(long l, long m, long n) {
        return l+m+n;
    }
}
