package com.ssbaktain.shortly.shorturl.exception;

public class ShortUrlPasswordMismatchException extends RuntimeException {

    public ShortUrlPasswordMismatchException(String shortKey) {
        super("Invalid password for short URL: " + shortKey);
    }
}
