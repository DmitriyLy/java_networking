package console_chat.common;

import java.io.IOException;
import java.util.Properties;

public class Utils {

    public static int getPortNumber() {
        return Integer.parseInt(loadProperties().getProperty("main.port"));
    }

    public static int getServerBacklogSize() {
        return Integer.parseInt(loadProperties().getProperty("server.backlog.size"));
    }

    private static Properties loadProperties() {
        try (var input = Utils.class.getClassLoader().getResourceAsStream("chat_configuration.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
