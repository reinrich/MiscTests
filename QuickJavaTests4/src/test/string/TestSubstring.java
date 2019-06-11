package test.string;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class TestSubstring {

    private static Field valueField;
    private static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe)field.get(null);

            valueField = String.class.getDeclaredField("value");
            valueField.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static Object getValue(String string) {
        try {
            return valueField.get(string);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String testString = "Hello World!";
        String subString = testString.substring(1, 5);
        String internedString = subString.intern();
        
        char[] chararray = (char[]) getValue(subString);
        System.out.println("Substring: '" + subString + "' length: " + subString.length() + " chararray length: "
        + ((char[]) getValue(subString)).length);
        System.out.println("internedString: '" + internedString + "' length: " + internedString.length() + " chararray length: "
        + ((char[]) getValue(internedString)).length);
        
    }

}
