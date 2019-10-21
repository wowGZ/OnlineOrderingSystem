package com.biyesheji.dao;

import com.biyesheji.pojo.Category;
import com.biyesheji.pojo.CategoryExample;

import java.util.List;

public interface CategoryMapper extends CrudDao<Category>{

     List<Category> selectByExample(CategoryExample example);

}