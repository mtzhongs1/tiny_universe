package com.ailu.server.handler;

import com.ailu.exception.BaseException;
import com.ailu.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    //业务错误
    @ExceptionHandler(BaseException.class)
    public Result exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    //名字相同违反唯一性错误
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        //当唯一约束冲突时，返回用户名已存在
        if (message.contains("Duplicate entry")) {
            String username = message.split(" ")[2];
            String msg = username + "该用户名已存在";
            return Result.error(msg);
        } else {
            log.error("异常信息：{}", message);
            return Result.error(message);
        }
    }

    @ExceptionHandler
    public Result exceptionHandler(MethodArgumentNotValidException ex) {
        String message = ex.getMessage();
        log.error("参数校验错误：{}",message);
        return Result.error(message);
    }

}
