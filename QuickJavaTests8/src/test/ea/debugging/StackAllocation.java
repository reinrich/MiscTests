package test.ea.debugging;

public class StackAllocation {

    public static volatile int counter = 0;
    static volatile XYZ escape1, escape2;

    static int testMethod(long iterations) {
        XYZ dead_stack_obj = new XYZ(42, 52, 62);
        XYZ live_stack_obj = new XYZ(1, 2, 3);

        XYZ live_obj = new XYZ(10, 20, 30);
        XYZ dead_obj = new XYZ(12, 13, 14);
        escape1 = live_obj;
        escape2 = dead_obj;

        int   live_local = 10;
        int   dead_local = 101;

        int expected_live_stack_obj_x = live_stack_obj.x;
        
        while(iterations-- != 0) {
            counter++;
            live_stack_obj.z++;
            if (live_stack_obj.x != expected_live_stack_obj_x /* && notPrinted */) {
                expected_live_stack_obj_x = live_stack_obj.x;
                System.out.println("Debuggee: live_stack_obj._x has been modified. New value is " + live_stack_obj.x);
            }
        }

        live_local = live_obj.x + live_stack_obj.y; // Here, live_obj also dies.

        return live_local;
    }

    public static void call_caller(long calls, long iterations) {
        System.out.println(">>> calling caller");
        while (calls-- > 0) {
            testMethod(iterations);
        }
    }

    public static void main(String[] args) {
        System.out.println("RRRR");
        call_caller(1L << 10, 1L << 10);
        call_caller(1L << 10, 1L << 10);
        call_caller(1L << 10, 1L << 10);
        call_caller(10      , 1L << 60);
    }
  }
