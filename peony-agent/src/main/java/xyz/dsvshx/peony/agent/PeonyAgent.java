package xyz.dsvshx.peony.agent;

import java.lang.instrument.Instrumentation;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
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
                    byte[] bytes = javasistTest(clazz);
                    System.out.println(bytes);
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    // private static void transformClass(Class<?> clazz, Instrumentation instrumentation) {
    //     instrumentation.addTransformer(new ClassFileTransformer() {
    //         @Override
    //         public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
    //                 ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
    //
    //
    //             return new byte[0];
    //         }
    //     });
    //
    // }

    public static void agentmain(String agentOps, Instrumentation instrumentation) {
        try {

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static byte[] javasistTest(Class<?> clazz){
        ClassPool pool = ClassPool.getDefault();
        // 获取类
        CtClass ctClass = null;
        try {
            ctClass = pool.get(clazz.getName());
            CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
            for (CtMethod ctMethod : declaredMethods) {
                MethodInfo methodInfo = ctMethod.getMethodInfo();
                CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                CtClass[] parameterTypes = ctMethod.getParameterTypes();
                CtClass returnType = ctMethod.getReturnType();
                String returnTypeName = returnType.getName();

                // 定义属性
                ctMethod.addLocalVariable("startNanos", CtClass.longType);
                // 方法前加强
                ctMethod.insertBefore("{ startNanos = System.nanoTime(); }");
                // 定义属性
                ctMethod.addLocalVariable("parameterValues", pool.get(Object[].class.getName()));
                // 方法前加强

                // ctMethod.insertBefore("{ parameterValues = new Object[]{" + .toString() + "}; }");

            }
            String clazzName = ctClass.getName();
            return ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
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
