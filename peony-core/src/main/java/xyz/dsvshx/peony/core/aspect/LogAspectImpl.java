package xyz.dsvshx.peony.core.aspect;

import xyz.dsvshx.peony.core.model.CallRecord;
import xyz.dsvshx.peony.point.FrameworkPoint;

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
        CallRecord callRecord = new CallRecord();
        callRecord.setClassName(className);
        callRecord.setMethodName(methodName);
        callRecord.setDescriptor(descriptor);
        callRecord.setParamList(params);
        callRecord.setTransactionInfo(FrameworkPoint.getCurTraceId());
        callRecord.setStartTime(System.currentTimeMillis());
        printLog(callRecord, "before");
    }

    @Override
    public void error(String className, String methodName, String descriptor, Throwable throwable) {
        CallRecord callRecord = new CallRecord();
        callRecord.setClassName(className);
        callRecord.setMethodName(methodName);
        callRecord.setDescriptor(descriptor);
        callRecord.setThrowable(throwable);
        callRecord.setTransactionInfo(FrameworkPoint.getCurTraceId());
        callRecord.setFinishTime(System.currentTimeMillis());
        printLog(callRecord, "error");
    }

    @Override
    public void after(String className, String methodName, String descriptor, Object result) {
        CallRecord callRecord = new CallRecord();
        callRecord.setClassName(className);
        callRecord.setMethodName(methodName);
        callRecord.setDescriptor(descriptor);
        callRecord.setResult(result);
        callRecord.setTransactionInfo(FrameworkPoint.getCurTraceId());
        callRecord.setFinishTime(System.currentTimeMillis());
        printLog(callRecord, "after");
    }

    private static void printLog(CallRecord callRecord, String type) {
        if (callRecord.getTransactionInfo() != null) {
            System.out.printf("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$%s$$$$$$$$$$$$$$$$$$%n", type);
            System.out.println(callRecord);
        }
        // 后续可以发到kafka里
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
