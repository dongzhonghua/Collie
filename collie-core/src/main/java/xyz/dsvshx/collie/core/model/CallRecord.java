package xyz.dsvshx.collie.core.model;

import java.util.Arrays;

import xyz.dsvshx.collie.core.aspect.LogAspectImpl.TransactionInfo;
import xyz.dsvshx.collie.core.util.Encryption;

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
    private Object target;
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
        return "\nCallRecord{" +
                "\ntransactionInfo=" + transactionInfo.toString() +
                ", \nclassName='" + className + '\'' +
                ", \nmethodName='" + methodName + '\'' +
                ", \ndescriptor='" + descriptor + '\'' +
                ", \nparamList=" + Arrays.toString(paramList) +
                ", \nthrowable=" + throwable +
                ", \nresult=" + result +
                ", \nstartTime=" + startTime +
                ", \nfinishTime=" + finishTime +
                ", \ncntMs=" + cntMs +
                "\n}";
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
