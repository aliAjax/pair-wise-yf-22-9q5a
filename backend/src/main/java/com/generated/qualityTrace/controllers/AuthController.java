package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constructors.LoginResponse;
import com.generated.qualityTrace.routes.AuthRoutes;
import com.generated.qualityTrace.services.AuthService;
import com.generated.qualityTrace.types.LoginPayload;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器：登录签发 JWT。
 */
@RestController
@RequestMapping(AuthRoutes.BASE)
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping(AuthRoutes.LOGIN)
  public LoginResponse login(@Valid @RequestBody LoginPayload payload) {
    return authService.login(payload);
  }
}
