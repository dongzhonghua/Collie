package xyz.dsvshx;

import xyz.dsvshx.collie.javasist.CollieInterface;
import xyz.dsvshx.ioc.SummerApplication;
import xyz.dsvshx.ioc.annotation.Application;
import xyz.dsvshx.ioc.annotation.mvc.WebApplication;
import xyz.dsvshx.ioc.context.ApplicationContext;

/**
 * mvn package && java -javaagent:/Users/dongzhonghua03/Documents/github/collie/collie-agent/target/collie-agent-1
 * .0-SNAPSHOT-jar-with-dependencies.jar=server.name=collie -jar collie-test/target/collie-test-1.0-SNAPSHOT.jar
 *
 * @author dongzhonghua
 * Created on 2021-04-09
 */
@WebApplication
@Application(basepackage = "xyz.dsvshx")
public class CollieTestClient {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = SummerApplication.run(CollieTestClient.class, args);

        CollieTestClient collieTestClient = new CollieTestClient();
        System.out.println(collieTestClient.hello("dongzhonghua", 12));
        // System.out.println(collieTestClient.age(20));
        CollieInterface hi = new CollieInterface() {
            @Override
            public void hi() {
                System.out.println("hello, interface");
            }
        };
        hi.hi();
    }

    private String hello(String name, int age) throws Exception {
        Thread.sleep(1000);
        return "hello" + name;
    }

    private boolean age(int age) {
        try {
            Thread.sleep(3000);
            return age > 18;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
}
