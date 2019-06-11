package test.nio.channel.createconsume;

import java.nio.MappedByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateConsumeTest {

    public static final boolean doTrace = false;
    public int num;
    public MappedByteBuffer map;

    public CreateConsumeTest(int num) {
        this.num = num;
    }

    public static void main(String[] args) {
        try {
            int numPairs = Integer.parseInt(args[0]);
            for (int i=0; i < numPairs; i++) {
                CreateConsumeTest sharedObj = new CreateConsumeTest(i);
                CreateThread createThread = new CreateThread(sharedObj);
                createThread.start();
                AccessingThread accThread = new AccessingThread(sharedObj);
                accThread.start();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public String getFileName() {
        return "/tmp/dummyfile"+num;
    }

    public void setMap(MappedByteBuffer newMap) {
        trace("setting new map: " + newMap);
        this.map = newMap;
    }
    
    public MappedByteBuffer getMap() {
        return map;
    }

    private void trace(String msg) {
        if (CreateConsumeTest.doTrace) {
            System.out.println("Test: " + msg);
        }
    }
}
