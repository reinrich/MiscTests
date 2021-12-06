package test.ea;

import java.io.PrintStream;

public class ThreeLiveLocalObjsInLoopIteration implements Runnable {

    public static final int COMPILE_THRESHOLD = 20000;

    public static void main(String[] args) {
        new ThreeLiveLocalObjsInLoopIteration().run();
    }

    @Override
    public void run() {
        msgHL("WarmUp: START");
        int callCount = COMPILE_THRESHOLD + 1000;
        long iterations = 10;
        while (callCount-- > 0) {
            dontinline_leakHoldingMethodWithScalarReplacement(iterations);
        }
        msgHL("WarmUp: DONE");
    }

    public static class Box {
        public long v;

        public Box(long v) {
            this.v = v;
        }
    }
    
    public long dontinline_leakHoldingMethodWithScalarReplacement(long iterations) {
        long checksum = 0;
        Box b1 = new Box(iterations);
        Box b2 = new Box(iterations);
        while(iterations-- > 0) {
            b2 = b1;
            b1 = new Box(iterations);
            checksum += b1.v;
            checksum += b2.v;
        }
        return checksum;
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
