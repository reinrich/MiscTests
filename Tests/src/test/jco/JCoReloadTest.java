package test.jco;

import java.io.PrintStream;

import test.classloading.DirectClassLoader;
import test.classloading.PredicateClassLoader;

public class JCoReloadTest implements Runnable {

    public static final PrintStream OUT = System.out;

    public static void main(String[] args) {
        try {
            OUT.println("### 1st load of JCo:");
            staticRunTest();
            doGCs();
            OUT.println("Trigger heap dump now if you want to inspect loaded classes.");
            Thread.sleep(100000);
            OUT.println("### 2nd load of JCo:");
            staticRunTest();
            doGCs();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static class Canary {
        static {
            OUT.println("Canary initialized");
        }
    }

    private static void staticRunTest() throws Throwable {
//        Class cls = JCoReloadTest.class;
//        String canaryName = cls.getName() + "$Canary";
//        DirectClassLoader ldr = new PredicateClassLoader(cls.getClassLoader(), (name) -> name.startsWith("test.jco"));
//        ldr.newInstance(canaryName);
        Thread.ofPlatform().start(new JCoReloadTest()).join();
    }

    private static void doGCs() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            System.gc();
            Thread.sleep(100);
        }
    }

    @Override
    public void run() {
        Class cls = getClass();
        String wrapperName = cls.getName() + "$JcoWrapper";

        DirectClassLoader ldr =
                new PredicateClassLoader(cls.getClassLoader(), (name) -> {
                    // return name.startsWith("com.sap.conn.jco");
                    return name.startsWith("com.sap");
                });
        try {
            ldr.newInstance(wrapperName);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static class JcoWrapper {
        static {
            printJcoVersion();
        }

        private static void printJcoVersion() {
            String fullVersion = com.sap.conn.jco.About.getFullVersion();
            String[] lines = fullVersion.split("\n");
            for (String line : lines) {
                OUT.println(line);
                if (line.startsWith("Main-Class")) {
                    break;
                }
            }
        }
    }

//    private static void printJcoVersion() {
//        String fullVersion = About.getFullVersion();
//        String[] lines = fullVersion.split("\n");
//        for (String line : lines) {
//            OUT.println(line);
//            if (line.startsWith("Main-Class")) {
//                break;
//            }
//        }
//    }
}
