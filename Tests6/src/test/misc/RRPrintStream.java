package test.misc;

import java.io.OutputStream;
import java.io.PrintStream;

public class RRPrintStream extends PrintStream {

    private final boolean quiet;

    public RRPrintStream(OutputStream out, boolean quiet) {
        super(out);
        this.quiet = quiet;
    }
    
    public void println(String txt) {
        if (!quiet)
            super.println(txt);
    }

}
