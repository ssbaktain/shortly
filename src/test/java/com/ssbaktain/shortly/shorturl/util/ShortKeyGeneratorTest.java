package com.ssbaktain.shortly.shorturl.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShortKeyGeneratorTest {

    @Test
    @DisplayName("generate()는 정확히 7자 길이의 키를 반환한다")
    void generate_returns_seven_character_key() {
        String key = ShortKeyGenerator.generate();

        assertEquals(7, key.length());
    }

    @Test
    @DisplayName("generate()는 Base62 알파벳(0-9a-zA-Z만 포함한다")
    void generate_contains_only_base62_characters() {
        String key = ShortKeyGenerator.generate();

        assertTrue(key.matches("^[0-9a-zA-Z]{7}$"),
                "키가 Base62 알파벳 외 문자 포함: " + key);
    }

    @Test
    @DisplayName("generate()는 1000번 호출해도 중복 키를 만들지 않는다")
    void generate_produces_unique_keys() {
        Set<String> keys = new HashSet<>();
        int iterations = 1000;

        for (int i = 0; i < iterations; i++) {
            keys.add(ShortKeyGenerator.generate());
        }

        assertEquals(iterations, keys.size(),
                "중복 키 발생: 생성 " + iterations + " 중 unique " + keys.size());
    }
}
