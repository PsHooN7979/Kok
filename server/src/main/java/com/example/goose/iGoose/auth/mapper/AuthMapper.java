package com.example.goose.iGoose.auth.mapper;


import com.example.goose.iGoose.auth.dto.LoginRequest;
import com.example.goose.iGoose.auth.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

//@Mapper
public interface AuthMapper {
    void insertUser(UserVO userVO) throws Exception;

    @Select("SELECT * FROM \"USER\" WHERE \"id\" = #{id}")
    UserVO findById(String id) throws Exception;


    @Select("SELECT * FROM \"USER\" WHERE \"uuid\" = #{uuid}")
    UserVO findByUuid(String uuid) throws Exception;
}
