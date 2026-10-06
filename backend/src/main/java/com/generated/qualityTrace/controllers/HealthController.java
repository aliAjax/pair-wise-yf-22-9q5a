package com.generated.qualityTrace.controllers;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查。
 */
@RestController
public class HealthController {

  @GetMapping("/health")
  public Map<String, Object> health() {
    return Map.of(
        "status", "ok",
        "service", "quality-trace",
        "time", System.currentTimeMillis()
    );
  }
}
