package test.gc;

public class ObjectBase {

    private byte[] load;

    ObjectBase(byte[] load, byte magic) {
        this.load = load;
        load[0] = magic;
    }

    void check(byte magic) {
        if (load[0] != magic) {
            throw new Error("MAGIC " + magic + " not found");
        }
    }
}
