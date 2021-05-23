package xyz.dsvshx;

import xyz.dsvshx.peony.javasist.PeonyInterface;

/**
 * mvn package && java -javaagent:/Users/dongzhonghua03/Documents/github/peony/peony-agent/target/peony-agent-1
 * .0-SNAPSHOT-jar-with-dependencies.jar=server.name=peony -jar peony-test/target/peony-test-1.0-SNAPSHOT.jar
 *
 * @author dongzhonghua
 * Created on 2021-04-09
 */
public class PeonyTestClient {

    public PeonyTestClient() {
    }

    public static void main(String[] args) throws Exception {
        System.out.println("main方法启动");
        PeonyTestClient peonyTestClient = new PeonyTestClient();
        System.out.println(peonyTestClient.hello("dongzhonghua", 12));
        // System.out.println(peonyTestClient.age(20));
        PeonyInterface hi = new PeonyInterface() {
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
