package test.debugging;

public class TestUpdateLocalOfDoubleInlinedMethod {
    private static boolean doLoop;

    public static void main() {
        doLoop = true;
        while (doLoop) {
            caller();
        }
    }

    public static void caller() {
        // TODO Auto-generated method stub
        
    }
    
    public static void inlinedCallee() {
        // TODO Auto-generated method stub
        
        // UNFINISHED !!!!!!!!!
        
    }
}
