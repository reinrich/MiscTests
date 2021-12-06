package test.safepointpoll;

import java.util.HashMap;

public class Test3 {

    
    private volatile int counter;
    public Test3(Test3 next) {
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
        Test3 queue = genQueue(100);
        for(int i = 0; i < 6000; i++) {
            testMethod(queue);
        }
        System.out.println("checksum: " + checksum);
    }

    private static Test3[] genSrcArray(int i) {
        Test3[] res = new Test3[i];
        for (int j = 0; j < i; j++) {
            res[j] = genQueue(10);
        }
        return res;
    }

    private static Test3 genQueue(int i) {
        Test3 cur = new Test3(null);
        for (int j = 0; j < i; j++) {
            cur = new Test3(cur);
        }
        return cur;
    }

    private Test3 next;
    private int val;
    static long testMethod(Test3 cur) {
        if (cur.next != null) {
            if (cur.val != 0) {
                do {
                    cur = cur.next;
                } while (cur.next != null);
            }
        }
        return cur.val;
    }
}
