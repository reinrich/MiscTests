package test.inlining;

// TestCase: will testMethodB() be inlined with the compileonly command?
// ========
// java -XX:+UnlockDiagnosticVMOptions -XX:-UseOnStackReplacement '-XX:CompileCommand=compileonly,*::testMethodA' -Xbatch -XX:CICompilerCount=1 -showversion -cp /net/usr.work/d/jtests/QuickJavaTests/bin -XX:+PrintCompilation -XX:+PrintInlining -XX:-TieredCompilation -XX:+TraceCompilerOracle test.inlining.InliningSimpleVirt
//
// call_does_dispatch==false because the virtual call gets optimized based on CHA.
//-> pass_initial_checks() prevents inlining of testMethodB


public class InliningSimpleVirt {

    public static void main(String[] args) {
        long checksum = 0;
        InliningSimpleVirt is = new InliningSimpleVirt();
        for(int i=0; i<100000; i++) {
            checksum += is.testMethodA(i);
        }
        System.out.println("checksum: " + checksum);
    }
    
    public long testMethodA(int i) {
        return testMethodB(i % 17);        
    }
    
    public long testMethodB(int i) {
        return i % 19;
    }
}
