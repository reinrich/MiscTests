package test.gc;

import testlib.Tracer;

public class TestGCOptions {

    public static final int K = 1<<10;
    public static final int M = 1<<20;

    public enum TestType {
        NONE,
        CMS_ON_CLX209,
        CMS_ON_BDW214,
        G1_ON_BDW214,
        G1_ON_LU0486,
        G1_ON_LU0486_MORE_FULL_GCS,
        PARGC_ON_LU0486,
        PARGC_ON_LU0486_GERRIT,  // low old usage -> shrink with adaptive size policy
        UNKNOWN_TEST_TYPE
    }

    public enum HumTestType {
        HUM_NONE,
        HUM_LOW,
        HUM_MEDIUM,
        HUM_HIGH
    }

    public int trc_level;

    public int   obj_header_size_bytes;
    public float immortal_obj_heap_occupancy;
    public int   immortal_obj_size_bytes;
    public long  immortal_obj_heap_occupancy_bytes;
    public long  immortal_obj_count;

    public float mortal_obj_heap_occupancy;
    public int   mortal_obj_size_bytes;
    public long  mortal_obj_heap_occupancy_bytes;
    public long  mortal_obj_count;

    public float hum_obj_heap_occupancy;
    public int   hum_obj_size_bytes;
    public long  hum_obj_heap_occupancy_bytes;
    public long  hum_obj_count;

    public int alloc_percentage_immortal;
    public int alloc_percentage_mortal;
    public int alloc_percentage_short_lived;

    public int alloc_interval_humongous; // ordinary allocations until new humongous allocation

    // wait iterations after iterating 100 objects
    public long busy_wait_iterations = 0;

    public TestGCOptions() {
        initOptsWithDflts(this);
    }

    public TestGCOptions(TestType tt, HumTestType humtt) {
        initOptsWithDflts(this);
        long g1_region_size = JavaHeap.MAX_JAVA_HEAP_BYTES / 2048;
        hum_obj_size_bytes = (int) g1_region_size;
        switch(humtt) {
        case HUM_NONE:
            hum_obj_heap_occupancy = 0f;
            break;
        case HUM_LOW:
            hum_obj_heap_occupancy = 0.02f;
            alloc_interval_humongous = 1<<20;
            break;
        case HUM_MEDIUM:
            hum_obj_heap_occupancy = 0.06f;
            alloc_interval_humongous = 1<<19;
            break;
        case HUM_HIGH:
            hum_obj_heap_occupancy = 0.10f;
            alloc_interval_humongous = 1<<18;
            break;
        default:
            throw new Error("Unknown HumTestType:" + humtt);
        }
        hum_obj_heap_occupancy_bytes = (long) (hum_obj_heap_occupancy * JavaHeap.MAX_JAVA_HEAP_BYTES);
        hum_obj_count = hum_obj_heap_occupancy_bytes / hum_obj_size_bytes;

        switch(tt) {
        case CMS_ON_CLX209:
            initForCmsOn_clx209(this);
            break;
        case CMS_ON_BDW214:
            initForCmsOn_bdw214(this);
            break;
        case G1_ON_BDW214:
            initForG1OnBdw214(this);
            break;
        case G1_ON_LU0486:
            initForG1OnLu0486(this);
            break;
        case PARGC_ON_LU0486:
            initForParGCOnLu0486(this);
            break;
        case PARGC_ON_LU0486_GERRIT:
            initForParGCOnLu0486Gerrit(this);
            break;
        case G1_ON_LU0486_MORE_FULL_GCS:
            initForG1OnLu0486MoreFullGCs(this);
            break;
        default:
            throw new Error("unknown test type " + tt);
        }
    }

    public static void init_immortal_derived_settings(TestGCOptions gcOpts) {
        gcOpts.immortal_obj_heap_occupancy_bytes = (long) (gcOpts.immortal_obj_heap_occupancy * JavaHeap.MAX_JAVA_HEAP_BYTES);
        gcOpts.immortal_obj_count = gcOpts.immortal_obj_heap_occupancy_bytes / gcOpts.immortal_obj_size_bytes;
    }

    public static void init_mortal_derived_settings(TestGCOptions gcOpts) {
        gcOpts.mortal_obj_heap_occupancy_bytes = (long) (gcOpts.mortal_obj_heap_occupancy * JavaHeap.MAX_JAVA_HEAP_BYTES);
        gcOpts.mortal_obj_count = gcOpts.mortal_obj_heap_occupancy_bytes / gcOpts.mortal_obj_size_bytes;
    }

    public static void init_derived_settings_final(TestGCOptions gcOpts) {
        gcOpts.alloc_percentage_short_lived = 100-(gcOpts.alloc_percentage_immortal+gcOpts.alloc_percentage_mortal);
        gcOpts.busy_wait_iterations = getProperty("gcOpts.busy_wait_iterations", 1000);
    }

    // copy and adapt as needed
    public static void initOptsWithDflts(TestGCOptions gcOpts) {
        gcOpts.trc_level = 3;
        gcOpts.obj_header_size_bytes = 16; // roughly true for 64 bit
        gcOpts.immortal_obj_heap_occupancy = getProperty("gcOpts.immortal_obj_heap_occupancy", 0.15f);
        gcOpts.immortal_obj_size_bytes = 1*K;
        init_immortal_derived_settings(gcOpts);
        gcOpts.mortal_obj_heap_occupancy = 0.15f;
        gcOpts.mortal_obj_size_bytes = 256;
        init_mortal_derived_settings(gcOpts);
        gcOpts.alloc_percentage_immortal = (int)getProperty("gcOpts.alloc_percentage_immortal", 20);
        gcOpts.alloc_percentage_mortal   = (int)getProperty("gcOpts.alloc_percentage_mortal",   10);;
        init_derived_settings_final(gcOpts);
    }

    public static void initForCmsOn_clx209(TestGCOptions gcOpts) {
        // use dflt settings
    }

    public static void initForCmsOn_bdw214(TestGCOptions gcOpts) {
        // use dflt settings
    }

    public static void initForParGCOnLu0486(TestGCOptions gcOpts) {
        // use dflt settings
    }

    public static void initForParGCOnLu0486Gerrit(TestGCOptions gcOpts) {
        gcOpts.immortal_obj_heap_occupancy = 0.10f;
        init_immortal_derived_settings(gcOpts);
        init_derived_settings_final(gcOpts);
    }

    public static void initForG1OnLu0486MoreFullGCs(TestGCOptions gcOpts) {
        gcOpts.immortal_obj_heap_occupancy = 0.4f;
        init_immortal_derived_settings(gcOpts);
        gcOpts.mortal_obj_heap_occupancy = 0.3f;
        init_mortal_derived_settings(gcOpts);
        gcOpts.alloc_percentage_immortal = (int)getProperty("gcOpts.alloc_percentage_immortal", 20);
        gcOpts.alloc_percentage_mortal   = (int)getProperty("gcOpts.alloc_percentage_mortal",   50);;
        init_derived_settings_final(gcOpts);
        gcOpts.busy_wait_iterations = 1;
    }

    public static void initForG1OnBdw214(TestGCOptions gcOpts) {
        // use dflt settings
    }

    public static void initForG1OnLu0486(TestGCOptions gcOpts) {
        // use dflt settings
    }

    public void printOn(Tracer tracer) {
        tracer.log("JavaHeap.MAX_JAVA_HEAP_bytes: " + JavaHeap.MAX_JAVA_HEAP_BYTES);
        tracer.trcInstanceFields(this);
    }

    static long getProperty(String testProp, long dflt) {
        long res = dflt;
        try {
            res = Long.valueOf(System.getProperty(testProp));
        } catch(Throwable e) { // ignore and return dflt
        }
        return res;
    }

    static float getProperty(String testProp, float dflt) {
        float res = dflt;
        try {
            res = Float.valueOf(System.getProperty(testProp));
        } catch(Throwable e) { // ignore and return dflt
        }
        return res;
    }
}
