package testlib;

public interface Tracer {

    public void log();

    public void log(Object msg);

    public void log(int level, Object msg);

    public boolean trcActive(int level);

    public void logIncInd();

    public void logDecInd();

    public void trcInstanceFields(Object obj);

}
