package test.gc.g1.oopsoncarditerate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import sun.hotspot.WhiteBox;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;

import static jdk.internal.org.objectweb.asm.Opcodes.*;
import static jdk.internal.org.objectweb.asm.Type.*;

public class TestOopsOnCardIterateOverObjWithUnloadedClass {
    
    // Configuration
    public final static long TARGET_HEAP_OCCUPANCY_PERCENTAGE = 40;

    // Constants
    public  static final int CARD_SIZE_BYTES = 512;
    private static final int LONG_FIELD_SIZE_BYTES = 8;
    private static final long M = 1L<<20;
    private static final long K = 1L<<10;

    private static WhiteBox wb;

    private static Class<?> classToBeUnloaded;

    private static byte[] classToBeUnloadedBytesBytes;

    private ArrayList<ArrayList<HeapFiller>> allHeapFillers;

    private Runtime rt;

    private volatile HeapFiller[] tmp;

    public static void main(String[] args) {
        try {
            wb = WhiteBox.getWhiteBox();
            classToBeUnloaded = loadClassToBeUnloaded();
            new TestOopsOnCardIterateOverObjWithUnloadedClass().run();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void TODO() {
        throw new Error("TODO TODO TODO");
    }

    public void run() {
        try {
            printHeap();
            
            printHeapFillerFootPrint();

            fillHeap();

            dropClassToBeUnloadedAndItsInstances();

            // trigger marking to unload classes
            startG1ConcurrentMarkAndWaitForCompletion();

            triggerYoungGCsWhileModifyingOldObjs();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void printHeapFillerFootPrint() throws Exception {
        int count = 1000;
        tmp = new HeapFiller[count];
        System.gc();
        long allocatedBytesBefore = allocatedMemory();
        for (int ii=0; ii < count; ii++) {
            tmp[ii] = new HeapFiller();
        }
        System.gc();
        long allocatedBytesAfter = allocatedMemory();
        tmp = null;
        msg("HeapFiller size ~= " + ((float)(allocatedBytesAfter-allocatedBytesBefore)/count) + "  (before: "+allocatedBytesBefore/K+" KB  after: "+allocatedBytesAfter/K+" KB)");
    }

    private void printHeap() {
        rt = Runtime.getRuntime();
        System.gc();
        msg("freeMemory:\t" + rt.freeMemory()/M + " MB");
        msg("maxMemory:\t" + rt.maxMemory()/M + " MB");
        msg("g1NumMaxRegions:\t" + wb.g1NumMaxRegions());
        msg("g1RegionSize:\t" + wb.g1RegionSize()/K + " KB");
    }

    private void triggerYoungGCsWhileModifyingOldObjs() {
        while (true) {
            long occupancy = -1;
            for (ArrayList<HeapFiller> heapFillerChunk : allHeapFillers) {
                long lastOccupancy = occupancy;
                occupancy = 100*allocatedMemory() / rt.maxMemory();
                if (occupancy != lastOccupancy) {
                    msg("heap occupancy = " + occupancy + "%" + "  allocatedMemory = " + allocatedMemory()/M + " MB");
//                    if (occupancy >= 43) {
//                        wb.youngGC();
//                    }
                }
                for (HeapFiller hf : heapFillerChunk) {
                    hf.transienObjectInYoungGen = new byte[10];
                    hf.transienObjectInYoungGen = null;
                }
            }
        }
    }

    private void dropClassToBeUnloadedAndItsInstances() {
        classToBeUnloaded = null;
        for (ArrayList<HeapFiller> heapFillerChunk : allHeapFillers) {
            for (HeapFiller hf : heapFillerChunk) {
                hf.instanceOfClassToBeUnloaded = null;
            }
        }
    }

    private void fillHeap() throws Exception {
        allHeapFillers = new ArrayList<>();
        long maxRegs = wb.g1NumMaxRegions();
        long occupancy;

        do {
            int newHeapFillerCount = 10000;
            ArrayList<HeapFiller> newHeapFillers = new ArrayList<>(newHeapFillerCount);
            allHeapFillers.add(newHeapFillers);
            for (int i = newHeapFillerCount; i > 0; i--) {
                newHeapFillers.add(new HeapFiller());
            }
            occupancy = 100*allocatedMemory() / rt.maxMemory();
            msg("heap occupancy = " + occupancy + "%" + "  allocatedMemory = " + allocatedMemory()/M + " MB");
            if (occupancy >= TARGET_HEAP_OCCUPANCY_PERCENTAGE) {
                msg("System.gc()");
                System.gc();
                occupancy = 100*allocatedMemory() / rt.maxMemory();
                msg("heap occupancy = " + occupancy + "%" + "  allocatedMemory = " + allocatedMemory()/M + " MB");
            }
        } while (occupancy < TARGET_HEAP_OCCUPANCY_PERCENTAGE);
    }

    private long allocatedMemory() {
        return rt.totalMemory() - rt.freeMemory();
    }

    private static void startG1ConcurrentMarkAndWaitForCompletion() throws InterruptedException {
        msg("start G1 concurrent marking cycle.");
        wb.g1StartConcMarkCycle();
        do {
            Thread.sleep(1000);
        } while(wb.g1InConcurrentMark());
        msg("G1 concurrent marking cycle completed.");
    }

    private static Class<?> loadClassToBeUnloaded() throws IOException {
        if (classToBeUnloadedBytesBytes == null) {
//            msg("Generating bytes of the class to be unloaded.");
            String pkgName = TestOopsOnCardIterateOverObjWithUnloadedClass.class.getPackage().getName();
            String slashedPkgName = pkgName.replace('.', '/');
            String classNameWithoutPkg = "ClassToBeUnloaded";
            String className = pkgName+"."+classNameWithoutPkg;
            String slashedClassName = slashedPkgName+"/"+classNameWithoutPkg;

            // create bytes of the class
            ClassWriter cw = new ClassWriter(0);
            cw.visit(52, ACC_PUBLIC | ACC_SUPER, slashedClassName, null, "java/lang/Object", null);
            // creating default constructor
            MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
            // create enough field such that an instance occupies about half a card
            int fieldCount = CARD_SIZE_BYTES/2 / LONG_FIELD_SIZE_BYTES;
            String fieldName = "lf";
            for (int ii=0; ii < fieldCount; ii++) {
                cw.visitField(ACC_PUBLIC, fieldName+ii, DOUBLE_TYPE.getDescriptor(), null, null).visitEnd();;
            }
            cw.visitEnd();
            classToBeUnloadedBytesBytes = cw.toByteArray();
            dumpClass(classNameWithoutPkg, classToBeUnloadedBytesBytes);
        }

//        msg("Loading the synthetic class");
        SimpleClassLoader simpleLoader = new SimpleClassLoader();
        Class<?> resultCls = simpleLoader.myDefineClass(null, classToBeUnloadedBytesBytes, 0, classToBeUnloadedBytesBytes.length);
//        msg("Loaded " + classToBeUnloaded);
        return resultCls;
    }

    private static void dumpClass(String classNameWithoutPkg, byte[] classToBeUnloadedBytesBytes) throws IOException {
        FileOutputStream fos = new FileOutputStream(classNameWithoutPkg+".class");
        fos.write(classToBeUnloadedBytesBytes);
        fos.close();
    }

    private static void msg(String mm) {
        System.out.println(mm);
    }

    public static Object allocateInstanceOfClassToBeUnloaded() throws Exception {
//        return classToBeUnloaded.newInstance();
        return loadClassToBeUnloaded().newInstance();
    }

}
