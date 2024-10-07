package com.example.goose.iGoose.auth.controller;


import com.example.goose.iGoose.auth.dto.LoginRequest;
import com.example.goose.iGoose.auth.dto.VerificationRequest;
import com.example.goose.iGoose.auth.service.AuthService;
import com.example.goose.iGoose.auth.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
//    private final EmailAuthService verificationTokenService;

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserVO userVO) throws Exception {
        return authService.signup(userVO);
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {
        return authService.login(loginRequest);
    }

    // refreshToken 재발행
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Refresh-Token") String refreshToken) throws Exception {
        return authService.refreshAccessToken(refreshToken);
    }

    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Refresh-Token") String refreshToken) throws Exception {
        try {
            authService.logout(refreshToken);  // Service layer handles the logout logic
            return ResponseEntity.ok("로그아웃 성공");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("로그아웃 실패: " + e.getMessage());
        }
    }

    @GetMapping("/verify-code")
    public ResponseEntity<?> verifyEmail(@RequestBody VerificationRequest verificationRequest) throws Exception {

        try {
            authService.verifyEmail(verificationRequest.getEmail(), verificationRequest.getVerification());
            return ResponseEntity.ok("이메일 인증 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
//    @PostMapping("/auto-login")

}