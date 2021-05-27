package xyz.dsvshx.peony.point;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author dongzhonghua
 * Created on 2021-04-11
 */
public class FrameworkPoint {
    /**
     * 存储TransactionInfo
     */
    private final static ThreadLocal<TransactionInfo> TRANSACTION_INFO_THREAD_LOCAL = new ThreadLocal<>();
    private static TransactionInfo TRANSACTION_INFO;

    /**
     * 这种方式可以比较好的吧方法解耦出来，并且也比较好的解决了多个classloader带来的问题，但是引来了另一个问题
     * 反射必然会带来性能的损失，这个没有好的解决方案，但是本身是带采样的，所以还好
     * <p>
     * 这两个方法主要作用是：在这里没有什么比较特殊的作用，如果要记录日志可以在这里记录，但是设置tid等就不用在这里记录了
     */
    public static Method CONTEXT_ENTRY = null;
    public static Method CONTEXT_EXIT = null;

    public static Method CONTEXT_CUR_TRANSACTION_ID = null;

    public static TransactionInfo getCurTraceId() {
        TransactionInfo transactionInfo = TRANSACTION_INFO_THREAD_LOCAL.get();
        // 这段加的非常没有必要，如果是没有transactionInfo，还有没有必要再去生成一个？
        // if (transactionInfo == null) {
        //     try {
        //         transactionInfo = (TransactionInfo) CONTEXT_CUR_TRANSACTION_ID.invoke(null);
        //         System.out.println("》》》》》》》》》》" + transactionInfo);
        //     } catch (IllegalAccessException | InvocationTargetException e) {
        //         //
        //     }
        // }
        return transactionInfo;
    }

    /**
     * 由字节码调用，修改框架的字节码，调用这个函数
     * 适配各个框架，在框架入口处埋点。比如， my-rpc里可以在服务端收到请求之后把上游传过来的id埋进ThreadLocal里。
     * 并且执行CONTEXT_ENTRY中的逻辑。
     * exit类似
     * <p>
     * 要加入spanID parentSpanID
     *
     * @param traceId 追踪ID
     * @param spanId spanId 应该自己生成，不应该前面传给自己
     * @param parentSpanId 上个步骤的spanId
     */
    public static void enter(String traceId, String spanId, String parentSpanId) {
        try {
            // 好像也可以把这些放到CONTEXT_ENTRY里在做，但是这样的话传参什么的比较麻烦，还是这样比较方便。
            if (traceId == null || traceId.trim().length() == 0) {
                traceId = UUID.randomUUID().toString();
            }
            if (spanId == null || spanId.trim().length() == 0) {
                spanId = UUID.randomUUID().toString();
            }
            if (parentSpanId == null || parentSpanId.trim().length() == 0) {
                parentSpanId = "-1";
            }
            TRANSACTION_INFO = new TransactionInfo(traceId, spanId, parentSpanId);
            TRANSACTION_INFO_THREAD_LOCAL.set(TRANSACTION_INFO);
            System.out.printf(">>>>>>>>>Thread %s set, transaction info :%s%n", Thread.currentThread().getName(),
                    TRANSACTION_INFO);
            if (CONTEXT_ENTRY != null) {
                //invoke方法的签名为：Object invoke(Object obj, Object... args)，第一个参数是调用方法的对象，其余的提供了调用方法所需要的参数。
                // 对于静态方法，第一个参数可以被忽略，即直接设置为null
                CONTEXT_ENTRY.invoke(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void exit() {
        try {
            TRANSACTION_INFO_THREAD_LOCAL.remove();
            System.out.printf(">>>>>>>>>Thread %s exit, transaction info :%s%n", Thread.currentThread().getName(),
                    TRANSACTION_INFO);
            if (CONTEXT_EXIT != null) {
                CONTEXT_EXIT.invoke(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class TransactionInfo {
        String traceId;
        String spanId;
        String parentSpanId;

        public TransactionInfo(String traceId, String spanId, String parentSpanId) {
            this.traceId = traceId;
            this.spanId = spanId;
            this.parentSpanId = parentSpanId;
        }

        @Override
        public String toString() {
            return String.format("traceId: %s, spanId: %s, parentSpanId: %s", traceId, spanId, parentSpanId);
        }
    }
}
