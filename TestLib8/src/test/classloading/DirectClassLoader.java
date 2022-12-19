package test.classloading;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/**
 * Read the bytes of a class that would actually be loaded by the parent and define the class directly.
 */
public class DirectClassLoader extends ClassLoader {

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
        System.identityHashCode(this);
    }
}
