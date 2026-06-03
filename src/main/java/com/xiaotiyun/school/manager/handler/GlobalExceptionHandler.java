package com.xiaotiyun.school.manager.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import java.nio.file.AccessDeniedException;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @Resource
    LanguageUtil languageUtil;
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    public Result<String> handleValidException(Exception e) {
        BindingResult bindingResult;
        if (e instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        } else {
            bindingResult = ((BindException) e).getBindingResult();
        }
        
//        StringBuilder message = new StringBuilder();
//        for (FieldError fieldError : bindingResult.getFieldErrors()) {
//            message.append(fieldError.getField())
//                   .append(": ")
//                   .append(fieldError.getDefaultMessage())
//                   .append(", ");
//        }
//        message.delete(message.length() - 2, message.length());
        
        log.warn("参数校验失败: ", e);
        FieldError fieldError = bindingResult.getFieldErrors().get(0);
        String message = languageUtil.getMessage(fieldError.getDefaultMessage());
        if(!message.equals(fieldError.getDefaultMessage()))
        {
            return Result.failed(ResultCode.VALIDATE_FAILED.getCode(),message);
        }
        log.debug("查询不到错误文案信息 code：{},message:{}",fieldError.getDefaultMessage(),message);
        //如果表中查询不到这个code码返回默认文案
        return Result.failed(ResultCode.SYSTEM_ERROR);
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<String> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限不足AccessDeniedException: {}", e.getMessage());
        return Result.failed(ResultCode.FORBIDDEN);
    }
    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public Result<String> handleNotPermissionException(NotPermissionException e) {
        log.warn("权限不足NotPermissionException: {}", e.getMessage());
        return Result.failed(ResultCode.FORBIDDEN);
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(NotRoleException.class)
    public Result<String> handleNotRoleException(NotRoleException e) {
        log.warn("无此角色NotRoleException: {}", e.getMessage());
        return Result.failed(ResultCode.FORBIDDEN);
    }



    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(e.getMessage()));
    }

    @ExceptionHandler(BusinessMessageException.class)
    public Result<String> handleBusinessMessageException(BusinessMessageException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.failed(ResultCode.FAILED.getCode(), e.getMessage());
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.SYSTEM_ERROR.getMessageCode()));
    }

    /**
     * 处理切面错误
     */
    @ExceptionHandler(Throwable.class)
    public Result<String> handleException(Throwable e) {
        log.error("系统异常，切面错误抛出", e);
        return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.SYSTEM_ERROR.getMessageCode()));
    }

    // Sa-Token未登录异常
    @ExceptionHandler(NotLoginException.class)
    public Result<?> handlerNotLoginException(NotLoginException nle) {
        log.warn(nle.getMessage());
        return Result.failed(ResultCode.UNAUTHORIZED);
    }

    /**
     * 处理MySQL唯一键约束异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<String> handleDuplicateKeyException(DuplicateKeyException e) {
        log.warn("数据库唯一键冲突: {}", e.getMessage());
        return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.DATA_EXIST.getMessageCode()));
    }

}