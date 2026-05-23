package com.ssbaktain.shortly.common.util;

public class Base62Encoder {

    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = CHARACTERS.length();

    private Base62Encoder() {
    }

    public static String encode(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number must be non-negative");
        }

        if (number == 0) {
            return String.valueOf(CHARACTERS.charAt(0));
        }

        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            result.insert(0, CHARACTERS.charAt(remainder));
            number /= BASE;
        }

        return result.toString();
    }

    public static long decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            throw new IllegalArgumentException("Encoded string must not be null or empty");
        }

        long result = 0;
        for (char c : encoded.toCharArray()) {
            int index = CHARACTERS.indexOf(c);
            if (index < 0) {
                throw new IllegalArgumentException("Invalid character in encoded string: " + c);
            }
            result = result * BASE + index;
        }

        return result;
    }
}
