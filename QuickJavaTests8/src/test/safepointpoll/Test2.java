package test.safepointpoll;

import java.util.HashMap;

public class Test2 {

    
    private volatile int counter;
    public Test2(Test2 next) {
        this.next = next;
        this.val  = counter++;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        long checksum;
        
        // warmup
        checksum = 0;
        Test2[] src = genSrcArray(32);
        Test2[] dst = new Test2[64];
        for(int i = 0; i < 6000; i++) {
            testMethod(src, dst);
        }
        System.out.println("checksum: " + checksum);
    }

    private static Test2[] genSrcArray(int i) {
        Test2[] res = new Test2[i];
        for (int j = 0; j < i; j++) {
            res[j] = genQueue(10);
        }
        return res;
    }

    private static Test2 genQueue(int i) {
        Test2 cur = new Test2(null);
        for (int j = 0; j < i; j++) {
            cur = new Test2(cur);
        }
        return cur;
    }

    private Test2 next;
    private int val;
    static void testMethod(Test2[] src, Test2[] dst) {
        int newCapacity = dst.length;
        for (int j = 0; j < src.length; j++) {
            Test2 e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    System.out.println("j: "+j);
                    Test2 next = e.next;
                    int i = e.val & (newCapacity - 1);
                    e.next = dst[i];
                    dst[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }
//    static long testMethod(Test1 cur) {
//        long res = 0;
//        if (cur.next != null) {
//            if (cur.val != 0) {
//                do {
//                    res += cur.val;
//                    cur = cur.next;
//                } while (cur.next != null);
//            }
//        }
//        return res;
//    }
}
