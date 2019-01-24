package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by rabbit on 2018/2/12.
 */
@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetails(Integer productId){
        //与后台不同的地方在于,前台查看产品时我们要查看的产品都是处于在线状态的，下架或删除的都不会显示出来
        return iProductService.getProductDetails(productId);
    }

    @RequestMapping(value = "{productId}", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetailsRESTful(@PathVariable Integer productId){
        //与后台不同的地方在于,前台查看产品时我们要查看的产品都是处于在线状态的，下架或删除的都不会显示出来
        return iProductService.getProductDetails(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getLists(@RequestParam(value="categoryId",required = false) Integer categoryId,
                                             @RequestParam(value="keyword",required = false) String keyword,
                                             @RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
                                             @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                             @RequestParam(value = "orderBy", defaultValue = "") String orderBy){
        return iProductService.getLists(categoryId, keyword, pageNum, pageSize, orderBy);
    }

    @RequestMapping("{categoryId}/{keyword}/{pageNum}/{pageSize}/{orderBy}")
    @ResponseBody
    public ServerResponse<PageInfo> getListsRESTful(@PathVariable(value="categoryId") Integer categoryId,
                                             @PathVariable(value="keyword") String keyword,
                                             @PathVariable(value="pageNum") Integer pageNum,
                                             @PathVariable(value = "pageSize") Integer pageSize,
                                             @PathVariable(value = "orderBy") String orderBy){
        if(pageNum == null){
            pageNum = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }
        if(StringUtils.isBlank(orderBy)){
            orderBy = "price_asc";
        }
        return iProductService.getLists(categoryId, keyword, pageNum, pageSize, orderBy);
    }

    @RequestMapping("categoryId/{categoryId}/{pageNum}/{pageSize}/{orderBy}")
    @ResponseBody
    public ServerResponse<PageInfo> getListsRESTfulWithCategoryId(@PathVariable(value="categoryId") Integer categoryId,
                                                    @PathVariable(value="pageNum") Integer pageNum,
                                                    @PathVariable(value = "pageSize") Integer pageSize,
                                                    @PathVariable(value = "orderBy") String orderBy){
        if(pageNum == null){
            pageNum = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }
        if(StringUtils.isBlank(orderBy)){
            orderBy = "price_asc";
        }
        return iProductService.getLists(categoryId, "", pageNum, pageSize, orderBy);
    }

    @RequestMapping("keyword/{keyword}/{pageNum}/{pageSize}/{orderBy}")
    @ResponseBody
    public ServerResponse<PageInfo> getListsRESTfulWithKeyWord( @PathVariable(value="keyword") String keyword,
                                                    @PathVariable(value="pageNum") Integer pageNum,
                                                    @PathVariable(value = "pageSize") Integer pageSize,
                                                    @PathVariable(value = "orderBy") String orderBy){
        if(pageNum == null){
            pageNum = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }
        if(StringUtils.isBlank(orderBy)){
            orderBy = "price_asc";
        }
        return iProductService.getLists(null, keyword, pageNum, pageSize, orderBy);
    }



}
