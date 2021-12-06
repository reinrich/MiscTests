package test.mem.volatile_;

public class ReadAfterMonitorExitSimple {

    public static volatile ReadAfterMonitorExitSimple testObj;
    public volatile int  volIntA;
    public volatile int  volIntB;

    public ReadAfterMonitorExitSimple(int i) {
        volIntB = i;
    }

    public int testMethod(int inputVal) {
        synchronized (this) {
            volIntA = inputVal;
        }
        return volIntB;
    }
    
    public static void main(String[] args) {
        try {
            long checksum = 0;
            testObj = new ReadAfterMonitorExitSimple(1);

            for (int i = 0; i < 11000; i++) {
                checksum += testObj.testMethod(1);
            }

            Thread.sleep(100);
            
            for (int i = 0; i < 11000; i++) {
                checksum += testObj.testMethod(1);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
