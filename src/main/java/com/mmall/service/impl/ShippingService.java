package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by rabbit on 2018/2/16.
 */
@Service("iShippingService")
public class ShippingService implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse add(Integer userId, Shipping shipping){
        if(shipping == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARUGMENT.getCode(), ResponseCode.ILLEGAL_ARUGMENT.getDesc());
        }
        shipping.setUserId(userId);
        /**
         * 在xml里面的insert方法中有一个属性useGeneratedKeys，当useGeneratedKeys=true时执行完语句后会把ID返回给添加的对象
         */
        int resultCount = shippingMapper.insert(shipping);
        if(resultCount > 0){
            Map map = Maps.newHashMap();
            map.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新增收货地址成功！",map);
        }
        return ServerResponse.createByErrorMessage("新增收货地址失败！");
    }

    public ServerResponse update(Integer userId,Shipping shipping){
        if(shipping == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARUGMENT.getCode(), ResponseCode.ILLEGAL_ARUGMENT.getDesc());
        }
        shipping.setUserId(userId);
        int resultCount = shippingMapper.updateByUserId(shipping);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("更新收货地址成功！");
        }
        return ServerResponse.createByErrorMessage("更新收货地址失败！");
    }


    public ServerResponse del(Integer userId, Integer shippingId){
        if(shippingId == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARUGMENT.getCode(), ResponseCode.ILLEGAL_ARUGMENT.getDesc());
        }
        int resultCount = shippingMapper.deleteByUserIdAndPrimaryKey(userId, shippingId);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("删除收货地址成功！");
        }
        return ServerResponse.createByErrorMessage("删除收货地址失败！");
    }

    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        if(shippingId == null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARUGMENT.getCode(), ResponseCode.ILLEGAL_ARUGMENT.getDesc());
        }
        Shipping shipping = shippingMapper.selectByUserIdAndPrimaryKey(userId, shippingId);
        if(shipping != null){
            return ServerResponse.createBySuccess(shipping);
        }
        return ServerResponse.createByErrorMessage("查找收货地址失败！");
    }

    public ServerResponse<PageInfo> list(Integer userId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = Lists.newArrayList();
        shippingList = shippingMapper.getShippingListByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        if(shippingList.size() == 0){
            return ServerResponse.createBySuccess("您暂未添加任何收货地址,请添加新的收货地址！",pageInfo);
        }
        return ServerResponse.createBySuccess(pageInfo);
    }

}
