package xyz.dsvshx.peony.core.model;

import java.util.Arrays;

import xyz.dsvshx.peony.core.util.Encryption;
import xyz.dsvshx.peony.point.FrameworkPoint.TransactionInfo;

/**
 * @author dongzhonghua
 * Created on 2021-04-20
 */
public class CallRecord {
    // 唯一ID
    private TransactionInfo transactionInfo;
    private String className;
    private String methodName;
    /**
     * http rpc redis等
     */
    private String event;
    private String descriptor;
    private String params;
    private Object[] paramList;
    private Throwable throwable;
    private Object result;

    private long startTime;
    private long finishTime;
    private long cntMs;

    public String createCallRecordId() {
        if (transactionInfo != null && className != null && methodName != null) {
            String callInfo = transactionInfo + className + methodName;
            return Encryption.encryptByMD5(callInfo);
        }
        return null;
    }

    public TransactionInfo getTransactionInfo() {
        return transactionInfo;
    }

    public void setTransactionInfo(TransactionInfo transactionInfo) {
        this.transactionInfo = transactionInfo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Object[] getParamList() {
        return paramList;
    }

    public void setParamList(Object[] paramList) {
        this.paramList = paramList;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public long getCntMs() {
        return cntMs;
    }

    public void setCntMs(long cntMs) {
        this.cntMs = cntMs;
    }

    @Override
    public String toString() {
        return "CallRecord{" +
                "transactionInfo=" + transactionInfo.toString() +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", event='" + event + '\'' +
                ", descriptor='" + descriptor + '\'' +
                ", params='" + params + '\'' +
                ", paramList=" + Arrays.toString(paramList) +
                ", throwable=" + throwable +
                ", result=" + result +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                ", cntMs=" + cntMs +
                '}';
    }
}
