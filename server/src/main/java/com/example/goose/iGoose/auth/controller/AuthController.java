package com.example.goose.iGoose.auth.controller;


import com.example.goose.common.jwt.JwtTokenProvider;
import com.example.goose.common.service.RefreshTokenService;
import com.example.goose.iGoose.auth.dto.LoginRequest;
import com.example.goose.iGoose.auth.dto.TokenResponse;
import com.example.goose.iGoose.auth.service.AuthService;
import com.example.goose.iGoose.auth.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;


    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserVO userVO) throws Exception {
        authService.signup(userVO);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {
        return authService.login(loginRequest);
    }

}