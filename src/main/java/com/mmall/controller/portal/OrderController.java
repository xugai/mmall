package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.ShardedRedisPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by rabbit on 2018/2/17.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;


    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpServletRequest httpServletRequest,Integer shippingId){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtil.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
        }
        String userStr = ShardedRedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(),shippingId);
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpServletRequest httpServletRequest,Long orderNo){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtil.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
        }
        String userStr = ShardedRedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(),orderNo);
    }


    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpServletRequest httpServletRequest){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtil.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
        }
        String userStr = ShardedRedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest httpServletRequest, @RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
                                         @RequestParam(value="pageSize",defaultValue = "10") Integer pageSize){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtil.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
        }
        String userStr = ShardedRedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.list(user.getId(),pageNum,pageSize);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getOrderDetail(HttpServletRequest httpServletRequest,Long orderNo){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtil.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
        }
        String userStr = ShardedRedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }


    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse<Map> pay(HttpServletRequest httpServletRequest, Long orderNo, HttpServletRequest request){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtil.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
        }
        String userStr = ShardedRedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //该path用于记录生成的二维码图片的存储位置
        String path = request.getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo,user.getId(),path);
    }


    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> map = Maps.newHashMap();
        Map requestParams = request.getParameterMap();
        for(Iterator iterator = requestParams.keySet().iterator();iterator.hasNext();){
            String name = (String)iterator.next();
            String params = "";
            String [] paramsList =(String []) requestParams.get(name);
           for(int i = 0; i < paramsList.length; i++) {
               params = (i == paramsList.length - 1)?params + paramsList[i]:params + paramsList[i] + ",";
           }
            map.put(name,params);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",map.get("sign"),map.get("trade_status"),map.toString());

        //接下来的步骤非常重要,我们要验证回调的准确性,确认是由支付宝发出的回调,同时还要避免支付宝那边的重复通知
        map.remove("sign_type");
        try {
            boolean isAliPay_Callback = AlipaySignature.rsaCheckV2(map, Configs.getAlipayPublicKey(),"utf-8","RSA2");
            if(!isAliPay_Callback){
                logger.error("回调验证失败,此回调不是由支付宝发出的！");
                return ServerResponse.createByErrorMessage("回调验证失败,此回调不是由支付宝发出的！");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调验证发生异常！");
            e.printStackTrace();
        }

        // TODO: 2018/2/19 接下来要验证数据,在service层里校验,包括但不限于订单号、实际金额、商户id是否为该订单号对应的id
        ServerResponse serverResponse = iOrderService.callBack_Check(map);
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }



    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderpayStatus(HttpServletRequest httpServletRequest,Long orderNo){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtil.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
        }
        String userStr = ShardedRedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.queryOrderpayStatus(user.getId(),orderNo);
    }




}
