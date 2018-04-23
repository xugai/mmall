package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.utils.DateTimeUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by rabbit on 2018/2/9.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    //服务层调用服务层对象,属于平级注入或同级注入
    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse productSaveOrUpdate(Product product){
        if(product == null){
            return ServerResponse.createByErrorMessage("请求的参数错误,请重试！");
        }
        if(StringUtils.isNotBlank(product.getSubImages())){
            String [] imagesStr = product.getSubImages().split(",");
            if(imagesStr.length > 0) {
                String mainImage = imagesStr[0];
                product.setMainImage(mainImage);
            }
        }
        if(product.getId() == null){
            //说明是要新增产品
            int resultCount = productMapper.insert(product);
            if(resultCount > 0){
                return ServerResponse.createBySuccessMessage("新增产品保存成功！");
            }else{
                return ServerResponse.createByErrorMessage("新增产品保存失败！");
            }
        }else{
            //说明是要更新产品相关信息
            int resultCount = productMapper.updateByPrimaryKey(product);
            if(resultCount > 0){
                return ServerResponse.createBySuccessMessage("产品信息修改成功！");
            }else{
                return ServerResponse.createByErrorMessage("产品信息修改失败！");
            }
        }
    }

    public ServerResponse setSaleStatus(Integer productId,Integer status){
        if(productId == null || status == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARUGMENT.getCode(),ResponseCode.ILLEGAL_ARUGMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int resultCount = productMapper.updateByPrimaryKeySelective(product);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("产品上下架状态更新成功！");
        }
        return ServerResponse.createByErrorMessage("产品上下架状态更新失败！");
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorMessage("参数异常,请重试！");
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        return ServerResponse.createBySuccess("获取商品详情成功！",assembleProductDetailVo(product));
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //imageHost
        String imageHost = PropertiesUtil.getProperty("ftp.server.http.prefix","ftp://192.168.230.128/");
        productDetailVo.setImageHost(imageHost);
        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }


    public ServerResponse<PageInfo> getList(Integer pageNum,Integer pageSize){
        //startPage-->start
        //填充自己的sql查询逻辑
        //pageHelper-->收尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectProductList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo p = assembleProductListVo(product);
            productListVoList.add(p);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();

        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setSubtitle(product.getSubtitle());

        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","ftp://192.168.230.128/"));
        return productListVo;
    }

    public ServerResponse<PageInfo> productSearch(String productName,Integer productId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if(productName != null){
            //如果传入的productName不为空,则对该参数值进行模糊查找
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.productSearch(productName, productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product: productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse<ProductDetailVo> getProductDetails(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARUGMENT.getCode(),ResponseCode.ILLEGAL_ARUGMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        if(product.getStatus() == Const.ProductStatusEnum.ON_SALE.getCode()){
            ProductDetailVo productDetailVo = assembleProductDetailVo(product);
            return ServerResponse.createBySuccess(productDetailVo);
        }
        return ServerResponse.createByErrorMessage("产品已下架或者删除！");
    }


    /**
     * 重点研究！！！
     * @param categoryId
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    public ServerResponse<PageInfo> getLists(Integer categoryId,String keyword,Integer pageNum,Integer pageSize,String orderBy){
        if(categoryId == null && StringUtils.isBlank(keyword)){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARUGMENT.getCode(),ResponseCode.ILLEGAL_ARUGMENT.getDesc());
        }
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                //没有该分类,并且没有该关键字,我们不报错但返回一个空值回去
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVos = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVos);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //若categoryId不为空,则考虑到可能有子节点,搜索遍历出当前节点及其子节点出来
            categoryIdList = iCategoryService.getDeepCategory(categoryId).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        //排序处理,获取从前端返回的排序选择字符串(asc/desc),然后让MyBatis进行动态排序
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.productListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String [] orderByArray = orderBy.split("_");
                //因为MyBatis默认的排序格式是"price asc/desc",所以我们要进行字符串的切割
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size() == 0?null:categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product: productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
