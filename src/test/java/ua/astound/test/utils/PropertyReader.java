package ua.astound.test.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum PropertyReader {
    INSTANCE;
    private final Properties properties;

    PropertyReader() {
        properties = new Properties();
        try {
            properties.load(new FileReader("application.properties"));
            properties.load(new FileReader("dev-data.properties"));
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public String getPropertyValue(String name) {
        return properties.getProperty(name);
    }
}
