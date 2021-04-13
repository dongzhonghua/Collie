package xyz.dsvshx.peony.javasist;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

/**
 * @author dongzhonghua
 * Created on 2021-04-10
 */
public class JavasistTest {
    public static byte[] javasistTest(Class<?> clazz) {
        ClassPool pool = ClassPool.getDefault();
        // 获取类
        CtClass ctClass = null;
        try {
            ctClass = pool.get(clazz.getName());
            CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
            for (CtMethod ctMethod : declaredMethods) {
                MethodInfo methodInfo = ctMethod.getMethodInfo();
                CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                LocalVariableAttribute attr =
                        (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                CtClass[] parameterTypes = ctMethod.getParameterTypes();
                CtClass returnType = ctMethod.getReturnType();
                String returnTypeName = returnType.getName();


                // 定义属性
                ctMethod.addLocalVariable("startNanos", CtClass.longType);
                ctMethod.addLocalVariable("endNanos", CtClass.longType);
                ctMethod.insertBefore("{ startNanos = System.nanoTime(); }");
                // 方法前加强
                ctMethod.insertAfter("{endNanos = System.nanoTime(); }");
                ctMethod.addCatch("{xyz.dsvshx.peony.instrumentation.Point.point(0L, 0L, $e);"
                                + " throw $e; };",
                        ClassPool.getDefault().get("java.lang.Throwable"));


                // 定义属性
                ctMethod.addLocalVariable("parameterValues", pool.get(Object[].class.getName()));
                // 方法前加强



            }
            String clazzName = ctClass.getName();
            // 加载之后的类，这里需要用一个别的类加载器，否则会报重复的错误，因为一个类加载器只能加载一个名字相同的类。
            Class<?> aClass = ctClass.toClass(JavasistTest.class.getClassLoader().getParent());
            //获取修改之后的class文件
            ctClass.writeFile("/Users/dongzhonghua03/Documents/github/peony/peony-test/src/test");
            // 获取字节码
            return ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }


    public static void main(String[] args) throws Exception {
        byte[] bytes = javasistTest(Hello.class);
        // System.out.println(new String(bytes));

    }
}
