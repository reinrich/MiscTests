package test.ea.debugging;

// java -XX:-TieredCompilation -XX:+PrintIdeal -XX:-PrintAssembly -XX:+PrintOptoAssembly -XX:+TraceCompilerOracle -XX:+PrintCompilation -XX:CICompilerCount=1 -Xbatch -cp /u/w/d/jtests/QuickJavaTests/bin -XX:CompileCommand=compileonly,*::testMethod* test.ea.debugging.EliminatedStringArray

public class EliminatedStringArray {

    public static String strHallo = "Hallo";
    public static volatile boolean volFalse = false;
    public static Long[] someLongs = new Long[64];

    public static void main(String[] args) {
        for(int i = 0; i < 100_000; i++) {
            testMethod1();
        }
    }

    private static long testMethod4() {
        String[] stringArr = new String[1];
        stringArr[0] = strHallo;
        Long[] localLongs = new Long[16];
        Long _0 = new Long(0);
        Long _1 = new Long(1);
        Long _2 = new Long(2);
        Long _3 = new Long(3);
        Long _4 = new Long(4);
        Long _5 = new Long(5);
        Long _6 = new Long(6);
        Long _7 = new Long(7);
        boolean doCall = shouldCallDummy();
        if (doCall) {
            UnloadedTestClass.dummy();
        }
        long sum = _0+_1+_2+_3+_4+_5+_6+_7;
        return stringArr.length + sum;
    }

    private static int testMethod3() {
        String[] stringArr = new String[1];
        stringArr[0] = strHallo;
        Long[] localLongs = new Long[16];
        localLongs[0] = new Long(0);
        localLongs[1] = new Long(1);
        localLongs[2] = new Long(2);
        localLongs[3] = new Long(3);
        localLongs[4] = new Long(4);
        localLongs[5] = new Long(5);
        localLongs[6] = new Long(6);
        localLongs[7] = new Long(7);
        boolean doCall = shouldCallDummy();
        if (doCall) {
            UnloadedTestClass.dummy();
        }
        return stringArr.length;
    }

    private static int testMethod2() {
        String[] stringArr = new String[1];
        stringArr[0] = strHallo;
        someLongs[0] = new Long(0);
        someLongs[1] = new Long(1);
        someLongs[2] = new Long(2);
        someLongs[3] = new Long(3);
        someLongs[4] = new Long(4);
        someLongs[5] = new Long(5);
        someLongs[6] = new Long(6);
        someLongs[7] = new Long(7);
        boolean doCall = shouldCallDummy();
        if (doCall) {
            UnloadedTestClass.dummy();
        }
        return stringArr.length;
    }

    private static int testMethod1() {
        String[] stringArr = new String[1];
        stringArr[0] = strHallo ;
        boolean doCall = shouldCallDummy();
        if (doCall) {
            UnloadedTestClass.dummy();
        }
        return stringArr.length;
    }

    public static boolean shouldCallDummy() {
        return volFalse ;
    }

}

class UnloadedTestClass {

    public static void dummy() {

    }
    
}
