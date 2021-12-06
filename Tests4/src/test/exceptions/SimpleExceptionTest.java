package test.exceptions;

/*

./sapjvm_8/bin/java
-Xint
-XX:+TraceExceptions
-cp /u/w/d/jtests/QuickJavaTests4/bin
test.exceptions.SimpleExceptionTest

 */

public class SimpleExceptionTest {

    public static void main(String[] args) {
        System.out.print("START");
        for(int i=0; i<10; i++) {
            try {
                doThrow();
            } catch (Exception e) {
                System.out.println(">>>>>>>>>>>>");
            }
        }
        System.out.print("END");
    }

    private static void doThrow() throws Exception {
        doThrow_01();
    }

    private static void doThrow_01() throws Exception {
        doThrow_02();
    }

    private static void doThrow_02() throws Exception {
        System.out.println("<<<<<<<<<<<<");
        throw new Exception("Hello World!");
    }

}
