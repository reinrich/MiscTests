package testlib;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class TracingBackend implements Tracing {

    public static final Tracing INSTANCE;
    private static final int TRC_LVL;

    static {
        INSTANCE = new TracingBackend();
        String str = System.getProperty("gcOpts.trc_lvl");
        int val = 3;
        try {
            val = Integer.valueOf(str);
        } catch(Throwable t) { /* ignore */ }
        TRC_LVL = val;
    }

    private int indentation;

    private static String[] indentationStrings = new String[80];

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

    public void logIncInd() {
        indentation += 2;
    }

    public void logDecInd() {
        indentation -= 2;
    }

    public void log0(Object msg) {
        log(0, "###### " + Thread.currentThread() + " " + msg);
    }
    public void log() {
        log(1, "");
    }
    public void log(Object msg) {
        log(1, msg);
    }

    public void log(int level, Object msg) {
        if (level <= TRC_LVL) {
            printIndentation();
            System.out.println(msg);;
        }
    }

    public boolean trcActive(int level) {
        return level <= TRC_LVL;
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
                log(ff.getName() + ": " + val);
            }
        }
    }

    private static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe)field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long addressOf(Object o) throws Exception {
        Object[] array = new Object[] {o};

        long baseOffset = unsafe.arrayBaseOffset(Object[].class);
        int addressSize = unsafe.addressSize();
        long objectAddress;
        switch (addressSize) {
            case 4:
                objectAddress = unsafe.getInt(array, baseOffset);
                break;
            case 8:
                objectAddress = unsafe.getLong(array, baseOffset);
                break;
            default:
                throw new Error("unsupported address size: " + addressSize);
        }

        return(objectAddress);
    }
}
