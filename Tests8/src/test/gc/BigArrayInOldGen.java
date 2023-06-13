package test.gc;

public class BigArrayInOldGen {

    public static volatile Object[][] bigRoot;

    public static void main(String[] args) {
        int nrOfRoots = 1;
        int len = 8 * 1024 * 1024;
        int dups = 16; // The number of times we reuse a reference to an object
        bigRoot = new Object[nrOfRoots][];

        for (int i = 0; i < nrOfRoots; ++i) {
            bigRoot[i] = new Object[len];
        }

        while (true) {
            for (int r = 0; r < nrOfRoots; ++r) {
                Object[] big = bigRoot[r];

                for (int i = 0; i < len; i += dups) {
                    big[i] = new Object();

                    for (int j = i + 1; j < Math.min(len , i + dups); ++j) {
                        big[j] = big[i];
                    }
                }
            }
        }
    }
}
