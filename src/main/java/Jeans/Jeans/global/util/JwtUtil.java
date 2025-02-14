package Jeans.Jeans.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
    // token이 expire 되었으면 true를 리턴하는 함수
    public static boolean isExpired(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().getExpiration().before(new Date());
    }

    // token에서 userId를 꺼내어 리턴하는 함수
    public static String getUserId(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("userId", String.class);
    }

    // token 생성
    // createAccessToken과 createRefreshToken이 이 함수를 호출
    public static String createToken(String userId, String key, long expireTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);  // 아이디 저장

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))  // 발행 시각
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))  // 만료 시각
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    // AccessToken 생성
    public static String createAccessToken(String userId, String key, long expireTimeMs) {
        return createToken(userId, key, expireTimeMs);
    }

    // RefreshToken 생성
    public static String createRefreshToken(String userId, String key, long expireTimeMs) {
        return createToken(userId, key, expireTimeMs);
    }

    public static Claims parseRefreshToken(String value, String key) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(value)
                .getBody();
    }
}