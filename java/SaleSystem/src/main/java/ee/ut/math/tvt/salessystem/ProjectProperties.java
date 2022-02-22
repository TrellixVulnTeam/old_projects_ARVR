package ee.ut.math.tvt.salessystem;

import java.io.IOException;
import java.util.Properties;

public class ProjectProperties {

    private ProjectProperties() {
        throw new IllegalStateException("Utility class");
    }

    public static Properties get() {
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return properties;
    }
}
