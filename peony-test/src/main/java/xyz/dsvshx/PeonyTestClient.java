package xyz.dsvshx;

/**
 * mvn package && java -javaagent:/Users/dongzhonghua03/Documents/github/peony/peony-agent/target/peony-agent-1
 * .0-SNAPSHOT-jar-with-dependencies.jar=server.name=peony -jar peony-test/target/peony-test-1.0-SNAPSHOT.jar
 *
 * @author dongzhonghua
 * Created on 2021-04-09
 */
public class PeonyTestClient {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("main方法启动");
        System.out.println(PeonyTestClient.class.getClassLoader());
        // Thread.sleep(3000);
    }
}
