package xyz.dsvshx.peony.core.adaptor;

/**
 * 针对不同的框架进行适配，在框架的入口和出口的地方插入FrameworkPoint, 之后具体的逻辑会在FrameworkAspect处实现
 *
 * @author dongzhonghua
 * Created on 2021-05-24
 */
public interface FrameworkAdaptor {
    /**
     * 修改类
     *
     * @param loader 类加载器
     * @param className 类名
     * @param classBytes 类的字节码
     */
    byte[] modifyClass(ClassLoader loader, String className, byte[] classBytes);
}
