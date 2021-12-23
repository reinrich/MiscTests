package test.classloading;

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.commons.GeneratorAdapter;
import jdk.internal.org.objectweb.asm.commons.Method;
import testlib.TestBase;
import testlib.Tracer;

import static
       jdk.internal.org.objectweb.asm.Opcodes.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class ClassGenerator {

    private String pkgName;
    private String slashedPkgName;
    private Tracer tracer;

    public static void main(String[] args) {
        try {
            ClassGenerator gen = new ClassGenerator("test.generated", new TestBase());
            SimpleClassLoader cl = new SimpleClassLoader();
            byte[] ba = gen.generateClass("DummyTestClass", 10);
            cl.myDefineClass(null, ba);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClassGenerator(String pkgName, Tracer tracer) {
        this.pkgName = pkgName;
        this.slashedPkgName = pkgName.replace('.', '/');
        this.tracer = tracer;
    }

    public byte[] generateClass(String classNameWithoutPkg, int dummyMethodCount) throws Exception {
        String className = pkgName+"."+classNameWithoutPkg;
        String slashedClassName = slashedPkgName+"/"+classNameWithoutPkg;

        tracer.log(1, "Generating bytes of " + className);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        MethodVisitor mv;

        cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, slashedClassName, null, "java/lang/Object", null);
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        // generate dontinline_testMethod()
        {
            GeneratorAdapter ga = new GeneratorAdapter(ACC_PUBLIC, Method.getMethod("void dontinline_testMethod ()"), null, null, cw);
            ga.returnValue();
            ga.endMethod();
        }

        // generate dummy methods
        for (int mcount = 0; mcount < dummyMethodCount; mcount++) {
            GeneratorAdapter ga = new GeneratorAdapter(ACC_PUBLIC, Method.getMethod("void dummy" + mcount + " ()"), null, null, cw);
            ga.returnValue();
            ga.endMethod();
        }
        cw.visitEnd();

        byte[] classBytes = cw.toByteArray();

        if (tracer.trcActive(4)) {
            dumpClass(classNameWithoutPkg, classBytes);
        }

        return classBytes;
    }

    private void dumpClass(String classNameWithoutPkg, byte[] classToBeUnloadedBytesBytes) throws IOException {
        String classFileName = classNameWithoutPkg+".class";
        tracer.log("dumping class bytes to " + classFileName);
        FileOutputStream fos = new FileOutputStream(classFileName );
        fos.write(classToBeUnloadedBytesBytes);
        fos.close();
    }

}
