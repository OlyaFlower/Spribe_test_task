package org.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = Configuration.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.warn("Unable to find config.properties, using default values");
            } else {
                properties.load(input);
                logger.info("Configuration loaded successfully");
            }
        } catch (IOException ex) {
            logger.error("Error loading configuration", ex);
        }
    }

    public static String getBaseUrl() {
        return properties.getProperty("base.url");
    }
}
