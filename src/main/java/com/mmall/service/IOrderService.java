package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * Created by rabbit on 2018/2/17.
 */
public interface IOrderService {

    int getOrderCount();

    ServerResponse<Map> pay(Long orderNo, Integer userId, String path);

    ServerResponse callBack_Check(Map<String,String> map);

    ServerResponse<Boolean> queryOrderpayStatus(Integer userId,Long orderNo);

    ServerResponse createOrder(Integer userId,Integer shippingId);

    ServerResponse cancel(Integer userId,Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    //用户使用
    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> manageOrderList(Integer pageNum,Integer pageSize);

    //管理员使用
    ServerResponse<OrderVo> getOrderDetail(Long orderNo);

    ServerResponse<PageInfo> searchOrder(Long orderNo,Integer pageNum,Integer pageSize);

    ServerResponse sendOrderGoods(Long orderNo);

    void closeOrder(int hour);
}
