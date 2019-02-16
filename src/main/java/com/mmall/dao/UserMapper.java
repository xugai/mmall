package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    int getUserCount();

    List<User> getUserList();

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(String email);

    /**
     * 注意！当我们需要传多个参数的时候，需要使用到MyBatis的Param注解
     * @param username
     * @param password
     * @return
     */
    User selectLogin(@Param("username") String username,@Param("password") String password);

    String getForgetQuestion(String username);

    int forgetCheckAnswer(@Param("username") String username,
                          @Param("question") String question,
                          @Param("answer") String answer);

    int forgetResetPassword(@Param("username") String username,@Param("passwordNew") String passwordNew);

    int resetPassword(@Param("userId") int userId,@Param("password") String password);

    int checkEmailByUserId(@Param("userId") int userId,@Param("email") String email);
}