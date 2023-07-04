package rr.trc;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;

import static java.nio.file.StandardOpenOption.*;

/**
 * This class traces to a file. Useful when System.out and System.err have been redirected
 */

public class SimpleFileTrace {
    public static final String FILE_NAME = "/tmp/rr_simple_file_trace.txt";
    public static final String FILE_NAME_RAW = "/tmp/rr_simple_file_trace_raw.txt";
    
    private static PrintWriter writer;
    private static OutputStream stream;
    private static boolean failedToOpenFile;
    private static int count;
    
    public static synchronized void print(String msg) {
        if (failedToOpenFile) return;
        if (writer == null) {
            try {
                openTraceFile();
            } catch (IOException e) {
                e.printStackTrace();
                failedToOpenFile = true;
                return;
            }
        }
        writer.print(msg);
        writer.flush();
        try {
            stream.write(msg.getBytes(Charset.forName("UTF-8")));
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            writer.print("ERROR: cannot write to raw stream");
            writer.print(e);
            writer.flush();
        }
    }
    
    public static synchronized void println() {
        if (failedToOpenFile) return;
        if (writer == null) {
            try {
                openTraceFile();
            } catch (IOException e) {
                e.printStackTrace();
                failedToOpenFile = true;
                return;
            }
        }
        writer.println();
        writer.flush();
        try {
            stream.write(System.lineSeparator().getBytes(Charset.forName("UTF-8")));
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            writer.print("ERROR: cannot write to raw stream");
            writer.print(e);
            writer.flush();
        }
    }

    public static synchronized void println(String msg) {
        if (failedToOpenFile) return;
        if (writer == null) {
            try {
                openTraceFile();
            } catch (IOException e) {
                e.printStackTrace();
                failedToOpenFile = true;
                return;
            }
        }
        writer.println("--------------------------<"+(++count)+">------------------------");
        writer.println(msg);
        writer.flush();
        try {
            stream.write(msg.getBytes(Charset.forName("UTF-8")));
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            writer.print("ERROR: cannot write to raw stream");
            writer.print(e);
            writer.flush();
        }
    }

    private static void openTraceFile() throws IOException {
        File traceFile = new File(FILE_NAME);
        if (traceFile.exists()) {
            traceFile.delete();
        }
        writer = new PrintWriter(traceFile);
        stream = Files.newOutputStream(FileSystems.getDefault().getPath(FILE_NAME_RAW), CREATE, WRITE, TRUNCATE_EXISTING);
    }
}
