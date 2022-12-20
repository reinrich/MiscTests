import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// invokevirtual with final target loaded by child classloader

public class CallFinalVirtualFromOtherLoader {

    public static void main(String[] args) {
        try {
            new CallFinalVirtualFromOtherLoader().runTest(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static interface TestInterface {
        public int call() throws Throwable;
        public void setReceiver();
    }

    // Final callee loaded by child loader
    public static class ClassB_LVL_2 {
        public final int testMethod_statically_bound_callee_dontinline_dojit() {
            return 0;
        }
    }

    // Caller
    public static class ClassA_LVL_1 implements TestInterface {

        public ClassB_LVL_2 callReceiver;

        public int testMethod_dojit() throws Throwable {
            return callReceiver.testMethod_statically_bound_callee_dontinline_dojit();
        }

        @Override
        public int call() throws Throwable {
            return testMethod_dojit();
        }

        @Override
        public void setReceiver() {
            callReceiver = new ClassB_LVL_2();
        }
    }

    public void runTest(String[] args) throws Throwable {
        int checksum = 0;
        ClassLoader thisLoader = getClass().getClassLoader();
        log("CL: " + thisLoader);
        ClassLoader ldl = new DirectLeveledClassLoader(thisLoader, 2);
        Class<?> cls = ldl.loadClass(getClass().getName() + "$ClassA_LVL_1");
        TestInterface test = (TestInterface) cls.getDeclaredConstructor().newInstance();
        test.setReceiver();
        for (int i=0; i<30_000; i++) {
            checksum += test.call();
        }
        System.out.println("checksum:" + checksum);
    }

    /**
     * Build a chain of classloaders L0 ... Ln.
     * Lm will not delegate to its parent if a class to be loaded has the name
     * suffix LVL_m. Instead it will define the class directly except L0 which
     * delegates to Ln in that case.
     * {@link DirectLeveledClassLoader} corresponds to L0.
     * {@link LevelN} corresponds to others.
     *
     */
    public static class DirectLeveledClassLoader extends ClassLoader {
        private static final String CLASS_NAME_SUFFIX = "LVL_";
        private static final Pattern CLASS_PATTERN;
        static {
            CLASS_PATTERN = Pattern.compile(CLASS_NAME_SUFFIX + "(\\d+)");
        }

        private final int maxLevel;
        private final LevelN bottom;

        public DirectLeveledClassLoader(ClassLoader parent, int levels) {
            super(parent);
            maxLevel = levels;
            LevelN bot = null;
            int level = 1;
            // Have subclasses for levels 1 and 2 for better tracing
            if (levels > 0) {
                level++;
                bot = new Level1(this);
            }
            if (levels > 1) {
                level++;
                bot = new Level2(bot);
            }
            for (int i = 2; i < levels; i++) {
                bot = new LevelN(bot, level++);
            }
            bottom = bot;
        }

        private static int getLevelFromName(String name) {
            Matcher m;
            int lvl = -1;
            if ((m = CLASS_PATTERN.matcher(name)).find()) {
                lvl = Integer.parseInt(m.group(1));
            }
            return lvl;
        }

        protected Class<?> loadClass(String name, boolean resolve)
                throws ClassNotFoundException {
            int level = getLevelFromName(name);
            if (level >= 0) {
                if (level == 0 || level > maxLevel) {
                    throw new Error("Level " + level + " is out of bounds [1, " + maxLevel + "]");
                }
                System.err.println(this + ": delegating to bottom level for " + name);
                return bottom.loadClass(name, resolve);
            }
            System.err.println(this + ": delegating to parent for " + name);
            return super.loadClass(name, resolve);
        }

        private static class LevelN extends DirectClassLoader {
            private final int level;

            private LevelN(ClassLoader parent, int level) {
                super(parent);
                this.level = level;
            }

            @Override
            protected Class<?> loadClass(String name, boolean resolve)
                    throws ClassNotFoundException {
                synchronized (getClassLoadingLock(name)) {
                    Class<?> c = findLoadedClass(name);
                    if (c == null) {
                        String idh = Integer.toHexString(System.identityHashCode(this));
                        int lvl = getLevelFromName(name);
                        // System.err.println(this + " loadClass(" + name + ", " + resolve + ") lvl:" + lvl);
                        if (lvl >= 0 && this.level == lvl) {
                            System.err.println(getClass().getName() + "@" + idh + ": loading " + name + " at level " + lvl);
                            c = findClass(name);
                        } else {
                            System.err.println(this + ": delegating to parent for " + name);
                            c = super.loadClass(name, resolve);
                        }
                    }
                    if (resolve) {
                        resolveClass(c);
                    }
                    return c;
                }
            }
        }

        // Encode level in classname for better tracing
        private static class Level1 extends LevelN {
            private Level1(ClassLoader parent) {
                super(parent, 1);
            }
        }

        // Encode level in classname for better tracing
        private static class Level2 extends LevelN {
            private Level2(ClassLoader parent) {
                super(parent, 2);
            }
        }
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
                        System.out.println(this + " defined class " + className);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new ClassNotFoundException("Could not define directly '" + className + "'");
                    }
                }
                return c;
            }
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

    public void waitForEnter(String prompt) {
        log(prompt);
        waitForEnter();
    }
}
