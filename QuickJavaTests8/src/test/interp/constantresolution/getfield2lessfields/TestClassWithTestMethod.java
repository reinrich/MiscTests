package test.interp.constantresolution.getfield2lessfields;

public class TestClassWithTestMethod extends AbstrTestMethodHolder {
    
    static {
        if ((Main.numIterations & 0xff) == 0) {
            System.out.println("TestClassWithTestMethod loaded");
            System.out.println("CL: "+TestClassWithTestMethod.class.getClassLoader());
        }
    }
    
    @Override
    public long testMethodWithManyConstantPoolRefs() {
        TestClassWithAlotFields recv = new TestClassWithAlotFields();
        
        return recv.field0
        + recv.field0
        + recv.field1
        + recv.field2
        + recv.field3
        + recv.field4
        + recv.field5
        + recv.field6
        + recv.field7
        + recv.field8
        + recv.field9
 ;   }

}
