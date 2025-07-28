package test.gc;

class MortalObject extends ObjectBase {
    private static final byte MAGIC = 23;

    MortalObject(byte[] load) {
        super(load, MAGIC);
    }

    void check() {
        check(MAGIC);
    }
}
