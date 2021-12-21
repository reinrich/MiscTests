package test.loom;


// javac -d classes --enable-preview --release 18 ~/priv/git/reinrich/MiscTests/Tests/src/test/loom/TestStartVirtual.java
// java --enable-preview -cp classes test.loom.TestStartVirtual
public class TestStartVirtual {

    public static void main(String[] args) {
        Thread t = Thread.startVirtualThread(() -> {
            System.out.println("Hello World");
            System.out.println("Hello World");
            System.out.println("Hello World");
        });
        try {
            t.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
