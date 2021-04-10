package xyz.dsvshx.peony.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

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
                // if (!clazz.getName().startsWith("java.") && !clazz.getName().startsWith("sun.") && !clazz.getName()
                //         .startsWith("[Ljava.")) {
                //     System.out.println(clazz.getName());
                // }
                if (clazz.getName().startsWith("xyz.dsvshx")) {
                    log.info(">>>>>>>>>加载过的类：{}", clazz.getName());
                    transformClass(clazz, instrumentation);
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void transformClass(Class<?> clazz, Instrumentation instrumentation) {
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

                return new byte[0];
            }
        });

    }

    public static void agentmain(String agentOps, Instrumentation instrumentation) {
        try {

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
