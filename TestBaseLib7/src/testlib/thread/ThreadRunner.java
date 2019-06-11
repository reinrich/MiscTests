package testlib.thread;

public class ThreadRunner implements Runnable {

    @Override
    public void run() {
        int count = 100;
        while (true) {
            Thread tt = new Thread();
            tt.start();
            while(tt.isAlive()) {
                try {
                    tt.join(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            if (count-- == 0) {
                count = 100;
                System.out.println("Started/joined " + count + " threads");
            }
        }
    }

}
