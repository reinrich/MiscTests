package test.classloading;

public class SimpleClassLoader extends ClassLoader {

    public Class<?> myDefineClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }

}
