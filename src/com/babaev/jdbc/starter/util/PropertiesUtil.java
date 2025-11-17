package com.babaev.jdbc.starter.util;

import com.babaev.jdbc.starter.exception.DaoException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {
    private PropertiesUtil() {
    }

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    public static String getPropertyValue(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        try (InputStream inputStream = PropertiesUtil
                .class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        }catch (IOException e){
            throw new DaoException(e);
        }
    }
}
