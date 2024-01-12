package net.xolt.freecam.test.util;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;

public class TestUtils {
    /**
     * Get the value of an object's field using reflection.
     *
     * @param type The field type
     * @param instance The object from which to get the field
     * @param field The name of the field
     * @param <T> The field type
     * @return the field's value
     * @throws RuntimeException if anything goes wrong
     */
    public static <T> T getFieldValue(Class<T> type, Object instance, String field) {
        try {
            Class<?> instanceType = instance.getClass();
            Field declaredField = instanceType.getDeclaredField(field);
            declaredField.setAccessible(true);
            Object it = declaredField.get(instance);
            return type.cast(it);
        } catch (NoSuchFieldException
               | IllegalAccessException
               | InaccessibleObjectException
               | SecurityException
               | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }
}
