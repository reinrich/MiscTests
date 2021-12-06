package test.interp.constantresolution.getfield2;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static AbstrTestMethodHolder testMethodHolder;
    public static volatile long numIterations;
    static final AtomicInteger waitingThreads = new AtomicInteger(0);
    private static int numThreads;
    
    public static void main(String[] args) {
        try {
            URL[] urls;
            urls = new URL[] { new URL("file:./QuickJavaTests/bin_test_classloader/") };
            String testClassName = "test.interp.constantresolution.getfield2.TestClassWithAlotFields";

            ClassLoader loader = new URLClassLoader(urls);

            Class<?> cls = loader.loadClass(testClassName);
            Object instance = cls.newInstance();
            testMethodHolder = (AbstrTestMethodHolder) instance;
            
            numThreads = Integer.parseInt(args[0]);
            for (int i = 0; i < numThreads; i++) {
                TestMethodExecutor thread = new TestMethodExecutor();
                thread.start();
            }
            
            while (true) {
                loader = new URLClassLoader(urls);
                cls = loader.loadClass(testClassName);
                instance = cls.newInstance();
                waitForTestThreads();
                numIterations++;
            }
            
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
        
    }

    public static void waitForTestThreads() {
        while (waitingThreads.get() != numThreads) {
            Thread.yield();
        }
    }

}
