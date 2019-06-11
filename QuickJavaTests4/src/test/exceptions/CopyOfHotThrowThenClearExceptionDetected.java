package test.exceptions;

import java.security.AccessController;
import java.security.PrivilegedAction;

/*

./sapjvm_8/bin/java
-Xbatch
-XX:-UseOnStackReplacement
-XX:-TieredCompilation
-cp /u/w/d/jtests/QuickJavaTests4/bin
test.exceptions.HotThrowThenClearExceptionDetected

 */

public class CopyOfHotThrowThenClearExceptionDetected {

    public static void main(String[] args) {
        PrivilegedAction action = new HotThrowingAction();
        System.out.println("### Warm-up");
        for(int i=0; i<11000; i++) {
            try {
                action.run();
            } catch(Throwable t) { /* ignored */ }
        }
        System.out.println("### Calling privileged action");
        AccessController.doPrivileged(action);
    }
    
    public static class HotThrowingAction implements PrivilegedAction {

        public Object run() {
            throw new TestError();
        }

    }
}
