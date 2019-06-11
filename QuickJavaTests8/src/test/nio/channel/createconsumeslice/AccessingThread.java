package test.nio.channel.createconsumeslice;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

public class AccessingThread extends Thread {

    public CreateConsumeTest sharedObj;

    public AccessingThread(CreateConsumeTest cct) {
        this.sharedObj = cct;
    }

    public void run() {
        try {
            System.out.println("started " + this);
            int waitCounter = 1 << 20;
            final int traceCountShift = 26;
            long traceCount = 1 << traceCountShift;
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
                map = cct.getMap();
                map.put(0, (byte)90);
                if (traceCount-- == 0) {
//                    traceAlways("put 'Z' into mapped file");
                    traceCount = 1 << traceCountShift;
                }
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
