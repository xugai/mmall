package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by rabbit on 2018/2/9.
 */
@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping(value = "save.do")
    @ResponseBody
    public ServerResponse productSave(HttpServletRequest httpServletRequest, Product product) {
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtil.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
//        }
//        String userStr = ShardedRedisPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStr, User.class);
//        if (user == null) {
//            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
//        }
        // 全部都由拦截器进行登录判断以及管理员权限验证
        return iProductService.productSaveOrUpdate(product);
    }

    @RequestMapping(value = "set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpServletRequest httpServletRequest, Integer productId, Integer status) {
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtil.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
//        }
//        String userStr = ShardedRedisPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStr, User.class);
//        if (user == null) {
//            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
//        }
        // 全部都由拦截器进行登录判断以及管理员权限验证
        return iProductService.setSaleStatus(productId, status);
    }

    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetails(HttpServletRequest httpServletRequest, Integer productId) {
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtil.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
//        }
//        String userStr = ShardedRedisPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStr, User.class);
//        if (user == null) {
//            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
//        }
        // 全部都由拦截器进行登录判断以及管理员权限验证
        return iProductService.getProductDetail(productId);
    }


    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpServletRequest httpServletRequest, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtil.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
//        }
//        String userStr = ShardedRedisPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStr, User.class);
//        if (user == null) {
//            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
//        }
        // 全部都由拦截器进行登录判断以及管理员权限验证
        return iProductService.getList(pageNum, pageSize);
    }


    @RequestMapping(value = "search.do")
    @ResponseBody
    public ServerResponse<PageInfo> productSearch(HttpServletRequest httpServletRequest, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtil.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
//        }
//        String userStr = ShardedRedisPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStr, User.class);
//        if (user == null) {
//            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
//        }
        // 全部都由拦截器进行登录判断以及管理员权限验证
        return iProductService.productSearch(productName, productId, pageNum, pageSize);
    }

    @RequestMapping(value = "upload.do")
    @ResponseBody
    public ServerResponse<Map> uploadFile(HttpServletRequest httpServletRequest, @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request) {
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtil.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("请重新登录后再重试！");
//        }
//        String userStr = ShardedRedisPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStr, User.class);
//        if (user == null) {
//            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
//        }
        // 全部都由拦截器进行登录判断以及管理员权限验证
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix");

        Map filemap = Maps.newHashMap();
        filemap.put("uri", targetFileName);
        filemap.put("url", url);
        return ServerResponse.createBySuccess(filemap);
    }


    @RequestMapping(value = "richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(@RequestParam(value = "file", required = false) MultipartFile file,
                                 HttpServletRequest request, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        /*String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtil.isEmpty(loginToken)){
            resultMap.put("success", false);
            resultMap.put("msg", "请使用管理员身份登录后再操作！");
            return resultMap;
        }
        String userStr = ShardedRedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);*/
        /**
         * 富文本对于返回值有自己的要求,我们使用的是simditor,所以要按照simditor的要求进行返回
         *  "success": true/false,
         "   msg": "error message",
         "   file_path": "[real file path]"
         */
       /* if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请使用管理员身份登录后再操作！");
            return resultMap;
        }*/
        //以下一行代码会实现在该方法被调用时自动在webapp下面创建upload文件夹（若没有则自动创建）
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        if (StringUtils.isBlank(targetFileName)) {
            resultMap.put("success", false);
            resultMap.put("msg", "文件上传失败！");
            return resultMap;
        }

        String url = PropertiesUtil.getProperty("ftp.server.http.prefix");
        resultMap.put("success", true);
        resultMap.put("msg", "上传成功！");
        resultMap.put("file_path", url + targetFileName);
        //和前端约定好头部文件说明
        response.addHeader("Access-Control-Allow-Headers","X-File-Name");
        return resultMap;
    }

}
