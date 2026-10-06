package com.generated.qualityTrace.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 轻量 JWT 工具（HMAC-SHA256），不引入额外安全框架。
 *
 * <p>令牌结构：header.payload.signature，payload 内含 uid / sub(username) / role / exp。</p>
 */
@Component
public class JwtUtils {

  private static final String HMAC_ALG = "HmacSHA256";

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final SecretKeySpec keySpec;
  private final long expirationMs;

  public JwtUtils(@Value("${app.jwt.secret}") String secret,
                  @Value("${app.jwt.expiration-ms}") long expirationMs) {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    this.keySpec = new SecretKeySpec(keyBytes, HMAC_ALG);
    this.expirationMs = expirationMs;
  }

  /**
   * 生成令牌。
   */
  public String generate(Long userId, String username, String role) {
    try {
      Map<String, Object> header = new HashMap<>();
      header.put("alg", "HS256");
      header.put("typ", "JWT");

      long now = Instant.now().toEpochMilli();
      Map<String, Object> payload = new HashMap<>();
      payload.put("uid", userId);
      payload.put("sub", username);
      payload.put("role", role);
      payload.put("iat", now / 1000);
      payload.put("exp", (now + expirationMs) / 1000);

      String h = base64Url(objectMapper.writeValueAsBytes(header));
      String p = base64Url(objectMapper.writeValueAsBytes(payload));
      String sig = sign(h + "." + p);
      return h + "." + p + "." + sig;
    } catch (Exception e) {
      throw new IllegalStateException("failed to generate jwt", e);
    }
  }

  /**
   * 解析并校验令牌，失败抛出 401。
   */
  public JwtPayload parse(String token) {
    try {
      String[] parts = token.split("\\.");
      if (parts.length != 3) {
        throw BusinessException.of(401, ErrorCodes.AUTH_TOKEN_INVALID, ErrorMessages.AUTH_TOKEN_INVALID);
      }
      String expectedSig = sign(parts[0] + "." + parts[1]);
      if (!constantTimeEquals(expectedSig, parts[2])) {
        throw BusinessException.of(401, ErrorCodes.AUTH_TOKEN_INVALID, ErrorMessages.AUTH_TOKEN_INVALID);
      }
      byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
      Map<?, ?> payload = objectMapper.readValue(payloadBytes, Map.class);
      long exp = ((Number) payload.get("exp")).longValue();
      if (Instant.now().getEpochSecond() > exp) {
        throw BusinessException.of(401, ErrorCodes.AUTH_TOKEN_EXPIRED, ErrorMessages.AUTH_TOKEN_EXPIRED);
      }
      Long uid = payload.get("uid") == null ? null : ((Number) payload.get("uid")).longValue();
      String username = (String) payload.get("sub");
      String role = (String) payload.get("role");
      return new JwtPayload(uid, username, role);
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw BusinessException.of(401, ErrorCodes.AUTH_TOKEN_INVALID, ErrorMessages.AUTH_TOKEN_INVALID);
    }
  }

  private String sign(String content) throws Exception {
    Mac mac = Mac.getInstance(HMAC_ALG);
    mac.init(keySpec);
    byte[] sig = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
    return base64Url(sig);
  }

  private static String base64Url(byte[] bytes) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private static boolean constantTimeEquals(String a, String b) {
    if (a.length() != b.length()) {
      return false;
    }
    int diff = 0;
    for (int i = 0; i < a.length(); i++) {
      diff |= a.charAt(i) ^ b.charAt(i);
    }
    return diff == 0;
  }

  /**
   * JWT 载荷。
   */
  public record JwtPayload(Long userId, String username, String role) {
  }
}
