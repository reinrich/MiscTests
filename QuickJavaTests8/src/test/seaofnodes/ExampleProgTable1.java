package test.seaofnodes;

public class ExampleProgTable1 {
    
    private static int counter;
    public static boolean read() {
        return counter-- == 0;
    }
    
    public static void main(String[] args) {
        // warmup
        for(int i = 0; i < 100000; i++) {
            counter = 0;
            testMethod();
        }
        System.out.println("counter: " + counter);
    }
    
    // x0: 1;
    // 
    // do {
    //    x1: phi ( x0, x3 );
    //    cond: (x1 != 1);
    //    if (cond) {
    //      x2: 2;
    //    }
    //    x3: phi (x2, x1);
    // } while ( read() );
    // 
    // return x3;
    
    public static int testMethod() {
        int x = 1;
        do {
            boolean cond = (x != 1);
            if (cond) {
                x = 2;
            }
        } while ( read() );
        return x;
    }
}
