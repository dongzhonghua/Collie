package xyz.dsvshx.peony.agent.loader;

import java.net.URL;
import java.net.URLClassLoader;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dongzhonghua
 * Created on 2021-04-15
 */
@Slf4j
public class PeonyClassLoader extends URLClassLoader {
    public PeonyClassLoader(URL[] urls) {
        super(urls, getSystemClassLoader().getParent());
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        final Class<?> loadedC = findLoadedClass(name);
        if (loadedC != null) {
            return loadedC;
        }
        // 优先从parent（SystemClassLoader）里加载系统类，避免抛出ClassNotFoundException
        if (name != null && (name.startsWith("sun.") || name.startsWith("java."))) {
            return super.loadClass(name, resolve);
        }
        if (name != null && name.contains("xyz.dsvshx.peony.point")) {
            return super.loadClass(name, resolve);
        }
        try {
            Class<?> loadedClass = findClass(name);
            if (loadedClass != null) {
                if (resolve) {
                    resolveClass(loadedClass);
                }
                log.info(">>>>>>>>>>>>使用PeonyClassloader加载类：" + name);
                return loadedClass;
            }
        } catch (ClassNotFoundException ignored) {
        }
        return super.loadClass(name, resolve);
    }
}
