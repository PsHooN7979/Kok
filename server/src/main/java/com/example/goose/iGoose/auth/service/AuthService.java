package com.example.goose.iGoose.auth.service;

import com.example.goose.common.jwt.JwtTokenProvider;
import com.example.goose.common.service.RefreshTokenService;
import com.example.goose.iGoose.auth.dto.LoginRequest;
import com.example.goose.iGoose.auth.mapper.AuthMapper;
import com.example.goose.iGoose.auth.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.Date;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final JavaMailSender mailSender;


    // 회원가입 로직
    public ResponseEntity<?> signup(UserVO userVO) throws Exception {
        String verificationCode = generateVerificationCode();

        userVO.setUuid(UUID.randomUUID().toString());
        userVO.setPassword(passwordEncoder.encode(userVO.getPassword())); // 비밀번호 암호화
        userVO.setIs_verified(false);
        userVO.setEmail(userVO.getEmail());
        userVO.setVerification(verificationCode);
        userVO.setExpire(new Date());
        authMapper.insertUser(userVO);


        sendVerificationEmail(userVO.getEmail(), verificationCode)  ;
        return ResponseEntity.ok("이메일 인증 해라");

    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public void verifyEmail(String email, String code) throws Exception {
        UserVO userVO = authMapper.findByEmail(email);
        if (userVO == null) {
            throw new Exception("유저를 찾을 수 없습니다.");
        }

        if (!userVO.getVerification().equals(code)) {
            throw new Exception("코드가 일치하지 않습니다.");
        }

        Date now = new Date();
        long diff = now.getTime() - userVO.getExpire().getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

        if (minutes > 3) {
            throw new Exception("만료된 코드 입니다.");
        }
        userVO.setIs_verified(true);
        userVO.setExpire(null);
        authMapper.updateEmailVerified(userVO);


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

    // 이메일 인증 토큰 로직
    public String generateVerificationToken(UserVO userVO) throws Exception {
        return UUID.randomUUID().toString();
    }


    // 새로운 refreshToken 생성
    public ResponseEntity<?> refreshAccessToken(String refreshToken) throws Exception {
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }
        // 토큰 만료 여부 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("유효하지 않은 Refresh Token 입니다.");
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

    // email 인증 메일 전송 로직
    public void sendVerificationEmail(String email, String verification){
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address: " + email);
        }
        String subject = "이메일 인증";
        String message = "이메일 인증 코드 : " + verification + ". 인증 코드 만료시간은 3분 입니다.";

        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject(subject);
        emailMessage.setText(message);

        mailSender.send(emailMessage);
    }


}
