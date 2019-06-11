package test.ea.debugging;

public class StackAllocationLocked {

    public static volatile int counter = 0;
    static volatile XYZ escape1, escape2;
    private static long hundred;

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
            synchronized (live_stack_obj) {
                loop100(live_stack_obj);
                live_stack_obj.y++;
            }
            if (live_stack_obj.x != expected_live_stack_obj_x) {
                expected_live_stack_obj_x = live_stack_obj.x;
                System.out.println("Debuggee: live_stack_obj._x has been modified. New value is " + live_stack_obj.x);
            }
        }

        live_local = live_obj.x + live_stack_obj.y; // Here, live_obj also dies.

        return live_local;
    }

    private static void loop100(XYZ live_stack_obj) {
        for(long i = hundred; i >= 0; i--) {
            counter++;
            live_stack_obj.z++;
        }
    }

    public static void call_testMethod(long calls, long iterations) {
        System.out.println(">>> calling caller");
        while (calls-- > 0) {
            testMethod(iterations);
        }
    }

    public static void main(String[] args) {
        hundred = 100;
        startCount2Thread();
        call_testMethod(1L << 10, 1L << 10);
        call_testMethod(1L << 10, 1L << 10);
        call_testMethod(1L << 10, 1L << 10);
        call_testMethod(10      , 1L << 60);
    }

    static volatile boolean doCount2 = true;
    private static void startCount2Thread() {
        Thread thread = new Thread("Count2") {
            public void run() {
                long counter2 = 0;
                escape2 = new XYZ(12, 13, 14);
                while (doCount2) {
                    synchronized (escape2) {
                        counter2++;
//                        if ((counter2++ & ((1<<20)-1)) == 0)
//                            System.out.println("RRRR Started Count2");
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        };
        thread.start();
    }
  }
