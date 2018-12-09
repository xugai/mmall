package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.utils.BigDecimalUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by rabbit on 2018/2/13.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVo> addCart(Integer userId,Integer count,Integer productId){
        if(count == null || productId == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARUGMENT.getCode(),ResponseCode.ILLEGAL_ARUGMENT.getDesc());
        }
        //先判断要添加的商品是否在当前用户的购物车内
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if(cart == null){
            //如果不在当前用户的购物车内,则要新增一条产品数据
            Cart itemCart = new Cart();
            itemCart.setUserId(userId);
            itemCart.setQuantity(count);
            itemCart.setProductId(productId);
            itemCart.setChecked(Const.Cart.CHECKED);
            int resultCount = cartMapper.insert(itemCart);
            if(resultCount <= 0){
                return ServerResponse.createByErrorMessage("添加商品失败！");
            }
        }else{
            //如果商品已经存在当前用户的购物车内,则修改该商品在购物车中的数量
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKey(cart);
        }
        return this.list(userId);
    }

    public ServerResponse<CartVo> updateCart(Integer userId,Integer count,Integer productId){
        if(count == null || productId == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARUGMENT.getCode(),ResponseCode.ILLEGAL_ARUGMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if(cart != null){
            Cart cartForUpdate = new Cart();
            cartForUpdate.setId(cart.getId());
            cartForUpdate.setQuantity(count);
            int resultCount = cartMapper.updateByPrimaryKeySelective(cartForUpdate);
            //若更新失败,则返回空值
            if(resultCount <= 0){
               return ServerResponse.createByErrorMessage("购物车更新失败！");
            }
        }
        return this.list(userId);
    }

    public ServerResponse<CartVo> deleteCart(Integer userId, String productIds){
        if(StringUtils.isBlank(productIds)){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARUGMENT.getCode(),ResponseCode.ILLEGAL_ARUGMENT.getDesc());
        }
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        cartMapper.deleteCart(userId,productIdList);
//        CartVo cartVo = this.getCartVo(userId);
        return this.list(userId);
    }

    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo = this.getCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> selectOrUnSelectAll(Integer userId,Integer status){
        if(status == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARUGMENT.getCode(),ResponseCode.ILLEGAL_ARUGMENT.getDesc());
        }
        cartMapper.selectOrUnSelect(userId,null,status);
        return this.list(userId);
    }

    public ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer status){
        if(productId == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARUGMENT.getCode(),ResponseCode.ILLEGAL_ARUGMENT.getDesc());
        }
        cartMapper.selectOrUnSelect(userId,productId,status);
        return this.list(userId);
    }

    public ServerResponse<Integer> selectCartProductCount(Integer userId){
        int productsQuantityCount = cartMapper.selectCartProductCount(userId);
        return ServerResponse.createBySuccess(productsQuantityCount);
    }




    private CartVo getCartVo(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.setectCartsByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        //初始化购物车的总价
        BigDecimal cartTotalPrice = new BigDecimal("0");

        for (Cart cart : cartList) {
            CartProductVo cartProductVo = new CartProductVo();
            cartProductVo.setId(cart.getId());
            cartProductVo.setUserId(userId);
            cartProductVo.setProductChecked(cart.getChecked());
            cartProductVo.setProductId(cart.getProductId());

            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if(product != null){
                cartProductVo.setProductName(product.getName());
                cartProductVo.setProductMainImage(product.getMainImage());
                cartProductVo.setProductPrice(product.getPrice());
                cartProductVo.setProductSubtitle(product.getSubtitle());
                cartProductVo.setProductStatus(product.getStatus());
                cartProductVo.setProductStock(product.getStock());
                //判断库存
                int buyLimitCount = 0;
                if(product.getStock() >= cart.getQuantity()){
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    buyLimitCount = cart.getQuantity();
                    cartProductVo.setQuantity(buyLimitCount);
                }else{
                    //若库存小于购物车内的商品数量,则强制把购物车数量设置为库存数量,也就是最大可购买量
                    //同时在数据库中也把该购物车内商品的数量纠正为库存数量
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                    buyLimitCount = product.getStock();
                    cartProductVo.setQuantity(buyLimitCount);

                    Cart cartForQuantity = new Cart();
                    cartForQuantity.setId(cart.getId());
                    cartForQuantity.setQuantity(buyLimitCount);
                    int resultCount = cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    //如果更新数据库失败,则返回空值
                    if(resultCount <= 0){
                        return null;
                    }
                }
                //开始计算总价
                cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
            }
            if(cart.getChecked() == Const.Cart.CHECKED){
                cartTotalPrice = BigDecimalUtil.add(cartProductVo.getProductTotalPrice().doubleValue(),cartTotalPrice.doubleValue());
            }
            cartProductVoList.add(cartProductVo);
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setAllChecked(this.isAllChecked(userId));
        cartVo.setImageHost("http://img.immall.tk/");
        return cartVo;
    }

    private boolean isAllChecked(Integer userId){
        if(userId == null){
            return false;
        }
        int resultCount = cartMapper.isAllChecked(userId);
        if(resultCount != 0){
            return false;
        }
        return true;
    }
}
