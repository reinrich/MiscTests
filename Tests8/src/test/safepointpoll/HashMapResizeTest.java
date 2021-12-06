package test.safepointpoll;

import java.util.HashMap;

public class HashMapResizeTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

        for(int i = 0; i < 5000; i++) {
            createAndFillMap(512);
        }
        
    }

    static void createAndFillMap(int numEntries) {
        HashMap map = new HashMap();
        
        for(int i = 0 ; i < numEntries; i++) {
            Integer keyAndVal = new Integer(i);
            map.put(keyAndVal, keyAndVal);
        }
    }

}
