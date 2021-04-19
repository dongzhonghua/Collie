package xyz.dsvshx.peony.point;

import java.lang.reflect.Method;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dongzhonghua
 * Created on 2021-04-11
 */
@Slf4j
public class FrameworkPoint {


    /**
     * 存储traceId
     */
    private final static ThreadLocal<String> TRACE_ID_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 这种方式可以比较好的吧方法解耦出来，并且也比较好的解决了多个classloader带来的问题，但是引来了另一个问题
     * 反射必然会带来性能的损失，这个没有好的解决方案，但是本身是带采样的，所以还好
     */
    public static Method CONTEXT_ENTRY = null;
    public static Method CONTEXT_EXIT = null;

    /**
     * 适配各个框架，在框架入口处埋点。比如， my-rpc里可以在服务端收到请求之后把上游传过来的id埋进ThreadLocal里。
     * 并且执行CONTEXT_ENTRY中的逻辑。
     * exit类似
     *
     * @param traceId 追踪ID
     */
    public static void enterContext(String traceId) {
        try {
            if (traceId == null || traceId.trim().length() == 0) {
                traceId = UUID.randomUUID().toString();
            }
            TRACE_ID_THREAD_LOCAL.set(traceId);
            log.info(">>>>>>>>>>>>>>>>>>Thread {} set traceId:{}", Thread.currentThread().getName(), traceId);
            if (CONTEXT_ENTRY != null) {
                //invoke方法的签名为：Object invoke(Object obj, Object... args)，第一个参数是调用方法的对象，其余的提供了调用方法所需要的参数。
                // 对于静态方法，第一个参数可以被忽略，即直接设置为null
                CONTEXT_ENTRY.invoke(null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    public static void exitContext() {
        try {
            TRACE_ID_THREAD_LOCAL.remove();
            log.info(">>>>>>>>>>>>>>>>>>Thread {} exit", Thread.currentThread().getName());
            if (CONTEXT_EXIT != null) {
                CONTEXT_EXIT.invoke(null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
