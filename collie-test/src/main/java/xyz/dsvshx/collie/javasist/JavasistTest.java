package xyz.dsvshx.collie.javasist;

import java.util.Arrays;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;

/**
 * 测试类，主要逻辑移植到了MyTransformer
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
                String methodId = "";
                // 定义属性
                ctMethod.addLocalVariable("startNanos", CtClass.longType);
                // 方法前加强
                ctMethod.insertBefore("{startNanos = System.nanoTime(); }");
                // 打点加到最后
                // ctMethod.insertAfter(
                //         "{xyz.dsvshx.collie.agent.instrumentation.Point.point(\"" + methodId + "\", $args, startNanos, System"
                //                 + ".nanoTime(), $_);};");
                // 添加异常捕获，打点之后再throw
                // 这里存在着一定的瑕疵，startTime如何想传到catch里的话是不行的，超过他的作用域了。调换顺序也一直不行，所以直接写成-1了
                // 但是endTime是可以的，所以后续如果需要排序可以用endTime，效果是一样的。当然肯定有办法，但是我目前还没找到。
                // ctMethod.addCatch(
                //         "{xyz.dsvshx.collie.instrumentation.Point.point(\"" + methodId
                //                 + "\", $args, -1L, System.nanoTime(), $e);"
                //                 + " throw $e; };", ClassPool.getDefault().get("java.lang.Throwable"));

                CodeAttribute codeAttribute = ctMethod.getMethodInfo().getCodeAttribute();
                // 获取方法的入参的名称
                LocalVariableAttribute attr =
                        (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                // 获取方法入参的类型
                CtClass[] parameterTypes = ctMethod.getParameterTypes();

                System.out.println(attr.variableName(1));
                System.out.println(Arrays.toString(parameterTypes));

            }
            // MyClassLoader myClassLoader = new MyClassLoader(
            //         "/Users/dongzhonghua03/Documents/github/collie/collie-test/src/main/java/xyz/dsvshx/collie");
            // // 加载之后的类，这里需要用一个别的类加载器，否则会报重复的错误，因为一个类加载器只能加载一个名字相同的类。
            // Class<?> aClass = ctClass.toClass(myClassLoader);
            // //获取修改之后的class文件
            // ctClass.writeFile("/Users/dongzhonghua03/Documents/github/collie/collie-test/src/test/java");
            // Object obj = aClass.newInstance();
            // myClassLoader.loadClass("xyz.dsvshx.collie.agent.instrumentation.Point");
            // aClass.getDeclaredMethod("getName", MyClassLoader.class, boolean.class).invoke(obj, new MyClassLoader(""),
            //         true);
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
