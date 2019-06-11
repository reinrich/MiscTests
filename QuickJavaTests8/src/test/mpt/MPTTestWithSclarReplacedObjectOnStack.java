package test.mpt;

public class MPTTestWithSclarReplacedObjectOnStack {

    public static void main(String[] args) {
        int iterations = 1<<20;
        int thrCount = 0;
        while(iterations -- > 0) {
            testMethodWithScalarReplacedObject();
            thrCount++;
            if ((iterations & 1023) == 0) {
                try {
                    System.out.println("### started " + thrCount + " threads");
                    Thread.sleep(100);
                } catch (InterruptedException e) { /* ignored */ }
            }
        }
    }

    
    public static class ThreadStarterDummy {

        public void startNewThread(Thread thr) {
            thr.start();
        }

    }
    
    public static void testMethodWithScalarReplacedObject() {
        Thread thr = new Thread(new Runnable() {
            
            @Override
            public void run() {
                System.out.print("T");
            }
        });
        ThreadStarterDummy dummyStarter = new ThreadStarterDummy();
        dummyStarter.startNewThread(thr);
    }

}
