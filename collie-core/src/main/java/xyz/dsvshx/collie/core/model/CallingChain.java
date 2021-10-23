package xyz.dsvshx.collie.core.model;

import lombok.Builder;
import lombok.Data;
import xyz.dsvshx.collie.point.FrameworkPoint.TransactionInfo;

/**
 * @author dongzhonghua
 * Created on 2021-04-10
 */
@Data
@Builder
public class CallingChain {
    private TransactionInfo transactionInfo;
    private CallRecord curRecord;
}