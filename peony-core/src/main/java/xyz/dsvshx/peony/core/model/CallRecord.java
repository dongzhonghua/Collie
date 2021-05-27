package xyz.dsvshx.peony.core.model;

import lombok.Data;
import xyz.dsvshx.peony.point.FrameworkPoint.TransactionInfo;

/**
 * @author dongzhonghua
 * Created on 2021-04-20
 */
@Data
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
}
