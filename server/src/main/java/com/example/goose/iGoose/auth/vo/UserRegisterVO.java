package com.example.goose.iGoose.auth.vo;


import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterVO {

    private String uuid;

    private String password;

    private String id;

    private String email;

    private boolean is_verified = false;
}
