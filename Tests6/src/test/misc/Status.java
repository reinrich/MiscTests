package test.misc;

public class Status {

    public String msg;
    public boolean passed;

    public Status(boolean passed, String msg) {
        this.passed = passed;
        this.msg = msg;
    }

    public static Status failed(String msg) {
        return new Status(false, msg);
    }
    public static Status passed(String msg) {
        return new Status(true, msg);
    }
    
    public String toString() {
        return (passed ? "STATUS: passed : " : "STATUS: failed : ") + msg;
    }

}
