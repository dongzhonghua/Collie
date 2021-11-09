package xyz.dsvshx;

import java.lang.ref.WeakReference;

/**
 * @author dongzhonghua
 * Created on 2021-11-10
 */
public class ThreadLocalTest {

    public static void main(String[] args) throws InterruptedException {
        // System.out.println("--gc后--");
        // Thread t2 = new Thread(() -> test("def", true));
        // t2.start();
        // t2.join();
        // String str = new String("abc");
        // str = null;
        WeakReference<String> weakReference = new WeakReference<>(new String("abc"));
        System.gc();
        System.out.println("a");
    }
}
