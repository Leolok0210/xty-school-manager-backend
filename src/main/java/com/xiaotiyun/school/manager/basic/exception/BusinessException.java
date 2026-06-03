package com.xiaotiyun.school.manager.basic.exception;

import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final String message;
    
    public BusinessException(String message) {
        super(message);
        this.message = message;
    }
}