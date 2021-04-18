package xyz.dsvshx.peony.agent.config;

/**
 * @author dongzhonghua
 * Created on 2021-04-15
 */
public class AgentConfigHolder {
    private static String serverName;
    private static String[] agentPath;
    private static String packageName;

    public static void holdConfig(String args) {
        agentPath = args.split(",");
    }

    public static String getAgentCorePath() {
        return agentPath[0];
    }

    public static String getAgentSpyPath() {
        return agentPath[1];
    }

    public static String getPackageName() {
        return packageName;
    }

    public static String getServerName() {
        return serverName;
    }
}
