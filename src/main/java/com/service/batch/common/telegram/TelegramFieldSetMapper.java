package com.service.batch.common.telegram;

import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.transform.FieldSet;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TelegramFieldSetMapper<T extends Telegram> implements FieldSetMapper<T> {

    private final Class<? extends T> targetType;
    private final List<Field> mappedFields = new ArrayList<>();

    public TelegramFieldSetMapper(Class<? extends T> targetType) {
        this.targetType = targetType;
        for (Field field : targetType.getDeclaredFields()) {
            if (field.getAnnotation(FixedLength.class) == null) {
                continue;
            }
            field.setAccessible(true);
            mappedFields.add(field);
        }
    }

    @Override
    public T mapFieldSet(FieldSet fieldSet) {
        try {
            T instance = targetType.getDeclaredConstructor().newInstance();

            for (Field field : mappedFields) {
                String rawValue = fieldSet.readString(field.getName());
                Object parsed = this.convertValue(rawValue, field.getType());
                field.set(instance, parsed);
            }

            return instance;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to map FieldSet to " + targetType.getSimpleName(), e);
        }
    }

    private Object convertValue(String rawValue, Class<?> propertyType) {
        if (rawValue == null) {
            return defaultValue(propertyType);
        }

        String value = rawValue.trim();
        if (value.isEmpty()) {
            return propertyType == String.class ? "" : defaultValue(propertyType);
        }

        if (propertyType == String.class) {
            return value;
        }
        if (propertyType == Integer.class || propertyType == int.class) {
            return Integer.parseInt(value);
        }
        if (propertyType == Long.class || propertyType == long.class) {
            return Long.parseLong(value);
        }
        if (propertyType == Double.class || propertyType == double.class) {
            return Double.parseDouble(value);
        }
        if (propertyType == Float.class || propertyType == float.class) {
            return Float.parseFloat(value);
        }
        if (propertyType == Boolean.class || propertyType == boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (propertyType == BigDecimal.class) {
            return new BigDecimal(value);
        }

        return value;
    }

    private Object defaultValue(Class<?> propertyType) {
        if (!propertyType.isPrimitive()) {
            return null;
        }

        if (propertyType == boolean.class) {
            return false;
        }
        if (propertyType == byte.class) {
            return (byte) 0;
        }
        if (propertyType == short.class) {
            return (short) 0;
        }
        if (propertyType == int.class) {
            return 0;
        }
        if (propertyType == long.class) {
            return 0L;
        }
        if (propertyType == float.class) {
            return 0F;
        }
        if (propertyType == double.class) {
            return 0D;
        }
        if (propertyType == char.class) {
            return '\0';
        }

        return null;
    }
}
