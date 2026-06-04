package com.ssbaktain.shortly.common.exception;

import com.ssbaktain.shortly.shorturl.exception.ShortUrlExpiredException;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlNotFoundException;
import com.ssbaktain.shortly.member.exception.MemberNotFoundException;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlPasswordMismatchException;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlPasswordRequiredException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShortUrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerShortUrlNotFound(
            ShortUrlNotFoundException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerMemberNotFound(
            MemberNotFoundException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ShortUrlExpiredException.class)
    public ResponseEntity<ErrorResponse> handlerShortUrlExpired(
            ShortUrlExpiredException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.GONE.value(),
                "Gone",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.GONE).body(response);
    }

    @ExceptionHandler(ShortUrlPasswordRequiredException.class)
    public ResponseEntity<ErrorResponse> handlerShortUrlPasswordRequired(
            ShortUrlPasswordRequiredException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ShortUrlPasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlerShortUrlPasswordMismatch(
            ShortUrlPasswordMismatchException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handlerIllegalArgument(
            IllegalArgumentException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed");

        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handlerMethodNotAllowed(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Method Not Allowed",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handlerDataIntegrityViolation(
            DataIntegrityViolationException e, HttpServletRequest request) {
        log.warn("DB constraint violation at {}: {}", request.getRequestURI(), e.getMessage());
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                "Resource conflict occurred",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlerGenericException(
            Exception e, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), e.getMessage(), e);
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
