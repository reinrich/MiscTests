package test.misc;

public class EndlessLoop3 {

    public long field;
    public long loopCount = 1L << 60;

    public static void main(String[] args) {
        new EndlessLoop3().run();
    }

    public void run() {
        long checksum = 0;
        for (int i = 0; i < 100; i++) {
            checksum += testMethod(10000);
        }
        checksum += testMethod(1L << 60);
    }

    public long testMethod(long l) {
        // TODO Auto-generated method stub
        return 0;
    }

}
