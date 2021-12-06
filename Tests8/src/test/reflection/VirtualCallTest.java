package test.reflection;

import java.lang.reflect.Method;

public class VirtualCallTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Class[] parameterTypes = new Class[] {String.class};
            // get method from ClassA
            Method meth = ClassA.class.getMethod("printMsg", parameterTypes);
            Object[] argList = new Object[] { "Bonjour!" };
            // invoke on instance of ClassB, which overrides the method
            // -> to which method will be dispatched??
            meth.invoke(new ClassB(), argList);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}

class ClassA {
    public void printMsg(String msg) {
        System.out.println(ClassA.class + ".printMsg: " + msg);
    }
}

class ClassB extends ClassA{
    // override!
    public void printMsg(String msg) {
        System.out.println(ClassB.class + ".printMsg: " + msg);
    }
}
