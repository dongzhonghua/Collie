package xyz.dsvshx.peony.core.aspect;

import java.util.ArrayList;
import java.util.List;

import xyz.dsvshx.peony.point.Point;


/**
 * 方法执行之前
 *
 * @author dongzhonghua
 * Created on 2021-04-20
 */
public class MethodCallLisener {

    private static List<Aspect> aspects;

    public static void init() throws NoSuchMethodException {
        // 获取Aspect所有的实现类
        aspects = new ArrayList<>();
        // 这种方式可能得结合spi使用，直接用不行
        // ServiceLoader<Aspect> aspectImpls = ServiceLoader.load(Aspect.class);
        aspects.add(new LogAspectImpl());
        // 初始化Spy的方法
        Point.beforeMethod = MethodCallLisener.class.getMethod("before", String.class,
                String.class, String.class, Object[].class);
        Point.completeMethod = MethodCallLisener.class.getMethod("complete", String.class,
                String.class, String.class, Object.class);
    }

    public static void before(String className, String methodName, String descriptor, Object[] params) {
        try {
            if (SamplingRate.needSampling()) {
                for (Aspect aspect : aspects) {
                    aspect.before(className, methodName, descriptor, params);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void complete(String className, String methodName, String descriptor, Object returnValueOrThrowable) {
        try {
            if (SamplingRate.needSampling()) {
                if (returnValueOrThrowable instanceof Throwable) {
                    for (Aspect aspect : aspects) {
                        aspect.error(className, methodName, descriptor, (Throwable) returnValueOrThrowable);
                    }
                } else {
                    for (Aspect aspect : aspects) {
                        aspect.after(className, methodName, descriptor, returnValueOrThrowable);
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }
}
