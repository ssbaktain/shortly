package com.ssbaktain.shortly.shorturl.exception;

public class ShortUrlPasswordRequiredException extends RuntimeException {

    public ShortUrlPasswordRequiredException(String shortKey) {
        super("Password required for short URL: " + shortKey);
    }
}
