package test.misc;

import java.io.PrintStream;

public class NewTests {

    /* standalone interface */
    public static void main(String argv[]) {
        NewTests test = new NewTests();
        test.ref = new RRPrintStream(System.out, false);
        compileGetChars();
        Status rs;
        
        rs = test.StringBuffer0044_stripped(1, 2, 7, 0);
        System.out.println("### finished with " + rs);
    }

    private static void compileGetChars() {
        StringBufferGetCharsTest.warmUp(10000);
    }

    public PrintStream ref;

    private Status StringBuffer0044_stripped(int srcB, int srcE, int dstB, int dstS) {
        StringBuffer sb = new StringBuffer("sample string buffer");
        char dst[] = null;

        // cycle including all possible combinations
        { //srcBegin cycle
            { //srcEnd cycle
                { //dstBegin cycle
                    { //dst size cycle
//                        notifyCycle(srcB, srcE, dstB, dstS);
                        dst = new char[ 0 ];
                        try {
                            sb.getChars(0, 1, dst, Integer.MAX_VALUE);
                        } catch (IndexOutOfBoundsException iobe) {}

                    }
                }
            }
        }
        return Status.passed("OKAY");
    }
    
    private void notifyCycle(int srcB, int srcE, int dstB, int dstS) {
        ref.println("srcB:"+srcB+" srcE:"+srcE+" dstB:"+dstB+" dstS:"+dstS);
    }
}
