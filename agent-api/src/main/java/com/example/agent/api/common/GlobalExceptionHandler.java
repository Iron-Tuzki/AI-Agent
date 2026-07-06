package com.example.agent.api.common;

import com.example.agent.domain.exception.AgentBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，用于将参数校验异常、业务异常和系统异常转换为统一 API 响应。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理请求参数校验失败异常。
     *
     * @param exception 参数校验异常
     * @return 统一失败响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("请求参数不合法");
        return ResponseEntity.badRequest().body(ApiResponse.fail("BAD_REQUEST", message));
    }

    /**
     * 处理 Agent 业务异常。
     *
     * @param exception Agent 业务异常
     * @return 统一失败响应
     */
    @ExceptionHandler(AgentBusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleAgentBusinessException(AgentBusinessException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.fail("BUSINESS_ERROR", exception.getMessage()));
    }

    /**
     * 处理未预期的系统异常。
     *
     * @param exception 系统异常
     * @return 统一失败响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("SYSTEM_ERROR", "系统内部异常"));
    }
}
