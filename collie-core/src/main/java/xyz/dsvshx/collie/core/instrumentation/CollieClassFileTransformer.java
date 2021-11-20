package xyz.dsvshx.collie.core.instrumentation;

import static xyz.dsvshx.collie.core.adaptor.ClassAdaptor.COMMON_CLASS;
import static xyz.dsvshx.collie.core.adaptor.ClassAdaptor.frameworkAdaptors;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.collie.core.adaptor.ClassAdaptor;
import xyz.dsvshx.collie.core.aspect.SpyImpl;

/**
 * @author dongzhonghua
 * Created on 2021-04-14
 */
@Slf4j
public class CollieClassFileTransformer implements ClassFileTransformer {

    private final String spyJarPath;
    private final static ConcurrentMap<String, Object> MODIFY_CLASS_MAP = new ConcurrentHashMap<>();


    public CollieClassFileTransformer(String spyJarPath) {
        this.spyJarPath = spyJarPath;
        initAspects();
        initAdaptors();
    }

    private void initAdaptors() {
        ClassAdaptor.init();
    }

    private void initAspects() {
        SpyImpl.init();
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBytes) {
        // 一定要排除
        if (className.startsWith("sun/") || className.startsWith("com/sun/") ||
                className.startsWith("java/") || className.startsWith("jdk/")
                || className.startsWith("javax/")) {
            return classfileBytes;
        }
        // 把自己排除掉
        if (className.startsWith("xyz/dsvshx/collie/")) {
            return classfileBytes;
        }
        // 排除调动态代理类，和使用idea调试时候的类
        if (className.contains("CGLIB") || className.contains("intellij") || className.contains("jetbrains")) {
            return classfileBytes;
        }
        // 实在是太多了，修改哪些包合适呢？一个一个的去掉简直太傻逼了
        if (className.contains("com/alibaba/fastjson")) {
            return classfileBytes;
        }
        if (className.contains("javassist/")) {
            return classfileBytes;
        }
        if (className.contains("org/reflections")) {
            return classfileBytes;
        }
        if (className.contains("org/groovy/debug")) {
            return classfileBytes;
        }

        // 适配一些第三方框架, 目前针对summer和dzh-rpc进行支持
        ClassAdaptor frameworkAdaptor = frameworkAdaptors.get(className);
        if (frameworkAdaptor != null) {
            System.out.println("===================transform===================：" + className);
            return frameworkAdaptor.modifyClass(className, classfileBytes, spyJarPath);
        }

        // FIXME: 2021/11/14 只先对自己的进行转换，否则需要过滤的太多了，需要在思考一下如何过滤或者只对自己配置的做转换
        if (!className.contains("xyz/dsvshx")) {
            return classfileBytes;
        }
        System.out.println("===================transform===================：" + className);
        return frameworkAdaptors.get(COMMON_CLASS).modifyClass(className, classfileBytes, spyJarPath);

    }
}
