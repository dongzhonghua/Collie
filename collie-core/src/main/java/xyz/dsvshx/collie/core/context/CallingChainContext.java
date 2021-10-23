package xyz.dsvshx.collie.core.context;

import xyz.dsvshx.collie.core.model.CallingChain;

/**
 * 调用链上下文，感觉这个其实可以不用了，用链表，还不如发送到kafka呢
 *
 * @author dongzhonghua
 * Created on 2021-05-24
 */
public class CallingChainContext {
    private final static ThreadLocal<CallingChain> CALLING_CHAIN_CONTEXT = new ThreadLocal<>();

}
