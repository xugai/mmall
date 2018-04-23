package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by rabbit on 2018/2/9.
 */
public interface ICategoryService {

    ServerResponse addCategory(Integer parentId, String categoryName);

    ServerResponse setCategoryName(Integer categoryId,String categoryName);

    ServerResponse<List<Category>> getCategory(Integer categoryId);

    ServerResponse<List<Integer>> getDeepCategory(Integer categoryId);
}
