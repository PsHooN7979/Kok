package com.example.goose.iGoose.auth.service;

import com.example.goose.common.jwt.JwtTokenProvider;
import com.example.goose.common.service.CustomUserDetailsService;
import com.example.goose.common.service.RefreshTokenService;
import com.example.goose.iGoose.auth.dto.LoginRequest;
import com.example.goose.iGoose.auth.mapper.AuthMapper;
import com.example.goose.iGoose.auth.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService userDetailsService;


    // 회원가입 로직
    public void signup(UserVO userVO) throws Exception {
        userVO.setUuid(UUID.randomUUID().toString());
        System.out.println("암호화 전 비밀번호:" + userVO.getPassword());
        userVO.setPassword(passwordEncoder.encode(userVO.getPassword())); // 비밀번호 암호화
        System.out.println("암호화 후 비밀번호:" + userVO.getPassword());
        authMapper.insertUser(userVO);
    }

    // 로그인 로직
    public ResponseEntity<?> login(LoginRequest loginRequest) throws Exception {
        UserVO user = authMapper.findById(loginRequest.getId()); // Id로 유저 정보 검색

        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) { // password 비교

            String accessToken = jwtTokenProvider.generateAccessToken(user.getUuid());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUuid());

            // refresh Token Redis에 저장
            long refreshTokenValidity = jwtTokenProvider.getRefreshTokenValidity();
            refreshTokenService.storeRefreshToken(user.getUuid(), refreshToken, refreshTokenValidity);
            // uuid와 refreshToken, 만료 기간 저장

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Refresh-Token", refreshToken)
                    .body("로그인 성공");
        } else {
            return ResponseEntity.status(401).body("로그인 실패");
        }
    }

    public ResponseEntity<?> refreshAccessToken(String refreshToken) throws Exception {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("유효하지 않은 Rrefresh Token 입니다.");
        }

        String uuid = jwtTokenProvider.getUserIdFromToken(refreshToken);

        if (!refreshTokenService.isValidRefreshToken(uuid, refreshToken)) {
            return ResponseEntity.status(401).body("유효하지 않거나 만료된 Refresh Token 입니다.");
        }

        // 유저 정보 찾기
        UserVO userVO = authMapper.findByUuid(uuid);

        // 유저 정보 없으면 오류처리
        if (userVO == null) {
            return ResponseEntity.status(401).body("유저 정보를 찾을 수 없습니다.");
        }

        if(refreshToken.equals(refreshTokenService.getRefreshToken(userVO.getUuid()))) {
            refreshTokenService.storeRefreshToken(uuid, refreshToken, System.currentTimeMillis());
        }
        else
            return ResponseEntity.status(401).body("redis에 저장된 refreshToken과 값이 다름");





        // 새로운 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(userVO.getUuid());
        String newRefeshToken = jwtTokenProvider.generateRefreshToken(userVO.getUuid());

        // 새로 생성한 토큰으로 수정
        refreshTokenService.storeRefreshToken(userVO.getUuid(), newRefeshToken, jwtTokenProvider.getRefreshTokenValidity());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + newAccessToken)
                .header("Refresh-Token", newRefeshToken)
                .body("토큰 재발행 성공");
    }

}
