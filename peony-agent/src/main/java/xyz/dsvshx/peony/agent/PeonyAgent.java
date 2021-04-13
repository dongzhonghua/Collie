package xyz.dsvshx.peony.agent;

import java.lang.instrument.Instrumentation;

import lombok.extern.slf4j.Slf4j;

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
            Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
            for (Class<?> clazz : loadedClasses) {
                if (clazz.getName().startsWith("xyz.dsvshx")) {
                    log.info(">>>>>>>>>加载过的类：{}", clazz.getName());

                }
            }
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


    // public static void point(final MethodInfo method, final long startNanos, Object[] parameterValues, Object returnValues) {
    //     System.out.println("监控 - Begin");
    //     System.out.println("方法：" + method.getClazzName() + "." + method.getMethodName());
    //     System.out.println("入参：" + JSON.toJSONString(method.getParameterNameList()) + " 入参[类型]：" + JSON.toJSONString(method.getParameterTypeList()) + " 入数[值]：" + JSON.toJSONString(parameterValues));
    //     System.out.println("出参：" + method.getReturnType() + " 出参[值]：" + JSON.toJSONString(returnValues));
    //     System.out.println("耗时：" + (System.nanoTime() - startNanos) / 1000000 + "(s)");
    //     System.out.println("监控 - End\r\n");
    // }
    //
    // public static void point(final int methodId, Throwable throwable) {
    //     System.out.println("监控 - Begin");
    //     System.out.println("方法：" + method.getClazzName() + "." + method.getMethodName());
    //     System.out.println("异常：" + throwable.getMessage());
    //     System.out.println("监控 - End\r\n");
    // }

}
