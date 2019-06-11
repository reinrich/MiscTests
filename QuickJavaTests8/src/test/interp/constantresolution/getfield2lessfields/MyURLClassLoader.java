package test.interp.constantresolution.getfield2lessfields;

import java.net.URL;
import java.net.URLClassLoader;

public class MyURLClassLoader extends URLClassLoader {

    private static volatile long _id;

    public MyURLClassLoader(URL[] urls) {
        super(urls);
        _id++;
    }

    public String toString() {
        return "MyCL(id:" + _id + ")";
    }
    
}
