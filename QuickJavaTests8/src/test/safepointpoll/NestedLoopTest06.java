package test.safepointpoll;


public class NestedLoopTest06 {
    public static final    Object notNull = new Object();
    public static Object inLoc;
    public static Object outLoc;
    public static NestedLoopTest06[] gTable;
    public static NestedLoopTest06[] gNewTable;

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

    private NestedLoopTest06 next;
    
    public static void nestedLoop() {
        NestedLoopTest06[] src = gTable;
        NestedLoopTest06[] newTable = gNewTable;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++) {
            NestedLoopTest06 e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    NestedLoopTest06 next = e.next;
//                    int i = indexFor(e.hash, newCapacity);
                    int i = j;
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }

    static void callNestedLoop() {
        while (true) {
            nestedLoop();
            System.out.println("Calling nestedLoop");
        }
    }
}
