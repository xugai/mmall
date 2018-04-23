package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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
    public ServerResponse<PageInfo> orderList(HttpSession session, @RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
                                              @RequestParam(value="pageSize",defaultValue = "10") Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.manageOrderList(user.getId(),pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("当前用户无权限操作,请更换为管理员角色后再尝试本次操作！");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> getOrderDetail(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.getOrderDetail(orderNo);
        }else{
            return ServerResponse.createByErrorMessage("当前用户无权限操作,请更换为管理员角色后再尝试本次操作！");
        }
    }


    /**
     * 管理员通过订单号查询订单,我们把查询出来的结果进行分页处理,虽然目前是进行精确匹配,但考虑到后期的
     * 业务扩展后,我们的后台可以实现通过手机号、收货人姓名、收货地址等进行模糊匹配,因此我们在最开始的
     * 时候就进行分页处理,方便后期的多条数据显示.
     * @param session
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> searchOrder(HttpSession session, Long orderNo,
                                               @RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
                                               @RequestParam(value="pageSize",defaultValue = "10") Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.searchOrder(orderNo,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("当前用户无权限操作,请更换为管理员角色后再尝试本次操作！");
        }
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse orderSendGoods(HttpSession session,Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.sendOrderGoods(orderNo);
        }else{
            return ServerResponse.createByErrorMessage("当前用户无权限操作,请更换为管理员角色后再尝试本次操作！");
        }
    }





    //detail,search,send

}
