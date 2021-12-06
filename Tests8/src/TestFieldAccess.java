public class TestFieldAccess {
    public int iField;
    public static volatile int counter;
    public static volatile boolean doWait;

    public static void main(String[] args) {
        TestFieldAccess tt = new TestFieldAccess();
        doWait = true;
        boolean lDoWait = true;
        while (lDoWait) {
            lDoWait = doWait;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Continueing...");
        tt.iField = 42;
        tt.tryAccess();
    }

    public void tryAccess() {
        for (int i = 0; i < 100; i++) {
            if (iField == 42) {
                counter++;
            }
        }
    }
}
