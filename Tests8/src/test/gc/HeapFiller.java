package test.gc;

import java.lang.reflect.Field;

import sun.misc.Unsafe;
import test.gc.microbench.TestGC;

/**
 * Builds a tree structure or a plain array of long values of the given size with the given ratio between
 * references and other data.
 */
public class HeapFiller {

    private static final int ID_IDX = 0;
    private static final int SIZE_IDX = 1;
    
    private static final int SIZEOF_OBJECT_HEADER = 16;
    private static final int SIZEOF_COOP = 4; // 4 bytes per compressed oop
    private static final int SIZEOF_LONG = 8; // 8 bytes per long
    private static final int TREE_ARITY = 8;

    private static int REFERENCES;
    private static long REFERENCES_BYTES;

    private static volatile long id;
    private static Unsafe unsafe;
    private static int SMALL_OBJS_PER_HEAPFILLER;
    private static float REFERENCES_VS_DATA_RATIO;
    private static float NON_REF_BYTES;
    private static int DATA_ELEMENTS;
    private static int NODE_SIZE;
    
    private long[] data; // should be large in comparison to its HeapObj wrapper
    private HeapFiller[] subTrees;

    private Object[] smallObjects;

    public static void config(TestBaseOld test, HeapFillerConfig hfConfig) {
        SMALL_OBJS_PER_HEAPFILLER = hfConfig.smallObjectsPerHeapFiller;
        REFERENCES_VS_DATA_RATIO  = hfConfig.referenceVsDataRatio;
        REFERENCES = SMALL_OBJS_PER_HEAPFILLER + TREE_ARITY+3; // subtrees + data array + small objects
        REFERENCES_BYTES = REFERENCES * SIZEOF_COOP;
        NON_REF_BYTES = REFERENCES_BYTES / REFERENCES_VS_DATA_RATIO;
        DATA_ELEMENTS = (int) Math.ceil((float)NON_REF_BYTES / (float)SIZEOF_LONG);
        NODE_SIZE = (int) Math.ceil(NON_REF_BYTES + REFERENCES_BYTES);

        test.message("### HeapFiller Configuration");
        test.incMessageIndentation();
        test.message("SMALL_OBJS_PER_HEAPFILLER = " + SMALL_OBJS_PER_HEAPFILLER);
        test.message("REFERENCES_VS_DATA_RATIO = " + REFERENCES_VS_DATA_RATIO);
        test.message("REFERENCES = " + REFERENCES);
        test.message("REFERENCES_BYTES = " + REFERENCES_BYTES);
        test.message("NON_REF_BYTES = " + NON_REF_BYTES);
        test.message("DATA_ELEMENTS = " + DATA_ELEMENTS);
        test.decMessageIndentation();
    }

    /**
     * Builds a tree structure or a plain array of long values of the given size with the given ratio between
     * references and other data.
     * 
     * @param byteSize
     *            Byte size of this filler including its subtrees
     * @param REFERENCES_VS_DATA_RATIO
     *            Ration between references and other data. A tree structure
     *            will be created if the value is greater than 0 such that the
     *            bytes of the references forming the tree divided by the bytes
     *            of the data arrays held by each tree node
     */
    public HeapFiller(int byteSize) {
        this(byteSize, true);
    }

    /**
     * Builds a tree structure or a plain array of long values of the given size with the given ratio between
     * references and other data.
     * 
     * @param byteSize
     *            Byte size of this filler including its subtrees
     * @param isRoot
     *            Root of this HeapFiller graph
     */
    public HeapFiller(int byteSize, boolean isRoot) {
        int dataElements = 0;
        if (REFERENCES_VS_DATA_RATIO != 0) {
            dataElements = DATA_ELEMENTS;
            int sizeSubTrees = byteSize - NODE_SIZE;
            int sizeOneSubTree = sizeSubTrees / TREE_ARITY;
            subTrees = new HeapFiller[TREE_ARITY];
            int i = 0;
            while (sizeSubTrees >= NODE_SIZE && i < TREE_ARITY) {
                subTrees[i++] = new HeapFiller(sizeOneSubTree, false);
                sizeSubTrees -= sizeOneSubTree;
            }
            dataElements += sizeSubTrees > 0 ? Math.ceil((float)sizeSubTrees / (float)SIZEOF_LONG) : 0;
        } else {
            dataElements = byteSize / SIZEOF_LONG;
        }
        if (isRoot && dataElements < 2) {
            throw new Error("HeapFiller size should be at least 2 elements (to store id and size)");
        }
        data = new long[dataElements];
        smallObjects = new Object[SMALL_OBJS_PER_HEAPFILLER];
        if (isRoot) {
            data[ID_IDX] = allocId();
            data[SIZE_IDX] = byteSize;
        }
    }

    private long allocId() {
        return id++;
    }

    public long getId() {
        return data[ID_IDX];
    }
    

    public long getSize() {
        return data[SIZE_IDX];
    }

    @Override
    public String toString() {
        try {
            return getId() + "@" + Long.toHexString(Addresser.addressOf(data));
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR in " + getClass().getName() + ".toString()";
        }
    }
    
    /**
     * Get the address of a given object.
     */
    public static class Addresser {
        public static long addressOf(Object o)
                throws Exception
        {
            Object[] array = new Object[] {o};

            long baseOffset = unsafe.arrayBaseOffset(Object[].class);
            int addressSize = unsafe.arrayIndexScale(Object[].class);
            long objectAddress;
            switch (addressSize)
            {
            case 4:
                objectAddress = unsafe.getInt(array, baseOffset);
                break;
            case 8:
                objectAddress = unsafe.getLong(array, baseOffset);
                break;
            default:
                throw new Error("unsupported address size: " + addressSize);
            }       

            return(objectAddress);
        }

        static
        {
            try
            {
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                unsafe = (Unsafe)field.get(null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public int depth() {
        if (subTrees[0] == null) {
            return 1;
        }
        return 1 + subTrees[0].depth();
    }

}
