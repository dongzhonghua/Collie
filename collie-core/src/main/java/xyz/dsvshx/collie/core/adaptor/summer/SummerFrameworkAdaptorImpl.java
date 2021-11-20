package xyz.dsvshx.collie.core.adaptor.summer;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import xyz.dsvshx.collie.core.adaptor.ClassAdaptor;

/**
 * 改造summer，注入traceId
 *
 * @author dongzhonghua
 * Created on 2021-05-26
 */
public class SummerFrameworkAdaptorImpl extends ClassAdaptor {
    @Override
    public  byte[] modifyClass(String className, byte[] classBytes, String spyJarPath) {
        try {
            if (SUMMER_ADAPTOR_CLASS.equals(className)) {
                ClassPool classPool = ClassPool.getDefault();
                classPool.appendClassPath(spyJarPath);
                String clazzname = className.replace("/", ".");
                CtClass ctClass = classPool.get(clazzname);
                CtMethod doHandlerMethod = ctClass.getDeclaredMethod("doHandler");
                // 没想到这么简单就成了？
                doHandlerMethod.insertBefore("{"
                        + "String traceId = fullHttpRequest.headers().get(\"collie-trace-id\");"
                        + "String parentSpanId = fullHttpRequest.headers().get(\"collie-span-id\");"
                        + "xyz.dsvshx.collie.point.SpyAPI.atFrameworkEnter(traceId, \"\", parentSpanId);"
                        + "}");
                doHandlerMethod.insertAfter("{"
                        + "String traceId = fullHttpRequest.headers().get(\"collie-trace-id\");"
                        + "String parentSpanId = fullHttpRequest.headers().get(\"collie-span-id\");"
                        + "xyz.dsvshx.collie.point.SpyAPI.atFrameworkExit(traceId + \"|\" + parentSpanId);"
                        + "}");
                ctClass.writeFile(
                        "/Users/dongzhonghua03/Documents/github/collie/collie-core/src/main/java/xyz/dsvshx/collie/core"
                                + "/instrumentation");
                return ctClass.toBytecode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classBytes;
    }
}
