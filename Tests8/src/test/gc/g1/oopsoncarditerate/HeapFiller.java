package test.gc.g1.oopsoncarditerate;

public class HeapFiller {
    
    public Object instanceOfClassToBeUnloaded;
    public byte[] liveObject;
    public Object transienObjectInYoungGen;
    
    public HeapFiller() throws Exception {
        instanceOfClassToBeUnloaded = TestOopsOnCardIterateOverObjWithUnloadedClass.allocateInstanceOfClassToBeUnloaded();
        liveObject = new byte[TestOopsOnCardIterateOverObjWithUnloadedClass.CARD_SIZE_BYTES / 2];
    }
}
