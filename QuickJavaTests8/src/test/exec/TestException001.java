package test.exec;

public class TestException001 {

    public static void main(String[] args) {
        try {
            doThrowException();
        } catch (MyTestException ee) {
            // ignored
        }
    }

    public static void doThrowException() throws MyTestException {
        throw new MyTestException();
    }

}
