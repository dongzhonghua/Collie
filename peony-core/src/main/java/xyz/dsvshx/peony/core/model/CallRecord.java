package xyz.dsvshx.peony.core.model;

import lombok.Data;

/**
 * @author dongzhonghua
 * Created on 2021-04-20
 */
@Data
public class CallRecord {
    // 唯一ID
    private String transactionId;
    private String spanId;
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
    private Long cntMs;

    public Long getCntMs() {
        return finishTime >= startTime ? finishTime - startTime : null;
    }

    public static String getMethodId(String className, String methodName) {
        return className + methodName;
    }
}
