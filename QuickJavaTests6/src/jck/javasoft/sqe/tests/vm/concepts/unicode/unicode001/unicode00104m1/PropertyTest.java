package jck.javasoft.sqe.tests.vm.concepts.unicode.unicode001.unicode00104m1;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.IOException;

public class PropertyTest {

  public static String dojit_testMethod(String argv[], PrintStream out) {

        String oldProp = null;
        String msg = null;
        try {
            oldProp    = getOldProp();
            msg = null;
            xdojit_doThrow(msg);
        } catch (Throwable e) {
            //msg = "### Expected exception: " + e;
            msg = e.getMessage();
        }
        
        return oldProp;
    }
    private static String getOldProp() {
        return "RRRRROldProp";
    }
    
    private static void xdojit_doThrow(String msg) {
        throw new Error();
    }
    public static void main(String argv[]) {
        int times =Integer.parseInt(argv[0]);
        for (int i = 0; i < times; i++) {
            System.out.println("###### ITER:"+i);
            String oldProp = dojit_testMethod(argv, System.out);
            System.out.println("###### oldProp:"+oldProp);
        }
    }
} // end Class unicode00104m1
