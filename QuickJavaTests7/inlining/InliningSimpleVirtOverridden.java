package test.inlining;

// TestCase: will testMethodB() be inlined with the compileonly command?
// ========
// java -XX:+UnlockDiagnosticVMOptions -XX:-UseOnStackReplacement '-XX:CompileCommand=compileonly,*::testMethodA' -Xbatch -XX:CICompilerCount=1 -showversion -cp /net/usr.work/d/jtests/QuickJavaTests/bin -XX:+PrintCompilation -XX:+PrintInlining -XX:-TieredCompilation -XX:+TraceCompilerOracle test.inlining.InliningSimpleVirtOverridden
//
// JIT tries to inline OverwritesTestMethodB.testMethodB (with guard), but
// pass_initial_checks() rejects it. Note: call_does_dispatch==false because of the guard.

public class InliningSimpleVirtOverridden {

    public static void main(String[] args) {
        long checksum = 0;
        InliningSimpleVirtOverridden is = new OverwritesTestMethodB();
        for(int i=0; i<100000; i++) {
            checksum += is.testMethodA(i);
        }
        System.out.println("checksum: " + checksum);
    }
    
    public long testMethodA(int i) {
        return testMethodB(i % 17); // calls overwritten method       
    }
    
    public long testMethodB(int i) {
        return i % 19;
    }
}

class OverwritesTestMethodB extends InliningSimpleVirtOverridden {
    @Override
    public long testMethodB(int i) {
        return i % 23;
    }
}
