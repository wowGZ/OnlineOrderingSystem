package com.biyesheji.dao;

import com.biyesheji.pojo.Review;
import com.biyesheji.pojo.ReviewExample;

import java.util.List;

public interface ReviewMapper extends CrudDao<Review>{

    List<Review> selectByExample(ReviewExample example);

}