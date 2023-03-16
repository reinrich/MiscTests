package test.methodhandles;

import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.*;

import java.lang.invoke.MethodHandle;

public class TestDropArgs {

    static boolean printStack = false;

    public static void main(String[] args) {
        try {
            MethodHandle cat = lookup().findStatic(TestDropArgs.class,
                    "doAdd", methodType(int.class, int.class, int.class));
            MethodHandle d0 = dropArguments(cat, 2, int.class);
            d0 = dropArguments(d0, 3, int.class);
            d0 = dropArguments(d0, 4, int.class);
            d0 = dropArguments(d0, 5, int.class);
            d0 = dropArguments(d0, 6, int.class);
            d0 = dropArguments(d0, 7, int.class);
            d0 = dropArguments(d0, 8, int.class);
            d0 = dropArguments(d0, 9, int.class);
            d0 = dropArguments(d0, 10, int.class);
            d0 = dropArguments(d0, 11, int.class);
            d0 = dropArguments(d0, 12, int.class);
            d0 = dropArguments(d0, 13, int.class);
            d0 = dropArguments(d0, 14, int.class);
            d0 = dropArguments(d0, 15, int.class);
            int res = 0;
            for (int i = 0; i < 1000*1000; i++) {
                res = (int)d0.invokeExact(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
            }
            printStack = true;
            res = (int)d0.invokeExact(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
            System.out.println("Result: " + res);
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    public static int doAdd(int a, int b) {
        if (printStack) {
            Thread.dumpStack();
        }
        return a + b;
    }

    // public static String doAdd(int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9, int a10, int a11, int a12, int a13, int a14, int a15, int a16) {
    //     Thread.dumpStack();
    //     return
    // }

}
