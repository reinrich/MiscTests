package test.safepointpoll;

public class NestedLoopTest02 {
    public static volatile Object outLoc;;
    public static MyList[] circularListArray;
    public static MyList[] nonCircularListArray;

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            createLists();
            Thread thread = new Thread() {
                public void run() {
                    callNestedLoop();
                }
            };
            thread.start();

            for (int i = 0; i < (100); i++) {
//                while(inLoc == null) {}
                Thread.sleep(20);
//                inLoc = null; // terminates nested loop
            }
            System.out.println("DONE !!!");
        } catch (Throwable t) {throw new Error(t);}
    }
    
    private static void createLists() {
        // TODO Auto-generated method stub
        
    }

    public static void nestedLoop(MyList[] lists) {
        for (int i = 0; i < lists.length; i++) {
            MyList e = lists[i];
            while (e != null) {
                outLoc = e;
                e = e.next;
            }
        }
    }

    static void callNestedLoop() {
        while (true) {
//            nestedLoop(100);
            System.out.println("Calling nestedLoop");
        }
    }
}

class MyList {
    MyList next;

    public MyList(MyList next) { this.next = next; }
}