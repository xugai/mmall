package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by rabbit on 2018/2/23.
 */
@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;


    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpServletRequest httpServletRequest, @RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
                                              @RequestParam(value="pageSize",defaultValue = "10") Integer pageSize){
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtil.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
//        }
//        String userStr = ShardedRedisPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStr, User.class);
//        if(user == null){
//            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            return iOrderService.manageOrderList(pageNum,pageSize);
//        }else{
//            return ServerResponse.createByErrorMessage("当前用户无权限操作,请更换为管理员角色后再尝试本次操作！");
//        }
        // 全部都由拦截器进行登录判断以及管理员权限验证
        return iOrderService.manageOrderList(pageNum,pageSize);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> getOrderDetail(HttpServletRequest httpServletRequest, Long orderNo){
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtil.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
//        }
//        String userStr = ShardedRedisPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStr, User.class);
//        if(user == null){
//            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            return iOrderService.getOrderDetail(orderNo);
//        }else{
//            return ServerResponse.createByErrorMessage("当前用户无权限操作,请更换为管理员角色后再尝试本次操作！");
//        }
        // 全部都由拦截器进行登录判断以及管理员权限验证
        return iOrderService.getOrderDetail(orderNo);
    }


    /**
     * 管理员通过订单号查询订单,我们把查询出来的结果进行分页处理,虽然目前是进行精确匹配,但考虑到后期的
     * 业务扩展后,我们的后台可以实现通过手机号、收货人姓名、收货地址等进行模糊匹配,因此我们在最开始的
     * 时候就进行分页处理,方便后期的多条数据显示.
     * @param httpServletRequest
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> searchOrder(HttpServletRequest httpServletRequest, Long orderNo,
                                               @RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
                                               @RequestParam(value="pageSize",defaultValue = "10") Integer pageSize){
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtil.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
//        }
//        String userStr = ShardedRedisPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStr, User.class);
//        if(user == null){
//            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            return iOrderService.searchOrder(orderNo,pageNum,pageSize);
//        }else{
//            return ServerResponse.createByErrorMessage("当前用户无权限操作,请更换为管理员角色后再尝试本次操作！");
//        }
        // 全部都由拦截器进行登录判断以及管理员权限验证
        return iOrderService.searchOrder(orderNo,pageNum,pageSize);
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse orderSendGoods(HttpServletRequest httpServletRequest,Long orderNo){
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtil.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
//        }
//        String userStr = ShardedRedisPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStr, User.class);
//        if(user == null){
//            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            return iOrderService.sendOrderGoods(orderNo);
//        }else{
//            return ServerResponse.createByErrorMessage("当前用户无权限操作,请更换为管理员角色后再尝试本次操作！");
//        }
        // 全部都由拦截器进行登录判断以及管理员权限验证
        return iOrderService.sendOrderGoods(orderNo);
    }





    //detail,search,send

}
