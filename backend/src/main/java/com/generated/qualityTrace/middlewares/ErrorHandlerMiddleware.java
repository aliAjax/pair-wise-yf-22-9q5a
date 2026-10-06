package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.exceptions.BizException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 全局异常处理：业务异常按错误码返回，未知异常兜底 500。 */
@RestControllerAdvice
public class ErrorHandlerMiddleware {
  private static final Logger log = LoggerFactory.getLogger(ErrorHandlerMiddleware.class);

  @ExceptionHandler(BizException.class)
  public ResponseEntity<Map<String, Object>> handleBiz(BizException e) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", e.getErrorCode());
    body.put("message", e.getMessage());
    return ResponseEntity.status(e.getHttpStatus()).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleUnknown(Exception e) {
    log.error("unhandled error", e);
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", "INTERNAL_ERROR");
    body.put("message", "internal error");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
