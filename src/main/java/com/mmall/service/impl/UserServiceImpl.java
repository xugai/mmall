package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.utils.MD5Util;
import com.mmall.utils.ShardedRedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by rabbit on 2018/2/6.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        if(userMapper.checkUsername(username) == 0){
            return ServerResponse.createByErrorMessage("用户不存在！");
        }

        // 密码登录，MD5加密
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误！");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功！",user);
    }

    public ServerResponse<String> register(User user){
        if(!this.checkValid(user.getUsername(),Const.USERNAME).isSuccess()){
            return ServerResponse.createByErrorMessage("用户已存在！");
        }
        if(!this.checkValid(user.getEmail(),Const.EMAIL).isSuccess()){
            return ServerResponse.createByErrorMessage("邮箱已注册！");
        }
        //对用户注册的密码进行MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        user.setRole(Const.ROLE.ROLE_CUSTOMER);
        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("发生异常错误,注册失败！");
        }
        return ServerResponse.createBySuccessMessage("注册成功！");
    }

    public ServerResponse<String> checkValid(String str,String type){
       if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
           if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在！");
                }
           }
           if(Const.EMAIL.equals(type)){
               int resultCount = userMapper.checkEmail(str);
               if(resultCount > 0){
                   return ServerResponse.createByErrorMessage("该邮箱已注册！");
               }
           }
       }else{
           return ServerResponse.createByErrorMessage("参数错误！");
       }
        return ServerResponse.createBySuccessMessage("校验成功！");
    }

    public ServerResponse<String> forgerGetQuestion(String username){
         if(this.checkValid(username,Const.USERNAME).isSuccess()){
             return ServerResponse.createByErrorMessage("用户不存在！");
         }
        String question = userMapper.getForgetQuestion(username);
        if(!org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServerResponse.createByErrorMessage("问题是空的！");
        }
        return ServerResponse.createBySuccess(question);
    }

    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        int resultCount = userMapper.forgetCheckAnswer(username,question,answer);
        if(resultCount > 0){
            //进入该分支，说明该用户的该问题的答案是正确的
            //传入token给下一个重置密码页面，用于防止横向越权以及确定修改密码的有效操作期限
            String forgetToken = UUID.randomUUID().toString();
            ShardedRedisPoolUtil.setex(Const.TOKEN_PREFIX + username, Const.RedisCacheExTime.REDIS_FORGET_TOKEN_CACHE_TIME, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("您提交的答案是错误的！");
    }

    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(this.checkValid(username,Const.USERNAME).isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在！");
        }
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("请求的参数错误,token需要传递！");
        }
        String token = ShardedRedisPoolUtil.get(Const.TOKEN_PREFIX + username);
        if(org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期！");
        }
        if(token.equals(forgetToken)) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.forgetResetPassword(username, md5Password);
            if (resultCount > 0) {
                return ServerResponse.createBySuccessMessage("密码重置成功！");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token！");
        }
        return ServerResponse.createByErrorMessage("密码重置失败！");
    }

    public ServerResponse<String> resetPassword(User user,String passwordOld,String passwordNew){
        int resultCount = userMapper.resetPassword(user.getId(),MD5Util.MD5EncodeUtf8(passwordOld));
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误,请重试！");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("密码重置成功！");
        }
        return ServerResponse.createByErrorMessage("密码重置失败！");
    }

    public ServerResponse<User> updateInformation(User user){
        //登录状态下的用户更新是不能修改用户名的
        int resultCount = 0;
        resultCount = userMapper.checkEmailByUserId(user.getId(),user.getEmail());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("邮箱已被其他用户注册,请重新修改！");
        }
        /**
         * 感觉前面验证成功的话,可以把该方法的参数user直接传递下去的,不需要重新new一个user出来,
         * 然后再让参数user为new出来的user进行赋值,再把new出来的user传递下去,这样好像代码冗余?
         */
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        resultCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(resultCount > 0) {
            return ServerResponse.createBySuccess("信息修改成功！", updateUser);
        }
        return ServerResponse.createByErrorMessage("信息修改失败！");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("当前用户不存在！");
        }
        //把从数据库中获取到的用户信息传递过去，但要把密码设为空，密码不能显示在用户信息页面上
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    public ServerResponse checkAdminRole(User user){
        if(user.getRole().intValue() == Const.ROLE.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
