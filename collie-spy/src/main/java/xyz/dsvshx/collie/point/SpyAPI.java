package xyz.dsvshx.collie.point;

/**
 * @author dongzhonghua
 * Created on 2021-11-09
 */
public class SpyAPI {
    public static final AbstractSpy NOPSPY = new NopSpy();
    private static volatile AbstractSpy spyInstance = NOPSPY;

    public static volatile boolean INITED;

    public static AbstractSpy getSpy() {
        return spyInstance;
    }

    public static void setSpy(AbstractSpy spy) {
        spyInstance = spy;
    }

    public static void setNopSpy() {
        setSpy(NOPSPY);
    }

    public static boolean isNopSpy() {
        return NOPSPY == spyInstance;
    }

    public static void init() {
        INITED = true;
    }

    public static boolean isInited() {
        return INITED;
    }

    public static void destroy() {
        setNopSpy();
        INITED = false;
    }

    public static void atEnter(Class<?> clazz, String methodInfo, Object target, Object[] args) {
        spyInstance.atEnter(clazz, methodInfo, target, args);
    }

    public static void atExit(Class<?> clazz, String methodInfo, Object target, Object[] args,
            Object returnObject) {
        spyInstance.atExit(clazz, methodInfo, target, args, returnObject);
    }

    public static void atExceptionExit(Class<?> clazz, String methodInfo, Object target,
            Object[] args, Throwable throwable) {
        spyInstance.atExceptionExit(clazz, methodInfo, target, args, throwable);
    }

    public static void atFrameworkEnter(String traceId, String spanId, String parentSpanId) {
        spyInstance.atFrameworkEnter(traceId, spanId, parentSpanId);
    }

    public static void atFrameworkExit(String info) {
        spyInstance.atFrameworkExit(info);
    }


    public static abstract class AbstractSpy {
        public abstract void atEnter(Class<?> clazz, String methodInfo, Object target,
                Object[] args);

        public abstract void atExit(Class<?> clazz, String methodInfo, Object target, Object[] args,
                Object returnObject);

        public abstract void atExceptionExit(Class<?> clazz, String methodInfo, Object target,
                Object[] args, Throwable throwable);

        public abstract void atFrameworkEnter(String traceId, String spanId, String parentSpanId);

        public abstract void atFrameworkExit(String info);
    }

    static class NopSpy extends AbstractSpy {

        @Override
        public void atEnter(Class<?> clazz, String methodInfo, Object target, Object[] args) {
        }

        @Override
        public void atExit(Class<?> clazz, String methodInfo, Object target, Object[] args,
                Object returnObject) {
        }

        @Override
        public void atExceptionExit(Class<?> clazz, String methodInfo, Object target, Object[] args,
                Throwable throwable) {
        }

        @Override
        public void atFrameworkEnter(String traceId, String spanId, String parentSpanId) {

        }

        @Override
        public void atFrameworkExit(String info) {

        }
    }
}

