package test.loom;


// javac -d classes --enable-preview --release 18 ~/priv/git/reinrich/MiscTests/Tests/src/test/loom/TestStartVirtual.java
// java --enable-preview -cp classes test.loom.TestStartVirtual
public class TestStartVirtual implements Runnable {

    public static void main(String[] args) {
        Thread t = Thread.startVirtualThread(new TestStartVirtual());
        try {
            t.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Hello Virtual World!");
        System.out.println("Hello Virtual World!");
        System.out.println("Hello Virtual World!");
    }

}
