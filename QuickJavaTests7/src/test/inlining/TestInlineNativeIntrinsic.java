package test.inlining;

public class TestInlineNativeIntrinsic {

    public static void main(String[] args) {
        Object myObj = new Object();
        for (int i = 0; i < 100000; i++) {
            dontinline_testMethod(myObj);
        }
    }

    public static int dontinline_testMethod(Object myObj) {
        return myObj.hashCode();
    }
}
