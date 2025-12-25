package com.service.batch.common.telegram;

import org.springframework.batch.infrastructure.item.file.transform.DefaultFieldSet;
import org.springframework.batch.infrastructure.item.file.transform.FieldSet;
import org.springframework.batch.infrastructure.item.file.transform.LineTokenizer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TelegramFixedLengthTokenizer<T> implements LineTokenizer {

    private final Class<T> clazz;
    private final List<Field> fieldList = new ArrayList<>();
    private final List<Integer> lengthList = new ArrayList<>();

    public TelegramFixedLengthTokenizer(Class<T> clazz) {
        this.clazz = clazz;
        this.init();
    }

    private void init() {
        for (Field field : clazz.getDeclaredFields()) {
            FixedLength fixedLength = field.getAnnotation(FixedLength.class);
            if (fixedLength != null) {
                field.setAccessible(true);
                fieldList.add(field);
                lengthList.add(fixedLength.length());
            }
        }
    }

    @Override
    public FieldSet tokenize(String line) {
        var tokens = new ArrayList<>();

        int pos = 0;
        for (int length : lengthList) {
            int end = Math.min(pos + length, line.length());
            tokens.add(line.substring(pos, end));
            pos = end;
        }

        var names = fieldList.stream()
                .map(Field::getName)
                .toArray(String[]::new);

        @SuppressWarnings("SuspiciousToArrayCall")
        var array = tokens.toArray(String[]::new);

        return new DefaultFieldSet(array, names);
    }
}
