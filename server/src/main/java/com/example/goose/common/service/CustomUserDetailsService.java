package com.example.goose.common.service;

import com.example.goose.iGoose.auth.dto.LoginRequest;
import com.example.goose.iGoose.auth.mapper.AuthMapper;
import com.example.goose.iGoose.auth.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthMapper authMapper;

    @Override
    public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
        UserVO userVO = null;
        try {
            userVO = authMapper.findByUuid(uuid);
        } catch (Exception e) {
            // Handle the exception (e.g., log it, throw a new exception, etc.)
            throw new UsernameNotFoundException("User not found with uuid: " + uuid, e);
        }

        if (userVO == null) {
            throw new UsernameNotFoundException("User not found with uuid: " + uuid);
        }



        return org.springframework.security.core.userdetails.User.builder()
                .username(userVO.getId())
                .password(userVO.getPassword())  // Ensure this is the encrypted password
//                .roles("USER")  // Replace with actual roles if needed
                .build();
    }

}