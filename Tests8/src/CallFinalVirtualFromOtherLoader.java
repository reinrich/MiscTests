import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Scenario: invokevirtual with final target with other classloader

public class CallFinalVirtualFromOtherLoader {

    public static void main(String[] args) {
        try {
            new CallFinalVirtualFromOtherLoader().runTest(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static interface TestInterface {
        public int function() throws Throwable;
        public void setReceiver();
        public void clearReceiver();
    }

    // Final callee loaded in non-delegating loader
    public static class ClassB_LOAD_AT_LEVEL_1 {
        public final int testMethod_statically_bound_callee_dontinline_dojit() {
            return 0;
        }
    }

    // Caller
    public static class ClassA_LOAD_AT_LEVEL_0 implements TestInterface {

        public ClassB_LOAD_AT_LEVEL_1 callReceiver;

        public int testMethod_dojit() throws Throwable {
            int res = 0;
            if (doCall) {
                callReceiver.testMethod_statically_bound_callee_dontinline_dojit();
            }
            return res;
        }

        @Override
        public int function() throws Throwable {
            return testMethod_dojit();
        }

        @Override
        public void setReceiver() {
            callReceiver = new ClassB_LOAD_AT_LEVEL_1();
        }

        @Override
        public void clearReceiver() {
            callReceiver = null;
        }
    }

    public static boolean doCall;

    enum TestVariant {
        C1_WITH_LAZY_LOAD, // Callsite is C1 compiled when the declared/static receiver was not yet loaded -> vanilla virtual call
        EAGER_LOAD,        // Callsite is C2 compiled when the declared/static receiver was already loaded -> optimized virtual call
    }

    public TestVariant variant = TestVariant.EAGER_LOAD;

    public void runTest(String[] args) throws Throwable {
        int checksum = 0;
        LeveledDirectClassLoader ldl = new LeveledDirectClassLoader(getClass().getClassLoader(), 2);
        TestInterface test = (TestInterface) ldl.newInstance(getClass().getName() + "$ClassA_LOAD_AT_LEVEL_0");
        if (variant == TestVariant.EAGER_LOAD) {
            doCall = true;
            test.setReceiver();
        }
        for (int i=0; i<30_000; i++) {
            checksum += test.function();
        }
        System.out.println("checksum:" + checksum);
        if (variant == TestVariant.C1_WITH_LAZY_LOAD) {
            waitForEnter("Press Enter load LEVEL_1 class and make statically bound call");
            doCall = true;
            test.setReceiver();
            log("Calling test function");
            checksum += test.function();
            log("DONE: Calling test function");
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

    public void waitForEnter(String prompt) {
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
         * Define the class with the given name directly and for convenietly
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
            System.identityHashCode(this);
        }
    }

    /**
     * Build a hierarchy of classloader L0 ... Ln.
     * If a class to be loaded has the name suffix LOAD_AT_LEVEL_m, then
     * it will be loaded by Lm (0 <= m <= n).
     */
    public static class LeveledDirectClassLoader extends DirectClassLoader {
        public static final String CLASS_NAME_SUFFIX = "LOAD_AT_LEVEL_";
        public static final Pattern CLASS_PATTERN;
        static {
            CLASS_PATTERN = Pattern.compile(CLASS_NAME_SUFFIX + "(\\d+)");
        }
        DirectClassLoader classLoaders[];
        public LeveledDirectClassLoader(ClassLoader parent, int levels) {
            super(parent);
            DirectClassLoader[] loaders = new DirectClassLoader[levels];
            loaders[0] = this;
            System.err.println("loaders[0] : " + this);
            for (int i = 1; i < loaders.length; i++) {
                loaders[i] = new DirectClassLoader(loaders[i - 1]);
                System.err.println("loaders[" + i +"] : " + loaders[i]);
            }
            classLoaders = loaders;
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve)
                throws ClassNotFoundException
            {
                synchronized (getClassLoadingLock(name)) {
                    Class<?> c = findLoadedClass(name);
                    if (c == null) {
                        Matcher m;
                        String idh = Integer.toHexString(System.identityHashCode(this));
                        if ((m = CLASS_PATTERN.matcher(name)).find()) {
                            int level = Integer.parseInt(m.group(1));
                            if (level >= classLoaders.length) {
                                throw new Error("Level " + level + " too deep");
                            }
                            System.err.println(getClass().getName() + "@" + idh + ": loading " + name + " at level " + level + " with " + classLoaders[level]);
                            c = classLoaders[level].findClass(name);
                        } else {
                            System.err.println(getClass().getName() + "@" + idh + ": loading " + name + " from parent");
                            c = super.loadClass(name, resolve);
                        }
                    }
                    if (resolve) {
                        resolveClass(c);
                    }
                    return c;
                }
            }

        /**
         * Define the class with the given name directly and for convenietly
         * instantiate it using the default constructor.
         */
        public Object newInstance(String className)
                throws ClassNotFoundException,
                InstantiationException, IllegalAccessException, IllegalArgumentException,
                InvocationTargetException, NoSuchMethodException, SecurityException {
            Class<?> c = loadClass(className);
            return c.getDeclaredConstructor().newInstance();
        }

        public void clearLoaderFromLevel(int i) {
            for(; i < classLoaders.length; i++) {
                classLoaders[i] = null;
            }
        }
    }
}
