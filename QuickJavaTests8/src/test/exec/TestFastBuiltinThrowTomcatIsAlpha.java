package test.exec;

public class TestFastBuiltinThrowTomcatIsAlpha {

    private static final int ITERATIONS = 40000;
    private static int INPUT_LENGTH = 100;
    private static int[] chars;
    private static boolean[] results;
    private static boolean[] IS_ALPHA;
    private static ArrayIndexOutOfBoundsException escapedExc;

    public static void main(String[] args) {
        setUp();
        runTest_dojit1();
    }

    private static void runTest_dojit1() {
        for (int i = 0; i < ITERATIONS; i++) {
            for (int j = 0; j < chars.length; j++) {
                try {
                    results[j] = is_alpha1_dontinline_dojit(chars[j]);
                } catch(ArrayIndexOutOfBoundsException e) {
                    results[j] = false;
                }
            }
        }
    }

    private static void runTest_dojit2() {
        for (int i = 0; i < ITERATIONS; i++) {
            for (int j = 0; j < chars.length; j++) {
                results[j] = is_alpha2a_dontinline_dojit(chars[j]);
            }
        }
    }

    // https://github.com/apache/tomcat/blob/26ba86cdbd40ca718e43b82e62b3eb49d004c3d6/java/org/apache/tomcat/util/http/parser/HttpParser.java#L266-L274
    private static boolean is_alpha1_dontinline_dojit(int i) {
        try {
            return IS_ALPHA[i];
        } catch(ArrayIndexOutOfBoundsException e) {
            escapedExc = e;
            return false;
//            throw e;
        }
    }

    // https://github.com/apache/tomcat/blob/26ba86cdbd40ca718e43b82e62b3eb49d004c3d6/java/org/apache/tomcat/util/http/parser/HttpParser.java#L266-L274
    private static boolean is_alpha2a_dontinline_dojit(int i) {
        try {
            return is_alpha2b_dontinline_dojit(i);
        } catch(ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    private static boolean is_alpha2b_dontinline_dojit(int i) {
        return IS_ALPHA[i];
    }

    private static void setUp() {
        chars = new int[INPUT_LENGTH];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = 300;
        }
        results = new boolean[INPUT_LENGTH];
        IS_ALPHA = new boolean[256];
    }

}
