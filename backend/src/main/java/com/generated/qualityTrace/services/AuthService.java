package com.generated.qualityTrace.services;

import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.UserRole;
import com.generated.qualityTrace.constructors.LoginResponse;
import com.generated.qualityTrace.models.User;
import com.generated.qualityTrace.repositories.UserMapper;
import com.generated.qualityTrace.types.LoginPayload;
import com.generated.qualityTrace.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务：校验账号密码并签发 JWT。
 */
@Service
public class AuthService {

  private static final Logger log = LoggerFactory.getLogger(AuthService.class);

  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;
  private final AuditLogService auditLogService;

  public AuthService(UserMapper userMapper,
                     PasswordEncoder passwordEncoder,
                     JwtUtils jwtUtils,
                     AuditLogService auditLogService) {
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtils = jwtUtils;
    this.auditLogService = auditLogService;
  }

  public LoginResponse login(LoginPayload payload) {
    User user = userMapper.findByUsername(payload.username());
    if (user == null || !passwordEncoder.matches(payload.password(), user.getPasswordHash())) {
      log.warn(LogTemplates.AUTH_LOGIN_FAILED, payload.username(), "bad credentials");
      auditLogService.record(payload.username(), "LOGIN_FAILED", "USER", payload.username(), "bad credentials");
      throw BusinessException.of(401, ErrorCodes.AUTH_LOGIN_FAILED, ErrorMessages.AUTH_LOGIN_FAILED);
    }
    if (UserRole.from(user.getRole()) == null) {
      throw BusinessException.of(403, ErrorCodes.AUTH_ACCOUNT_DISABLED, ErrorMessages.AUTH_ACCOUNT_DISABLED);
    }
    String token = jwtUtils.generate(user.getId(), user.getUsername(), user.getRole());
    log.info(LogTemplates.AUTH_LOGIN_SUCCESS, user.getUsername(), user.getRole());
    auditLogService.record(user.getUsername(), "LOGIN_SUCCESS", "USER", user.getUsername(),
        "role=" + user.getRole());
    return new LoginResponse(
        token,
        "Bearer",
        user.getId(),
        user.getUsername(),
        user.getRole(),
        UserRole.valueOf(user.getRole()).label(),
        user.getDisplayName()
    );
  }
}
