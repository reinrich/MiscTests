package test.calls;

/**
 *
 * java -XX:+TraceCompilerOracle -Xbatch -XX:-TieredCompilation -XX:+PrintCompilation -cp /net/usr.work/d/jtests/QuickJavaTests/bin '-XX:CompileCommand=compileonly,*::myTestMethod' test.calls.CallWithLongSpills
 * 
 * @author d
 */

public class CallWithLongSpills {

    public static void main(String[] args) {
        long checksum=0;
        for(int i=0; i<30000; i++) {
            checksum += myTestMethod(i, i, i, i, i, i);
        }
        System.out.println("checksum="+checksum);
    }

    public static long myTestMethod(long a, long b, long c, long d, long e, long f) {
        long res = anotherMethod(a);
        return res+a+b+c+d+e+f;
    }

    public static long anotherMethod(long c) {
        return c+1;
    }

}
