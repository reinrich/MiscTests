package test.misc;

public class TestNestedRealloc {

    public static void main(String[] args_ignored) {
            // Warm-up
            for (int i = 50_000; i > 0; i--) {
                testMethod_dontinline(1, 2, 3, false);
            }
    }

    static class Outer {
        public Outer(int b1, Inner inner) {
            b = b1; i = inner;
        }
        int b;
        Inner i;
    }
    static class Inner {
        public Inner(int b2, int b3) {
            int[] a2 = {b2, b3};
            a = a2;
        }

        int[] a;
    }

    public static int testMethod_dontinline(int b1, int b2, int b3, boolean makeUncommonCall) {
        Outer o = new Outer(b1, new Inner(b2, b3));
        int u = makeUncommonCall ? uncommon() : 1;
        return u + o.b + o.i.a[0] + o.i.a[1];
    }

    static int dummy;
    static int dummy2;
    public static int uncommon() {
        return dummy % dummy2;
    }
}
