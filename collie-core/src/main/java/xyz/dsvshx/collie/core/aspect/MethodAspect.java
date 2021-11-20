package xyz.dsvshx.collie.core.aspect;

/**
 * 类方法切面，在这里实现方法增强的逻辑，主要是记录时间和方法，类信息
 *
 * @author dongzhonghua
 * Created on 2021-04-20
 */
public interface MethodAspect {
    /**
     * 前置通知
     *
     * @param clazz 类名
     * @param methodName 方法名
     * @param methodDesc 方法描述
     * @param target 目标类实例
     * 若目标为静态方法,则为null
     * @param args 参数列表
     * @throws Throwable 通知过程出错
     */
    void before(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args) throws Throwable;

    /**
     * 返回通知
     *
     * @param clazz 类名
     * @param methodName 方法名
     * @param methodDesc 方法描述
     * @param target 目标类实例
     * 若目标为静态方法,则为null
     * @param args 参数列表
     * @param returnObject 返回结果
     * 若为无返回值方法(void),则为null
     * @throws Throwable 通知过程出错
     */
    void after(
            Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject)
            throws Throwable;

    /**
     * 异常通知
     *
     * @param clazz 类名
     * @param methodName 方法名
     * @param methodDesc 方法描述
     * @param target 目标类实例
     * 若目标为静态方法,则为null
     * @param args 参数列表
     * @param throwable 目标异常
     * @throws Throwable 通知过程出错
     */
    void error(
            Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable)
            throws Throwable;

}
