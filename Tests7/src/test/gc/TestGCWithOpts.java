package test.gc;

import test.gc.TestGCOptions.TestType;
import testlib.TestBase;

//
// Test Description
//
// Test continuously allocates the following type of java objects:
//
//     Immortal objects: Allocated in the build-up phase. Each allocated object remains reachable during
//     the whole test.
//
//     Mortal objects: after reaching MORTAL_OBJ_HEAP_OCCUPANCY older mortal objects are replaced by newly
//     created ones, so that older ones can die.
//
//     Short lived objects: are allocated without keeping the reference.
//

public class TestGCWithOpts extends TestBase {

    public static void main(String[] args) {
//        TestGCOptions exOpts = new TestGCOptions();
        TestGCOptions exOpts = new TestGCOptions(TestType.CMS_ON_LU0486);
        new GCLoadProducer(exOpts).run();
    }
}
