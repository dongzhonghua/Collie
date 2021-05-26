package xyz.dsvshx.peony.core.adaptor.summer;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import xyz.dsvshx.peony.core.adaptor.FrameworkAdaptor;

/**
 * 改造summer，注入traceId
 *
 * @author dongzhonghua
 * Created on 2021-05-26
 */
public class SummerFrameworkAdaptorImpl implements FrameworkAdaptor {
    @Override
    public byte[] modifyClass(ClassLoader loader, String className, byte[] classBytes, String spyJarPath) {
        try {
            if (className.equals("xyz/dsvshx/ioc/mvc/RequestHandler")) {
                ClassPool classPool = ClassPool.getDefault();
                classPool.appendClassPath(spyJarPath);
                String clazzname = className.replace("/", ".");
                CtClass ctClass = classPool.get(clazzname);
                CtMethod doHandlerMethod = ctClass.getDeclaredMethod("doHandler");
                // 没想到这么简单就成了？
                doHandlerMethod.insertBefore("{"
                        + "String traceId = fullHttpRequest.headers().get(\"peony-trace-id\");"
                        + "String parentSpanId = fullHttpRequest.headers().get(\"peony-span-id\");"
                        + "xyz.dsvshx.peony.point.FrameworkPoint.enter(traceId, \"\", parentSpanId);"
                        + "}");
                doHandlerMethod.insertAfter("{"
                        + "xyz.dsvshx.peony.point.FrameworkPoint.exit();"
                        + "}");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
