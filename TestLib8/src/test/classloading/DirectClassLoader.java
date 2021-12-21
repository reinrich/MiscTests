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

    private Class<?> defineDirectly(String className) throws ClassNotFoundException {
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
            return defineClass(className, b, 0, b.length);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ClassNotFoundException("Could not define directly '" + className + "'");
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
        Class<?> c = defineDirectly(className);
        return c.getDeclaredConstructor().newInstance();
    }

    public DirectClassLoader(ClassLoader parent) {
        super(parent);
    }
}
