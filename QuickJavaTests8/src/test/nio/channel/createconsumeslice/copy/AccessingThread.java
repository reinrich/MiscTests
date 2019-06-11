package test.nio.channel.createconsumeslice.copy;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

public class AccessingThread extends Thread {

    public CreateConsumeTest sharedObj;

    public AccessingThread(CreateConsumeTest cct) {
        this.sharedObj = cct;
    }

//    public volatile dummy;
    
    public void run() {
        try {
            System.out.println("started " + this);
            int waitCounter = 1 << 20;
            int traceCount = 1 << 10;
            ByteBuffer map;
            CreateConsumeTest cct = sharedObj;
            
            while ((map = cct.getMap()) == null) {
                // wait for the map...
                if (waitCounter-- == 0) {
                    //traceAlways("still waiting...");
                    //trace("still waiting...");
                    waitCounter = 1 << 20;
                    //Thread.yield();
                }
            }
            while (true) {
                System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//                waitCounter = 1 << 20;

                map = cct.getMap();
                map.put((byte)90);
                if (traceCount-- == 0) {
                    traceAlways("put 'Z' into mapped file");
//                    traceAlways("map: "+map);
                    traceCount = 1 << 16;
                }
//                cct.setMap(null);
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
