package rr.ideal.dump;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DumpNode {

    public static final int TRC_LEVEL = 3;

    private ArrayList<Node> _nodes;
    private String _idealFileName;
    private int _firstLine;
    private int _lastLine;
    private int _nodeIdx;
    private int _depth;

    public DumpNode(String[] args) {
        if (args.length != 3) {
            printUsageAndExit("Wrong number of parameters", null);
        }

        try {
            parseIdealFile(args[0]);
        } catch (Exception e) {
            printUsageAndExit("Could not parse ideal file", e);
        }

        try {
            _nodeIdx = Integer.parseInt(args[1]);
        } catch (Exception e) {
            printUsageAndExit("Could not parse node idx", e);
        }

        try {
            _depth = Integer.parseInt(args[2]);
        } catch (Exception e) {
            printUsageAndExit("Could not parse depth", e);
        }
    }

    private void parseIdealFile(String idealFile) {
        int firstLineIdx = idealFile.indexOf(':');
        _idealFileName = firstLineIdx >= 0 ? idealFile.substring(0, firstLineIdx) : idealFile;

        if (firstLineIdx >= 0) {
            String firstLineStr = _idealFileName.substring(firstLineIdx);

            int lastLineIdx = firstLineStr.indexOf(':');
            if (lastLineIdx > 0) {
                String lastLineStr = firstLineStr.substring(lastLineIdx);
                _lastLine = Integer.parseInt(lastLineStr);
                firstLineStr = firstLineStr.substring(0, lastLineIdx);
            }

            _firstLine = Integer.parseInt(firstLineStr);
        }
    }

    public static void main(String[] args) {
        new DumpNode(args).dump();
    }

    private void dump() {
        readNodesFromFile();
        dumpAll();
    }

    private void dumpAll() {
        msg("All nodes: ");
        for (int i = 1; i < _nodes.size(); i++) {
            System.out.println(_nodes.get(i));
        }
    }

    private void readNodesFromFile() {
        _nodes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(_idealFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
               Node n = Node.parseNode(line);
               add(n);
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private void add(Node n) {
        _nodes.ensureCapacity(n._idx);
        int eltsToAdd = n._idx - _nodes.size() + 1;
        while(eltsToAdd-- > 0) {
            _nodes.add(Node.NONE);
        }
        _nodes.add(n._idx, n);
    }

    private void printUsageAndExit(String errMsg, Exception e) {
        System.err.println();
        System.err.println("Error:" + errMsg);
        System.err.println();
        System.err.println("Usage: " + getClass() + " <ideal graph file[:firstline[:lastline]]> <node idx to dump> <+/-depth>");
        System.err.println();
        if (e != null) {
            System.err.println("Exception: " + e);
            e.printStackTrace(System.err);
        }
        System.exit(1);
    }

    public static void msg(String m) {
        System.out.println("### " + m);;
    }
}

class Node {

    public static final Node NONE = new Node(0, "NULL", new int[0], new int[0], "# none");
    public int _idx;
    public String _name;
    public String _spec;

    public int[] _in;
    public int[] _out;
    
    // e.g. 327 CallStaticJava  ===  335  1  7  8  1 ( 21  10  1  1 ) [[ 329  36  35  26 ]] # Static _new_instance_Java  rawptr:NotNull ( java/lang/Object:NotNull * ) C=0.000100 RRTest::dontinline_testMethod @ bci:0 !orig=25 !jvms: RRTest::dontinline_testMethod @ bci:0
    private static Pattern nodePattern = Pattern.compile("\\s+(\\d+)\\s+(\\w+)\\s+===\\s+(.+)\\[\\[(.*)\\]\\](.*)");

    // Ex: 335  1  7  8  1 ( 21  10  1  1 ) 
    private static Pattern idxsPattern = Pattern.compile("\\s*(\\d+)");

    public Node(int idx, String name, int[] ins, int[] outs, String spec) {
        _idx = idx;
        _name = name;
        _in = ins;
        _out = outs;
        _spec = spec;
    }
    
    public String toString() {
        if (this == NONE) {
            return "NONE";
        }
        String result = String.format("%c%d\t%s\t === ", ' ', _idx, _name);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < _in.length; i++) {
            ((_in[i] > 0) ? sb.append(_in[i]) : sb.append('_')).append(' ');
        }
        result += sb.toString();

        sb = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < _in.length; i++) {
            if (first) {
                sb.append(' ');
                first = false;
            }
            ((_in[i] > 0) ? sb.append(_in[i]) : sb.append('_')).append(' ');
        }
        result += "[[" + sb.toString() + "]]";
        result += _spec;
        return result;
    }

    public static Node parseNode(String nodeLine) {
        msg("Parsing:<<<" + nodeLine + ">>>");
        Matcher matcher = nodePattern.matcher(nodeLine);
        Node n = NONE;
        if (matcher.find()) {
            if (trc(1)) {
                msg("idx:" +  matcher.group(1));
                msg("name:" + matcher.group(2));
                msg("ins:" +  matcher.group(3));
                msg("outs:" + matcher.group(4));
                msg("spec:" + matcher.group(5));
            }
            n = new Node(
                    Integer.parseInt(matcher.group(1)), // idx
                    matcher.group(2), // name
                    parseIdxs(matcher.group(3)), // ins
                    parseIdxs(matcher.group(4)), // outs
                    matcher.group(5) // spec
                    );
        }
        return n;
    }

    private static int[] parseIdxs(String idxs) {
        if (trc(3)) {
            msg("parseIdxs:\"" + idxs + '"');
        }
        Matcher matcher = idxsPattern.matcher(idxs);
        int cnt = 0;
        while (matcher.find()) cnt++;
        int[] result = new int[cnt];
        matcher.reset();
        int i = 0;
        while (matcher.find()) {
            result[i] = Integer.parseInt(matcher.group(1));
            if (trc(3)) {
                msg(""+result[i]);
            }
            i++;
        }
        return result;
    }

    private static boolean trc(int i) {
        return i <= DumpNode.TRC_LEVEL;
    }

    public static void msg(String m) {
        DumpNode.msg(m);
    }
}
