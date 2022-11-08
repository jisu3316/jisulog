package com.jisulog.api.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class JisulogException extends RuntimeException {

    public final Map<String, String> validation = new HashMap<>();

    public JisulogException(String message) {
        super(message);
    }

    public JisulogException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }
}
