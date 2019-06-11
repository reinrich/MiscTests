package test.misc;

import java.io.File;
import java.io.IOException;

public class TestThreadDumpFileSystemCanonicalizeInfo {

    private static boolean alreadyPrinted;

    public static void main(String[] args) {
//        String path = "/priv/d0/p5€/../tmp";
//        String path = "c:\\work\\..\\tmp";
        String path = args[1];
        File ff = new File(path);
        String canonPath = getCanonicalPath(ff);
        System.out.println(path + " -> " + canonPath);
        while (true) {
            canonPath = getCanonicalPath(ff);
            if (!alreadyPrinted) {
                alreadyPrinted = true;
                System.out.println(path + " -> " + canonPath);
            }
        }
    }

    private static String getCanonicalPath(File ff) {
        try {
            return ff.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

}
