package org.sepehr.jblockchain;

import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties {

    private static final ApplicationProperties APPLICATION_PROPERTIES =
            new ApplicationProperties();

    private Properties properties;

    private ApplicationProperties() {
        try {
            properties = new Properties();
            properties.load(Main.class.getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ApplicationProperties getInstance() {
        return APPLICATION_PROPERTIES;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

}
