package test.indy;

import test.TestBase;
import test.classloading.SimpleClassLoader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.commons.GeneratorAdapter;
import jdk.internal.org.objectweb.asm.commons.Method;
import sun.hotspot.WhiteBox;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Constructor;

public class CallSiteDependenciesExperiments extends TestBaseOld {

    public static final long YOUNG_GC_TRIGGER_BYTES_TO_ALLOCATE = 10*M;
    public static final int  YOUNG_GC_TRIGGER_ALLOC_GRANULARTY_BYTES = 2*K; 

    public static void main(String[] args) {
        new CallSiteDependenciesExperiments().runTest();
    }

    public String pkgName;
    public String indyClassNameWithoutPkg;
    public String slashedPkgName;
    public String indyClassName;
    public String indySlashedClassName;
    public String indyClassDesc;

    public String controllerClassName;
    public String controllerSlashedClassName;
    public String controllerClassDesc;

    public Handle BOOTSTRAP_METHOD;
    public Class<?> indyClass;
    public SimpleClassLoader simpleLoader;

    public int counter;

    public WhiteBox wb;
    public Runnable indyRnbl;
    public long INDY_CALLEE_LOOPS = 10;
    public PhantomReference phantomSimpleLoader;

    public void runTest() {
        try {
            // setup
            wb = WhiteBox.getWhiteBox();
            pkgName = getClass().getPackage().getName();
            slashedPkgName = pkgName.replace('.', '/');
            indyClassNameWithoutPkg = "ClassWithInvokeDynamic";
            indyClassName = pkgName+"."+indyClassNameWithoutPkg;
            indySlashedClassName = slashedPkgName+"/"+indyClassNameWithoutPkg;
            indyClassDesc = "L"+indySlashedClassName+";";
            controllerClassName = getClass().getName();
            controllerSlashedClassName = controllerClassName.replace('.', '/');
            controllerClassDesc = "L"+controllerSlashedClassName+";";

            String bsmdesc = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class,
                    MethodType.class).toMethodDescriptorString();
            BOOTSTRAP_METHOD = new Handle(H_INVOKESTATIC, controllerSlashedClassName, "bootstrap", bsmdesc);
            trcInstanceFields(this);

            char ch;
            while ((ch = getCharacter("Enter command character:")) != 'x') {
                switch (ch) {
                case 'r': // run test
                    generateAndLoadClassWithInvokedynamik(false);
                    break;
                case 'R': // run test with endless loop in nmethod
                    generateAndLoadClassWithInvokedynamik(true);
                    break;
                case 's':
                    log("forcing nmethod sweeping");
                    wb.forceNMethodSweep();
                    break;
                case 'g':
                    log("calling System.gc()");
                    System.gc();
                    break;
                case 'y':
                    triggerYoungGC();
                    break;
                case 'u':
                    log("trigger unloading of generated class");
                    indyRnbl = null;
                    indyClass = null;
                    phantomSimpleLoader = null;
                    simpleLoader = null;
                    break;
                case 'U':
                    log("trigger unloading of generated class");
                    indyRnbl = null;
                    indyClass = null;
                    phantomSimpleLoader = new PhantomReference<>(simpleLoader, new ReferenceQueue<>());
                    simpleLoader = null;
                    break;
                case 'c':
                    log("check: indyClass: " + indyClass);
                    System.gc();
                    break;
                default:
                    log("UNKNOWN COMMAND: '" + ch + "'");
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void triggerYoungGC() {
        log("triggering young gc by allocating " + YOUNG_GC_TRIGGER_BYTES_TO_ALLOCATE/M + "MB");
        long bytes_to_allocate = YOUNG_GC_TRIGGER_BYTES_TO_ALLOCATE;
        do {
            @SuppressWarnings("unused")
            byte[] dummy = new byte[YOUNG_GC_TRIGGER_ALLOC_GRANULARTY_BYTES];
        } while ((bytes_to_allocate -= YOUNG_GC_TRIGGER_ALLOC_GRANULARTY_BYTES) > 0);
    }

    private void invokeRunnableOtherThread(Runnable rnbl, int times) {
        Thread tt = new Thread() {
            public void run() {
                log("invoking generated Runnable " + times + " times");
                int iter = times;
                while (iter-- > 0) {
                    rnbl.run();
                }
                log("DONE");
            }
        };
        tt.start();
    }

    public void indyCalleeMethod() {
        long times = INDY_CALLEE_LOOPS;
        while (times-- > 0) {
            counter++;
        }
    }

    public void generateAndLoadClassWithInvokedynamik(boolean enterEndlessLoopInNmethod) throws Exception {
        log("Generating bytes of " + indyClassName);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, indySlashedClassName, null, "java/lang/Object",
                new String[]{"java/lang/Runnable"});
        {
            fv = cw.visitField(ACC_PRIVATE, "controller", controllerClassDesc, null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "("+controllerClassDesc+")V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(7, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(8, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, indySlashedClassName, "controller", controllerClassDesc);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLineNumber(9, l2);
            mv.visitInsn(RETURN);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLocalVariable("this", indyClassDesc, null, l0, l3, 0);
            mv.visitLocalVariable("controller", controllerClassDesc, null, l0, l3, 1);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            GeneratorAdapter ga = new GeneratorAdapter(ACC_PUBLIC, Method.getMethod("void run ()"), null, null, cw);
            ga.loadThis();
            ga.getField(Type.getType(indySlashedClassName), "controller", Type.getType(CallSiteDependenciesExperiments.class));
            String sig = "("+controllerClassDesc+")V";
            ga.invokeDynamic("indyCalleeMethod", sig, BOOTSTRAP_METHOD);
            ga.returnValue();
            ga.endMethod();
        }
        cw.visitEnd();

        byte[] classWithInvokeDynamicBytes = cw.toByteArray();
        dumpClass(indyClassNameWithoutPkg, classWithInvokeDynamicBytes);

        simpleLoader = new SimpleClassLoader();
        log("Loading the synthetic class using the ClassLoader " + simpleLoader);
        indyClass = simpleLoader.myDefineClass(null, classWithInvokeDynamicBytes, 0, classWithInvokeDynamicBytes.length);
        log("Loaded " + indyClass);

        // get jit compiled
        Constructor<?> ctor = indyClass.getConstructor(getClass());
        indyRnbl = (Runnable) ctor.newInstance(this);

        for (int i = 0; i < 4; i++) {
            invokeRunnableOtherThread(indyRnbl, 100000);
            Thread.sleep(1000);
            log("counter:"+counter);
        }

        if (enterEndlessLoopInNmethod) {
            log("triggering endless loop in indyCalleeMethod()");
            INDY_CALLEE_LOOPS=1<<60;
            invokeRunnableOtherThread(indyRnbl, 1000);
        }
    }

    private static void dumpClass(String classNameWithoutPkg, byte[] classToBeUnloadedBytesBytes) throws IOException {
        FileOutputStream fos = new FileOutputStream(classNameWithoutPkg+".class");
        fos.write(classToBeUnloadedBytesBytes);
        fos.close();
    }

    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
        MethodHandle mine = MethodHandles.lookup().findVirtual(CallSiteDependenciesExperiments.class, "indyCalleeMethod", MethodType.methodType(Void.TYPE));
        return new MutableCallSite(mine);
      }

}
