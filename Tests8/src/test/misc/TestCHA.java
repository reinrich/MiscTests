package test.misc;

public class TestCHA implements Runnable {

    public static volatile TestCHA receiver;

    private static long duration;

    private static TestCHA theTestObject;

    private static volatile boolean checkerNotRunning;
    private static final long ms = 1;
    private static final long s  = 1000*ms;
    private static final int LOOP_ITERATIONS = 1000_000;
    

    public static void main(String[] args) {
        theTestObject = new TestCHA();
        
        //////// setup
        receiver = theTestObject;
        
        //////// warm-up
        msg("### warmup");
        duration = 1*s;
        testMethod();
        testMethod();
        testMethod();
        
        //////// run test
        msg("### test run");
        duration = 1000*s;
        Thread checker = new Thread(theTestObject);
        checkerNotRunning = false;
        synchronized (theTestObject) {
            checker.start();
            while (checkerNotRunning) {
                try {
                    theTestObject.wait();
                } catch (InterruptedException e) {/* ignored */}
            }
        }

        // let checker run for a while
        try {
            Thread.sleep(1000*s);
        } catch (InterruptedException e) {/* ignored */}
        
        msg("### replacing receiver object");
//        receiver = new TestCHAUnloadedSubClass();
        new TestCHAUnloadedSubClass();
        
        // wait again to see, if checker finds an error
        try {
            Thread.sleep(2*s);
        } catch (InterruptedException e) {/* ignored */}
    }

    private static void msg(String theMsg) {
        System.out.println(theMsg);
        System.out.flush();
        System.err.println(theMsg);
        System.err.flush();
    }

    @Override
    public void run() {
        long tsStart = System.currentTimeMillis();
        msg("### checker thread is running");
        synchronized (theTestObject) {
            checkerNotRunning = false;
            theTestObject.notify();
        }        
        testMethod();
        msg("### checker is exiting after " + (System.currentTimeMillis() - tsStart)/1000 + "s");
    }

    public static void testMethod() {
        long tsStart = System.currentTimeMillis();
        while (System.currentTimeMillis() - tsStart < duration) {
            for (int i = 0; i < LOOP_ITERATIONS; i++) {
                receiver.assertType();
            }
        }
    }

    public void assertType() {
        Class<? extends TestCHA> thisClass = this.getClass();
        if (thisClass != TestCHA.class) {
            throw new Error();
        }
    }

}

class TestCHAUnloadedSubClass extends TestCHA {
    
    public TestCHAUnloadedSubClass() {
        TestCHA.receiver = this;
    }
    
    @Override
    public void assertType() {
        Class<? extends TestCHA> thisClass = this.getClass();
        if (thisClass != TestCHAUnloadedSubClass.class) {
            throw new Error();
        }
    }
}