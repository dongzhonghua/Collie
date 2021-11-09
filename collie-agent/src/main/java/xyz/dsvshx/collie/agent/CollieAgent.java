package xyz.dsvshx.collie.agent;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;

import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.collie.agent.jvm.Metric;
import xyz.dsvshx.collie.agent.loader.CollieClassLoader;

/**
 * @author dongzhonghua
 * Created on 2021-04-09
 */
@Slf4j
public class CollieAgent {
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String COLLIE_CORE_JAR = USER_HOME + "/.collie/collie-core.jar";
    private static final String COLLIE_SPY_JAR = USER_HOME + "/.collie/collie-spy.jar";
    private static final String TRANSFORMER = "xyz.dsvshx.collie.core.instrumentation.CollieClassFileTransformer";

    private static ClassLoader collieClassLoader;
    private static ScheduledExecutorService EXECUTOR_SERVICE;

    /**
     * jvm启动时运行这个函数
     */
    public static void premain(String agentOps, Instrumentation instrumentation) {
        main(agentOps, instrumentation);
    }

    public static void agentmain(String agentOps, Instrumentation instrumentation) {
        main(agentOps, instrumentation);
    }

    private static ClassLoader getClassLoader(Instrumentation inst, File agentCoreFile) throws Throwable {
        return loadOrDefineClassLoader(agentCoreFile);
    }

    private static ClassLoader loadOrDefineClassLoader(File agentCoreFile) throws Throwable {
        if (collieClassLoader == null) {
            collieClassLoader = new CollieClassLoader(new URL[] {agentCoreFile.toURI().toURL()});
        }
        return collieClassLoader;
    }

    private static synchronized void main(String agentOps, Instrumentation instrumentation) {
        try {
            log.info(">>>>>>>collie agent 启动...");

            // 使用启动类加载器load spy

            File agentSpyFile = new File(COLLIE_SPY_JAR);
            if (!agentSpyFile.exists()) {
                System.out.println("Agent jar file does not exist: " + agentSpyFile);
                return;
            }
            instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(agentSpyFile));

            // load core
            File agentCoreFile = new File(COLLIE_CORE_JAR);
            if (!agentCoreFile.exists()) {
                System.out.println("Agent jar file does not exist: " + agentCoreFile);
                return;
            }
            // 使用自定义的类加载器加载core包
            ClassLoader collieClassLoader = getClassLoader(instrumentation, agentCoreFile);
            // addTransformer
            Class<?> collieClassFileTransformer = collieClassLoader.loadClass(TRANSFORMER);
            Constructor<?> declaredConstructor = collieClassFileTransformer.getDeclaredConstructor(String.class);
            instrumentation.addTransformer((ClassFileTransformer) declaredConstructor.newInstance(COLLIE_SPY_JAR));
            // jvm信息，暂时关闭
            // processJvmInfo();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void processJvmInfo() {
        // TODO: 2021/5/22 如何关闭？
        EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);
        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            Metric.printMemoryInfo();
            Metric.printGCInfo();
        }, 0, 10000, TimeUnit.MILLISECONDS);
    }

    private static void shutdownJvmInfo() {
        EXECUTOR_SERVICE.shutdown();
    }
}
