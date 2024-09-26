package com.example.goose.common.filter;

import com.example.goose.common.jwt.JwtTokenProvider;
import com.example.goose.common.service.CustomUserDetailsService;
import com.example.goose.common.service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 요청 헤더에서 JWT 토큰 추출
        String token = getJwtFromRequest(request);

        // 토큰이 존재하고 유효하다면 사용자 인증 처리
        if (token != null) {
            // accessToken이 만료되었는지 확인
            boolean isTokenExpired = jwtTokenProvider.isTokenExpired(token);

            if (isTokenExpired) {
                // accessToken이 만료되었을 때, refreshToken을 사용해서 새로운 accessToken을 발급하는 로직
                String refreshToken = request.getHeader("Refresh-Token");
                if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                    // refreshToken이 유효한지 확인 후 새로운 accessToken 발급
                    String uuid = jwtTokenProvider.getUserIdFromToken(refreshToken);
                    if (refreshTokenService.isValidRefreshToken(uuid, refreshToken)) {
                        String newAccessToken = jwtTokenProvider.generateAccessToken(uuid);
                        // 헤더에 새로운 accessToken 추가
                        response.setHeader("Authorization", "Bearer " + newAccessToken);
                    }
                }
            } else {
                // accessToken이 유효한 경우 처리
                authenticateUserFromToken(token, request);
            }
        }

        // 다음 필터로 이동
        filterChain.doFilter(request, response);
    }

    // JWT 토큰을 요청 헤더에서 추출
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // "Bearer " 이후의 JWT 토큰 값만 추출
        }
        return null;
    }

    private void authenticateUserFromToken(String token, HttpServletRequest request) {
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

}
