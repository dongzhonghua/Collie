package xyz.dsvshx.peony;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * @author dongzhonghua
 * Created on 2021-04-11
 */
public class MyClassLoader extends ClassLoader {
    private String path;

    public MyClassLoader(String clazzPath) {
        this.path = clazzPath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> log = null;
        byte[] bytes = gatDate();
        if (bytes != null) {
            log = defineClass(name, bytes, 0, bytes.length);
        }

        return log;
    }

    private byte[] gatDate() {
        File file = new File(path);
        if (file.exists()) {
            FileInputStream inputStream = null;
            ByteArrayOutputStream outputStream = null;

            try {
                inputStream = new FileInputStream(file);
                outputStream = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, size);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                return outputStream.toByteArray();
            }
        }
        return null;
    }
}

