package com.generated.qualityTrace;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * quality-trace 制造业质量追溯 API 服务入口。
 *
 * <p>本服务把工单、批次、质检结论与不良记录接成一条放行链：
 * 终检合格且无未处置严重不良时批次才可放行；不良记录一旦变更，
 * 已算出的放行结论立即作废并重算。</p>
 */
@SpringBootApplication
@MapperScan("com.generated.qualityTrace.repositories")
public class QualityTraceApplication {
  public static void main(String[] args) {
    SpringApplication.run(QualityTraceApplication.class, args);
  }
}
