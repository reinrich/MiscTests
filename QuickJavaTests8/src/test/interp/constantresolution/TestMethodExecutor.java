package test.interp.constantresolution;

public class TestMethodExecutor extends Thread {

    public void run() {
        long testMethodCalls = 0;
        while (true) {
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
            
            Main.waitForNextIteration();
        }
    }
    
}
