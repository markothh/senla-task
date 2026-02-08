package model.utils.di;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TypeConverter {
    private static final Logger logger = LogManager.getLogger();
    private static final String CONVERT_ERROR_MSG = "Не удалось конвертировать значение. Не обработан тип";

    public static Object convert(String value, Field field, Class<?> type) {
        Class<?> targetType = type != Object.class ? type : field.getType();

        if (isPrimitiveOrWrapper(targetType)) {
            return convertSimple(targetType, value);
        }

        if (targetType.isArray()) {
            Class<?> componentType = targetType.getComponentType();
            String[] parts = value.split(",");

            Object array = Array.newInstance(componentType, parts.length);

            for (int i = 0; i < parts.length; i++) {
                Array.set(array, i, convertSimple(componentType, parts[i]));
            }
            return array;
        }

        if (List.class.isAssignableFrom(targetType)) {
            return convertCollection(field, value);
        }

        return value;
    }

    private static Object convertSimple(Class<?> targetType, String value) {
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        }
        if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(value);
        }
        if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (targetType == String.class) {
            return value;
        } else {
            logger.fatal(CONVERT_ERROR_MSG);
            throw new RuntimeException();
        }
    }

    private static Object convertCollection(Field field, String value) {

        ParameterizedType genericType =
                (ParameterizedType) field.getGenericType();

        Class<?> elementType =
                (Class<?>) genericType.getActualTypeArguments()[0];

        List<Object> list = new ArrayList<>();

        for (String part : value.split(",")) {
            list.add(convertSimple(elementType, part));
        }

        return list;
    }

    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() || WRAPPER_TYPES.contains(type);
    }

    private static final Set<Class<?>> WRAPPER_TYPES = Set.of(
            Boolean.class,
            Integer.class,
            Float.class,
            Double.class
    );
}
