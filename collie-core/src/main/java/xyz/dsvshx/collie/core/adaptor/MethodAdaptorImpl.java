package xyz.dsvshx.collie.core.adaptor;

import static xyz.dsvshx.collie.core.util.JavassistUtils.isAbstract;
import static xyz.dsvshx.collie.core.util.JavassistUtils.isNative;
import static xyz.dsvshx.collie.core.util.JavassistUtils.isStatic;

import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;

public class MethodAdaptorImpl extends ClassAdaptor {

    @Override
    public byte[] modifyClass(String className, byte[] classfileBuffer, String spyJarPath) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            // 必须要有这个，否则会报point找不到，搞了大半天 https://my.oschina.net/xiaominmin/blog/3153685
            classPool.appendClassPath(spyJarPath);
            String clazzname = className.replace("/", ".");
            CtClass ctClass = classPool.get(clazzname);
            // 排除掉注解，接口，枚举
            if (!ctClass.isAnnotation() && !ctClass.isInterface() && !ctClass.isEnum()) {
                // 针对所有函数操作
                for (CtBehavior ctBehavior : ctClass.getDeclaredMethods()) {
                    addMethodAspect(clazzname, ctBehavior, false);
                }
                // 所有构造函数
                // for (CtBehavior ctBehavior : ctClass.getDeclaredConstructors()) {
                //     addMethodAspect(clazzname, ctBehavior, true);
                // }
                ctClass.writeFile(
                        "/Users/dongzhonghua03/Documents/github/collie/collie-core/src/main/java/xyz/dsvshx/collie/core"
                                + "/instrumentation");
            }
            return ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
            return classfileBuffer;
        }
    }

    private static void addMethodAspect(String clazzname, CtBehavior ctBehavior, boolean isConstructor)
            throws Exception {
        if (isNative(ctBehavior)
                || isAbstract(ctBehavior)
                || "toString".equals(ctBehavior.getName())
                || "getClass".equals(ctBehavior.getName())
                || "equals".equals(ctBehavior.getName())
                || "hashCode".equals(ctBehavior.getName())) {
            return;
        }
        // 方法前增强
        // 如果是基本数据类型的话，传参为Object是不对的，需要转成封装类型
        // 转成封装类型的话非常方便，使用$w就可以，不影响其他的Object类型
        String methodName = isConstructor ? ctBehavior.getName() + "#" : ctBehavior.getName();
        String methodInfo = methodName + "|" + ctBehavior.getMethodInfo().getDescriptor();
        String target = isStatic(ctBehavior) ? "null" : "this";
        ctBehavior.insertBefore(
                String.format("{xyz.dsvshx.collie.point.SpyAPI.atEnter(%s, \"%s\", %s, %s);}",
                        "$class", methodInfo, target, "($w)$args")
        );
        // 方法后增强
        ctBehavior.insertAfter(
                String.format("{xyz.dsvshx.collie.point.SpyAPI.atExit(%s, \"%s\", %s, %s, %s);}",
                        "$class", methodInfo, target, "($w)$args", "($w)$_")
        );
        // 异常出增强
        ctBehavior.addCatch(
                String.format("{xyz.dsvshx.collie.point.SpyAPI.atExceptionExit(%s, \"%s\", %s, %s, %s);"
                                + "throw $e;}",
                        "$class", methodInfo, target, "($w)$args", "$e"),
                ClassPool.getDefault().get("java.lang.Throwable")
        );
    }
}