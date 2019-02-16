package com.mmall.controller.backend;

import com.mmall.common.ServerResponse;
import com.mmall.service.IOrderService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * Created by rabbit on 2019/2/16.
 */
@RequestMapping("/manage/statistic")
@Controller
public class StatisticController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("base_count.do")
    @ResponseBody
    public ServerResponse<HashMap<String, Integer>> basicCount(){
        HashMap<String, Integer> statisticMap = new HashMap<String, Integer>();
        int userCount = iUserService.getUserCount();
        int productCount = iProductService.getProductCount();
        int orderCount = iOrderService.getOrderCount();
        statisticMap.put("userCount", userCount);
        statisticMap.put("productCount", productCount);
        statisticMap.put("orderCount", orderCount);
        return ServerResponse.createBySuccess(statisticMap);
    }
}
