package testlib;

import java.lang.reflect.Field;
import java.util.Scanner;

public class TestBase implements Tracer {

    public static final int K = 1<<10;
    public static final int M = 1<<20;

    private int trcLevel;
    private int indentation;
    private Scanner sysInScanner;

    private static String[] indentationStrings = new String[80];

    public TestBase() {
        this(0);
    }

    public TestBase(int verboseLevel) {
        this.trcLevel = verboseLevel;
    }

    public void TODO(String msg) {
        System.out.println("*** TODO " + (msg != null ? msg + " " : "") + "***");
        System.out.println();
        Thread.dumpStack();
        System.exit(1);
    }
    public void TODO() {
        TODO(null);
    }

    private void printIndentation() {
        if (indentation > 0) {
            String indentStr = indentationStrings[indentation];
            if (indentStr == null) {
                char[] tmp = new char[indentation];
                for (int i = 0; i < tmp.length; i++) {
                    tmp[i] = ' ';
                }
                indentStr = indentationStrings[indentation] = new String(tmp);
            }
            System.out.print(indentStr);
        }
    }


    public void msgIncInd() {
        indentation += 2;
    }

    public void msgDecInd() {
        indentation -= 2;
    }

    public void msg() {
        msg(0, "");
    }
    public void msg(Object msg) {
        msg(0, msg);
    }

    public void msg(int level, Object msg) {
        if (level <= trcLevel) {
            printIndentation();
            System.out.println(msg);;
        }
    }

    public float rndF(float f, int digits) {
        return Math.round(f*digits*10)/(digits*10f);
    }

    public static int rndF(float f) {
        return Math.round(f);
    }

    public boolean trcActive(int level) {
        return level <= trcLevel;
    }

    public void trcInstanceFields(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field ff = fields[i];
            if (!java.lang.reflect.Modifier.isStatic(ff.getModifiers())) {
                Object val;
                try {
                    val = ff.get(obj);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    val = "n.a. (IllegalArgumentException | IllegalAccessException)";
                }
                msg(ff.getName() + ": " + val);
            }
        }
    }

    public void waitForEnter(String prompt) {
        msg(prompt);
        waitForEnter();
    }

    public void waitForEnter() {
        try {
            do {
                System.in.read();
            } while (System.in.available() > 0);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public char getCharacter(String prompt) {
        msg(prompt);
        return getCharacter();
    }

    public synchronized char getCharacter() {
        char ch = 0;
        try {
            do {
                if (sysInScanner == null) {
                    sysInScanner = new Scanner(System.in);
                }
                ch  = sysInScanner.nextLine().charAt(0);
            } while (System.in.available() > 0);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return ch;
    }
}
