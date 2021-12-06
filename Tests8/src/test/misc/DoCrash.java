package test.misc;


import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class DoCrash {

    public static Unsafe usafe;

    public static void main(String[] args) {
        try {
            getUnsafe();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }
        
        usafe.putAddress(0, 42);
    }
    
    private static void getUnsafe() throws Exception {
        Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        usafe = (sun.misc.Unsafe) field.get(null);
    }

}
