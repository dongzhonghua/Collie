package xyz.dsvshx.peony.core.context;

import xyz.dsvshx.peony.core.model.CallingChain;

/**
 * 调用链上下文，在ThreadLocal中存储TraceId和SpanId
 *
 * @author dongzhonghua
 * Created on 2021-05-24
 */
public class CallingChainContext {
    private final static ThreadLocal<CallingChain> CALLING_CHAIN_CONTEXT = new ThreadLocal<>();




}
