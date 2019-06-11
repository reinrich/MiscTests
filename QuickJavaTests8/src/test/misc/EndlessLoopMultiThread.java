package test.misc;

public class EndlessLoopMultiThread implements Runnable {

    public static volatile boolean doLoop=true;
    private int threadNum;

    public EndlessLoopMultiThread(int i) {
        threadNum = i;
    }

    public static void main(String[] args) {
        int threadCount = 2;
        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(new EndlessLoopMultiThread(i));
            t.start();
        }
    }

    public void run() {
        int i = 0;
        while (doLoop) {
            try {
                Thread.sleep(1000);
                switch (threadNum) {
                case 0:
                    brk0();
                    break;
                case 1:
                    brk1();
                    break;
                }
                System.out.println("Thread " + threadNum + ": " + i++);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private void brk0() {
        // set breakpoint
    }

    private void brk1() {
        // set breakpoint
    }
}
