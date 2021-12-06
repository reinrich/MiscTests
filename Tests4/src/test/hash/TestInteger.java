package test.hash;

public class TestInteger {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Integer[] arrayOfIntegers = new Integer[1000];
        
        for (int i = 0; i < arrayOfIntegers.length; i++) {
            arrayOfIntegers[i] = new Integer(i);
        }
        
        long checksum = 0;
        for (int j = 0; j < 1000; j++) {
            for (int i = 0; i < arrayOfIntegers.length; i++) {
                checksum += testMethod(arrayOfIntegers[i]);
            }
        }
        System.out.println("checksum: "+checksum);
    }

    public static long testMethod(Object integerInput) {
        return integerInput.hashCode() + 4711;
    }

}
