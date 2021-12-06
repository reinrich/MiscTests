package test.inlining;

// TestCase: will testMethodB() be inlined with the compileonly command?
// ========
// java -XX:+UnlockDiagnosticVMOptions -XX:-UseOnStackReplacement '-XX:CompileCommand=compileonly,*::testMethodA' -Xbatch -XX:CICompilerCount=1 -showversion -cp /net/usr.work/d/jtests/QuickJavaTests/bin -XX:+PrintCompilation -XX:+PrintInlining -XX:-TieredCompilation -XX:+TraceCompilerOracle test.inlining.InliningSimple
//
// -> pass_initial_checks() prevents inlining of testMethodB, because it calls
// CompilerOracle::should_exclude() which rejects testMethodB, because it is not on the
// compileonly positive list.

public class InliningSimple {

    public static void main(String[] args) {
        long checksum = 0;
        for(int i=0; i<100000; i++) {
            checksum += testMethodA(i);
        }
        System.out.println("checksum: " + checksum);
    }
    
    public static long testMethodA(int i) {
        return testMethodB(i % 17);        
    }
    
    public static long testMethodB(int i) {
        return i % 19;
    }
}
