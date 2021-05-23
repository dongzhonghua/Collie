package xyz.dsvshx.peony.core.aspect;

import java.util.Arrays;

/**
 * @author dongzhonghua
 * Created on 2021-04-20
 */
public class LogAspectImpl implements Aspect {

    /**
     * 具体的实现调用链日志的打印
     */
    @Override
    public void before(String className, String methodName, String descriptor, Object[] params) {
        System.out.printf("------------------------\n"
                        + "className:%s,\nmethodName:%s,\nstartTime:%d,\nendTime:%d,\ncost:%d,\nreturn:%s\n", className,
                methodName, System.currentTimeMillis(), -1, -1, Arrays.toString(params));

    }

    @Override
    public void error(String className, String methodName, String descriptor, Throwable throwable) {

    }

    @Override
    public void after(String className, String methodName, String descriptor, Object returnValueOrThrowable) {
        System.out.printf("------------------------\n"
                        + "className:%s,\nmethodName:%s,\ndescriptor:%s,\nendTime:%d,\ncost:%d,\nreturn:%s\n",
                className, methodName, descriptor, System.currentTimeMillis(), -1, returnValueOrThrowable);
    }
}
