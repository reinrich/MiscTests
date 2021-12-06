package test.interp.constantresolution.getfield2lessfields;

public class TestClassWithAlotFields {

    static {
        if ((Main.numIterations & 0xff) == 0) {
            System.out.println("TestClassWithAlotFields loaded");
            System.out.println("CL: "+TestClassWithTestMethod.class.getClassLoader());
        }
    }

    public long field0;
    public long field1;
    public long field2;
    public long field3;
    public long field4;
    public long field5;
    public long field6;
    public long field7;
    public long field8;
    public long field9;

}
