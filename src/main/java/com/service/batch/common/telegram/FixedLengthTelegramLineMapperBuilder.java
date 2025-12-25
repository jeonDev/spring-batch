package com.service.batch.common.telegram;

import org.springframework.batch.infrastructure.item.file.LineMapper;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.LineTokenizer;

import java.util.HashMap;
import java.util.Map;

public class FixedLengthTelegramLineMapperBuilder<T extends Telegram> {

    private final Map<String, Class<? extends T>> patternType = new HashMap<>();

    public <E extends T> FixedLengthTelegramLineMapperBuilder<T> patternType(String pattern, Class<E> clazz) {
        patternType.put(pattern, clazz);
        return this;
    }

    public LineMapper<T> build() {
        var lineTokenizer = new HashMap<String, LineTokenizer>();
        var fieldSetMapper = new HashMap<String, FieldSetMapper<T>>();

        patternType.forEach((key, value) -> {
            lineTokenizer.put(key, new TelegramFixedLengthTokenizer<>(value));
            fieldSetMapper.put(key, new TelegramFieldSetMapper<>(value));
        });

        return new PatternMatchingCompositeLineMapper<>(
                lineTokenizer,
                fieldSetMapper
        );
    }
}
