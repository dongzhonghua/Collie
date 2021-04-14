package xyz.dsvshx.peony.agent.instrumentation;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

/**
 * @author dongzhonghua
 * Created on 2021-04-14
 */
public class MyTransformer implements ClassFileTransformer {

    // 被处理的方法列表
    final static Map<String, List<String>> methodMap = new HashMap<String, List<String>>();

    public MyTransformer() {
        // add("xyz.dsvshx");
        // add("");
    }

    private void add(String methodString) {
        String className = methodString.substring(0, methodString.lastIndexOf("."));
        String methodName = methodString.substring(methodString.lastIndexOf(".") + 1);
        List<String> list = methodMap.computeIfAbsent(className, k -> new ArrayList<>());
        list.add(methodName);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // 一定要排除
        if (className.startsWith("sun/") || className.startsWith("java/") || className.startsWith("jdk/")) {
            return classfileBuffer;
        }
        // 把自己排除掉
        if (className.startsWith("xyz/dsvshx/peony/agent")) {
            return classfileBuffer;
        }
        // 排除调动态代理类
        if (className.contains("CGLIB")) {
            return classfileBuffer;
        }
        // 适配一些第三方框架
        // TODO: 2021/4/14  

        System.out.println("className:" + className);
        try {
            ClassPool classPool = new ClassPool();
            classPool.insertClassPath(new LoaderClassPath(loader));
            CtClass ctClass = classPool.get(className.replace("/", "."));
            //                CtMethod ctMethod= ctClass.getDeclaredMethod("run");
            CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
            for (CtMethod ctMethod : declaredMethods) {
                String methodId = CallingChain.getMethodId(className, ctMethod.getName());
                // 定义属性
                ctMethod.addLocalVariable("startNanos", CtClass.longType);
                // 方法前加强
                ctMethod.insertBefore("{startNanos = System.nanoTime(); }");
                // 打点加到最后
                ctMethod.insertAfter(
                        "{xyz.dsvshx.peony.agent.instrumentation.Point.point(\"" + methodId
                                + "\", $args, startNanos, System"
                                + ".nanoTime(), $_);};");
                // 添加异常捕获，打点之后再throw
                // 这里存在着一定的瑕疵，startTime如何想传到catch里的话是不行的，超过他的作用域了。调换顺序也一直不行，所以直接写成-1了
                // 但是endTime是可以的，所以后续如果需要排序可以用endTime，效果是一样的。当然肯定有办法，但是我目前还没找到。
                ctMethod.addCatch(
                        "{xyz.dsvshx.peony.agent.instrumentation.Point.point(\"" + methodId
                                + "\", $args, -1L, System.nanoTime(), $e);"
                                + " throw $e; };", ClassPool.getDefault().get("java.lang.Throwable"));
            }
            return ctClass.toBytecode();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
