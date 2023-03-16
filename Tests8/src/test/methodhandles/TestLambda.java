package test.methodhandles;

public class TestLambda {

    static boolean printStack = false;

    private interface TestInterface {
        int testMethod(int a, int b, Object c);
    }

    public static void main(String[] args) {
        try {
            TestInterface add2ints = (a, b, c) -> { Thread.dumpStack(); return a + b; };
            System.out.println("Result of lambda expression: " + add2ints.testMethod(1, 2, null));
        } catch (Throwable e) {
            throw new Error(e);
        }
    }
}
