package testlib;

import java.util.Scanner;

public abstract class TestBase implements Tracing {

    private Scanner sysInScanner;

    public float rndF(float f, int digits) {
        return Math.round(f*digits*10)/(digits*10f);
    }

    public static int rndF(float f) {
        return Math.round(f);
    }

    public void waitForEnter(String prompt) {
        log(prompt);
        waitForEnter();
    }

    public void waitForEnter() {
        try {
            do {
                System.in.read();
            } while (System.in.available() > 0);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public char getCharacter(String prompt) {
        log(prompt);
        return getCharacter();
    }

    public synchronized char getCharacter() {
        char ch = 0;
        try {
            do {
                if (sysInScanner == null) {
                    sysInScanner = new Scanner(System.in);
                }
                ch  = sysInScanner.nextLine().charAt(0);
            } while (System.in.available() > 0);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return ch;
    }

    public void runTest() {
        throw new Error("Subclass should override the runTest method");
    }
}
