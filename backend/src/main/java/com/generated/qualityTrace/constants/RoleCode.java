package com.generated.qualityTrace.constants;

/** RBAC 角色编码。解除待处置只允许 QUALITY_MANAGER。 */
public enum RoleCode {
  /** 质检员。 */
  INSPECTOR,
  /** 产线主管。 */
  LINE_SUPERVISOR,
  /** 质量经理：唯一能解除待处置的岗位。 */
  QUALITY_MANAGER,
  /** 审计员：只读。 */
  AUDITOR
}
