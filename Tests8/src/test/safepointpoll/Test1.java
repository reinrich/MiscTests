package test.safepointpoll;

import java.util.HashMap;

public class Test1 {

    
    private volatile int counter;
    public Test1(Test1 next) {
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
        Test1 queue = genQueue(100);
        for(int i = 0; i < 6000; i++) {
            testMethod(queue);
        }
        System.out.println("checksum: " + checksum);
    }

    private static Test1[] genSrcArray(int i) {
        Test1[] res = new Test1[i];
        for (int j = 0; j < i; j++) {
            res[j] = genQueue(10);
        }
        return res;
    }

    private static Test1 genQueue(int i) {
        Test1 cur = new Test1(null);
        for (int j = 0; j < i; j++) {
            cur = new Test1(cur);
        }
        return cur;
    }

    private Test1 next;
    private int val;
    static long testMethod(Test1 cur) {
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
