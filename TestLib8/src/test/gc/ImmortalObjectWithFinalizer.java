package test.gc;

class ImmortalObjectWithFinalizer extends ImmortalObject {

    ImmortalObjectWithFinalizer(byte[] load) {
        super(load);
    }

    @Override
    protected void finalize() throws Throwable {
        check();
    }
}
