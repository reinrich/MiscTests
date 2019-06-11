

import java.security.AccessController;
import java.security.PrivilegedAction;

/*

./sapjvm_8/bin/java
-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n
-Xbatch
-XX:-UseOnStackReplacement
-XX:-TieredCompilation
-XX:+PrintCompilation
-cp
/u/w/d/jtests/QuickJavaTests4/bin
ExceptionCaughtOutOfPhaseAssertion

 */

public class ExceptionCaughtOutOfPhaseAssertion {

    public static void main(String[] args) {
        PrivilegedAction action = new HotThrowingAction();
        System.out.println("###### Warm-up");
        for(int i=0; i<11000; i++) {
            try {
                action.run();
            } catch(Throwable t) { /* ignored */ }
        }
        System.out.println("###### Warm-up done");
        System.out.println("###### Executing privileged action");
        AccessController.doPrivileged(action);
    }
    
    public static class HotThrowingAction implements PrivilegedAction {
        public Object run() {
            throw new Error();
        }
    }
}
