package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * Created by rabbit on 2018/2/9.
 */
public interface IProductService {

    Integer getProductCount();

    ServerResponse productSaveOrUpdate(Product product);

    ServerResponse setSaleStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getList(Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> productSearch(String productName,Integer productId,Integer pageNum,Integer pageSize);

    ServerResponse<ProductDetailVo> getProductDetails(Integer productId);

    ServerResponse<PageInfo> getLists(Integer categoryId,String keyword,Integer pageNum,Integer pageSize,String orderBy);
}
