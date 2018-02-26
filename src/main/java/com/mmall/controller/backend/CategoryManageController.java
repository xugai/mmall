package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
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
    public ServerResponse addCategory(HttpSession session, @RequestParam(value="parentId",defaultValue = "0") Integer parentId,
                                      String categoryName){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("当前用户无权限操作,请更换为管理员角色！");
        }
        return iCategoryService.addCategory(parentId, categoryName);
    }

    @RequestMapping(value="set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session,Integer categoryId,String categoryName){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("当前用户无权限操作,请更换为管理员角色！");
        }
        return iCategoryService.setCategoryName(categoryId, categoryName);
    }

    @RequestMapping(value="get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getCategory(HttpSession session,@RequestParam(value="categoryId",defaultValue = "0") Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("当前用户无权限操作,请更换为管理员角色！");
        }
        return iCategoryService.getCategory(categoryId);
    }

    @RequestMapping(value="get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getDeepCategory(Integer categoryId){
        return iCategoryService.getDeepCategory(categoryId);
    }

}
