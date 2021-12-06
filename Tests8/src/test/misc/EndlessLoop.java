package test.misc;

public class EndlessLoop {

    public static int durationSecs;

    public static void main(String[] args) {
        durationSecs = parseDuration(args);
        System.out.println("Java: ENDLESSLOOP (dration " + durationSecs + "s)");
        long end = System.currentTimeMillis() + 1000 * durationSecs;
        while (durationSecs == 0 || System.currentTimeMillis() < end) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private static int parseDuration(String[] args) {
        return args.length > 0 ? Integer.parseInt(args[0]) : 0;
    }
}
