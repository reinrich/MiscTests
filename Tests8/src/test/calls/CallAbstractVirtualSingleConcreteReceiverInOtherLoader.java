package test.calls;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

import test.classloading.DirectClassLoader;

// Monomorphic Virtual Call
// Declared (static) receiver is abstract.
// Single concrete receiver from other classloader L.
// L is kept alive by nmethod N with opt virt. call while on stack because of dependencies
// L becomes unreachable when nmethod N is not on stack. N is made not entrant then.

public class CallAbstractVirtualSingleConcreteReceiverInOtherLoader {
    public static ReferenceQueue queue;
    public static PhantomReference prRecv;
    public static PhantomReference prLoader;

    public static boolean callSystemGCWithTestMethodOnStack;
    public static DeclaredReceiver recv;

    public static void main(String[] args) {
        new CallAbstractVirtualSingleConcreteReceiverInOtherLoader().runTest();
    }

    // Class with abstract method
    public static abstract class DeclaredReceiver {
        public abstract int testMethod_callee();
    }

    // Class with single concrete receiver. To be loaded by other classloader.
    public static class ConcreteReceiverR1 extends DeclaredReceiver {
        @Override
        public int testMethod_callee() {
            return 0;
        }
    }

    public void runTest() {
        try {
            runTest(getClass().getName() + "$ConcreteReceiverR1");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void runTest(String receiverClassName) throws Throwable {
        waitForEnter("Press enter to to start test with '" + receiverClassName + "' as receiver");
        DirectClassLoader loader = new DirectClassLoader(getClass().getClassLoader());
        recv = (DeclaredReceiver) loader.newInstance(receiverClassName);
        queue = new ReferenceQueue();
        prRecv = new PhantomReference(recv, queue);
        prLoader = new PhantomReference(loader, queue);
        int checksum = 0;
        for (int i=0; i<30_000; i++) {
            checksum += testMethod_opt_virt_call_dojit(recv);
        }
        System.out.println("checksum:" + checksum);

        callSystemGCWithTestMethodOnStack = true;
        loader = null;
        recv = null;
        boolean caughtNpe = false;
        try {
            checksum += testMethod_opt_virt_call_dojit(recv);
        } catch (NullPointerException npe) {
            caughtNpe = true;
        }
        if (!caughtNpe) throw new Error("No NPE");

        waitForEnter("Press enter to call System.GC() (testmethod _not_ on stack)");
        PhantomReference pr = null;
        int gcCount = 0;
        while (gcCount++ < 7) {
            System.gc();
            while ((pr = (PhantomReference) queue.remove(500)) != null) {
                log((pr == prLoader ? "Loader" : "Receiver") + " is unreachable");
            }
        }
    }

    public static void maybeCallSystemGC() {
        if (callSystemGCWithTestMethodOnStack) {
            waitForEnter("Press enter to call System.GC() (testmethod _is_ on stack)");
            PhantomReference pr = null;
            int gcCount = 0;
            while (gcCount++ < 7) {
                System.gc();
                try {
                    while ((pr = (PhantomReference) queue.remove(500)) != null) {
                        log((pr == prLoader ? "Loader" : "Receiver") + " is unreachable");
                    }
                } catch (IllegalArgumentException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int testMethod_opt_virt_call_dojit(DeclaredReceiver receiver) {
        maybeCallSystemGC();
        return receiver.testMethod_callee();
    }

    public static void waitForEnter(String prompt) {
        log(prompt);
        waitForEnter();
    }

    public static void log(String msg) {
        System.out.println(msg);
    }

    public static void waitForEnter() {
        try {
            do {
                System.in.read();
            } while (System.in.available() > 0);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
