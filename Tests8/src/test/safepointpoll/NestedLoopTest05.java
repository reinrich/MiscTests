package test.safepointpoll;


public class NestedLoopTest05 {
    public static final    Object notNull = new Object();
    public static Object inLoc;
    public static Object outLoc;
    public static NestedLoopTest05[] gTable;
    public static NestedLoopTest05[] gTableCircular;
    public static NestedLoopTest05 staticResult;

    public NestedLoopTest05(NestedLoopTest05 next) {
        this.next = next;
    }
    public NestedLoopTest05() {
        this.next = null;
    }
    private void setNext(NestedLoopTest05 next) {
        this.next = next;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            gTable = new NestedLoopTest05[128];
            NestedLoopTest05 list = new NestedLoopTest05(new NestedLoopTest05());
            for (int i = 0; i < gTable.length; i++) {
                gTable[i] = list;
            }
            
            // table with circular list
            gTableCircular = new NestedLoopTest05[128];
            NestedLoopTest05 listTail = new NestedLoopTest05();
            NestedLoopTest05 listHead = new NestedLoopTest05(listTail);
            listTail.setNext(listHead); // circular list
            for (int i = 0; i < gTableCircular.length; i++) {
                gTableCircular[i] = listHead;
            }

            callNestedLoop();
            
        } catch (Throwable t) {throw new Error(t);}
    }


    private NestedLoopTest05 next;
    
    public static void nestedLoop(NestedLoopTest05[] src) {
        for (int j = 0; j < src.length; j++) {
            NestedLoopTest05 e = src[j];
            if (e != null) {
                while (e.next != null) {
                    e = e.next;
                }
                staticResult = e;
            }
        }
    }

    static void callNestedLoop() {
        for (int i = 0; i < 40000; i++) {
            nestedLoop(gTable);
        }
    }
}
