package test.interp.constantresolution.getfield3lessfields;

public class TestClassWithAlotFields extends AbstrTestMethodHolder {

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
    @Override
    public long testMethodWithManyConstantPoolRefs() {
        return field0
        + field0
        + field1
        + field2
        + field3
        + field4
        + field5
        + field6
        + field7
        + field8
        + field9
 ;   }

}
