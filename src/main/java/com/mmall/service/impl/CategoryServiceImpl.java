package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by rabbit on 2018/2/9.
 */
@Service("iCategoryService")
@Slf4j
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

//    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    public ServerResponse addCategory(Integer parentId,String categoryName){
        //判断参数是否异常
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误,请重试！");
        }
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);   //这个分类是可用的
        int resultCount = categoryMapper.insert(category);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("添加商品种类成功！");
        }
        return ServerResponse.createByErrorMessage("添加商品种类失败！");
    }

    public ServerResponse setCategoryName(Integer categoryId,String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误,请重试！");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("商品种类名称修改成功！");
        }
        return ServerResponse.createByErrorMessage("商品种类名称修改失败！");
    }

    public ServerResponse<List<Category>> getCategory(Integer categoryId){
        if(categoryId == null){
            return ServerResponse.createByErrorMessage("参数错误,请重试！");
        }
        List<Category> categoryList = categoryMapper.getCategory(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            log.debug("未找到当前节点的子节点！");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    public ServerResponse<List<Integer>> getDeepCategory(Integer categoryId){
        if(categoryId == null){
            return ServerResponse.createByErrorMessage("参数错误,请重试！");
        }
        Set<Category> categorySet = Sets.newHashSet();
        categorySet = recursionGetCategoryId(categorySet,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        for (Category c: categorySet) {
            categoryIdList.add(c.getId());
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    public Set<Category> recursionGetCategoryId(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.getCategory(categoryId); //用于获取当前节点的子节点集合
        for (Category c: categoryList) {
            recursionGetCategoryId(categorySet,c.getId());
        }
        return categorySet;
    }
}
