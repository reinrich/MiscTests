package test.misc;

public class Primes {

    public static void main(String[] args) {
        int limit = Integer.parseInt(args[0]);
        int limit2 = Integer.parseInt(args[1]);
        boolean[] primes = new boolean[limit];
        findPrimes(new boolean[10000]); // warm-up
        findPrimes(primes);
        int elemsOnLine = 0;
        for (int i = 1; i < Math.min(limit2, primes.length); i += 2) {
            boolean b = primes[i];
            if (b) {
                System.out.print(i+" ");
                if (++elemsOnLine > 10) {
                    System.out.println();
                    elemsOnLine = 0;
                }
            }
        }
        System.out.println();
    }

    public static void findPrimes(boolean[] primes) {
        for (int i = 1; i < primes.length; i+=2) {
            primes[i] = true;
        }
        int pr = 3;
        while (pr <= (primes.length >> 1)) {
            // mark multiples of pr as non-primes
            for (int i = pr+pr; i < primes.length; i += pr) {
                primes[i] = false;
            }
            // find next primes
            while (!primes[pr += 2]) {
                
            }
        }
    }
}
