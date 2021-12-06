package jck.javasoft.sqe.tests.vm.concepts.unicode.unicode001.unicode00104m1;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.IOException;

//interface iunicode {
\u0069\u006e\u0074\u0065\u0072\u0066\u0061\u0063\u0065 i\u0075\u006e\u0069\u0063\u006f\u0064\u0065 {
    // int y = 0;
    \u0069\u006e\u0074\u0020\u4e02\u0020\u003d\u0020\u0030\u003b

    public int geti(int j);
}

//interface kunicode {
\u0069\u006e\u0074\u0065\u0072\u0066\u0061\u0063\u0065\u0020k\u0075\u006e\u0069\u0063\u006f\u0064\u0065 {
    // int x = 0;
    \u0069\u006e\u0074\u0020\u9de5\u0020\u003d\u0020\u0030\u003b

    public int seti(int j);
}

//class exunicode {
\u0063\u006c\u0061\u0073\u0073\u0020\u0065\u0078\u0075\u006e\u0069\u0063\u006f\u0064\u0065\u0020\u007b
//    public int getsomething();
    public int get\u0447\u0435\u0433\u043e\u043d\u0438\u0431\u0443\u0434\u044c() {
        return 0;
    }
}


//public class unicode00104m1 extends exunicode implements iunicode, kunicode {
\u0070\u0075\u0062\u006c\u0069\u0063 \u0063\u006c\u0061\u0073\u0073 unicode00104m1 \u0065\u0078\u0074\u0065\u006e\u0064\u0073\u0020ex\u0075\u006e\u0069\u0063\u006f\u0064\u0065\u0020implem\u0065\u006e\u0074\u0073 iunicode, kunicode\u0020\u007b

//    int geti(int i) {
    public int \u0067\u0065\u0074\u0069(int i) {
        return 0;
    }
//    int seti(int i) {
    public int seti(\u0069\u006e\u0074\u0020\u9de0) {
        return 0;
    }

  public static int run(String argv[], PrintStream out) {

        int    rez     = 0;
        unicode00104m1 \u043d\u043e\u0432 = new unicode00104m1();
        int    i = \u043d\u043e\u0432.seti(rez),
               j = \u043d\u043e\u0432.get\u0447\u0435\u0433\u043e\u043d\u0438\u0431\u0443\u0434\u044c();

// The part of code from this place to "return" is very important for coverage purpose
// !!!!!!! DO NOT DELETE THIS CODE !!!!!!!!
// and do not change it without the coverage measurement
        int    itmp;
        Process pr;
        InputStream in;
        String oldProp = null;
        try {
            oldProp    = System.getProperty("file.encoding");
            String sac = "/bin/\u0065\u006E\u0076\u0800"; // \u0065\u006E\u0076\u0800 -> env?
            System.setProperty("file.encoding", "UTF-8");
            pr = Runtime.getRuntime().exec(sac);
            in = pr.getInputStream();
            while ((itmp = in.read()) != -1){}
            in.close();
            pr.waitFor();
        } catch (SecurityException e) {
            out.println("It is impossible to setup the test.");
        } catch (IOException e) {
            out.println("Expected exception: " + e);
        } catch (Throwable e) {
            out.println("Unexpected exception: " + e);
        //    rez = 2;  we checks that java understand an unicode
        //    the result of execution do not affect to the check of assertion
        }
        try {
            if(oldProp != null)
                System.setProperty("file.encoding", oldProp);
        } catch (Throwable e) {
            out.println("Exception: " + e);
        }
        return rez;
    }
    public static void main(String argv[]) {
        int times =Integer.parseInt(argv[0]);
        for (int i = 0; i < times; i++) {
            System.out.println("ITER:"+i);
            int rc = run(argv, System.out);
            System.out.println("RC:"+rc);
        }
    }
} // end Class unicode00104m1
