package test.ea.debugging;

// java -XX:-TieredCompilation -XX:+PrintIdeal -XX:-PrintAssembly -XX:+PrintOptoAssembly -XX:+TraceCompilerOracle -XX:+PrintCompilation -XX:CICompilerCount=1 -Xbatch -cp /u/w/d/jtests/QuickJavaTests/bin test.ea.debugging.EliminatedObjectWithLongFields

public class EliminatedObjectWithLongFields {

    public static String strHallo = "Hallo";
    public static boolean bFalse = false;
    public static Long[] someLongs = new Long[64];
    public static Object lock = new Object();
    public static int staticCounter;

    public static void main(String[] args) {
        for(int i = 0; i < 100_000; i++) {
            testMethod1();
        }
    }

    private static long testMethod1() {
        XYZLong xyz = new XYZLong(1L, 2L, 3L);
        boolean doCall = dontjit_shouldCallDummy();
        if (doCall) {
            UnloadedTestClass.dontjit_dummy();
        }
        return xyz.x + xyz.y + xyz.z;
    }

    public static boolean dontjit_shouldCallDummy() {
        staticCounter++;
        if (staticCounter > 50_000 && staticCounter < 50_010) {
            synchronized (lock ) {
                // hack JVM_MonitorNotify to query and print object ids of scalar replaced objs
                lock.notify();
            }
        }
        return bFalse ;
    }

    static class UnloadedTestClass {
        public static void dontjit_dummy() {

        }
    }
}

class XYZLong {

    public long x, y, z;

    public XYZLong(long l, long m, long n) {
        x = l;
        y = m;
        z = n;
    }
}
