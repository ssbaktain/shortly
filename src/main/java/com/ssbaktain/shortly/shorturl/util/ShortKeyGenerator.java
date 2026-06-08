package com.ssbaktain.shortly.shorturl.util;

import java.security.SecureRandom;

public class ShortKeyGenerator {

    private static final String CHARACTERS =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int KEY_LENGTH = 7;
    private static final SecureRandom RANDOM = new SecureRandom();

    private ShortKeyGenerator() {
    }

    public static String generate() {
        StringBuilder sb = new StringBuilder(KEY_LENGTH);
        for (int i = 0; i < KEY_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
