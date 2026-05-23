package com.ssbaktain.shortly.shorturl.exception;

public class ShortUrlExpiredException extends RuntimeException {

    public ShortUrlExpiredException(String shortKey) {
        super("Short URL has expired: " + shortKey);
    }
}
