package rr.load;

public class CPULoadGenerator {

    public static void main(String[] args) {
        int nThreads = Integer.parseInt(args[0]);
        Thread t = null;
        for (int i = 0; i < nThreads; i++) {
            t = new Thread(() -> {
                System.out.println("Started thread " + Thread.currentThread().getName());
                while (true) {}
                }, "Load Generator " + i);
            t.start();
        }
        try {
            if (t != null) {
                t.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
