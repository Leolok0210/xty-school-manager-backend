package com.xiaotiyun.school.manager.basic.common;

import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import lombok.Data;

/**
 * 统一返回结果封装
 */
@Data
public class Result<T> {
    private int error;
    private String message;
    private T data;

    private Result() {}

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> successByMessage(String message) {
        Result<T> result = new Result<>();
        result.setError(ResultCode.SUCCESS.getCode());
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setError(ResultCode.SUCCESS.getCode());
        LanguageUtil languageUtil = SpringContextUtil.getBean(LanguageUtil.class);
        result.setMessage(languageUtil.getMessage(ResultCode.SUCCESS.getMessageCode()));
        result.setData(data);
        return result;
    }

    public static <T> Result<T> failed() {
        return failed(ResultCode.FAILED);
    }

    public static <T> Result<T> failed(ResultCode errorCode) {
        Result<T> result = new Result<>();
        LanguageUtil languageUtil = SpringContextUtil.getBean(LanguageUtil.class);
        result.setError(errorCode.getCode());
        result.setMessage(languageUtil.getMessage(errorCode.getMessageCode()));
        return result;
    }

    public static <T> Result<T> failed(int code, String message) {
        Result<T> result = new Result<>();
        result.setError(code);
        result.setMessage(message);
        return result;
    }
} 