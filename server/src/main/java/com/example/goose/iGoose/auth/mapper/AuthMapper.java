package com.example.goose.iGoose.auth.mapper;


import com.example.goose.iGoose.auth.dto.LoginRequest;
import com.example.goose.iGoose.auth.vo.UserVO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AuthMapper {
    void insertUser(UserVO userVO) throws Exception;

    @Select("SELECT * FROM \"USER\" WHERE \"id\" = #{id}")
    UserVO findById(String id) throws Exception;

    @Select("SELECT * FROM \"USER\" WHERE \"uuid\" = #{uuid}")
    UserVO findByUuid(String uuid) throws Exception;

    @Select("SELECT * FROM \"USER\" WHERE email = #{email}")
    UserVO findByEmail(String email);

    @Update("UPDATE \"USER\" SET is_verified = #{is_verified}, verification = #{verification} WHERE uuid = #{uuid}")
    void updateEmailVerified(UserVO userVO);

}
