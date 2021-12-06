package test.misc;

import static test.misc.VMVersion.VmType.*;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VMVersion {
    enum VmType {
        VM_TYPE_DBG,
        VM_TYPE_OPT,
        VM_TYPE_UNKNOWN
    };
    private static Object vmType;
    public static void main(String[] args) {
        test01();
        test02();
    }

    public static void test01() {
        {
            String prop_type = System.getProperty("com.sap.vm.type");
            vmType = VM_TYPE_UNKNOWN;
            if (prop_type != null) {
                if (prop_type.equals("opt")) {
                    vmType = VM_TYPE_OPT;
                } else if(prop_type.contains("dbg")) {
                    vmType = VM_TYPE_DBG;
                }
            }
            
            System.out.println("prop_type: " + prop_type);
            System.out.println("vmType:"+vmType);
        }
    }

    public static void test02() {
        String[] testCases = {
                "8.1.035 25.51-b02",
                "8.1.1804 25.51-b02",
                "8.1.internal 25.51-b02",
                "9.1.180228 9.0.4+011",
                "11.070 9.0.4+011"
                };
        Arrays.stream(testCases).forEach(tc -> test02_parse(tc));
    }

    private static void test02_parse(String tc) {
//        Pattern p = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)\\. .*");
//        Pattern p = Pattern.compile("(\\d+)\\.(\\d).(\\d\\d\\d) .*");
        int J = -1;
        int M = -1;
        int P = -1;

        System.out.println("## parsing " + tc);
        Matcher m = null;
        if ((m = Pattern.compile("(\\d+)\\.(\\d).(\\d\\d\\d) .*").matcher(tc)).find()) {
            J = Integer.parseInt(m.group(1));
            M = Integer.parseInt(m.group(2));
            P = Integer.parseInt(m.group(3));
        } else if ((m = Pattern.compile("(\\d+)\\.(\\d\\d\\d) .*").matcher(tc)).find()) {
            J = Integer.parseInt(m.group(1));
            P = Integer.parseInt(m.group(2));
        } else if ((m = Pattern.compile("(\\d+)\\..*").matcher(tc)).find()) {
            J = Integer.parseInt(m.group(1));
        }
        System.out.println("J:" + J + " M:" + M + " P:" + P);
    }
}


