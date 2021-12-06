package test.misc;

public class TestRegisterNmethodRace {

    private static final int COMP_LEVEL_FULL_OPTIMIZATION_LIMIT = 1 << 20;
    private static final int COMP_LEVEL_FULL_PROFILE_LIMIT = 300;
    private static final int LOOP_LIMIT = 1 << 20;

    private int initVal;

    public TestRegisterNmethodRace(int val) {
        initVal = val;
    }

    public static void main(String[] args) {
        // try {
        // Thread.sleep(100);
        // } catch (InterruptedException e) {
        // }
        //
        TestRegisterNmethodRace obj = new TestRegisterNmethodRace(1);
        
        message("triggering jit compilation on level 3");
        obj.getTestMethodCompiledLevel3();
        message("wait for jit compilation on level 3");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        message("waiting for jit done");

        obj.triggerAccessOfUnloadedVolatileField();
        message("waiting for access of unloaded volatile field");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        message("triggering jit compilation on level 4");
        obj.getTestMethodCompiledLevel4();
        message("!!!! wait for jit compilation on level 4");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        message("waiting for jit done");

        invalidateCompiledCode();
        obj.getTestMethodCallerCompiled();
        message("wait for jit to compile testMethodCaller()");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        message("waiting for jit done");
        obj.testMethodCaller();
    }

    public void triggerAccessOfUnloadedVolatileField() {
        Thread t1 = new Thread(() -> {
            message("access unloaded volatile field");
            theTestMethodA(true);
        });
        t1.start();
    }

    public static void invalidateCompiledCode() {
        message("invalidate comp level 4 nmethod by classloading");
        new TestRegisterNmethodRaceSubClass(1);
    }

    private static void message(String msg) {
        System.out.flush();
        System.out.println("++++++++++++ Java:" + msg);
        System.out.flush();
    }

    public void getTestMethodCompiledLevel3() {
        int limit = COMP_LEVEL_FULL_PROFILE_LIMIT;
        for (int i = 0; i < limit; i++) {
            theTestMethodA(false);
        }
    }

    public void getTestMethodCompiledLevel4() {
        int limit = COMP_LEVEL_FULL_OPTIMIZATION_LIMIT;
        for (int i = 0; i < limit; i++) {
            theTestMethodA(false);
        }
    }

    public void getTestMethodCallerCompiled() {
        int limit = COMP_LEVEL_FULL_OPTIMIZATION_LIMIT;
        for (int i = 0; i < limit; i++) {
            testMethodCaller();
        }
    }

    private void testMethodCaller() {
        theTestMethodA(false);
    }

    static class Unloaded {
        public static volatile int volatileField = 1;
        
        static {
            System.out.println("R R R");
        }
    }

    public int theTestMethodA(boolean accessUnloadedVolatileField) {
        int res = getInitVal();
        if (accessUnloadedVolatileField) {
            res += Unloaded.volatileField;
        }
        return res;
    }

    public int getInitVal() {
        return initVal;
    }
}
