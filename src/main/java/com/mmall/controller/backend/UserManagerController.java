package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.ShardedRedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by rabbit on 2018/2/8.
 */
@Controller
@RequestMapping("/manage/user")
public class UserManagerController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session,HttpServletResponse httpServletResponse){
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()) {
            User user = response.getData();
            if (user.getRole() == Const.ROLE.ROLE_ADMIN) {
                ShardedRedisPoolUtil.setex(session.getId(),
                        Const.RedisCacheExTime.REDIS_SESSION_EXTIME,
                        JsonUtil.obj2String(response.getData()));
                CookieUtil.writeLoginToken(session.getId(), httpServletResponse);
                return response;
            }else{
                return ServerResponse.createByErrorMessage("不是管理员,无法登录！");
            }
        }else{
            return response;
        }
    }
}
