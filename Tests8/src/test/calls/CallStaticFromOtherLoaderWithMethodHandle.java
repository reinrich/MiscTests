package test.calls;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import test.classloading.DirectClassLoader;
import testlib.TestBase;

public class CallStaticFromOtherLoaderWithMethodHandle extends TestBase {

    public static final MethodHandle MH;
    public static final Object[] ARGS = new Object[0];
    public static void main(String[] args) {
        try {
            new CallStaticFromOtherLoaderWithMethodHandle().runTest(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static {
        MethodHandle mh = null;
        try {
            Class<?> receiverClass = CallStaticFromOtherLoaderWithMethodHandle.class;
            String receiverClassName = receiverClass.getName();
            DirectClassLoader loader = new DirectClassLoader(receiverClass.getClassLoader());
            Class<?> clonedReceiverClass = loader.findClass(receiverClassName);

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodType mt = MethodType.methodType(Integer.class);
            mh = lookup.findStatic(clonedReceiverClass, "testMethod_static_callee_dontinline", mt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MH = mh;
    }
    public byte[] dummy;
    public void runTest(String[] args) throws Throwable {
        int checksum = 0;
        for (int i=0; i<30_000; i++) {
            checksum += (Integer)testMethod_dojit();
        }
        System.out.println("checksum:" + checksum);
        waitForEnter("Press Enter to start GC Load");
        System.gc();
        System.gc();
        System.gc();
        for (int i=0; i<30_000; i++) {
            checksum += (Integer)testMethod_dojit();
        }
    }

    public static Integer testMethod_dojit() throws Throwable {
        return (Integer)MH.invokeExact();
    }

    public static Integer testMethod_static_callee_dontinline() {
        return 0;
    }
}
