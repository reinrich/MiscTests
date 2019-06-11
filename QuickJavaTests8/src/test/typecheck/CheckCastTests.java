package test.typecheck;

public class CheckCastTests {

    public static interface InterfaceA{}
    public static interface InterfaceB{}
    public static interface InterfaceC{}
    
    public static class Sub1 implements InterfaceA, InterfaceB, InterfaceC {
        // empty
    }
    
    public static class Sub1WithoutB implements InterfaceA, /*InterfaceB,*/ InterfaceC {
        // empty
    }
    
    public static class Sub2 extends Sub1 {
        // empty
    }

    public static InterfaceA dummy;

    public static void main(String[] args) {
        donjit_callTestMethod_1();
    }

    // check class casts
    public static void donjit_callTestMethod_0() {
//        // test case 1: same type, Sub2 not loaded
//        Object obj = new Sub1();
//        // test case 2: obj is real subtype
//        Object obj = new Sub2();
        // test case 1: same type, Sub2 loaded
        new Sub2();
        Object obj = new Sub1();
        for(int i = 0; i < 10_010; i++) {
            testMethod_0(obj);
        }
    }

    public static Sub1 testMethod_0(Object obj) {
        return (Sub1) obj;
    }

    public static void donjit_callTestMethod_1() {
        Object obj  = new Object();
        Object obj1 = new Sub1();
        for(int i = 0; i < 10_000; i++) {
            testMethod_1(obj1);
        }
        System.err.println("### warm-up done");
        dummy = (InterfaceA)obj1;   // kills _secondary_super_cache
        testMethod_1(obj1); // cache miss
        testMethod_1(obj1); // cache hit?
        try {
//            testMethod_1(obj); // cache hit? (no interfaces implemented)
        } catch (Exception e) { // ignored
            System.out.println("### Exception");
        }
        try {
//            testMethod_1(new Sub1WithoutB()); // cache hit? (some interfaces implemented)
        } catch (Exception e) { // ignored
            System.out.println("### Exception");
        }
    }

    public static InterfaceB testMethod_1(Object obj) {
        return (InterfaceB) obj;
    }

}
