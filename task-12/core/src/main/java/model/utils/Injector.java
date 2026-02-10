package model.utils;

import model.annotations.ConfigProperty;
import model.annotations.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class Injector {
    private static final Logger logger = LogManager.getLogger();
    private static final Set<Object> initialized = new HashSet<>();

    public static void inject(Object target) {
        if (target == null) {
            return;
        }
        if (initialized.contains(target)) {
            return;
        }

        Class<?> c = target.getClass();
        while (c != Object.class) {
            for (Field field: c.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Inject.class)) {
                    continue;
                }
                initialized.add(target);

                try {
                    Object dependency = getDependency(field.getType());
                    field.setAccessible(true);
                    field.set(target, dependency);

                    inject(dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            c = c.getSuperclass();
        }

        if (hasConfigProperties(target)) {
            ConfigLoader.configure(target);
        }
    }

    private static Object getDependency(Class<?> c) {
        try {
            Method getInstanceMethod = c.getDeclaredMethod("getInstance");
            return getInstanceMethod.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.fatal("Ошибка внедрения зависимости. Не удалось получить экземпляр {}.", c.getSimpleName());
            throw new RuntimeException();
        }
    }

    private static boolean hasConfigProperties(Object target) {
        if (target == null) return false;

        Class<?> c = target.getClass();

        while (c != Object.class) {
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigProperty.class)) {
                    return true;
                }
            }
            c = c.getSuperclass();
        }
        return false;
    }
}
