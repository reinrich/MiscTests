package test.nio.channel.createconsume;

import java.nio.MappedByteBuffer;

public class AccessingThread extends Thread {

    public CreateConsumeTest sharedObj;

    public AccessingThread(CreateConsumeTest cct) {
        this.sharedObj = cct;
    }

    public void run() {
        try {
            System.out.println("started " + this);
            int waitCount = 1 << 20;
            int traceCount = 1 << 10;
            MappedByteBuffer map;
            CreateConsumeTest cct = sharedObj;
            
            while (true) {
                while ((map = cct.getMap()) == null) {
                    // wait for the map...
                    if (waitCount-- == 0) {
                        //traceAlways("still waiting...");
                        //trace("still waiting...");
                        waitCount = 1 << 10;
                        Thread.yield();
                    }
                }

                map.put((byte)88);
                if (traceCount-- == 0) {
                    traceAlways("put 'X' into mapped file");
                    traceCount = 1 << 10;
                }
                cct.setMap(null);
            }
            
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    private void trace(String msg) {
        if (CreateConsumeTest.doTrace) {
            System.out.println("Accessor: " + msg);
        }
    }

    private void traceAlways(String msg) {
        System.out.println("Accessor: " + msg);
    }

    public String toString() {
        return "AccessThread #" + sharedObj.num; 
    }
}
