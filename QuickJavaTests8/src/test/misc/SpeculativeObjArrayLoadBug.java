package test.misc;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// java -cp classes -Xbatch -XX:CompileCommand=dontinline,*::dontinline_* -Xmx128m -XX:+UseSerialGC test.misc.SpeculativeObjArrayLoadBug

public class SpeculativeObjArrayLoadBug {

    private static Map<Class<?>, Object> DEFAULT_TYPE_VALUES;


    public static void main(String[] args_ignored) {
        try {
            Class<?>[] ctorArgTypes  = {Integer.class};
            Constructor<?> ctor = SpeculativeObjArrayLoadBug.class.getConstructor(ctorArgTypes);
            Constructor<?> ctorDefault = SpeculativeObjArrayLoadBug.class.getConstructor();

            Object[] args     = {Integer.valueOf(1)};
            Object[] argsNull = {null};
            Object[] argsNone = {};
            Constructor<?> ctorEff;
            Object[] argsEff;

            // Warm-up
            for (long i = 1L<<20; i > 0; i--) {
                if ((i % 1000)  > 20) {
                    argsEff = argsNull;
                    ctorEff = ctor;
                } else {
                    argsEff = args;
                    ctorEff = ctor;
                }
                dontinline_testMethod(ctorEff, argsEff);
            }
            // Call testmethod with default ctor -> crash!!!
            for (long i = 1L<<60; i > 0; i--) {
                argsEff = argsNone;
                ctorEff = ctorDefault;
                dontinline_testMethod(ctorEff, argsEff);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public SpeculativeObjArrayLoadBug(Integer i1) {
    }

    public SpeculativeObjArrayLoadBug() {
    }

    public static <T> T dontinline_testMethod(Constructor<T> ctor, Object... args) throws Exception {
        Class<?>[] parameterTypes = ctor.getParameterTypes();
        // Load L below is executed at this point.
        // The result is put into the OopMap for GC of the allocation in the next line.
        // If ctor.parameterTypes.length is 0, e.g. for the default constructor then
        // the loaded value is no heap reference and GC crashes.
        Object[] argsWithDefaultValues = new Object[args.length];
        for (int i = 0 ; i < args.length; i++) {
            if (args[i] == null) {
                Class<?> parameterType = parameterTypes[i];  // Load L
                argsWithDefaultValues[i] = (parameterType.isPrimitive() ? DEFAULT_TYPE_VALUES.get(parameterType) : null);
            }
            else {
                argsWithDefaultValues[i] = args[i];
            }
        }
        return ctor.newInstance(argsWithDefaultValues);
    }
 
    static {
        Map<Class<?>, Object> values = new HashMap<>();
        values.put(boolean.class, false);
        values.put(byte.class, (byte) 0);
        values.put(short.class, (short) 0);
        values.put(int.class, 0);
        values.put(long.class, (long) 0);
        DEFAULT_TYPE_VALUES = Collections.unmodifiableMap(values);
    }
}
