package com.biyesheji.dao;

import com.biyesheji.pojo.OrderItem;
import com.biyesheji.pojo.OrderItemExample;

import java.util.List;

public interface OrderItemMapper extends CrudDao<OrderItem>{

    List<OrderItem> selectByExample(OrderItemExample example);

}