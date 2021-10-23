package xyz.dsvshx.collie.core.aspect;

/**
 * 类方法切面，在这里实现方法增强的逻辑，主要是记录时间和方法，类信息
 *
 * @author dongzhonghua
 * Created on 2021-04-20
 */
public interface MethodAspect {
    /**
     * 方法执行之前调用，可以实现多个，比如打印日志或者其他的功能。
     */
    void before(String className, String methodName, String descriptor, Object[] params);

    /**
     * 方法抛出异常
     */
    void error(String className, String methodName, String descriptor, Throwable throwable);

    void after(String className, String methodName, String descriptor, Object result);
}
