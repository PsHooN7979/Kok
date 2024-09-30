package com.example.goose.iGoose.auth.service;

import com.example.goose.common.jwt.JwtTokenProvider;
import com.example.goose.common.service.CustomUserDetailsService;
import com.example.goose.common.service.RefreshTokenService;
import com.example.goose.iGoose.auth.dto.LoginRequest;
import com.example.goose.iGoose.auth.mapper.AuthMapper;
import com.example.goose.iGoose.auth.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService userDetailsService;


    // 회원가입 로직
    public void signup(UserVO userVO) throws Exception {
        userVO.setUuid(UUID.randomUUID().toString());
        userVO.setPassword(passwordEncoder.encode(userVO.getPassword())); // 비밀번호 암호화
        authMapper.insertUser(userVO);
    }

    // 로그인 로직
    public ResponseEntity<?> login(LoginRequest loginRequest) throws Exception {
        UserVO user = authMapper.findById(loginRequest.getId()); // 로그인할때 입력한 Id 값으로 유저 정보 검색

        // 암호화 한 password 비교
        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {

            String accessToken = jwtTokenProvider.generateAccessToken(user.getUuid());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUuid());

            // refresh Token Redis에 저장
            long refreshTokenValidity = jwtTokenProvider.getRefreshTokenValidity();
            refreshTokenService.storeRefreshToken(user.getUuid(), refreshToken, refreshTokenValidity);
            // uuid와 refreshToken, 만료 기간 저장

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Refresh-Token", "Bearer " + refreshToken)
                    .body("로그인 성공");
        } else {
            return ResponseEntity.status(401).body("로그인 실패");
        }
    }

    // 로그아웃 로직
    public void logout(String refreshToken) throws Exception {

        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);

        }
        
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new Exception("유효하지 않은 refreshToken");
        }

        String uuid = jwtTokenProvider.getUserIdFromToken(refreshToken);

        String storedRefreshToken = refreshTokenService.getRefreshToken(uuid);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new Exception("저장된 Refresh Token과 일치하지 않습니다.");
        }

        refreshTokenService.deleteRefreshToken(uuid);

    }


    // 새로운 refreshToken 생성
    public ResponseEntity<?> refreshAccessToken(String refreshToken) throws Exception {
        // 토큰 만료 여부 검증
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);

        }
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("유효하지 않은 Rrefresh Token 입니다.");
        }

        String uuid = jwtTokenProvider.getUserIdFromToken(refreshToken);

        if (!refreshTokenService.isValidRefreshToken(uuid, refreshToken)) {
            return ResponseEntity.status(401).body("유효하지 않거나 만료된 Refresh Token 입니다.");
        }


        // 유저 정보 찾기
        UserVO userVO = authMapper.findByUuid(uuid);

        // 유저 정보 없으면 예외처리
        if (userVO == null) {
            return ResponseEntity.status(401).body("유저 정보를 찾을 수 없습니다.");
        }

        // redis에 있는 refreshToken과 값이 다르면 예외처리
        if (!refreshTokenService.isValidRefreshToken(uuid, refreshToken)) {
            return ResponseEntity.status(401).body("유효하지 않거나 만료된 Refresh Token 입니다.");
        }

        // 새로운 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(userVO.getUuid());
        String newRefeshToken = jwtTokenProvider.generateRefreshToken(userVO.getUuid());

        // 새로 생성한 토큰으로 수정
        refreshTokenService.storeRefreshToken(userVO.getUuid(), newRefeshToken, jwtTokenProvider.getRefreshTokenValidity());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + newAccessToken)
                .header("Refresh-Token", "Bearer " + newRefeshToken)
                .body("토큰 재발행 성공");
    }

}
