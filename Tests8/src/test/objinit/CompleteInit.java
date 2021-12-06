package test.objinit;

public class CompleteInit {

    public static CompleteInit obj;
    public CompleteInit _a;
    public CompleteInit _b;
    
    public CompleteInit(CompleteInit a, CompleteInit b) {
        _a = a;
        _b = b;
    }


    public CompleteInit() {    }


    public static void testMethod(CompleteInit a, CompleteInit b) {
        obj = new CompleteInit(a, b);
    }
    
    
    public static void main(String[] args) {
        CompleteInit a = new CompleteInit();
        CompleteInit b = new CompleteInit();
        for (int i = 0; i < 1000000; i++) {
            testMethod(a, b);
        }
    }

}
