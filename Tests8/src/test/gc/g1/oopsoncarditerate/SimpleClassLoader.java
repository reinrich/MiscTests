package test.gc.g1.oopsoncarditerate;

public class SimpleClassLoader extends ClassLoader {
    public Class<?> myDefineClass(String name, byte[] b, int off, int len) {
        return defineClass(name, b, off, len);
    }
}