package testlib;

public interface Tracing {

    default Tracing getBackend() {
        return TracingBackend.INSTANCE;
    }

    public default void log() {
        getBackend().log();
    }

    public default void log0(Object msg) {
        getBackend().log0(msg);
    }

    public default void log(Object msg) {
        getBackend().log(msg);
    }

    public default void log(int level, Object msg) {
        getBackend().log(level, msg);
    }

    public default boolean trcActive(int level) {
        return getBackend().trcActive(level);
    }

    public default void logIncInd() {
        getBackend().logIncInd();
    }

    public default void logDecInd() {
        getBackend().logDecInd();
    }

    public default void trcInstanceFields(Object obj) {
        getBackend().trcInstanceFields(obj);
    }

    public default String humanReadable(int val) {
        if (Math.abs(val) < 1000) {
            return String.valueOf(val);
        }
        return String.valueOf(val / 1000) + "k";
    }
}
