package test.gc;

//-verbose:gc -Xms2G -Xmx2G -Xmn1G -XX:+UseParallelGC -XX:ParallelGCThreads=16 -XX:-UseAdaptiveSizePolicy

public class BigArrayInOldGenRR_Every2ndCardDirty {


    public static final int K = 1024;
    public static final int M = 1024*K;
    public static final int BIG_ARR_LEN = 8 * M;
    public static final int CARD_SIZE_BYTES = 512;
    public static final int CARD_SIZE_WORDS = CARD_SIZE_BYTES / 8;
    public static final int STRIDE = 2 * CARD_SIZE_WORDS;
    public static final int ELTS_PER_CARD = CARD_SIZE_WORDS;
    public static final Object[] BIG_OLD_ARRAY = new Object[BIG_ARR_LEN];
    public static final int NUM_TMPS_FOR_GC = 500 * K;
    public static int first_elt;
    public static volatile byte[] blackHole;

    public static void main(String[] args) {
        System.out.println();
        System.out.println("BIG_ARR_LEN:" + BIG_ARR_LEN + " STRIDE:" + STRIDE + " ELTS_PER_CARD:" + ELTS_PER_CARD);
        System.out.println();

        for (int i = 0; i < BIG_ARR_LEN; i += 1) {
            BIG_OLD_ARRAY[i] = new Object();
        }
        System.gc();

        while (true) {
            allocateNewElements();
            triggerYoungGC();
        }
    }

    public static void triggerYoungGC() {
        for (int i = 0; i < NUM_TMPS_FOR_GC; i += 1) {
            blackHole = new byte[2000];
        }
    }

    static void allocateNewElements() {
        final Object[] big = BIG_OLD_ARRAY;

        System.out.println("Allocating new elements.");
        for (int i = first_elt++; i < BIG_ARR_LEN; i += STRIDE) {
            big[i] = new Object();
            for (int j = i + 1; j < Math.min(BIG_ARR_LEN , i + ELTS_PER_CARD); ++j) {
                big[j] = new Object();
            }
        }
        System.out.println("DONE.");
    }
}
