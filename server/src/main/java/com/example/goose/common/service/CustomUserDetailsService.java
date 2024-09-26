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

//    public UserVO authenticateUser(String id, String password) {
//        // Lookup the user by id (this can be done via a repository or mapper)
//        UserVO user = findUserById(id);
//
//        // Verify password (you may need to hash the input password for comparison)
//        if (user != null && passwordMatches(password, user.getPassword())) {
//            return user;
//        }
//
//        return null; // Return null if authentication fails
//    }
//
//    private boolean passwordMatches(String rawPassword, String hashedPassword) {
//        // Implement password matching logic here (e.g., use BCryptPasswordEncoder)
//        return rawPassword.equals(hashedPassword); // Example, replace with actual hashing check
//    }
//
//    private UserVO findUserById(String id) {
//        // Implement logic to fetch the user from the database
//        return authMapper.findById(id); // Replace with your data fetching logic
//    }


    @Override
    public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
        UserVO userVO = null;
        try {
            userVO = authMapper.findByUuid(uuid);
        } catch (Exception e) {
            // Handle the exception (e.g., log it, throw a new exception, etc.)
            throw new UsernameNotFoundException("User not found with username: " + uuid, e);
        }

        if (userVO == null) {
            throw new UsernameNotFoundException("User not found with username: " + uuid);
        }



        return org.springframework.security.core.userdetails.User.builder()
                .username(userVO.getId())
                .password(userVO.getPassword())  // Ensure this is the encrypted password
//                .roles("USER")  // Replace with actual roles if needed
                .build();
    }

}