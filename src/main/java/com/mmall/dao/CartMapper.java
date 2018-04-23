package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdAndProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);

    List<Cart> setectCartsByUserId(Integer userId);

    int isAllChecked(Integer userId);

    int deleteCart(@Param("userId") Integer userId,@Param("productIdList") List<String> productIdList);

    int selectOrUnSelect(@Param("userId") Integer userId,@Param("productId") Integer productId,@Param("status") Integer status);

    int selectCartProductCount(Integer userId);

    List<Cart> selectCheckedCartByUserID(Integer userId);
}