import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

// Monomorphic Virtual Call
// Declared (static) receiver is abstract.
// Single concrete receiver from other classloader L.
// L is kept alive by nmethod N with opt virt. call while on stack because of dependencies
// L becomes unreachable when nmethod N is not on stack. N is made not entrant then.

public class CallAbstractVirtualSingleConcreteReceiverInOtherLoader {
    public static ReferenceQueue queue;
    public static PhantomReference prRecv;
    public static PhantomReference prLoader;

    public static boolean callSystemGCWithTestMethodOnStack;
    public static DeclaredReceiver recv;

    public static void main(String[] args) {
        new CallAbstractVirtualSingleConcreteReceiverInOtherLoader().runTest();
    }

    // Class with abstract method
    public static abstract class DeclaredReceiver {
        public abstract int testMethod_callee();
    }

    // Class with single concrete receiver. To be loaded by other classloader.
    public static class ConcreteReceiverR1 extends DeclaredReceiver {
        @Override
        public int testMethod_callee() {
            return 0;
        }
    }

    public void runTest() {
        try {
            runTest(getClass().getName() + "$ConcreteReceiverR1");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void runTest(String receiverClassName) throws Throwable {
        waitForEnter("Press enter to to start test with '" + receiverClassName + "' as receiver");
        DirectClassLoader loader = new DirectClassLoader(getClass().getClassLoader());
        recv = (DeclaredReceiver) loader.newInstance(receiverClassName);
        queue = new ReferenceQueue();
        prRecv = new PhantomReference(recv, queue);
        prLoader = new PhantomReference(loader, queue);
        int checksum = 0;
        for (int i=0; i<30_000; i++) {
            checksum += testMethod_opt_virt_call_dojit(recv);
        }
        System.out.println("checksum:" + checksum);

        callSystemGCWithTestMethodOnStack = true;
        loader = null;
        recv = null;
        boolean caughtNpe = false;
        try {
            checksum += testMethod_opt_virt_call_dojit(recv);
        } catch (NullPointerException npe) {
            caughtNpe = true;
        }
        if (!caughtNpe) throw new Error("No NPE");

        waitForEnter("Press enter to call System.GC() (testmethod _not_ on stack)");
        PhantomReference pr = null;
        int gcCount = 0;
        while (gcCount++ < 7) {
            System.gc();
            while ((pr = (PhantomReference) queue.remove(100)) != null) {
                log((pr == prLoader ? "Loader" : "Receiver") + " is unreachable");
            }
        }
    }

    public static void maybeCallSystemGC() {
        if (callSystemGCWithTestMethodOnStack) {
            waitForEnter("Press enter to call System.GC() (testmethod _is_ on stack)");
            PhantomReference pr = null;
            int gcCount = 0;
            while (gcCount++ < 7) {
                System.gc();
                try {
                    while ((pr = (PhantomReference) queue.remove(100)) != null) {
                        log((pr == prLoader ? "Loader" : "Receiver") + " is unreachable");
                    }
                } catch (IllegalArgumentException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int testMethod_opt_virt_call_dojit(DeclaredReceiver receiver) {
        maybeCallSystemGC();
        return receiver.testMethod_callee();
    }

    public static void waitForEnter(String prompt) {
        log(prompt);
        waitForEnter();
    }

    /**
     * Read the bytes of a class that would actually be loaded by the parent and define the class directly.
     */
    public static class DirectClassLoader extends ClassLoader {

        public Class<?> findClass(String className) throws ClassNotFoundException {
            synchronized (getClassLoadingLock(className)) {
                // First, check if the class has already been loaded
                Class<?> c = findLoadedClass(className);
                if (c == null) {
                    try {
                        String binaryName = className.replace(".", "/") + ".class";
                        URL url = getParent().getResource(binaryName);
                        if (url == null) {
                            throw new IOException("Resource not found: '" + binaryName + "'");
                        }
                        InputStream is = url.openStream();
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                        int nRead;
                        byte[] data = new byte[16384];
                        while ((nRead = is.read(data, 0, data.length)) != -1) {
                            buffer.write(data, 0, nRead);
                        }

                        byte[] b = buffer.toByteArray();
                        c = defineClass(className, b, 0, b.length);
                        System.out.println("DirectLoader defined class " + className);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new ClassNotFoundException("Could not define directly '" + className + "'");
                    }
                }
                return c;
            }
        }

        /**
         * Define the class with the given name directly and for convenience
         * instantiate it using the default constructor.
         */
        public Object newInstance(String className)
                throws ClassNotFoundException,
                InstantiationException, IllegalAccessException, IllegalArgumentException,
                InvocationTargetException, NoSuchMethodException, SecurityException {
            Class<?> c = findClass(className);
            return c.getDeclaredConstructor().newInstance();
        }

        public DirectClassLoader(ClassLoader parent) {
            super(parent);
        }
    }

    public static void log(String msg) {
        System.out.println(msg);
    }

    public static void waitForEnter() {
        try {
            do {
                System.in.read();
            } while (System.in.available() > 0);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
