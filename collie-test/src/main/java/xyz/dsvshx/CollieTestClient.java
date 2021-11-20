package xyz.dsvshx;

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
        // System.out.println("====================123");
        ApplicationContext applicationContext = SummerApplication.run(CollieTestClient.class, args);
        // System.out.println("====================456");
        //
        //
        // CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        // HttpGet httpGet = new HttpGet("http://localhost:8088/hello");
        //
        // CloseableHttpResponse response = closeableHttpClient.execute(httpGet);
        // System.out.println(response);
        // System.out.println("====================789");
        System.out.println(new CollieTestClient().hello("dzh", 16));

    }

    private String hello(String name, int age) throws Exception {
        Thread.sleep(1000);
        boolean b = age(age);
        return "hello" + name + "，是否成年：" + b;
    }

    private boolean age(int age) {
        return age > 18;
    }
}
