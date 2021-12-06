package test.misc;

public class EndlessLoop {

    public static final long ITERATIONS = 1000000;

    public static void main(String[] args) {
        System.out.println("### Entering endless loop");
        long totalIterations = 0;
        while (true) {
            doLoop(ITERATIONS);
            totalIterations += ITERATIONS;
            System.out.println("### Sleeping after " + totalIterations + " iterations");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { /* ignored */ }
        }
    }

    public static void doLoop(long iterations) {
        while(iterations-- > 0); // busy wait
    }

}
