package Model.Utils;

import Model.Annotations.ConfigProperty;
import Model.Annotations.Inject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Injector {
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
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            String errMessage = String.format("Ошибка внедрения зависимости. Не удалось получить экземпляр %s.", c.getSimpleName());
            Logger.getGlobal().severe(errMessage);
            throw new RuntimeException(errMessage);
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
