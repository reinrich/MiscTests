package test.ea;

import java.io.PrintStream;

import testlib.TestBase;

public class StringBuilderLockElimination extends TestBase implements Runnable {

    public static final int COMPILE_THRESHOLD = 20000;

    public static void main(String[] args) {
        new StringBuilderLockElimination().run();
    }

    @Override
    public void run() {
        log0("WarmUp: START");
        int callCount = COMPILE_THRESHOLD + 1000;
        while (callCount-- > 0) {
            dontinline_testMethod();
        }
        log0("WarmUp: DONE");
    }

    public static class Box {
        public long v;

        public Box(long v) {
            this.v = v;
        }
    }

    public String dontinline_testMethod() {
        StringBuffer sb = new StringBuffer();
        sb.append(System.currentTimeMillis());
        String s = sb.toString();
        return s;
    }

    public static PrintStream out() {
        return System.out;
    }

    public static PrintStream err() {
        return System.err;
    }
}
