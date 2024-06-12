package test.typecheck;

public class CheckCastTests {

    public static interface InterfaceA{}
    public static interface InterfaceB{}
    public static interface InterfaceC{}

    public static class Sub1 implements InterfaceA, InterfaceB, InterfaceC {
        // empty
    }

    public static class Sub2 implements InterfaceA, InterfaceB, InterfaceC {
        // empty
    }

    public static class Sub3 implements InterfaceA, InterfaceB, InterfaceC {
        // empty
    }

    public static class Sub4 implements InterfaceA, InterfaceB, InterfaceC {
        // empty
    }

    public static class Sub1WithoutB implements InterfaceA, /*InterfaceB,*/ InterfaceC {
        // empty
    }

    public static InterfaceA dummy;

    public static void main(String[] args) {
        dontjit_callTestMethod_1();
    }

    public static Sub1 testMethod_0(Object obj) {
        return (Sub1) obj;
    }

    public static void dontjit_callTestMethod_1() {
        Object obj  = new Object();
        Object obj1 = new Sub1();
        Object obj2 = new Sub2();
        Object obj3 = new Sub3();
        Object obj4 = new Sub4();
        for(int i = 0; i < 10_000; i++) {
            dojit_testMethod_1(obj1);
            dojit_testMethod_1(obj2);
            dojit_testMethod_1(obj3);
            dojit_testMethod_1(obj4);
        }
        System.err.println("### warm-up done");
        dummy = (InterfaceA)obj1;   // kills _secondary_super_cache
        dojit_testMethod_1(obj1); // cache miss
        dojit_testMethod_1(obj1); // cache hit?
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

    public static InterfaceB dojit_testMethod_1(Object obj) {
        return (InterfaceB) obj;
    }

}
