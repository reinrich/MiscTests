package test.classloading;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Build a chain of classloader L0 ... Ln.
 * If a class to be loaded has the name suffix LOAD_AT_LEVEL_m, then
 * it will be loaded by Lm (0 <= m <= n).
 */
public class LeveledDirectClassLoader extends DirectClassLoader {
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
     * Define the class with the given name directly and for convenience
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