package test.misc;

public class EndlessLoop2 {

    public long field;
    public long loopCount = 1L << 60;

    public static void main(String[] args) {
        new EndlessLoop2().run();
    }

    public void run() {
        System.out.println("Java: ENDLESSLOOP");
        while (loopCount -- > 0) {
            field++;
        }
    }

}
