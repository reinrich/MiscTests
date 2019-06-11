package test.gc;

import java.util.ArrayList;

import test.gc.microbench.TestOptions;

public abstract class TestBaseOld {

    private int verboseLevel;
    private int indentation;
    private ArrayList<Long> results;
    private long resultsAverage;
    
    public TestOptions opts;
    
    private static String[] indentationStrings = new String[80];

    /**
     * @param verboseLevel
     * @param numResults
     *            Should be specified to avoid resizing of
     *            {@link TestBaseOld#results} which can have effects on gc.
     */
    public TestBaseOld(int verboseLevel, int numResults) {
        this.verboseLevel = verboseLevel;
        results = new ArrayList<Long>(numResults);
    }

    public TestBaseOld() {
        this.verboseLevel = 3;
        results = new ArrayList<Long>();
    }

    public TestBaseOld(int verboseLevel, int numResults, TestOptions opts) {
        this(verboseLevel, numResults);
        this.opts = opts;
    }

    public void addResult(long res) {
        results.add(res);
    }

    public void printResults() {
        message();
        message("### Test Results");
        incMessageIndentation();
        resultsAverage = (long) results.stream().mapToLong(x->x).average().getAsDouble();
        results.forEach(res -> message(res.toString() + ((res > outlierFactor()*resultsAverage || res < resultsAverage/outlierFactor()) ? " outlier!!!" : "")));
        decMessageIndentation();
    }

    public abstract float outlierFactor();

    public void verboseMessage(String msg) {
        if (verboseLevel == 3) {
            printIndentation();
            System.out.println(msg);
        }
    }
    
    public void verboseMessage(Object obj) {
        if (verboseLevel == 3) {
            printIndentation();
            System.out.println(obj);
        }
    }
    
    public void message(String msg) {
        printIndentation();
        System.out.println(msg);
    }

    public void message(String msg, boolean newline) {
        if (!newline) {
            System.out.print(msg);
        } else {
            message(msg);
        }
    }

    public void messageNoIndent(String msg) {
        System.out.println(msg);
    }

    
    public void message() {
        System.out.println();
    }

    private void printIndentation() {
        if (indentation > 0) {
            String indentStr = indentationStrings[indentation];
            if (indentStr == null) {
                char[] tmp = new char[indentation];
                for (int i = 0; i < tmp.length; i++) {
                    tmp[i] = ' ';
                }
                indentStr = indentationStrings[indentation] = new String(tmp);
            }
            System.out.print(indentStr);
        }
    }


    public void incMessageIndentation() {
        indentation += 2;
    }

    public void decMessageIndentation() {
        indentation -= 2;
    }

    public int verboseLevel() {
        return verboseLevel;
    }
    
    public int setVerbose(int verbLevel) {
        int prevLevel = verboseLevel;
        verboseLevel = verbLevel;
        return prevLevel;
    }

    public void verboseMessage(int level, String msg) {
        if (level >= verboseLevel) {
            message(msg);
        }
    }

    public void verboseMessage(int level, Object msg) {
        if (level >= verboseLevel) {
            message(msg.toString());
        }
    }

    public float round(float f, int digits) {
        return Math.round(f*digits*10)/(digits*10f);
    }

}
