package rr.tools.gdl;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HighlightSubgraph {
    
    private static String inputGdlFile;
    // nodes of subgraph which is to be highlighted;
    // note: there are ids which aren't numbers, e.g. '42_1'
    private static ArrayList<String> idsOfSelectedNodes;

    // highlight a subgraph by *not* omitting its edges;
    // edges of the rest of the graph are omitted
    private static boolean highlightByNotOmitting = false;
    // highlight a subgraph by tagging its edges as near edges
    private static boolean highlightAsNearEdge    = false;
    // highlight a subgraph by tagging its edges as priority edges
    private static boolean highlightAsPriorityEdge = false;
    
    private static final Pattern edgePattern =
        Pattern.compile("edge: \\{sourcename: \"node_([^\"]+)\" targetname: \"node_([^\"]+)\" .* class: ([0-9]+)");

    private static void removeDuplicate(ArrayList arlList) {
     HashSet h = new HashSet(arlList);
     arlList.clear();
     arlList.addAll(h);
    }
    
    private static Matcher matchPattern(Pattern pattern, String line) {
        Matcher matcher = pattern.matcher(line);
        if (!matcher.find()) {
            throw new Error("cannot match pattern '" + pattern + "' with the line '" + line + "'");
        }
        return matcher;
    }
    
    private static void processGdlFile() throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(inputGdlFile));
        try {
          while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            Matcher matcher = edgePattern.matcher(line);
            boolean foundEdge = matcher.find();
            boolean highlight = false;
            if (foundEdge) {
                // analyze edge and determine whether it has to be highlighted
                boolean is_ctrl_edge = matcher.group(3).equals("02"); // class: 02
                boolean is_data_edge = matcher.group(3).equals("05"); // class: 05
                boolean n1_matches = idsOfSelectedNodes.contains(matcher.group(1));
                boolean n2_matches = idsOfSelectedNodes.contains(matcher.group(2));

                boolean both_nodes_of_edge_selected = n1_matches && n2_matches;
                boolean one_node_of_edge_selected = n1_matches || n2_matches;
                highlight = highlight || both_nodes_of_edge_selected;
                // skip data edges if only one node is selected; otherwise the graph gets to big
                highlight = highlight || (one_node_of_edge_selected && !is_data_edge);
                // highlight ctrl edges even if only one node is selected
                highlight = highlight || (one_node_of_edge_selected && is_ctrl_edge);
            }
            if (!foundEdge
                // print edge only if highlighted otherwise omit it
                || (highlight && highlightByNotOmitting)
                // verbatim print edge if we don't omit edges and if it is not to be highlighted
                || (!highlight && !highlightByNotOmitting)) {
                System.out.println(line);
            } else if (highlight && !highlightAsNearEdge ) {
                // highlight by tagging as near edge
                System.out.println("nearedge" + line.substring(4, line.length()));
            } else if (highlight && !highlightAsPriorityEdge) {
                System.out.println(line.substring(0, line.length() - 1) + " priority: 2}");                
            }
          }
        }
        finally{
          scanner.close();
        }
      }
    
    private static void printUsage() {
        System.err.println();
        System.err.println("usage: HighlightSubgraph <highlight mode> <node id> [<node id> ...]");
        System.err.println();
        System.err.println("   highlight modes:");
        System.err.println();
        System.err.println("         'O': highlight subgraph by *not* omitting its edges");
        System.err.println("         'N': highlight a subgraph by tagging its edges as near edges");
        System.err.println("         'P': highlight a subgraph by tagging its edges as priority edges");
        System.err.println();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length <= 1) {
            printUsage();
            System.exit(1);
        }
        int i = 0;
        inputGdlFile = args[i++];
        // args[0] == 1 > highlight als nearedge 
        highlightByNotOmitting = args[i].equals("O");
        highlightAsNearEdge = args[i].equals("N");
        highlightAsPriorityEdge = args[i].equals("P");
        if (!highlightByNotOmitting && !highlightAsNearEdge && !highlightAsPriorityEdge) {
            System.err.println();
            System.err.println("ERROR: no highlight mode selected");
            printUsage();
            System.exit(1);
        }
        i++;
        
        idsOfSelectedNodes = new ArrayList<String>();
        for(; i < args.length; i++) {
            idsOfSelectedNodes.add(args[i]);
        }
        removeDuplicate(idsOfSelectedNodes);
        Collections.sort(idsOfSelectedNodes);
        // print sorted list of selected nodes; can be used as input for next run
        for (Iterator iterator = idsOfSelectedNodes.iterator(); iterator.hasNext();) {
            String nodeid = (String) iterator.next();
            System.err.print(nodeid + " ");
        }
        System.err.println();
        try {
            processGdlFile();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
