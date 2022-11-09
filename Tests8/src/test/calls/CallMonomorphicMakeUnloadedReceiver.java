package test.calls;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

import test.classloading.DirectClassLoader;
import testlib.TestBase;

// Monomorphic Virtual Call
// Declared (static) receiver is abstract.
// Single concrete receiver.
// nmethod with opt virt. call ist unloaded when concrete method is unloaded
// Check inline cache clearing of caller.

public class CallMonomorphicMakeUnloadedReceiver extends TestBase {

    public static void main(String[] args) {
        new CallMonomorphicMakeUnloadedReceiver().runTest();
    }

    public static abstract class DeclaredReceiver {
        public abstract int testMethod_callee();
    }

    public static class ConcreteReceiverR1 extends DeclaredReceiver {
        @Override
        public int testMethod_callee() {
            return 0;
        }
    }

    public PhantomReference prRecv;
    public ReferenceQueue queue;
    public PhantomReference prLoader;

    public void runTest() {
        try {
            runTest(getClass().getName() + "$ConcreteReceiverR1");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    public void runTest(String receiverClassName) throws Throwable {
        waitForEnter("Press Enter to to start test with '" + receiverClassName + "' as receiver");
        DirectClassLoader loader = new DirectClassLoader(getClass().getClassLoader());
        DeclaredReceiver recv = (DeclaredReceiver) loader.newInstance(receiverClassName);
        queue = new ReferenceQueue();
        prRecv = new PhantomReference(recv, queue);
        prLoader = new PhantomReference(loader, queue);
        int checksum = 0;
        for (int i=0; i<30_000; i++) {
            checksum += testMethod_callercaller_dojit(recv);
        }
        System.out.println("checksum:" + checksum);

        waitForEnter("Press Enter to call System.GC()");
        loader = null;
        recv = null;
        PhantomReference pr = null;
        int gcCount = 0;
        while (gcCount++ < 7) {
            System.gc();
            sleep(100);
            while ((pr = (PhantomReference) queue.poll()) != null) {
                log((pr == prLoader ? "Loader" : "Receiver") + " is unreachable");
            }
        }
    }

    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignored
        }
    }
    public static int testMethod_callercaller_dojit(DeclaredReceiver receiver) {
        return testMethod_opt_virt_call_dojit(receiver);
    }

    public static int testMethod_opt_virt_call_dojit(DeclaredReceiver receiver) {
        return receiver.testMethod_callee();
    }
}
