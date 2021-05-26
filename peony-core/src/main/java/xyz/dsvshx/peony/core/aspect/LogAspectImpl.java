package xyz.dsvshx.peony.core.aspect;

import com.alibaba.fastjson.JSONObject;

/**
 * @author dongzhonghua
 * Created on 2021-04-20
 */
public class LogAspectImpl implements MethodAspect, FrameworkAspect {

    /**
     * 具体的实现调用链日志的打印
     */
    @Override
    public void before(String className, String methodName, String descriptor, Object[] params) {
        System.out.printf("---------------------------------before------------------\n"
                        + "className:%s,\nmethodName:%s,\ndescriptor:%s,\nstartTime:%d,\nparams:%s\n\n",
                className, methodName, descriptor, System.currentTimeMillis(), JSONObject.toJSON(params));
    }

    @Override
    public void error(String className, String methodName, String descriptor, Throwable throwable) {
        System.out.printf("---------------------------------complete------------------\n"
                        + "className:%s,\nmethodName:%s,\ndescriptor:%s,\nendTime:%d,\ncost:%d,\nreturn:%s\n\n",
                className, methodName, descriptor, System.currentTimeMillis(), -1,
                JSONObject.toJSON(throwable));
    }

    @Override
    public void after(String className, String methodName, String descriptor, Object returnValueOrThrowable) {
        System.out.printf("---------------------------------complete------------------\n"
                        + "className:%s,\nmethodName:%s,\ndescriptor:%s,\nendTime:%d,\ncost:%d,\nreturn:%s\n\n",
                className, methodName, descriptor, System.currentTimeMillis(), -1,
                JSONObject.toJSON(returnValueOrThrowable));
    }

    /**
     * 具体的业务实现，如果要用链表来存储调用链信息的话可以在这里埋入一个ThreadLocal 链表
     */
    @Override
    public void entry() {
        // TODO: 2021/5/26 如果用链表存储在这里写，不过暂时不用，想去用写到MySQL的方法，至于kafka也可以用，但不必要
    }

    @Override
    public void exit() {

    }
}
