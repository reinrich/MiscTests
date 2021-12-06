package test.nio.channel.createconsumeslice.copy;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class CreateThread extends Thread {

    public CreateConsumeTest sharedObj;
    
    public CreateThread(CreateConsumeTest cct) {
        this.sharedObj = cct;
    }
    
    public void run() {
        try {
            System.out.println("started " + this);

            int waitCounter = 1 << 20;
            RandomAccessFile raf = new RandomAccessFile(sharedObj.getFileName(), "rw");
            FileChannel channel = raf.getChannel();
            CreateConsumeTest cct = sharedObj;
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 1024);

            while (true) {
                ByteBuffer slice = map.slice();
                slice.position(waitCounter & 0x7);
                cct.setMap(slice);

                trace("waiting for map to become null");
                waitCounter = 1 << 20;
                while (cct.getMap() != null) {
                    if (waitCounter-- == 0) {
                        //traceAlways("still waiting...");
                        //trace("still waiting...");
                        waitCounter = 1 << 20;
                        //Thread.yield();
                    }
                }
            }
        
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private void traceAlways(String msg) {
        System.out.println("Create: " + msg);
    }

    private void trace(String msg) {
        if (CreateConsumeTest.doTrace) {
            System.out.println("Create: " + msg);
        }
    }

    public String toString() {
        return "CreateThread #" + sharedObj.num; 
    }

}
