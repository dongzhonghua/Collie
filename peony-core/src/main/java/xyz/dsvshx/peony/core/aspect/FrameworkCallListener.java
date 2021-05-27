package xyz.dsvshx.peony.core.aspect;

import java.util.ArrayList;
import java.util.List;

import xyz.dsvshx.peony.point.FrameworkPoint;

/**
 * 框架的入口和出口
 *
 * @author dongzhonghua
 * Created on 2021-05-24
 */
public final class FrameworkCallListener {

    private final static List<FrameworkAspect> CONTEXT_LISTENERS;

    static {
        CONTEXT_LISTENERS = new ArrayList<>();
        CONTEXT_LISTENERS.add(new LogAspectImpl());
    }

    //
    public static void init() {
        try {
            FrameworkPoint.CONTEXT_ENTRY = FrameworkAspect.class.getMethod("entry");
            FrameworkPoint.CONTEXT_EXIT = FrameworkAspect.class.getMethod("exit");
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
