package testlib.gc;

import testlib.Tracer;

public class ReferenceProcessorLoadProducerOptions {

    public static final int K = 1<<10;
    public static final int M = 1<<20;

    public RefTestType testType;
    public int trc_level;
    public int soft_refs_count;
    public int soft_refs_turnover;
    public int soft_refs_clearing_batch_size;

    public enum RefTestType {
        REF_NONE,
        REF_FEW_SOFTREFERENCES_STRONG_REACHABLE,
        REF_LOW_SOFTREFERENCES_TURNOVER // Creates a large amout of soft references with strongly
                                        // reachable referents.  Drops a few strong refs and creates
                                        // new soft refs when the corresponding soft refs are
                                        // cleares. Run with low SoftRefLRUPolicyMSPerMB,
    }

    public ReferenceProcessorLoadProducerOptions(RefTestType tt) {
        testType = tt;
        switch(tt) {
        case REF_FEW_SOFTREFERENCES_STRONG_REACHABLE:
            initForFewSoftRefsTestType(this);
            break;
        case REF_LOW_SOFTREFERENCES_TURNOVER:
            initForLowSoftreferencesTurnover(this);
            break;
        default:
            throw new Error("unknown test type " + tt);
        }
    }

    // copy and adapt as needed
    public static void initForFewSoftRefsTestType(ReferenceProcessorLoadProducerOptions rpOpts) {
        rpOpts.trc_level = 3;
        rpOpts.soft_refs_count = 10000;
    }

    // copy and adapt as needed
    public static void initForLowSoftreferencesTurnover(ReferenceProcessorLoadProducerOptions rpOpts) {
        rpOpts.trc_level = 3;
        rpOpts.soft_refs_count = 10000;
        rpOpts.soft_refs_turnover = 100;
        rpOpts.soft_refs_clearing_batch_size = 100;
    }

    public void printOn(Tracer tracer) {
        tracer.trcInstanceFields(this);
    }
}
