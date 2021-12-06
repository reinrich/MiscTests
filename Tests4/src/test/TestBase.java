package test;

public class TestBase {

    public static final int K = 1<<10;
    public static final int M = 1<<20;

    private int trcLevel;
    private int indentation;

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


    public void trcIncInd() {
        indentation += 2;
    }

    public void trcDecInd() {
        indentation -= 2;
    }

    public void trcMsg() {
        trcMsg(0, "");
    }
    public void trcMsg(Object msg) {
        trcMsg(0, msg);
    }

    public void trcMsg(int level, Object msg) {
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

}
