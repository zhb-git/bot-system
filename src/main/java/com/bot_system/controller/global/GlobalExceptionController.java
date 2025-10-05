package com.bot_system.controller.global;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import com.bot_system.exception.BizException;
import com.bot_system.exception.BotException;
import com.bot_system.model.response.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * @className: SysAdmin
 * @author: Java之父
 * @date: 2025/10/5 18:53
 * @version: 1.0.0
 * @description: 全局异常处理器
 * 统一捕获并返回规范的响应格式
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

    /**
     * 处理表单参数校验异常（@Valid用于表单对象）
     *
     * @param e BindException
     * @return 响应
     */
    @ExceptionHandler(BindException.class)
    R<String> bindExceptionHandler(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + "：" + err.getDefaultMessage())
                .reduce((a, b) -> a + "；" + b)
                .orElse("参数输入有误");
        return R.fail(msg);
    }

    /**
     * 处理JSON参数校验异常（@RequestBody + @Valid）
     *
     * @param e MethodArgumentNotValidException
     * @return 响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    R<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + "：" + err.getDefaultMessage())
                .reduce((a, b) -> a + "；" + b)
                .orElse("参数输入有误");
        return R.fail(msg);
    }

    /**
     * 处理单个参数校验异常（@Validated用于控制器方法参数）
     *
     * @param e ConstraintViolationException
     * @return 响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    R<String> constraintViolationExceptionHandler(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("；"));
        return R.fail(msg);
    }

    /**
     * 处理文件上传大小超限
     *
     * @param e MaxUploadSizeExceededException
     * @return 响应
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    R<String> maxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException e) {
        log.error("上传文件过大：{}", e.getMessage());
        return R.fail("上传文件大小超出限制");
    }

    /**
     * 处理资源未找到异常（静态资源）
     *
     * @return 响应
     */
    @ExceptionHandler(NoResourceFoundException.class)
    R<String> noResourceFoundExceptionHandler(HttpServletRequest request) {
        log.error("请求资源不存在：{}", request.getRequestURI());
        return R.fail("资源不存在");
    }

    /**
     * 处理请求地址不存在异常（路由404）
     *
     * @param request HttpServletRequest
     * @return 响应
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    R<String> noHandlerFoundExceptionHanler(HttpServletRequest request) {
        log.error("请求地址不存在：{}", request.getRequestURI());
        return R.fail("请求地址不存在");
    }

    /**
     * 处理未登录异常
     *
     * @return 响应
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    R<String> notLoginExceptionHandler() {
        return R.notLogin();
    }

    /**
     * 处理无权限异常
     *
     * @return 响应
     */
    @ExceptionHandler(NotRoleException.class)
    R<String> notRoleExceptionHandler() {
        return R.fail("权限不足");
    }

    /**
     * 处理业务执行异常
     *
     * @param e 异常
     * @return 响应
     */
    @ExceptionHandler(BizException.class)
    R<String> bizExceptionHandler(HttpServletRequest request, BizException e) {
        log.error("业务执行异常 -> uri：{} error：{}", request.getRequestURI(), e.getMessage());
        return R.fail(e.getMessage());
    }

    /**
     * 处理机器人异常
     *
     * @param e 异常
     * @return 响应
     */
    @ExceptionHandler(BotException.class)
    R<String> botExceptionHandler(HttpServletRequest request, BotException e) {
        log.error("机器人异常 -> uri：{} error：{}", request.getRequestURI(), e.getMessage());
        return R.fail(e.getMessage());
    }

    /**
     * 处理未知异常
     *
     * @param e       异常
     * @param request HttpServletRequest
     * @return 响应
     */
    @ExceptionHandler(Exception.class)
    R<String> exceptionHandler(Exception e, HttpServletRequest request) {
        log.error("接口请求异常 -> uri：{} error：{}", request.getRequestURI(), e.getMessage(), e);
        return R.error("请求异常: " + e.getMessage());
    }
}
