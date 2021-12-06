package test.interp.constantresolution.getfield1;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {

    public static AbstrTestMethodHolder testMethodHolder;
    //public static volatile long numIterations;
    public static Object lock = new Object();
    public static Object lock2 = new Object();
    private static int waitingThreads = 0;
    private static int numThreads;
    
    public static void main(String[] args) {
        try {
            URL[] urls;
            urls = new URL[] { new URL("file:./QuickJavaTests/bin_test_classloader/") };
            String testClassName = "test.interp.constantresolution.TestClassWithAlotFields";

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
                //numIterations++;
                synchronized (lock2) {
                    while (waitingThreads != numThreads) {
                        lock2.wait();
                    }
                    synchronized (lock) {
                        testMethodHolder = (AbstrTestMethodHolder) instance;
                        lock.notifyAll();
                    }
                }
            }
            
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
        
    }

    public static void waitForNextIteration() {
        try {
            synchronized(lock) {
                waitingThreads ++;
                if (waitingThreads == numThreads) {
                    testMethodHolder = null;
                    synchronized (lock2) {
                        lock2.notify();
                    }
                }
                while (waitingThreads != numThreads) {
                    lock.wait();
                }
                while (testMethodHolder == null) {
                    lock.wait();
                }
                waitingThreads --;
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }
        
    }

}
