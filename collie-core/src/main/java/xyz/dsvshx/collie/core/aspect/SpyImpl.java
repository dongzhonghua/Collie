package xyz.dsvshx.collie.core.aspect;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import xyz.dsvshx.collie.point.SpyAPI;
import xyz.dsvshx.collie.point.SpyAPI.AbstractSpy;

/**
 * @author dongzhonghua
 * Created on 2021-11-11
 */
public class SpyImpl extends AbstractSpy {

    // 这是什么设计模式？感觉和委派模式有点像，但又不完全是。
    private static List<MethodAspect> methodAspects;
    private static List<FrameworkAspect> frameworkAspects;
    public final static ThreadLocal<Boolean> NEED_SAMPLING = ThreadLocal.withInitial(() -> true);

    public static void init() {
        // 获取Aspect所有的实现类
        LogAspectImpl logAspect = new LogAspectImpl();
        methodAspects = new ArrayList<>();
        frameworkAspects = new ArrayList<>();
        methodAspects.add(logAspect);
        frameworkAspects.add(logAspect);
        // 初始化Spy的方法
        SpyAPI.setSpy(new SpyImpl());
    }

    @Override
    public void atEnter(Class<?> clazz, String methodInfo, Object target, Object[] args) {
        try {
            String[] methodInfos = splitMethodInfo(methodInfo);
            String methodName = methodInfos[0];
            String methodDesc = methodInfos[1];
            for (MethodAspect methodAspect : methodAspects) {
                methodAspect.before(clazz, methodName, methodDesc, target, args);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void atExit(Class<?> clazz, String methodInfo, Object target, Object[] args, Object returnObject) {
        try {
            String[] methodInfos = splitMethodInfo(methodInfo);
            String methodName = methodInfos[0];
            String methodDesc = methodInfos[1];
            for (MethodAspect methodAspect : methodAspects) {
                methodAspect.after(clazz, methodName, methodDesc, target, args, returnObject);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodInfo, Object target, Object[] args, Throwable throwable) {
        try {
            if (NEED_SAMPLING.get()) {
                String[] methodInfos = splitMethodInfo(methodInfo);
                String methodName = methodInfos[0];
                String methodDesc = methodInfos[1];
                for (MethodAspect methodAspect : methodAspects) {
                    methodAspect.error(clazz, methodName, methodDesc, target, args, throwable);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    // TODO: 2021/11/13 做一些初始化或者日志打印等工作
    @Override
    public void atFrameworkEnter(String traceId, String spanId, String parentSpanId) {
        boolean needSampling = SamplingRate.needSampling();
        NEED_SAMPLING.set(needSampling);
        System.out.println("=========================是否需要埋点=============================" + NEED_SAMPLING.get());
        for (FrameworkAspect frameworkAspect : frameworkAspects) {
            try {
                frameworkAspect.entry(traceId, spanId, parentSpanId);
            } catch (Throwable throwable) {
                //
            }
        }
    }

    // TODO: 2021/11/13 做一些清理工作
    @Override
    public void atFrameworkExit(String info) {
        for (FrameworkAspect frameworkAspect : frameworkAspects) {
            try {
                frameworkAspect.exit(info);
            } catch (Throwable throwable) {
                //
            }
        }
    }

    private String[] splitMethodInfo(String methodInfo) {
        return methodInfo.split(Pattern.quote("|"));
    }
}
