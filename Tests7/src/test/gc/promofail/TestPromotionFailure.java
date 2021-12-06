package test.gc.promofail;

import java.lang.reflect.Field;

import test.JavaHeap;
import test.tools.NestedArrayList;
import test.TestBase;

//
// Test Description
//
// Synthetic test to trigger promotion failures
//
// Test allocates the following type of java objects:
//
//     Immortal objects: Allocated in the build-up phase. Each allocated object remains reachable during
//     the whole test.
//
//     Mortal objects: after reaching MORTAL_OBJ_HEAP_OCCUPANCY older mortal objects are replaced by newly
//     created ones, so that older ones can die.
//
//     Short lived objects: are allocated without keeping the reference.
//
// Allocations of these types are interleaved as given with ALLOC_PERCENTAGE_*
//
// Phases
//
//     Build-up: allocate objects of all types until the target occupancy of immortal and mortal
//     objects is reached. If MORTAL_OBJ_HEAP_OCCUPANCY is reached, then begin to replace old mortal
//     objects with new ones.
//
//     Continous allocation: allocate new mortal objects and replace old ones with the new
//     ones. Interleave allocation with allocation of short lived objects according to ALLOC_PERCENTAGE_*
//
//     Promotion failure: This is a sub phase of the previous phase. Young gen is configured to
//     occupy 50% of the heap. IMMORTAL_OBJ_HEAP_OCCUPANCY and MORTAL_OBJ_HEAP_OCCUPANCY are set to
//     values such that mortal and immortal objects occupy already most of the old generation.  When
//     PROMO_FAILURE_THRESHOLD_BYTES are allocated, we start the process which aims to cause a
//     promotion failure. To do so we begin to keep the short lived objects in a list while
//     allocating more. With this we will get young gcs with only live objects. Objects in eden
//     and in the from-survivor-space don't fit into the to-survivor-space and have to be
//     promoted. Eden + from-space is almost as big as the old gen, which is mostly used already
//     anyway. This makes a promotion failure very likely.
//
// To make the test deterministic, we call System.gc() before starting the process for a promotion
// failure and we specify SurvivorRatio to avoid resizing of the survivor spaces.
//
// To test preservation of mark words during promotion failure we call hashCode() on every mortal object.
//
// GC=-XX:+UseParallelGC
// HS=8000 ; export MN=$((10 * $HS / 20)); echo "HS=$HS MN=$MN"
// LOG=-Xlog:heap*=error,gc=trace,gc+phases=info,gc+promotion=debug
//
// $JAVA $GC -XX:SurvivorRatio=8 -XX:+ParallelPromotionFailureCleanup $LOG -Xmn${MN}m -Xms${HS}m -Xmx${HS}m -cp /s/sapjvm_work/d/jtests/QuickJavaTests7/bin test.gc.promofail.TestPromotionFailure
//

public class TestPromotionFailure extends TestBase {

    public static final int TRC_LEVLE = 3;
    
    // Constants and Configuration
    public static final int   OBJ_HEADER_SIZE_BYTES = 16; // roughly true for 64 bit
    public static final float IMMORTAL_OBJ_HEAP_OCCUPANCY = 0.15f;
    public static final int   IMMORTAL_OBJ_SIZE_BYTES = 1*K;
    public static final long  IMMORTAL_OBJ_HEAP_OCCUPANCY_BYTES = (long) (IMMORTAL_OBJ_HEAP_OCCUPANCY * JavaHeap.MAX_JAVA_HEAP_BYTES);
    public static final long  IMMORTAL_OBJ_COUNT = IMMORTAL_OBJ_HEAP_OCCUPANCY_BYTES / IMMORTAL_OBJ_SIZE_BYTES;

    public static final float MORTAL_OBJ_HEAP_OCCUPANCY = 0.15f;
    public static final int   MORTAL_OBJ_SIZE_BYTES = 256;
    public static final long  MORTAL_OBJ_HEAP_OCCUPANCY_BYTES = (long) (MORTAL_OBJ_HEAP_OCCUPANCY * JavaHeap.MAX_JAVA_HEAP_BYTES);
    public static final long  MORTAL_OBJ_COUNT = MORTAL_OBJ_HEAP_OCCUPANCY_BYTES / MORTAL_OBJ_SIZE_BYTES;

    public static final int ALLOC_PERCENTAGE_IMMORTAL = 20;
    public static final int ALLOC_PERCENTAGE_MORTAL   = 10;
    public static final int ALLOC_PERCENTAGE_SHORT_LIVED = 100-(ALLOC_PERCENTAGE_IMMORTAL+ALLOC_PERCENTAGE_MORTAL);
    
    // after allocating PROMO_FAILURE_THRESHOLD_BYTES we try to trigger a promotion failure
    public static final boolean PROMO_FAILURE_TRIGGER_SYSTEM_GC_BEFORE = true;
    public static final long    PROMO_FAILURE_THRESHOLD_BYTES = 8*JavaHeap.MAX_JAVA_HEAP_BYTES;
    public static final float   PROMO_FAILURE_LIVE_OCCUPANCY = 0.8f;
    public static final long    PROMO_FAILURE_BYTES_TO_KEEP_ALIVE = (long)((PROMO_FAILURE_LIVE_OCCUPANCY-(IMMORTAL_OBJ_HEAP_OCCUPANCY+MORTAL_OBJ_HEAP_OCCUPANCY))*JavaHeap.MAX_JAVA_HEAP_BYTES);
    
    private NestedArrayList<byte[]> immortalObjs;
    private NestedArrayList<byte[]> mortalObjs;

    private JavaHeap heap;

    private boolean shouldContinueToAllocate;

    public static void main(String[] args) {
        try {
            new TestPromotionFailure().run();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public TestPromotionFailure() throws IllegalArgumentException, IllegalAccessException {
        super(TRC_LEVLE);
        printConfiguration();
        heap = new JavaHeap(this);
    }

    private void printConfiguration() throws IllegalArgumentException, IllegalAccessException {
        msg("JavaHeap.MAX_JAVA_HEAP_BYTES: " + JavaHeap.MAX_JAVA_HEAP_BYTES);
        Field[] fields = getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field ff = fields[i];
            if (java.lang.reflect.Modifier.isStatic(ff.getModifiers())) {
                msg(ff.getName() + ": " + ff.get(null));
            }
        }
    }

    private void run() {
        buildUpObjectGraph();
        shouldContinueToAllocate = true;
        continouslyAllocateObjects();
    }

    private void buildUpObjectGraph() {
        immortalObjs = new NestedArrayList<>();
        mortalObjs = new NestedArrayList<>();
        long moIdx = 0;

        msg("###### Build-up of object graph");
        msgIncInd();
        msg(heap);
        while(immortalObjs.size() < IMMORTAL_OBJ_COUNT || mortalObjs.size() < MORTAL_OBJ_COUNT) {
            // allocate 100 objects according to the ALLOC_PERCENTAGES
            int immoToAlloc = immortalObjs.size() >= IMMORTAL_OBJ_COUNT ? 0 : ALLOC_PERCENTAGE_IMMORTAL;
            int moToAlloc = ALLOC_PERCENTAGE_MORTAL;
            int shortToAlloc = ALLOC_PERCENTAGE_SHORT_LIVED;

            while(moToAlloc+immoToAlloc+shortToAlloc > 0) {
                if (immoToAlloc > 0) {
                    immoToAlloc--;
                    immortalObjs.add(allocImmortalObject());
                }
                if (moToAlloc > 0) {
                    moToAlloc--;
                    mortalObjs.set(moIdx++, allocMortalObject());
                    if (moIdx == MORTAL_OBJ_COUNT) moIdx = 0;
                }
                if (shortToAlloc > 0) {
                    shortToAlloc--;
                    allocImmortalObject(); // allocated and instantly dropped
                }
            }
        }
        msg("allocated " + immortalObjs.size() + " immortalObjs (" + immortalObjs + ")");
        heap.gc();
        msg(heap);
        msgDecInd();
        msg();
    }

    private void continouslyAllocateObjects() {
        NestedArrayList<byte[]> objsToKeepForPromotionFailure = new NestedArrayList<>();
        long moIdx = 0;
        long bytesAllocated = 0;
        long bytesAllocatedForPromoFailure = 0;

        msg("###### Continously allocating objects");
        msgIncInd();
        msg(heap);
        boolean shouldTriggerPromoFailure = false;
        while(shouldContinueToAllocate) {
            // allocate 100-IMMORTAL_OBJ_COUNT objects according to the ALLOC_PERCENTAGES
            int moToAlloc = ALLOC_PERCENTAGE_MORTAL;
            int shortToAlloc = ALLOC_PERCENTAGE_SHORT_LIVED;
            
            if (!shouldTriggerPromoFailure) {
                if (bytesAllocated > PROMO_FAILURE_THRESHOLD_BYTES) {
                    msg("###### entering promotion failure mode");
                    if (PROMO_FAILURE_TRIGGER_SYSTEM_GC_BEFORE) {
                        heap.gc();
                    }
                    shouldTriggerPromoFailure = true;
                    bytesAllocatedForPromoFailure = 0;
                }
            } else {
                if (bytesAllocatedForPromoFailure > PROMO_FAILURE_BYTES_TO_KEEP_ALIVE) {
                    msg("###### leaving promotion failure mode");
                    objsToKeepForPromotionFailure = new NestedArrayList<>();
                    if (PROMO_FAILURE_TRIGGER_SYSTEM_GC_BEFORE) {
                        heap.gc();
                    }
                    shouldTriggerPromoFailure = false;
                    bytesAllocated = 0;
                }
            }

            while(moToAlloc+shortToAlloc > 0) {
                if (moToAlloc > 0) {
                    moToAlloc--;
                    mortalObjs.set(moIdx++, allocMortalObject());
                    if (moIdx == MORTAL_OBJ_COUNT) moIdx = 0;
                    bytesAllocated += MORTAL_OBJ_SIZE_BYTES;
                }
                if (shortToAlloc > 0) {
                    shortToAlloc--;
                    byte[] tmp = allocMortalObject(); // allocated and instantly dropped
                    if (shouldTriggerPromoFailure) {
                        objsToKeepForPromotionFailure.add(tmp); // unless a promotion failure should be triggered
                        bytesAllocatedForPromoFailure += MORTAL_OBJ_SIZE_BYTES;
                    }
                    bytesAllocated += MORTAL_OBJ_SIZE_BYTES;
                }
            }
        }
        msg(heap);
        msgDecInd();
        msg();
    }

    private byte[] allocImmortalObject() {
        return new byte[IMMORTAL_OBJ_SIZE_BYTES-OBJ_HEADER_SIZE_BYTES];
    }

    private byte[] allocMortalObject() {
        byte[] result = new byte[MORTAL_OBJ_SIZE_BYTES-OBJ_HEADER_SIZE_BYTES];
        result.hashCode();
        return result;
    }
}
