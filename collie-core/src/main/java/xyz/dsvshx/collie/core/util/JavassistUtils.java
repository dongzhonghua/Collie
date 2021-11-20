package xyz.dsvshx.collie.core.util;

import javassist.CtBehavior;
import javassist.Modifier;

/**
 * @author dongzhonghua
 * Created on 2021-11-13
 */
public class JavassistUtils {
    public static boolean isNative(CtBehavior method) {
        return Modifier.isNative(method.getModifiers());
    }

    public static boolean isAbstract(CtBehavior method) {
        return Modifier.isAbstract(method.getModifiers());
    }

    public static boolean isStatic(CtBehavior method) {
        return Modifier.isStatic(method.getModifiers());
    }
}
