package test.gc;

class ImmortalObject extends ObjectBase {

    private static final byte MAGIC = 42;

    ImmortalObject(byte[] load) {
        super(load, MAGIC);
    }

    void check() {
        check(MAGIC);
    }
}
