package com.xiaotiyun.school.manager.basic.exception;

public class BusinessMessageException extends RuntimeException {
    private final String message;

    public BusinessMessageException(String message) {
        super(message);
        this.message = message;
    }
}
