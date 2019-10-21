package com.biyesheji.dao;

import com.biyesheji.pojo.Order;
import com.biyesheji.pojo.OrderExample;

import java.util.List;

public interface OrderMapper extends CrudDao<Order>{

    List<Order> selectByExample(OrderExample example);

}