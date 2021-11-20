package xyz.dsvshx.collie.core.adaptor;

import java.util.HashMap;
import java.util.Map;

import xyz.dsvshx.collie.core.adaptor.summer.SummerFrameworkAdaptorImpl;

/**
 * 针对不同的框架进行适配，在框架的入口和出口的地方插入FrameworkPoint, 之后具体的逻辑会在FrameworkAspect处实现
 *
 * @author dongzhonghua
 * Created on 2021-05-24
 */
public abstract class ClassAdaptor {
    public static Map<String, ClassAdaptor> frameworkAdaptors;
    public static final String SUMMER_ADAPTOR_CLASS = "xyz/dsvshx/ioc/mvc/RequestHandler";
    public static final String COMMON_CLASS = "common";

    public static void init() {
        frameworkAdaptors = new HashMap<>();
        frameworkAdaptors.put(SUMMER_ADAPTOR_CLASS, new SummerFrameworkAdaptorImpl());
        frameworkAdaptors.put(COMMON_CLASS, new MethodAdaptorImpl());
    }


    /**
     * 修改类
     *
     * @param className 类名
     * @param classBytes 类的字节码
     * @param spyJarPath spy
     */
    public abstract byte[] modifyClass(String className, byte[] classBytes, String spyJarPath);
}
