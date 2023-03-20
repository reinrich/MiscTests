package test.classloading;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Build a chain of classloaders L0 ... Ln.
 * Lm will not delegate to its parent if a class to be loaded has the name
 * suffix LVL_m. Instead it will define the class directly except L0 which
 * delegates to Ln in that case.
 * {@link DirectLeveledClassLoader} corresponds to L0.
 * {@link LevelN} corresponds to others.
 *
 */
public class DirectLeveledClassLoader extends ClassLoader {
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
        if (levels > 1) {
            level++;
            bot = new Level3(bot);
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
        private Level1(ClassLoader parent) { super(parent, 1); }
    }
    // Encode level in classname for better tracing
    private static class Level2 extends LevelN {
        private Level2(ClassLoader parent) { super(parent, 2); }
    }
    // Encode level in classname for better tracing
    private static class Level3 extends LevelN {
        private Level3(ClassLoader parent) { super(parent, 3); }
    }
}
