package test.ea.debugging;

public class CopyOfStackAllocation {

    public static volatile int counter = 0;
    static volatile XYZ escape1, escape2;

    static void callee() {
      //while (run == 1) {
        // spin forever until we reset 'run' in the debugger
      //}
    }

    static int caller(long iterations) {
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
            //          synchronized(live_obj) {
            // 1. case
            // - Can fields of o be accessed here at a breakpoint (o is not dead!)
            //   => fields of o must be collected from the stack and copied to a new o on the heap
            // - Can fields of o be accessed here with thread suspend?
            //   => No deoptimization happens. Can we still collect the object fields from the stack?
            //callee();
            counter++;
            if (live_stack_obj.x != expected_live_stack_obj_x /* && notPrinted */) {
                expected_live_stack_obj_x = live_stack_obj.x;
                System.out.println("Debuggee: live_stack_obj._x has been modified. New value is " + live_stack_obj.x);
            }
            //          }
        }

        live_local = live_obj.x + live_stack_obj.y; // Here, live_obj also dies.

        callee();

        return live_local;
    }

    public static void call_caller(long calls, long iterations) {
        System.out.println(">>> calling caller");
        while (calls-- > 0) {
            caller(iterations);
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

//class MyObj {
//
//    int _x, _y, _z;
//
//
//    MyObj(int x, int y, int z) {
//        _x = x;
//        _y = y;
//        _z = z;
//    }
// }
