package com.generated.qualityTrace.constants;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户角色（RBAC）。
 */
public enum UserRole {
  INSPECTOR("质检员"),
  LINE_SUPERVISOR("产线主管"),
  QUALITY_MANAGER("质量经理"),
  AUDITOR("审计员");

  private final String label;

  UserRole(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }

  public static UserRole from(String raw) {
    if (raw == null) {
      return null;
    }
    for (UserRole r : values()) {
      if (r.name().equalsIgnoreCase(raw.trim())) {
        return r;
      }
    }
    return null;
  }

  public static Set<String> names() {
    return Arrays.stream(values()).map(Enum::name).collect(Collectors.toSet());
  }
}
