package test.classloading;

import jdk.internal.org.objectweb.asm.ClassWriter;
import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class Test001 {

    private static byte[] syntheticClassBytes;
    
    public static void main(String[] args) {
        while (true) {
            loadSyntheticClassAndLetLoaderDie();
            msg("Calling System.gc()");
            System.gc();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // ignored
            } 
        }
    }

    private static void msg(String msgTxt) {
        System.out.println(msgTxt);
    }

    public static void loadSyntheticClassAndLetLoaderDie() {
        if (syntheticClassBytes == null) {
            msg("Generating the binary version of the synthetic class.");
            ClassWriter cw = new ClassWriter(0);
            cw.visit(52, ACC_PRIVATE | ACC_SUPER, "test/classloading/SyntheticTestClass", null, "java/lang/Object", null);
            cw.visitEnd();
            syntheticClassBytes = cw.toByteArray();
        }
        msg("Loading the synthetic class");
        SimpleClassLoader cl = new SimpleClassLoader();
        Class<?> cls = cl.myDefineClass("test.classloading.SyntheticTestClass", syntheticClassBytes);
        msg("Loaded " + cls);
    }

}
