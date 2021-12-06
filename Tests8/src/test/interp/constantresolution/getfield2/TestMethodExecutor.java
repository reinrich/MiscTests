package test.interp.constantresolution.getfield2;

public class TestMethodExecutor extends Thread {

    public void run() {
        long testMethodCalls = 0;
        while (true) {
            long lastIterationNum = Main.numIterations;
            long res = Main.testMethodHolder.testMethodWithManyConstantPoolRefs();
            if (res != 0) {
                for (int i = 0; i < 100; i++) {
                    System.out.println("ERROR!!!!!!!!!!");
                    System.exit(2);
                }
            }
            if ((testMethodCalls++ & 0xf) == 0) {
                System.out.print(".");
            }
            
            Main.waitingThreads.incrementAndGet();
            while(Main.numIterations == lastIterationNum ) {
                Thread.yield();
            }
            Main.waitingThreads.decrementAndGet();
        }
    }
    
}
