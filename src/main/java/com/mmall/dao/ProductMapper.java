package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {

    int getProductCount();

    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectProductList();

    List<Product> productSearch(@Param("productName") String productName, @Param("productId") Integer productId);

    List<Product> selectByNameAndCategoryIds (@Param("productName") String productName,@Param("categoryIdList") List<Integer> categoryIdList);

    /**
     *这里必须要用Integer接收sql执行的返回值,因为可能有商品在这个时候被删除了,
     *这样其库存就是null,但用int接收的话会报错,因此我们要用Integer去接收
     * @param productId
     * @return
     */
    Integer getStockByProductId(Integer productId);
}