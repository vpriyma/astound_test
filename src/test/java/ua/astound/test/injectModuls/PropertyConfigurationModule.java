package ua.astound.test.injectModuls;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("application.properties"));
            properties.load(new FileReader("dev-data.properties"));
            Names.bindProperties(binder(), properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
