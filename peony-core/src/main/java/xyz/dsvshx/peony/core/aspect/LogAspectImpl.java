package xyz.dsvshx.peony.core.aspect;

import com.alibaba.fastjson.JSONObject;

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
        System.out.printf("---------------------------------before------------------\n"
                        + "className:%s,\nmethodName:%s,\ndescriptor:%s,\nstartTime:%d,\nparams:%s\n",
                className, methodName, descriptor, System.currentTimeMillis(), JSONObject.toJSON(params));
    }

    @Override
    public void error(String className, String methodName, String descriptor, Throwable throwable) {
        System.out.printf("---------------------------------complete------------------\n"
                        + "className:%s,\nmethodName:%s,\ndescriptor:%s,\nendTime:%d,\ncost:%d,\nreturn:%s\n",
                className, methodName, descriptor, System.currentTimeMillis(), -1,
                JSONObject.toJSON(throwable));
    }

    @Override
    public void after(String className, String methodName, String descriptor, Object returnValueOrThrowable) {
        System.out.printf("---------------------------------complete------------------\n"
                        + "className:%s,\nmethodName:%s,\ndescriptor:%s,\nendTime:%d,\ncost:%d,\nreturn:%s\n",
                className, methodName, descriptor, System.currentTimeMillis(), -1,
                JSONObject.toJSON(returnValueOrThrowable));
    }
}
