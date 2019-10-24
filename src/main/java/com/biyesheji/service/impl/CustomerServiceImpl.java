package com.biyesheji.service.impl;

import com.biyesheji.dao.CustomerMapper;
import com.biyesheji.pojo.Customer;
import com.biyesheji.pojo.CustomerExample;
import com.biyesheji.service.CustomerService;
import com.biyesheji.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public Customer foreLogin(Customer customer) {
        CustomerExample example = new CustomerExample();
        customer.setPassword(MD5Util.getStringMD5(customer.getPassword()));
        example.createCriteria().andNameEqualTo(customer.getName()).andPasswordEqualTo(customer.getPassword());
        List<Customer> customers = customerMapper.selectByExample(example);

        return customers.size()>0?customers.get(0):null;
    }

    @Override
    public void save(Customer customer) {
        customer.setPassword(MD5Util.getStringMD5(customer.getPassword()));
        customerMapper.insert(customer);
    }

    @Override
    public void update(Customer customer){
        customerMapper.updateByPrimaryKey(customer);
    }

//    @Override
//    public void updateByName(Customer customer) {
//        customerMapper.updateByName(customer);
//    }
//
//    @Override
//    public Customer selectByName(Customer customer) {
//        return customerMapper.selectByName(customer);
//    }

    @Override
    public Customer get(int cstid) {
        return customerMapper.selectByPrimaryKey(cstid);
    }

    @Override
    public List<Customer> list() {
        return customerMapper.selectByExample(null);
    }

    @Override
    public void shezhihuiyuan(int id) {
        customerMapper.enableStatus(id);
    }

    @Override
    public void del(int id) {
        customerMapper.deleteByPrimaryKey(id);
    }


}
