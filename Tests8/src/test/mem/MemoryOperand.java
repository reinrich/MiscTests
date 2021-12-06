package test.mem;

/*

./sapjvm_8/bin/java
-XX:+ClientCompiler
-XX:+PrintLIR
-XX:+PrintIR
-XX:+TraceCompilerOracle
-XX:+PrintCompilation
-XX:CICompilerCount=1
-Xbatch
'-XX:CompileCommand=compileonlywithinlining,*::testMethod*'
'-XX:CompileCommand=dontinline,*::dontinline*'
-XX:-TieredCompilation
-cp
/u/w/d/jtests/QuickJavaTests/bin
test.mem.MemoryOperand

 */

// ./sapjvm_8/bin/java -XX:+ClientCompiler -XX:+PrintLIR -XX:+PrintIR -XX:+TraceCompilerOracle -XX:+PrintCompilation -XX:CICompilerCount=1 -Xbatch -cp /u/w/d/jtests/QuickJavaTests/bin '-XX:CompileCommand=compileonly,*::testMethod*' -XX:-TieredCompilation test.mem.MemoryOperand

public class MemoryOperand {

//    public static volatile int _i1, _i2, _i3, _i4, _i5, _i6, _i7, _i8, _i9, _i10;
//    public static volatile int _i11, _i12, _i13, _i14, _i15, _i16, _i17, _i18, _i19;
    
    public static class MemoryOperandHolder {
        int theMemoryOperand;
    }

    public static volatile int volatileIntField;
    
    public static void main(String[] args) {
        MemoryOperandHolder mem = new MemoryOperandHolder();
        int res=0;
        float fres=0f;
        double dres=0d;
        float f=2f;
        double d=2d;
        for (int i = 0; i < 50_000; i++) {
//            testMethod1(mem, 1);
//            testMethod2(i);
//            testMethod3(i, i, i, i, i, i, i, i, i, i, i, i, i, i, i, i, i, i, i);
//            fres=testMethod4(f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f);
//            dres=testMethod5(d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d);
          testMethod6();
        }
        System.out.println("### res:"+res+"   fres:"+fres+"   dres:"+dres);
    }

    // check frame size; where do spills begin?
    public static int testMethod6() {
//        int fieldVal = volatileIntField;
        int call_val = dontinline_getIntValue();
        int fieldVal = 1;
        return fieldVal + call_val;
    }

    public static double testMethod5(double f1, double f2, double f3, double f4, double f5, double f6, double f7, double f8, double f9, double f10,
            double f11, double f12, double f13, double f14, double f15, double f16, double f17, double f18, double f19) {
        dontinline_getIntValue();
        return f1+f2+f3+f4+f5+f6+f7+f8+f9+f10+f11+f12+f13+f14+f15+f16+f17+f18+f19;
    }

    public static float testMethod4(float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10,
            float f11, float f12, float f13, float f14, float f15, float f16, float f17, float f18, float f19) {
        dontinline_getIntValue();
        return f1+f2+f3+f4+f5+f6+f7+f8+f9+f10+f11+f12+f13+f14+f15+f16+f17+f18+f19;
    }

    public static int testMethod3(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10,
            int i11, int i12, int i13, int i14, int i15, int i16, int i17, int i18, int i19) {
        int call_val = dontinline_getIntValue();
        return i1+i2+i3+i4+i5+i6+i7+i8+i9+i10+i11+i12+i13+i14+i15+i16+i17+i18+i19 + call_val;
    }

    public static int testMethod2(int ii) {
        int call_val = dontinline_getIntValue();
        return ii + call_val;
    }

    public static int testMethod1(MemoryOperandHolder mem, int lval) {
        return mem.theMemoryOperand + lval;
    }

    public static int dontinline_getIntValue() {
        return 42;
    }

}
