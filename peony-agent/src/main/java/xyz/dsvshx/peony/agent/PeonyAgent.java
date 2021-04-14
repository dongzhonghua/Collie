package xyz.dsvshx.peony.agent;

import java.lang.instrument.Instrumentation;

import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.peony.agent.instrumentation.MyTransformer;

/**
 * @author dongzhonghua
 * Created on 2021-04-09
 */
@Slf4j
public class PeonyAgent {
    /**
     * jvm启动时运行这个函数
     */
    public static void premain(String agentOps, Instrumentation instrumentation) {
        try {
            log.info(">>>>>>>进入premain，agent参数：{}", agentOps);
            instrumentation.addTransformer(new MyTransformer());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    public static void agentmain(String agentOps, Instrumentation instrumentation) {
        try {

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
