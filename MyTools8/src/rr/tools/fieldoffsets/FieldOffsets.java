package rr.tools.fieldoffsets;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import sun.misc.Unsafe;

public class FieldOffsets {

    private static Unsafe usafe;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.err.println("");
                System.err.println("usage: " + FieldOffsets.class + " <class name>");
                System.err.println("");
                System.exit(1);
            }

            getUnsafe();
            
            String clsName = args[0];
            Class cls = Class.forName(clsName);
            
            while (cls != null) {
                printFieldsOfClass(cls);
                Class subCls = cls;
                cls = cls.getSuperclass();
                if (cls != null) {
                    System.out.println(subCls + " extends " + cls);
                }
            }
            
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void getUnsafe() throws Exception {
        Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        usafe = (sun.misc.Unsafe) field.get(null);
    }

    private static void printFieldsOfClass(Class cls) {
        Field[] fields = cls.getDeclaredFields();
        
        System.out.println();
        System.out.println("Fields of " + cls + ":");
        System.out.println("======================================================");
        System.out.println("offset        field");
        System.out.println("------        -----");
        for (int i = 0; i < fields.length; i++) {
            Field fld = fields[i];
            System.out.printf("%6d ", fieldOffset(fld));
            System.out.println(fld);
        }
    }

    private static int fieldOffset(Field fld) {
        if (Modifier.isStatic(fld.getModifiers()))
            return (int) usafe.staticFieldOffset(fld);
        else
            return (int) usafe.objectFieldOffset(fld);
    }


}
