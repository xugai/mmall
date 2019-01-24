package com.mmall.controller.backend;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by rabbit on 2018/2/9.
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;


    @RequestMapping(value="add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpServletRequest httpServletRequest, @RequestParam(value = "parentId",defaultValue = "0") Integer parentId,
                                      String categoryName){
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtil.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
//        }
//        String userStr = ShardedRedisPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStr, User.class);
//        if(user == null){
//            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
//        if(!iUserService.checkAdminRole(user).isSuccess()){
//            return ServerResponse.createByErrorMessage("当前用户无权限操作,请更换为管理员角色！");
//        }
        // 全部都由拦截器进行登录判断以及管理员权限验证
        return iCategoryService.addCategory(parentId, categoryName);
    }

    @RequestMapping(value="set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpServletRequest httpServletRequest,Integer categoryId,String categoryName){
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtil.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
//        }
//        String userStr = ShardedRedisPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStr, User.class);
//        if(user == null){
//            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
//        if(!iUserService.checkAdminRole(user).isSuccess()){
//            return ServerResponse.createByErrorMessage("当前用户无权限操作,请更换为管理员角色！");
//        }
        // 全部都由拦截器进行登录判断以及管理员权限验证
        return iCategoryService.setCategoryName(categoryId, categoryName);
    }

    @RequestMapping(value="get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getCategory(HttpServletRequest httpServletRequest,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtil.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
//        }
//        String userStr = ShardedRedisPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStr, User.class);
//        if(user == null){
//            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
//        if(!iUserService.checkAdminRole(user).isSuccess()){
//            return ServerResponse.createByErrorMessage("当前用户无权限操作,请更换为管理员角色！");
//        }
        // 全部都由拦截器进行登录判断以及管理员权限验证
        return iCategoryService.getCategory(categoryId);
    }

    @RequestMapping(value="get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getDeepCategory(Integer categoryId){
        return iCategoryService.getDeepCategory(categoryId);
    }

}
