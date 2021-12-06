package test.jit.misc;

import java.io.PrintStream;

public class ModificationOnDeadLocal implements Runnable {

    public static final int COMPILE_THRESHOLD = 20000;

    public static void main(String[] args) {
        new ModificationOnDeadLocal().run();
    }

    @Override
    public void run() {
        msgHL("WarmUp: START");
        int callCount = COMPILE_THRESHOLD + 1000;
        int param = 10;
        while (callCount-- > 0) {
            dontinline_testMethod(param);
        }
        msgHL("WarmUp: DONE");
    }

    public static class Box {
        public long v;

        public Box(long v) {
            this.v = v;
        }
    }
    
    public void dontinline_testMethod(int p1) {
        p1--;
        dontinline_sfpt();
    }

    public void dontinline_sfpt() {
        // empty
    }

    public static PrintStream out() {
        return System.out;
    }

    public static PrintStream err() {
        return System.err;
    }

    public static void msg(PrintStream ps, String m) {
        ps.println();
        ps.println("### " + m);
        ps.println();
    }
    public static void msg(String m) {
        msg(out(), m);
    }
    private static void msgHL(PrintStream out, String m, String m2, String m3) {
        PrintStream o = out();
        o.println(); o.println(); o.println();
        o.println("#####################################################");
        o.println("### " + m);
        if (m2 != null) o.println("### " + m2);
        if (m3 != null) o.println("### " + m3);
        o.println("###");
        o.println();
    }
    public static void msgHL(String m) {
        msgHL(m, null, null);
    }
    public static void msgHL(String m, String m2, String m3) {
        msgHL(out(), m, m2, m3);
    }
    public static void msgHL(PrintStream ps, String m) {
        msgHL(ps, m, null, null);
    }

    public static void msgErr(String m) {
        PrintStream o = err();
        o.println(); o.println(); o.println();
        o.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        o.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        o.println("!!! " + m);
        o.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        o.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        o.println();
    }
}
