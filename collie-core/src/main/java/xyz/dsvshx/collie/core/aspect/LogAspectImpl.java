package xyz.dsvshx.collie.core.aspect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import xyz.dsvshx.collie.core.model.CallRecord;

/**
 * @author dongzhonghua
 * Created on 2021-04-20
 */
public class LogAspectImpl implements MethodAspect, FrameworkAspect {

    private Map<String, CallRecord> callRecordMap = new HashMap<>();
    /**
     * 存储TransactionInfo
     */
    public final static ThreadLocal<TransactionInfo> TRANSACTION_INFO_THREAD_LOCAL = new ThreadLocal<>();
    private static TransactionInfo TRANSACTION_INFO;

    @Override
    public void before(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args) {
        CallRecord callRecord = new CallRecord();
        callRecord.setClassName(clazz.getName());
        callRecord.setMethodName(methodName);
        callRecord.setDescriptor(methodDesc);
        callRecord.setParamList(args);
        callRecord.setTarget(target);
        callRecord.setTransactionInfo(TRANSACTION_INFO_THREAD_LOCAL.get());
        callRecord.setStartTime(System.currentTimeMillis());
        printLog(callRecord, "before");
        callRecordMap.put(callRecord.createCallRecordId(), callRecord);
    }

    @Override
    public void after(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args,
            Object returnObject) {
        System.out.println(callRecordMap);
        CallRecord callRecord = new CallRecord();
        callRecord.setClassName(clazz.getName());
        callRecord.setMethodName(methodName);
        callRecord.setTarget(target);
        callRecord.setTransactionInfo(TRANSACTION_INFO_THREAD_LOCAL.get());
        CallRecord callRecord1 = callRecordMap.get(callRecord.createCallRecordId());
        if (callRecord1 != null) {
            callRecord1.setResult(returnObject);
            long finishTime = System.currentTimeMillis();
            callRecord1.setFinishTime(finishTime);
            callRecord1.setCntMs(finishTime - callRecord1.getStartTime());
            callRecordMap.put(callRecord.createCallRecordId(), callRecord1);
            printLog(callRecord1, "after");
        }
    }

    @Override
    public void error(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args,
            Throwable throwable) {
        CallRecord callRecord = new CallRecord();
        callRecord.setClassName(clazz.getName());
        callRecord.setMethodName(methodName);
        callRecord.setDescriptor(methodDesc);
        callRecord.setTarget(target);
        callRecord.setThrowable(throwable);
        callRecord.setTransactionInfo(TRANSACTION_INFO_THREAD_LOCAL.get());
        callRecord.setFinishTime(System.currentTimeMillis());
        printLog(callRecord, "error");
    }

    @Override
    public void entry(String traceId, String spanId, String parentSpanId) {
        try {
            if (traceId == null || traceId.trim().length() == 0) {
                traceId = UUID.randomUUID().toString();
            }
            if (spanId == null || spanId.trim().length() == 0) {
                spanId = UUID.randomUUID().toString();
            }
            if (parentSpanId == null || parentSpanId.trim().length() == 0) {
                parentSpanId = "-1";
            }
            TRANSACTION_INFO = new TransactionInfo(traceId, spanId, parentSpanId);
            TRANSACTION_INFO_THREAD_LOCAL.set(TRANSACTION_INFO);
            System.out.printf(">>>>>>>>>Thread %s set, transaction info :%s%n", Thread.currentThread().getName(),
                    TRANSACTION_INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exit(String info) {
        try {
            System.out.println(info);
            callRecordMap.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printLog(CallRecord callRecord, String type) {
        // if (callRecord.getTransactionInfo() != null) {
        if (callRecord.getTransactionInfo() == null) {
            callRecord.setTransactionInfo(new TransactionInfo("null", "null", "null"));
            System.out.printf("\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$%s$$$$$$$$$$$$$$$$$$", type);
            System.out.println(callRecord);
        }
        // }
        // 后续可以发到kafka里
    }

    public static class TransactionInfo {
        String traceId;
        String spanId;
        String parentSpanId;

        public TransactionInfo(String traceId, String spanId, String parentSpanId) {
            this.traceId = traceId;
            this.spanId = spanId;
            this.parentSpanId = parentSpanId;
        }

        @Override
        public String toString() {
            return String.format("traceId: %s, spanId: %s, parentSpanId: %s", traceId, spanId, parentSpanId);
        }
    }
}
