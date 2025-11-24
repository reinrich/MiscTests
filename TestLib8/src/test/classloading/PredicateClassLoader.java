package test.classloading;

import java.util.function.Predicate;

public class PredicateClassLoader extends DirectClassLoader {

    private static final boolean DEBUG = Boolean.getBoolean("DBG");

    private final Predicate<String> shouldHandle;

    public PredicateClassLoader(ClassLoader parent, Predicate<String> shouldHandle) {
        super(parent);
        this.shouldHandle = shouldHandle;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (!shouldHandle.test(name)) {
            if (DEBUG) {
                System.out.println(this + ": delegating to parent for " + name);
            }
            return super.loadClass(name);
        }
        if (DEBUG) {
            System.out.println(this + ": handling class " + name);
        }

        // Child-first loading for predicate-accepted classes
        synchronized (getClassLoadingLock(name)) {

            // If already loaded, return it
            Class<?> c = findLoadedClass(name);
            if (c != null) {
                return c;
            }

            try {
                // Try to find the class in this loader
                return findClass(name);
            } catch (ClassNotFoundException e) {
                // Fallback to parent only if not found here
                if (DEBUG) {
                    System.out.println(this + ": fallback to parent for " + name);
                }
                return super.loadClass(name);
            }
        }
    }
}
