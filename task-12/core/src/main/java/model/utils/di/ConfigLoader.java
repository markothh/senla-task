package model.utils.di;

import model.annotations.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

public class ConfigLoader {
    private static final String GET_CONFIG_FILE_ERROR_MSG = "Не удалось получить файл конфигурации";

    public static void configure(Object target) {
        Class<?> c = target.getClass();

        for (Field field: c.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigProperty.class)) {
                continue;
            }

            ConfigProperty cp = field.getAnnotation(ConfigProperty.class);
            String fileName = cp.configFileName();
            String propertyName = resolvePropertyName(cp, c, field);

            Properties props = loadProperties(fileName);
            String rawValue = props.getProperty(propertyName);

            if (rawValue == null) {
                continue;
            }

            Object value = TypeConverter.convert(rawValue, field, cp.type());

            try {
                field.setAccessible(true);
                field.set(target, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String resolvePropertyName(ConfigProperty cp, Class<?> c, Field field) {
        if (!cp.propertyName().isEmpty()) {
            return cp.propertyName();
        }

        return c.getSimpleName() + "." + field.getName();
    }

    private static Properties loadProperties(String fileName) {
        try (InputStream config = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (config == null) {
                throw new RuntimeException(GET_CONFIG_FILE_ERROR_MSG);
            }

            Properties props = new Properties();
            props.load(config);
            return props;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
