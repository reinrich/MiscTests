package test.controlflow;

public class SimpleLoop {

    public static void main(String[] args) {
        long iterations = 10;
        for (int i = 0; i < 100000; i++) {
            testMethod_01(iterations);
        }
    }

    public static void testMethod_01(long iterations) {
        for (int i = 0; i < iterations; i++) {
            // empty
        }
    }

}
