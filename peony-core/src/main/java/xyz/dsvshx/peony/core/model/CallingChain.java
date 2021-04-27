package xyz.dsvshx.peony.core.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author dongzhonghua
 * Created on 2021-04-10
 */
@Data
@Builder
public class CallingChain {
    private CallRecord callRecord;

    private CallingChain next;
    private CallingChain pre;

}
