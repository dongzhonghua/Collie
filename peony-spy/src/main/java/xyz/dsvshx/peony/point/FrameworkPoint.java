package xyz.dsvshx.peony.point;

import java.util.Arrays;

/**
 * @author dongzhonghua
 * Created on 2021-04-11
 */
public class FrameworkPoint {
    public static void point(String methodId, Object[] params, long startTime, long endTime, Object result) {
        try {
            if (result == null) {
                System.out.println("return void");
                result = "void";
            }
            System.out.printf("methodId:%s, params:%s, startTime:%d, endTime:%d, result:%s%n", methodId,
                    Arrays.toString(Arrays.stream(params).toArray()), startTime, endTime, result.toString());
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }


    public static void point(String methodId, Object[] params, long startTime, long endTime,
            Throwable throwable) {
    }
}
