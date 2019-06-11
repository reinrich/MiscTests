package test.calls;

/**
 *
 * java -XX:+TraceCompilerOracle -Xbatch -XX:-TieredCompilation -XX:+PrintCompilation -cp /net/usr.work/d/jtests/QuickJavaTests/bin '-XX:CompileCommand=compileonly,*::myTestMethod' test.calls.CallWithLongSpills
 * 
 * @author d
 */

public class CallWithOopSpills {

    public static void main(String[] args) {
        long checksum=0;
        Long one = new Long(1);
        for(int i=0; i<30000; i++) {
            checksum += myTestMethod(one, one, one, one, one, one);
        }
        System.out.println("checksum="+checksum);
    }

    public static long myTestMethod(Long a, Long b, Long c, Long d, Long e, Long f) {
        long res = anotherMethod(a);
        return res+a+b+c+d+e+f;
    }

    public static long anotherMethod(Long c) {
        return c+1;
    }

}
