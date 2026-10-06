package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 限流中间件：基于 IP 的滑动窗口计数，超出阈值返回 429。
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

  private static final Logger log = LoggerFactory.getLogger(RateLimitInterceptor.class);

  private final Map<String, Deque<Long>> buckets = new ConcurrentHashMap<>();

  @Value("${app.rate-limit.max-requests:120}")
  private int maxRequests;

  @Value("${app.rate-limit.window-ms:60000}")
  private long windowMs;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String ip = clientIp(request);
    long now = System.currentTimeMillis();
    Deque<Long> times = buckets.computeIfAbsent(ip, k -> new ArrayDeque<>());
    synchronized (times) {
      while (!times.isEmpty() && now - times.peekFirst() > windowMs) {
        times.pollFirst();
      }
      if (times.size() >= maxRequests) {
        log.warn(LogTemplates.RATE_LIMIT_TRIGGERED, ip, request.getRequestURI());
        throw BusinessException.of(429, ErrorCodes.RATE_LIMITED, ErrorMessages.RATE_LIMITED);
      }
      times.addLast(now);
    }
    return true;
  }

  private String clientIp(HttpServletRequest request) {
    String forwarded = request.getHeader("X-Forwarded-For");
    if (forwarded != null && !forwarded.isBlank()) {
      return forwarded.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}
