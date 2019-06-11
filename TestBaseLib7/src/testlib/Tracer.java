package testlib;

public interface Tracer {

    public void msg();

    public void msg(Object msg);

    public void msg(int level, Object msg);

    public boolean trcActive(int level);

    public void msgIncInd();

    public void msgDecInd();

    public void trcInstanceFields(Object obj);

}
