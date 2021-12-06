package test.lambda;

public class LambdaTest {

    public static void main(String[] args) {
        LambdaTest test = new LambdaTest();
        test.doTest();
        test.doTest2();
    }

    private int fourtyTwoFIELD = 43;
    
    interface IntegerMath {
        int operation(int a, int b);   
    }
    
    interface myRunnable {
        void run();
    }
    
    public void doTest() {
        System.out.println("### doTest");
        System.out.println("getClass() -> " + getClass());
        Runnable func = () -> {
            System.out.println("fourtyTwo: " + fourtyTwoFIELD);
            System.out.println("getClass() -> " + getClass());   
        };
        System.out.println("func:"+func);
        System.out.println("func.getClass():"+func.getClass());
        Thread thread = new Thread(func);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {/* ignored */}
    }

    public void doTest2() {
        System.out.println("### doTest2");
        Thread thread = new Thread() {
                public void run() {
                    System.out.println("fourtyTwo: " + fourtyTwoFIELD);
                    System.out.println("getClass() -> " + getClass());   
                }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {/* ignored */}
    }

}
