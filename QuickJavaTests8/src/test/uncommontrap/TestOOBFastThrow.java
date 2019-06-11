package test.uncommontrap;

public class TestOOBFastThrow {

    private static final int LENGTH = 200;

    public static void main(String[] args) {
        boolean[] arr = {false, true, false};

        System.out.println("\n\n\n        STARTED\n\n\n");
        for (long i = 0; i < 1L<<22; i++) {
            testMethod_dontinline2(3, null);
        }
        System.out.println("\n\n\n        FINISHED\n\n\n");
    }

    public static boolean testMethod_dontinline(int c, boolean[] ascii_alpha) {
        try {
            return ascii_alpha[c];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public static boolean testMethod_dontinline2(int c, boolean[] ascii_alpha) {
        try {
            if (ascii_alpha != null) {
                try {
                    if ((c >= 0) && (c < ascii_alpha.length)) {
                        return ascii_alpha[c];
                    } else {
                        throw new ArrayIndexOutOfBoundsException(c);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    return false;
                }
            } else {
                throw new NullPointerException();
            }
        } catch (NullPointerException npe) {
            return false;
        }
    }
}
