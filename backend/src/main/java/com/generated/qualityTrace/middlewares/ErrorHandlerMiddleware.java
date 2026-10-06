package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器：把异常统一转成 {code, message, timestamp, path} 错误响应。
 *
 * <p>service / controller 会分别包装并抛出 {@link BusinessException}，本处理器只负责
 * 统一格式化；错误码与文案分别来自 ErrorCodes / ErrorMessages。</p>
 */
@RestControllerAdvice
public class ErrorHandlerMiddleware {

  private static final Logger log = LoggerFactory.getLogger(ErrorHandlerMiddleware.class);

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex,
                                                            HttpServletRequest request) {
    log.warn("business error: code={} message={} path={}", ex.getCode(), ex.getMessage(),
        request.getRequestURI());
    return build(ex.getStatus(), ex.getCode(), ex.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
                                                               HttpServletRequest request) {
    StringBuilder detail = new StringBuilder();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      if (!detail.isEmpty()) {
        detail.append("; ");
      }
      detail.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage());
    }
    log.warn(LogTemplates.VALIDATION_FAILED, request.getRequestURI(), detail);
    return build(HttpStatus.BAD_REQUEST.value(), ErrorCodes.VALIDATION_FAILED,
        String.format(ErrorMessages.VALIDATION_FAILED, detail), request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException ex,
                                                               HttpServletRequest request) {
    log.warn("illegal argument: {}", ex.getMessage());
    return build(HttpStatus.BAD_REQUEST.value(), ErrorCodes.VALIDATION_FAILED,
        String.format(ErrorMessages.VALIDATION_FAILED, ex.getMessage()), request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleOther(Exception ex, HttpServletRequest request) {
    log.error("unexpected error", ex);
    return build(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCodes.INTERNAL_ERROR,
        ErrorMessages.INTERNAL_ERROR, request);
  }

  private ResponseEntity<Map<String, Object>> build(int status, String code, String message,
                                                     HttpServletRequest request) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", code);
    body.put("message", message);
    body.put("status", status);
    body.put("timestamp", LocalDateTime.now().toString());
    body.put("path", request.getRequestURI());
    return ResponseEntity.status(status).body(body);
  }
}
