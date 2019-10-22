package com.biyesheji.service;

import com.biyesheji.pojo.Customer;

import java.util.List;

public interface CustomerService extends CrudService<Customer>{

    /**
     * 返回登陆的用户
     * @param customer
     * @return
     */
    public Customer foreLogin(Customer customer);

    /**
     * 设置会员
     * @param id
     */
    public void shezhihuiyuan(int id);


    public void update(Customer customer);

//    public void updateByName(Customer customer);
//
//    public Customer selectByName(Customer customer);



}
