package xyz.dsvshx.peony.point;

import java.util.Arrays;

/**
 * 由于类加载器和应用隔离的需求，现在只能将这两个类放在单独的jar包里，当然这种方式非常的不优雅，先实现功能吧，后续这个怎么改需要好好想一下。
 *
 * @author dongzhonghua
 * Created on 2021-04-11
 */
public class Point {
    static {
        System.out.println("Point class loader is " + Point.class.getClassLoader());
    }

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


    public static void point(String methodId, Object[] params, long startTime, long endTime,
            Throwable throwable) {
    }
}
