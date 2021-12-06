package test.biasedlocking;

public class TestBiasedLocking {

    private static final int OBJEKTS_PER_SITE = 2000;
    private static final int LOCKS_PER_OBJ = 4;
    private static TestObjsWithSyncMethod[] objsToBeLocked;
    public static long checksum = 0;

    static class TestObjsWithSyncMethod {
        public static volatile long dummyVolatile = 1;
        
        public synchronized long syncTestMethod() {
            return dummyVolatile ;
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {

            objsToBeLocked = new TestObjsWithSyncMethod[OBJEKTS_PER_SITE];
            testNonSharedSite();

            objsToBeLocked = new TestObjsWithSyncMethod[OBJEKTS_PER_SITE];
            testOneThreadAllocatesOtherThreadLocks();
            
            objsToBeLocked = new TestObjsWithSyncMethod[OBJEKTS_PER_SITE];
            testSharedSite();

            objsToBeLocked = new TestObjsWithSyncMethod[OBJEKTS_PER_SITE];
            testLockAfterHash();
            
            objsToBeLocked = new TestObjsWithSyncMethod[OBJEKTS_PER_SITE];
            testHashAfterLock();
            
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }

    }

    private static void testNonSharedSite() throws Exception {
        checksum = 0;
        for (int i = 0; i < OBJEKTS_PER_SITE; i++) {
            objsToBeLocked[i] = new TestObjsWithSyncMethod();
            for (int j = 0; j < LOCKS_PER_OBJ; j++) {
                checksum += objsToBeLocked[i].syncTestMethod();
            }
        }
        
        System.out.println("testSharedSite: checksum == " + checksum);
    }

    private static void testOneThreadAllocatesOtherThreadLocks() throws Exception {
        // allocate in current thread
        checksum = 0;
        for (int i = 0; i < OBJEKTS_PER_SITE; i++) {
            objsToBeLocked[i] = new TestObjsWithSyncMethod();
        }
        
        // lock in other thread
        Thread thr = new Thread() {
            public void run() {
                for (int i = 0; i < OBJEKTS_PER_SITE; i++) {
                    for (int j = 0; j < LOCKS_PER_OBJ; j++) {
                        checksum += objsToBeLocked[i].syncTestMethod();
                    }
                }
            }
        };
        thr.start();
        thr.join();
        
        System.out.println("testOneThreadAllocatesOtherThreadLocks: checksum == " + checksum);
    }

    private static void testSharedSite() throws Exception {
        checksum = 0;
        for (int i = 0; i < OBJEKTS_PER_SITE; i++) {
            objsToBeLocked[i] = new TestObjsWithSyncMethod();
            for (int j = 0; j < LOCKS_PER_OBJ/2; j++) {
                checksum += objsToBeLocked[i].syncTestMethod();
            }
        }
        
        Thread thr = new Thread() {
            public void run() {
                for (int i = 0; i < OBJEKTS_PER_SITE; i++) {
                    for (int j = 0; j < LOCKS_PER_OBJ/2; j++) {
                        checksum += objsToBeLocked[i].syncTestMethod();
                    }
                }
            }
        };
        thr.start();
        thr.join();
        
        System.out.println("testSharedSite: checksum == " + checksum);
    }

    private static void testLockAfterHash() throws Exception {
        checksum = 0;
        long checksumOfHashes = 0;
        for (int i = 0; i < OBJEKTS_PER_SITE; i++) {
            objsToBeLocked[i] = new TestObjsWithSyncMethod();
            checksumOfHashes += objsToBeLocked[i].hashCode();
            for (int j = 0; j < LOCKS_PER_OBJ; j++) {
                checksum += objsToBeLocked[i].syncTestMethod();
            }
        }
        
        System.out.println("testLockAfterHash: checksum == " + checksum);
        System.out.println("testLockAfterHash: checksumOfHashes == " + checksumOfHashes);
    }

    private static void testHashAfterLock() throws Exception {
        checksum = 0;
        long checksumOfHashes = 0;
        for (int i = 0; i < OBJEKTS_PER_SITE; i++) {
            objsToBeLocked[i] = new TestObjsWithSyncMethod();
            for (int j = 0; j < LOCKS_PER_OBJ; j++) {
                checksum += objsToBeLocked[i].syncTestMethod();
            }
            checksumOfHashes += objsToBeLocked[i].hashCode();
        }
        
        System.out.println("testLockAfterHash: checksum == " + checksum);
        System.out.println("testLockAfterHash: checksumOfHashes == " + checksumOfHashes);
    }

}
