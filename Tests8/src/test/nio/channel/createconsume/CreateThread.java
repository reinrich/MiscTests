package test.nio.channel.createconsume;

import java.io.RandomAccessFile;
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

            int waitCount = 1 << 20;
            RandomAccessFile raf = new RandomAccessFile(sharedObj.getFileName(), "rw");
            FileChannel channel = raf.getChannel();
            CreateConsumeTest cct = sharedObj;

            while (true) {
                MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 1024);
                cct.setMap(map);

                trace("waiting for map to become null");
                while (cct.getMap() != null) {
                    if (waitCount-- == 0) {
                        //traceAlways("still waiting...");
                        //trace("still waiting...");
                        waitCount = 1 << 10;
                        Thread.yield();
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
