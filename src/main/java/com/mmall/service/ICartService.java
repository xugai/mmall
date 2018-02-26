package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * Created by rabbit on 2018/2/13.
 */
public interface ICartService {
    ServerResponse<CartVo> addCart(Integer userId, Integer count, Integer productId);

    ServerResponse<CartVo> updateCart(Integer userId,Integer count,Integer productId);

    ServerResponse<CartVo> deleteCart(Integer userId, String productIds);

    ServerResponse<CartVo> list(Integer userId);

    ServerResponse<CartVo> selectOrUnSelectAll(Integer userId,Integer status);

    ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer status);

    ServerResponse<Integer> selectCartProductCount(Integer userId);
}
