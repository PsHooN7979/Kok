package com.example.goose.iGoose.auth.controller;

import com.example.goose.common.jwt.JwtTokenProvider;
import com.example.goose.iGoose.auth.dto.TokenResponse;
import com.example.goose.iGoose.auth.service.AuthService;
import com.example.goose.iGoose.auth.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class test {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TestService testService;

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestHeader("Authorization") String token) throws Exception {
        return testService.test(token);
    }
}


