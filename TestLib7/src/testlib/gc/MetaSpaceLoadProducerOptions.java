package testlib.gc;

import testlib.Tracer;

public class MetaSpaceLoadProducerOptions {
    
    public static final int K = 1<<10;
    public static final int M = 1<<20;
    
    public enum MSTestType {
        MS_NONE,
        CMS_ON_LU0486,
        G1_ON_LU0486,
        G1_ON_LU0486_MORE_FULL_GCS,
        UNKNOWN_TEST_TYPE
    }

    public int trc_level;

    public int  immortal_loader_count;
    public int  classes_per_immortal_loader;

    public int  mortal_loader_count;
    public int  classes_per_mortal_loader;

    public int  short_lived_loader_count;
    public int  classes_per_short_lived_loader;

    public int  methods_per_class;

    public int alloc_classes_per_cycle_immortal;
    public int alloc_classes_per_cycle_mortal;
    public int alloc_classes_per_cycle_short_lived;

    // class allocation rate in the unit classes/ms
    public float class_allocation_rate_per_ms;

    public MetaSpaceLoadProducerOptions(MSTestType tt) {
        switch(tt) {
        case CMS_ON_LU0486:
            initForCmsOnLu0486(this);
            break;
        case G1_ON_LU0486:
            initForG1OnLu0486(this);
            break;
        case G1_ON_LU0486_MORE_FULL_GCS:
            initForG1OnLu0486MoreFullGCs(this);
            break;
        default:
            throw new Error("unknown test type " + tt);
        }
    }
    
    public static MSTestType getTestTypeFor(String testTypeStr) {
        MSTestType res = MSTestType.UNKNOWN_TEST_TYPE;
        if (testTypeStr.equals("CMS_ON_LU0486")) {
            res = MSTestType.CMS_ON_LU0486;
        } else if (testTypeStr.equals("G1_ON_LU0486")) {
            res = MSTestType.G1_ON_LU0486;
        } else if (testTypeStr.equals("G1_ON_LU0486_MORE_FULL_GCS")) {
            res = MSTestType.G1_ON_LU0486_MORE_FULL_GCS;
        } else {
            throw new Error("unknown test type " + testTypeStr);
        }
        return res;
    }

    // copy and adapt as needed
    public static void initForCmsOnLu0486(MetaSpaceLoadProducerOptions gcOpts) {
        gcOpts.trc_level = 3;

        gcOpts.immortal_loader_count    = 100;
        gcOpts.mortal_loader_count      = 100;
        gcOpts.short_lived_loader_count = 100;

        gcOpts.classes_per_immortal_loader    = 100;
        gcOpts.classes_per_mortal_loader      = 100;
        gcOpts.classes_per_short_lived_loader = 100;

        gcOpts.methods_per_class = 20;

        gcOpts.alloc_classes_per_cycle_immortal = 300;
        gcOpts.alloc_classes_per_cycle_mortal   = 300;
        gcOpts.alloc_classes_per_cycle_short_lived = 1000-(gcOpts.alloc_classes_per_cycle_immortal+gcOpts.alloc_classes_per_cycle_mortal);
        
        gcOpts.class_allocation_rate_per_ms = 100;
    }

    // copy and adapt as needed
    public static void initForG1OnLu0486(MetaSpaceLoadProducerOptions gcOpts) {
        gcOpts.trc_level = 3;
    
        gcOpts.immortal_loader_count    = 100;
        gcOpts.mortal_loader_count      = 100;
        gcOpts.short_lived_loader_count = 100;
    
        gcOpts.classes_per_immortal_loader    = 100;
        gcOpts.classes_per_mortal_loader      = 100;
        gcOpts.classes_per_short_lived_loader = 100;
    
        gcOpts.methods_per_class = 20;
    
        gcOpts.alloc_classes_per_cycle_immortal = 300;
        gcOpts.alloc_classes_per_cycle_mortal   = 300;
        gcOpts.alloc_classes_per_cycle_short_lived = 1000-(gcOpts.alloc_classes_per_cycle_immortal+gcOpts.alloc_classes_per_cycle_mortal);
        
        gcOpts.class_allocation_rate_per_ms = 50;
    }

    // copy and adapt as needed
    public static void initForG1OnLu0486MoreFullGCs(MetaSpaceLoadProducerOptions gcOpts) {
        gcOpts.trc_level = 3;
    
        gcOpts.immortal_loader_count    = 100;
        gcOpts.mortal_loader_count      = 100;
        gcOpts.short_lived_loader_count = 100;
    
        gcOpts.classes_per_immortal_loader    = 100;
        gcOpts.classes_per_mortal_loader      = 100;
        gcOpts.classes_per_short_lived_loader = 100;
    
        gcOpts.methods_per_class = 20;
    
        gcOpts.alloc_classes_per_cycle_immortal = 300;
        gcOpts.alloc_classes_per_cycle_mortal   = 300;
        gcOpts.alloc_classes_per_cycle_short_lived = 1000-(gcOpts.alloc_classes_per_cycle_immortal+gcOpts.alloc_classes_per_cycle_mortal);
        
        gcOpts.class_allocation_rate_per_ms = 10;
    }

    public void printOn(Tracer tracer) {
        tracer.trcInstanceFields(this);
    }
}
