package test.gc;

class MortalObjectWithFinalizer extends MortalObject {

    MortalObjectWithFinalizer(byte[] load) {
        super(load);
    }

    @Override
    protected void finalize() throws Throwable {
        check();
    }
}
