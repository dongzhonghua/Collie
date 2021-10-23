package xyz.dsvshx.collie.point;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 由于类加载器和应用隔离的需求，现在只能将这两个类放在单独的jar包里，当然这种方式非常的不优雅，先实现功能吧，后续这个怎么改需要好好想一下。
 *
 * @author dongzhonghua
 * Created on 2021-04-11
 */
public class Point {

    // 这两个在什么时候赋值呢？
    public static Method BEFORE_METHOD;
    public static Method COMPLETE_METHOD;

    static {
        System.out.println("----------------Point class loader is " + Point.class.getClassLoader());
    }

    public static void before(String className, String methodName, String descriptor, Object[] params) {
        if (BEFORE_METHOD != null) {
            try {
                BEFORE_METHOD.invoke(null, className, methodName, descriptor, params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void complete(String className, String methodName, String descriptor, Object returnValueOrThrowable) {
        if (COMPLETE_METHOD != null) {
            try {
                COMPLETE_METHOD.invoke(null, className, methodName, descriptor, returnValueOrThrowable);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 最初的实现方案，这种可以实现，也比较直观方便，但是如果采用启动类加载器加载这个包而且还不想污染应用，那么这个地方如果
     * 想依赖比如es之类的，那么就和我们的初衷相悖了。所以这个方式还是废弃了，虽然用反射的方法性能会损失，但是实现起来还是比
     * 较优雅的。
     */
    @Deprecated
    public static void point(String methodId, Object[] params, long startTime, long endTime, Object result) {
        try {
            if (result == null) {
                System.out.println("return void");
                result = "void";
            }
            // 从threadLocal获取traceId
            System.out.printf("------------------------\n"
                            + "methodId:%s,\nparams:%s,\nstartTime:%d,\nendTime:%d,\ncost:%d,\nreturn:%s\n", methodId,
                    Arrays.toString(Arrays.stream(params).toArray()), startTime, endTime, endTime - startTime, result);
        } catch (Exception e) {
            System.out.println("point打点出错：" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Deprecated
    public static void point(String methodId, Object[] params, long startTime, long endTime,
            Throwable throwable) {
    }
}
