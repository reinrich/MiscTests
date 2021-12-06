package test.safepointpoll;

public class NestedLoopTest03 {
    public static final    Object notNull = new Object();
    public static volatile Object inLoc;
    public static volatile Object outLoc;
    public static volatile boolean doLoop;
    public static volatile boolean looping;

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
            doLoop = true;
            thread.start();

            for (int i = 0; i < (10); i++) {
                for (int j = 0; j < (10); j++) {
                    while(inLoc == null) {}
                    Thread.sleep(20);
                    inLoc = null; // terminates nested loop
                    System.out.println("XXXX "+i+"/"+j);
                }
                doLoop = false;
                while(looping) {
                    inLoc = null;
                    Thread.sleep(1);
                }
                doLoop = true;
            }
            System.out.println("DONE !!!");
        } catch (Throwable t) {throw new Error(t);}
    }
    
    public static void nestedLoop() {
        while (doLoop) {
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
            looping = true;
            nestedLoop();
            looping = false;
            System.out.println("Calling nestedLoop");
        }
    }
}
