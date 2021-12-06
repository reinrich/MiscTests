package test.ea.debugging;

public class EliminatedObjectWithUncommonTrap {

    public static String strHallo = "Hallo";
    public static boolean bFalse = false;
    public static Long[] someLongs = new Long[64];
    public static Object lock = new Object();
    public static int staticCounter;

    public static void main(String[] args) {
        for(int i = 0; i < 100000; i++) {
            testMethod1();
        }
    }

    private static long testMethod1() {
        XYZ xyz = new XYZ(1, 2, 3);
        boolean doUncommon = dontjit_doUncommon();
        if (doUncommon) {
            return xyz.x + xyz.y + xyz.z + 1;
        }
        return xyz.x + xyz.y + xyz.z;
    }

    public static boolean dontjit_doUncommon() {
        staticCounter++;
        if (staticCounter > 50000 && staticCounter < 50010) {
            System.out.println("###### do uncommon now!!!");
            return true;
        }
        return false;
    }
}
