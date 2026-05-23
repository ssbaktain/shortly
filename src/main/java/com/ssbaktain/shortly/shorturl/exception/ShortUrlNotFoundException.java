package com.ssbaktain.shortly.shorturl.exception;

public class ShortUrlNotFoundException extends RuntimeException {

    public ShortUrlNotFoundException(String shortKey) {
        super("Short URL not found: " + shortKey);
    }
}
