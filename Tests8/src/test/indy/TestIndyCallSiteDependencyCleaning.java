package test.indy;

// Copied from //bas2/sapjvm/dev/common/java/src/sapjvm_generic_test/java/test/java/lang/ref/ReferenceObjectsHandling.java

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.commons.GeneratorAdapter;
import jdk.internal.org.objectweb.asm.commons.Method;
import testlib.TestBase;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Constructor;

@SuppressWarnings({ "javadoc", "restriction" })
public class TestIndyCallSiteDependencyCleaning extends TestBase {

    public static TestIndyCallSiteDependencyCleaning testController;

    public String pkgName;
    public String indyClassNameWithoutPkg;
    public String slashedPkgName;
    public String indyClassName;
    public String indySlashedClassName;
    public String indyClassDesc;

    public String controllerClassName;
    public String controllerSlashedClassName;
    public String controllerClassDesc;

    public Handle bootStrapMethodHandle;
    public Class<?> indyClass;
    public CallSite indyCallSite1;
    public CallSite indyCallSite2;
    public SimpleClassLoader simpleLoader;
    public PhantomReference<SimpleClassLoader> phantomSimpleLoader;

    public Runnable indyRnbl;

    public int counter; // dummy

    public static void main(String[] args) {
        new TestIndyCallSiteDependencyCleaning().runTest();
    }

    static class SimpleClassLoader extends ClassLoader {
        public Class<?> myDefineClass(String name, byte[] b, int off, int len) {
            return defineClass(name, b, off, len);
        }
    }

    @Override
    public void runTest() {
        try {
            // setup
            testController = this;
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
            bootStrapMethodHandle = new Handle(H_INVOKESTATIC, controllerSlashedClassName, "bootstrap", bsmdesc);

            generateAndLoadClassWithInvokedynamik();
            getIndyMethodJitCompiled();

            System.err.println("Drop all references to the indy class except the classloader, which is wrapped into a PhantomReference");
            // This makes the indy CallSite instance phantom reachable. GC will enqueue the CallSiteContext cleaner,
            // which is derived from j.l.r.PhantomReference.
            //
            // sapjvm_8: GC does *not* auto-clear PhantomReferences
            //   -> the class loader and MutableCallSite stay alive
            //   -> the nmethod has no dead reference and therefore is not unloaded
            //
            // sapjvm_9: GC *does* auto-clear PhantomReferences
            //   -> classloader and MutableCallSite remain dead
            //   -> the nmethod is unloaded and its dependencies are flushed
            //

            // w/o the phantom reference the nmethod would be made unloaded to get rid of the simpleLoader
            phantomSimpleLoader = new PhantomReference<>(simpleLoader, new ReferenceQueue<>());
            System.gc();
            System.gc();
            System.gc();
            System.err.printf("&simpleLoader: 0x%x\n", addressOf(simpleLoader));
            System.err.printf("&indyCallSite1: 0x%x\n", addressOf(indyCallSite1));
            System.err.printf("&indyCallSite2: 0x%x\n", addressOf(indyCallSite2));
            indyRnbl = null;
            indyClass = null;
            indyCallSite1 = indyCallSite2 = null;
            simpleLoader = null;

            System.err.println("Wait for CompileTask");
            waitForEnter();
            System.gc();
            System.gc();
            System.gc();
            System.err.println("Wait for the JIT");
            waitForEnter();



            System.err.println("Calling System.gc()");
            System.gc();
            System.err.println("Wait for the call site context cleaner to be called by the reference handler thread");
            Thread.sleep(500);
            // sapjvm_8: now the state is inconsistent, because the nmethod still has dependencies
            // to the call site, but the the call site removed the nmethod from its dependent nmethods.

            System.err.println("Drop the phantom reference to the class loader");
            phantomSimpleLoader = null;
            System.err.println("Calling System.gc()");
            // sapjvm_8: GC calls nmethod::make_unloaded() and hits a ShouldNotReachHere() when trying
            // to remove the nmethod from the call site, because it is not found there.
            System.gc();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Target method of the invokedynamic call site generated in {@link #generateAndLoadClassWithInvokedynamik()}
     */
    public void indyCalleeMethod() {
        counter++;
    }

    public void generateAndLoadClassWithInvokedynamik() throws Exception {
        System.err.println("Generating bytes of " + indyClassName);
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
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, indySlashedClassName, "controller", controllerClassDesc);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        { // generate TWO invokedynamic call sites just to check the dependency handling in this case, too
            GeneratorAdapter ga = new GeneratorAdapter(ACC_PUBLIC, Method.getMethod("void run ()"), null, null, cw);
            ga.loadThis();
            ga.getField(Type.getType(indySlashedClassName), "controller", Type.getType(getClass()));
            String sig = "("+controllerClassDesc+")V";
            ga.invokeDynamic("indyCalleeMethod", sig, bootStrapMethodHandle);
            ga.loadThis();
            ga.getField(Type.getType(indySlashedClassName), "controller", Type.getType(getClass()));
            ga.invokeDynamic("indyCalleeMethod", sig, bootStrapMethodHandle);
            ga.returnValue();
            ga.endMethod();
        }
        cw.visitEnd();

        byte[] classWithInvokeDynamicBytes = cw.toByteArray();

        simpleLoader = new SimpleClassLoader();
        System.err.println("Loading the synthetic class using the ClassLoader " + simpleLoader);
        indyClass = simpleLoader.myDefineClass(null, classWithInvokeDynamicBytes, 0, classWithInvokeDynamicBytes.length);
        System.err.println("Loaded " + indyClass);

        // get jit compiled
        Constructor<?> ctor = indyClass.getConstructor(getClass());
        indyRnbl = (Runnable) ctor.newInstance(this);
    }

    /**
     * Invokes the run method of the {@link TestIndyCallSiteDependencyCleaning#indyRnbl} many times
     * to get it jit compiled.
     *
     * @throws Exception
     */
    public void getIndyMethodJitCompiled() throws Exception {
        int times = 100 * 1000;
        System.err.println("invoking generated Runnable " + times + " times to get it jitted");
        int iter = times;
        while (iter-- > 0) {
            indyRnbl.run();
        }
    }

    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
        System.err.println(">>>#bootstrap");
        MethodHandle mine = MethodHandles.lookup().findVirtual(TestIndyCallSiteDependencyCleaning.class, "indyCalleeMethod", MethodType.methodType(Void.TYPE));
        CallSite cs = new MutableCallSite(mine);

        System.err.println("<<<#bootstrap:cs:"+cs);
        if (testController.indyCallSite1 == null) {
            testController.indyCallSite1 = cs;
        } else {
            testController.indyCallSite2 = cs;
        }
        return cs ;
      }
}
