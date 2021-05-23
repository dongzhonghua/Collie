package xyz.dsvshx.peony.core.instrumentation;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import xyz.dsvshx.peony.core.aspect.MethodCallLisener;
import xyz.dsvshx.peony.core.model.CallRecord;

/**
 * @author dongzhonghua
 * Created on 2021-04-14
 */
public class PeonyClassFileTransformer implements ClassFileTransformer {

    private final String spyJarPath;

    public PeonyClassFileTransformer(String spyJarPath) {
        this.spyJarPath = spyJarPath;
        initAspect();
    }

    private void initAspect() {
        try {
            MethodCallLisener.init();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // 一定要排除
        if (className.startsWith("sun/") || className.startsWith("java/") || className.startsWith("jdk/")) {
            return classfileBuffer;
        }
        // 把自己排除掉
        if (className.startsWith("xyz/dsvshx/peony/")) {
            return classfileBuffer;
        }
        // 排除调动态代理类，和使用idea调试时候的类
        if (className.contains("CGLIB") || className.contains("intellij") || className.contains("jetbrains")) {
            return classfileBuffer;
        }
        // 适配一些第三方框架
        // TODO: 2021/4/14  

        System.out.println("className:" + className);
        return classTransform(className, classfileBuffer);
    }

    private byte[] classTransform(String className, byte[] classfileBuffer) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            // 必须要有这个，否则会报point找不到，搞了大半天 https://my.oschina.net/xiaominmin/blog/3153685
            classPool.appendClassPath(spyJarPath);
            String clazzname = className.replace("/", ".");
            CtClass ctClass = classPool.get(clazzname);
            // 所有函数和构造函数
            for (CtBehavior ctBehavior : ctClass.getDeclaredBehaviors()) {
                // 方法前加强
                // before(String className, String methodName, String descriptor, Object[] params)
                ctBehavior.insertBefore(
                        String.format("{xyz.dsvshx.peony.point.Point.before(\"%s\", \"%s\", \"%s\", %s);}",
                                clazzname, ctBehavior.getName(), "还不知道传什么", "$args")
                );
                // 打点加到最后
                // complete(String className, String methodName, String descriptor, Object returnValueOrThrowable)
                ctBehavior.insertAfter(
                        String.format("{xyz.dsvshx.peony.point.Point.complete(\"%s\", \"%s\", \"%s\", %s);}",
                                clazzname, ctBehavior.getName(), "还不知道传什么", "$_")
                );
                // 捕获异常
                ctBehavior.addCatch(
                        String.format("{xyz.dsvshx.peony.point.Point.complete(\"%s\", \"%s\", \"%s\", %s);"
                                        + "throw $e;}",
                                clazzname, ctBehavior.getName(), "还不知道传什么", "$e"),
                        ClassPool.getDefault().get("java.lang.Throwable")
                );
            }
            // ctClass.writeFile(
            //         "~/Documents/github/peony/peony-core/src/main/java/xyz/dsvshx/peony/core/instrumentation");
            return ctClass.toBytecode();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            e.printStackTrace();
            return classfileBuffer;
        }
    }

    @Deprecated
    private byte[] transformClass(String className, byte[] classfileBuffer) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            // 必须要有这个，否则会报point找不到，搞了大半天 https://my.oschina.net/xiaominmin/blog/3153685
            classPool.appendClassPath(spyJarPath);
            CtClass ctClass = classPool.get(className.replace("/", "."));
            // 所有函数和构造函数
            for (CtBehavior ctBehavior : ctClass.getDeclaredBehaviors()) {
                String methodId = CallRecord.getMethodId(className, ctBehavior.getName());

                CodeAttribute codeAttribute = ctBehavior.getMethodInfo().getCodeAttribute();
                // 获取方法的入参的名称
                LocalVariableAttribute attr =
                        (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                // 获取方法入参的类型
                CtClass[] parameterTypes = ctBehavior.getParameterTypes();

                // 定义属性
                ctBehavior.addLocalVariable("startNanos", CtClass.longType);
                // 方法前加强
                ctBehavior.insertBefore("{startNanos = System.nanoTime(); }");
                // 打点加到最后
                ctBehavior.insertAfter(
                        "{xyz.dsvshx.peony.point.Point.point(\"" + methodId
                                + "\", $args, startNanos, System"
                                + ".nanoTime(), $_);}");
                // 添加异常捕获，打点之后再throw
                // 这里存在着一定的瑕疵，startTime如何想传到catch里的话是不行的，超过他的作用域了。调换顺序也一直不行，所以直接写成-1了
                // 但是endTime是可以的，所以后续如果需要排序可以用endTime，效果是一样的。当然肯定有办法，但是我目前还没找到。
                // 又想到一个点，可以在开始的时候打一个点，返回和异常的时候也打一个点，这样的话解决的就比较好了，不过还是麻烦了点，后续在优化吧
                ctBehavior.addCatch(
                        "{xyz.dsvshx.peony.point.Point.point(\"" + methodId
                                + "\", $args, -1L, System.nanoTime(), $e);"
                                + " throw $e; };", ClassPool.getDefault().get("java.lang.Throwable"));
            }
            return ctClass.toBytecode();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            e.printStackTrace();
            return classfileBuffer;
        }
    }
}
