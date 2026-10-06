package com.generated.qualityTrace.utils;

import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 日期工具：统一解析表单中的日期时间字符串。
 */
public final class DateUtils {

  private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private DateUtils() {
  }

  public static LocalDateTime now() {
    return LocalDateTime.now();
  }

  public static String format(LocalDateTime time) {
    return time == null ? null : DT.format(time);
  }

  /**
   * 支持 "yyyy-MM-dd HH:mm:ss" 与 ISO_LOCAL_DATE_TIME；空串返回 null。
   */
  public static LocalDateTime parse(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    String value = raw.trim();
    try {
      if (value.length() == 10) {
        return LocalDateTime.parse(value + " 00:00:00", DT);
      }
      return LocalDateTime.parse(value, DT);
    } catch (DateTimeParseException e) {
      try {
        return LocalDateTime.parse(value);
      } catch (DateTimeParseException ex) {
        throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
            String.format(ErrorMessages.VALIDATION_FAILED, "日期格式不合法：" + raw));
      }
    }
  }
}
