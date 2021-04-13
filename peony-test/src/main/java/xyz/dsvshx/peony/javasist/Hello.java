package xyz.dsvshx.peony.javasist;


/**
 * @author dongzhonghua
 * Created on 2021-04-10
 */
public class Hello {

    public String getName(String name) {
        System.out.println(name);
        return name + " + agent";
    }


    // public String getNames(String name) {
    //     long startNanos = 0;
    //     long endNanos = 0;
    //     try {
    //         startNanos = System.nanoTime();
    //         System.out.println(name);
    //         endNanos = System.nanoTime();
    //         return name;
    //     } catch (Exception e) {
    //         Point.point(startNanos, endNanos, e);
    //         throw e;
    //     }
    // }

}
