package test.nio.channel.createconsumeslice;

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

            final int traceCountShift = 20;
            int traceCount = 1 << traceCountShift;

            RandomAccessFile raf = new RandomAccessFile(sharedObj.getFileName(), "rw");
            FileChannel channel = raf.getChannel();
            CreateConsumeTest cct = sharedObj;
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 1024);

            while (true) {
                ByteBuffer slice = map.slice();
//                slice.position(waitCounter & 0x7);
                cct.setMap(slice);

                if (traceCount-- == 0) {
//                    traceAlways("Created new slices");
                    traceCount = 1 << traceCountShift;
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
