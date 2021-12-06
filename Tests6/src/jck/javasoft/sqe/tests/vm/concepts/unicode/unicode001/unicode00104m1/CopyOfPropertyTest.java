package jck.javasoft.sqe.tests.vm.concepts.unicode.unicode001.unicode00104m1;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.IOException;

public class CopyOfPropertyTest {

  public static int testMethod(String argv[], PrintStream out) {

        String oldProp = null;
        try {
            oldProp    = System.getProperty("file.encoding");
            out.println("### oldProp:"+oldProp);
            doThrow();
        } catch (Throwable e) {
            out.println("### Expected exception: " + e);
        }
        
        out.println("### oldProp:"+oldProp);
        return 0;
    }
    private static void doThrow() throws IOException {
        throw new IOException();
    }
    public static void main(String argv[]) {
        int times =Integer.parseInt(argv[0]);
        for (int i = 0; i < times; i++) {
            System.out.println("###### ITER:"+i);
            int rc = testMethod(argv, System.out);
            System.out.println("###### RC:"+rc);
        }
    }
} // end Class unicode00104m1
