package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getLists(@RequestParam(value="categoryId",required = false) Integer categoryId,
                                             @RequestParam(value="keyword",required = false) String keyword,
                                             @RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
                                             @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                             @RequestParam(value = "orderBy", defaultValue = "") String orderBy){
        return iProductService.getLists(categoryId, keyword, pageNum, pageSize, orderBy);
    }

}
