package test.safepointpoll;

public class NestedLoopTest01 {
    public static final    Object notNull = new Object();
    public static volatile Object inLoc;
    public static volatile Object outLoc;;

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Thread thread = new Thread() {
                public void run() {
                    callNestedLoop();
                }
            };
            thread.start();

            for (int i = 0; i < (100); i++) {
                while(inLoc == null) {}
                Thread.sleep(20);
                inLoc = null; // terminates nested loop
            }
            System.out.println("DONE !!!");
        } catch (Throwable t) {throw new Error(t);}
    }
    
    public static void nestedLoop(int iters) {
        for (int i = 0; i < iters; i++) {
            Object o = inLoc;
            while (o != null) {
                outLoc = o;
                o = inLoc;
            }
            inLoc = notNull;
        }
    }

    static void callNestedLoop() {
        while (true) {
            nestedLoop(10);
            System.out.println("Calling nestedLoop");
        }
    }
}
