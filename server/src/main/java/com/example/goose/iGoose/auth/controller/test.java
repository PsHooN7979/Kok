package com.example.goose.iGoose.auth.controller;

import com.example.goose.iGoose.auth.dto.TokenResponse;
import com.example.goose.iGoose.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class test {



    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestHeader("Authorization") String token) {
        if (token == null) {
            return ResponseEntity.badRequest().body("Authorization header is missing");
        }


        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return ResponseEntity.ok("Success with token: " + token);
    }
}


