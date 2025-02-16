package Jeans.Jeans.global.config;

import Jeans.Jeans.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Authorization header 가져오기
        final String authorization = request.getHeader("Authorization");
        log.info("Authorization: {}", authorization);

        // Bearer token인지 확인
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.error("올바르지 않은 Authorization입니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 이후의 token 추출
        String token = authorization.substring(7);

        // token이 expire되었는지 확인
        if (JwtUtil.isExpired(token, secretKey)) {
            log.error("토큰이 만료되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰에서 사용자 ID 가져오기
        String userId = JwtUtil.getUserId(token, secretKey);
        log.info("userId: {}", userId);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("USER")));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}