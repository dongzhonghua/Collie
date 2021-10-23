package xyz.dsvshx.collie.core.aspect;

import java.util.ArrayList;
import java.util.List;

import xyz.dsvshx.collie.point.FrameworkPoint;

/**
 * 框架的入口和出口
 *
 * @author dongzhonghua
 * Created on 2021-05-24
 */
public final class FrameworkCallListener {

    private static List<FrameworkAspect> CONTEXT_LISTENERS;


    //
    public static void init(FrameworkAspect logAspect) {
        try {
            CONTEXT_LISTENERS = new ArrayList<>();
            CONTEXT_LISTENERS.add(logAspect);
            FrameworkPoint.CONTEXT_ENTRY = FrameworkCallListener.class.getMethod("entry");
            FrameworkPoint.CONTEXT_EXIT = FrameworkCallListener.class.getMethod("exit");
        } catch (NoSuchMethodException e) {
            //
        }
    }

    /**
     * 调用链路入口监听
     */
    public static void entry() {
        for (FrameworkAspect listener : CONTEXT_LISTENERS) {
            try {
                listener.entry();
            } catch (Throwable throwable) {
                //
            }
        }
    }

    /**
     * 调用链路出口监听
     */
    public static void exit() {
        for (FrameworkAspect listener : CONTEXT_LISTENERS) {
            try {
                listener.exit();
            } catch (Throwable throwable) {
                //
            }
        }
    }
}
