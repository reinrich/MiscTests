package test;

public class Tools {
    
    public static final int BY_WRITING = 0;
    public static final int BY_READING = 1;

    // POWER5
    private static final int DCACHE_SIZE = 32*1024;
    private static final int DCACHE_LINE_SIZE = 128; // bytes
    private static final int DCACHE_LOG_LINE_SIZE = 7;
    private static final int DCACHE_ASSOC = 4;
    private static final int DCACHE_NUM_LINES = DCACHE_SIZE>>DCACHE_LOG_LINE_SIZE;
    private static final int ICACHE_SIZE = 64*1024;
    private static final int ICACHE_LINE_SIZE = 128; // bytes
    private static final int ICACHE_LOG_LINE_SIZE = 7;
    private static final int ICACHE_ASSOC = 2;
    private static final int ICACHE_NUM_LINES = ICACHE_SIZE>>ICACHE_LOG_LINE_SIZE;

    
    private static byte[] DCACHE = new byte[DCACHE_SIZE];

    public static int dummy;
    
    public static void killDCache(int mode) {
        if (mode == BY_WRITING) {
            for (int i = 0; i < DCACHE.length; i+=DCACHE_LINE_SIZE) {
                DCACHE[i] = 0;
            }
        } else if (mode == BY_READING) {
            int checksum = 0;
            for (int i = 0; i < DCACHE.length; i+=DCACHE_LINE_SIZE) {
                checksum += DCACHE[i];
            }
            dummy = checksum;
        }
    }

    public static void killDCachePartially(int mode, int beginLine, int percent) {
        beginLine = beginLine & (DCACHE_NUM_LINES-1);
        int endLine = beginLine + (int)(DCACHE_NUM_LINES * ((float)percent / 100.0f));
        int startByte = beginLine<<DCACHE_LOG_LINE_SIZE;
        int endByte = endLine<<DCACHE_LOG_LINE_SIZE;
        endByte = endByte > DCACHE.length ? DCACHE.length : endByte;
        if (mode == BY_WRITING) {
            for (int i = startByte; i < endByte; i+=DCACHE_LINE_SIZE) {
                DCACHE[i] = 0;
            }
        } else if (mode == BY_READING) {
            int checksum = 0;
            for (int i = startByte; i < endByte; i+=DCACHE_LINE_SIZE) {
                checksum += DCACHE[i];
            }
            dummy = checksum;
        }
    }
}
