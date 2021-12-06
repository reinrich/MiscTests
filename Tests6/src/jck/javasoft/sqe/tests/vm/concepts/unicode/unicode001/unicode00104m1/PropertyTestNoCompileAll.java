package jck.javasoft.sqe.tests.vm.concepts.unicode.unicode001.unicode00104m1;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.IOException;

public class PropertyTestNoCompileAll {

  public static void testMethod(String argv[], PrintStream out) {

        String oldProp = null;
        try {
            oldProp    = System.getProperty("file.encoding");
            if (out != null) out.println("### oldProp:"+oldProp);
            doThrow();
        } catch (IOException e) {
            if (out != null) out.println("### Expected exception: " + e);
        }
        
        if (out != null) out.println("### oldProp:"+oldProp);
    }
    private static void doThrow() throws IOException {
        throw new IOException();
    }
    public static void main(String argv[]) {
        int times =Integer.parseInt(argv[0]);
        for (int i = 0; i < times; i++) {
            testMethod(argv, null);
        }
        System.out.println("###### LAST ITER");
        testMethod(argv, System.out);
    }
} // end Class unicode00104m1
