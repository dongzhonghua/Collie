package xyz.dsvshx.peony;

import lombok.Data;

/**
 * @author dongzhonghua
 * Created on 2021-04-10
 */
@Data
public class CallingChain {
    // 唯一ID
    private String transactionId;
    private String className;
    private String methodName;
    private String descriptor;
    private Object[] params;
    private Throwable throwable;
    private Object returnValue;

    private long startTime;
    private long finishTime;

    private Long cntMs;

    private CallingChain next;
    private CallingChain pre;

    public Long getCntMs() {
        return finishTime >= startTime ? finishTime - startTime : null;
    }
}
