package test.calls;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import test.classloading.DirectClassLoader;
import test.classloading.FilterClassLoader;
import test.classloading.LeveledDirectClassLoader;
import testlib.TestBase;

// Scenario: invokevirtual with final target with other classloader

public class CallFinalVirtualFromOtherLoaderNEW extends TestBase {

    public static void main(String[] args) {
        try {
            new CallFinalVirtualFromOtherLoaderNEW().runTest(args);
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
    public static class ClassB_LVL_2 {
        public final int testMethod_statically_bound_callee_dontinline_dojit() {
            return 0;
        }
    }

    // Caller
    public static class ClassA_LVL_1 implements TestInterface {

        public ClassB_LVL_2 callReceiver;

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
            callReceiver = new ClassB_LVL_2();
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
        ClassLoader thisLoader = getClass().getClassLoader();
        log("CL: " + thisLoader);
        ClassLoader ldl = new DirectLeveledClassLoader(thisLoader, 2);
        Class<?> cls = ldl.loadClass(getClass().getName() + "$ClassA_LVL_1");
        TestInterface test = (TestInterface) cls.getDeclaredConstructor().newInstance();
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

    // A class with a name suffix LVL_N shall be loaded by loader on level N without parent delegation
    public static class DirectLeveledClassLoader extends ClassLoader {
        public static final String CLASS_NAME_SUFFIX = "LVL_";
        public static final Pattern CLASS_PATTERN;
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

        public static boolean shouldLoadOnLevel(String name) {
            return getLevelFromName(name) >= 0;
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

        private static class Level1 extends LevelN {
            private Level1(ClassLoader parent) {
                super(parent, 1);
            }
        }

        private static class Level2 extends LevelN {
            private Level2(ClassLoader parent) {
                super(parent, 2);
            }
        }
    }
}
