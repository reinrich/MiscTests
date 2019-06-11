package test.misc;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import testlib.TestBase;

public class TestThreadInNative extends TestBase implements Runnable {
    public FileInputStream fis;
    private int count;

    public TestThreadInNative(String inputFileName) {
        System.out.println("Opening " + inputFileName);
//        try {
////            fis = new FileInputStream(inputFileName);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            System.exit(1);
//        }
        fis = new FileInputStream(FileDescriptor.in);
    }

    public static void main(String[] args) {
        Runnable test = new TestThreadInNative(args[0]);
        test.run();
    }

    public void run() {
        long count=0;
        while(true) {
            count++;
            dontinline_testMethod();
            if ((count & ((1L << 14)-1)) == 0) {
                System.out.println("Called test method " + count + " times");
            }
        }
    }

//    public void dontinline_testMethod() {
////        waitForEnter();
//        try {
//            System.in.read();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    public int dontinline_testMethod() {
        int f2 = 42;
        int val = 0;
        try {
            val = fis.read();
//            val = System.in.read();
            count++;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return val + f2;
  }
}
