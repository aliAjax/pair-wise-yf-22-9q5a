package com.generated.qualityTrace.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** 极简 HMAC-SHA256 JWT 解析校验：只支持 HS256，验签失败或过期返回 null。 */
public final class JwtUtils {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private JwtUtils() {}

  @SuppressWarnings("unchecked")
  public static Map<String, Object> parseAndVerify(String token, String secret) {
    try {
      String[] parts = token.split("\\.");
      if (parts.length != 3) {
        return null;
      }
      String signingInput = parts[0] + "." + parts[1];
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      byte[] expected = mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));
      byte[] actual = Base64.getUrlDecoder().decode(parts[2]);
      if (!MessageDigest.isEqual(expected, actual)) {
        return null;
      }
      Map<String, Object> claims = MAPPER.readValue(
          Base64.getUrlDecoder().decode(parts[1]), Map.class);
      Object exp = claims.get("exp");
      if (exp instanceof Number && ((Number) exp).longValue() < System.currentTimeMillis() / 1000) {
        return null;
      }
      return claims;
    } catch (Exception e) {
      return null;
    }
  }
}
