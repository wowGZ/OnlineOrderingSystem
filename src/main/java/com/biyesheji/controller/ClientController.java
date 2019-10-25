package com.biyesheji.controller;

import com.biyesheji.pojo.Customer;
import com.biyesheji.pojo.Order;
import com.biyesheji.pojo.OrderItem;
import com.biyesheji.service.CustomerService;
import com.biyesheji.service.OrderItemService;
import com.biyesheji.service.OrderService;
import com.biyesheji.service.ProductService;

import com.biyesheji.util.MD5Util;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by WowGz
 * User: WowGz
 * Date: 2019/10/21
 * Time: 15:46
 * Blog: https://wowgz.com.cn
 */
@Controller
@RequestMapping("/android")
public class ClientController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;

    @RequestMapping("/login")
    public void loginFromClient(HttpServletResponse response,
                                @RequestParam("name") String name,
                                @RequestParam("password") String password) {
        Gson gson = new Gson();

        Customer customer = new Customer();
        customer.setName(name);
        customer.setPassword(password);
        System.out.println(customer.toString());

        response.setContentType("application/json;charset=UTF-8");

        PrintWriter out = null;
        JsonObject json = new JsonObject();
        try {
            out = response.getWriter();
            Customer resultCustomer = customerService.foreLogin(customer);
            if (resultCustomer == null) {
                json.addProperty("status", -1);// -1 是登录失败,用户不存在/密码错误
            } else {
                json.addProperty("status", 1);// 1 是登录成功
                json.addProperty("customer", gson.toJson(resultCustomer));
            }
            out.write(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            json.addProperty("status", -2);// -2 是登录失败,后台出现IO异常
            out.write(json.toString());
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping("/signIn")
    public void signIn(HttpServletResponse response,
                       @RequestParam("name") String name,
                       @RequestParam("password") String password,
                       @RequestParam("address") String address,
                       @RequestParam("phone") String phone) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setPassword(password);
        customer.setAddress(address);
        customer.setPhone(phone);
        customer.setStatus(0);//默认为普通会员

        customerService.save(customer);

        JsonObject json = new JsonObject();

        successOrFailure(response, json);
    }

    @RequestMapping("/forgetPassword")
    public void forgetPassword(HttpServletResponse response,
                               @RequestParam("name") String name,
                               @RequestParam("password") String password) {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = null;
        JsonObject json = new JsonObject();

        try {
            out = response.getWriter();

            List<Customer> list = customerService.list();

            int flag = 0;
            for (Customer cst :
                    list) {
                if (cst.getName().equals(name)) {

                    System.out.println(cst.toString());
                    System.out.println("------------------------------------------------------");

                    cst.setPassword(password);

                    System.out.println(cst.toString());
                    System.out.println("------------------------------------------------------");

                    customerService.update(cst);
                    flag++;
                    break;
                }
            }
            if (flag >= 1) {
                json.addProperty("status", 1);
            } else {
                json.addProperty("status", -1);
            }
            out.write(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            json.addProperty("status", -2);//后台IO异常
            out.write(json.toString());
        } finally {
            out.flush();
            out.close();
        }


    }

    @RequestMapping("/updatePersonalInfo")
    public void updatePersonalInfo(HttpServletResponse response,
                                   @RequestParam("id") Integer id,
                                   @RequestParam("name") String name,
                                   @RequestParam("password") String password,
                                   @RequestParam("address") String address,
                                   @RequestParam("phone") String phone) {
        Customer customer = new Customer(id, name, password, address, phone, Integer.parseInt("0"));

        customerService.update(customer);

        JsonObject json = new JsonObject();

        successOrFailure(response, json);
    }


    @RequestMapping("/showCustomerAllOrder")
    public void showCustomerAllOrder(HttpServletResponse response,
                                     @RequestParam("id") Integer id) {
        Gson gson = new Gson();

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = null;
        JsonObject json = new JsonObject();

        try {
            out = response.getWriter();

            Customer customer = customerService.get(id);
            List<Order> list = orderService.list(customer.getId());
            if (list.size() > 0) {
                orderItemService.fill(list);
                json.addProperty("status", 1);//成功
                json.addProperty("phone", customer.getPhone());
                json.addProperty("orderList", gson.toJson(list));//返回相关数据
            } else if (list.size() == 0) {
                json.addProperty("status", -1);//为查询到相关数据
            } else {
                json.addProperty("status", -2);
            }
            out.write(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            json.addProperty("status", -2);
            out.write(json.toString());
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping("/showAllProduct")
    public void showAllProduct(HttpServletResponse response) {
        Gson gson = new Gson();

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = null;
        JsonObject json = new JsonObject();

        try {
            out = response.getWriter();
//            json.addProperty("allProduct", gson.toJson(productService.list()));
            System.out.println(gson.toJson(productService.list()));
            out.write(gson.toJson(productService.list()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping("/submitOrder")
    public void submitOrder(HttpServletResponse response,
                            @RequestParam("id") String productId,
                            @RequestParam("number") String productNumber,
                            @RequestParam("customerId") Integer customerId,
                            @RequestParam("address") String address,
                            @RequestParam("phone")String phone) {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        PrintWriter out = null;
        response.setContentType("application/json;charset=UTF-8");

        //判断是否游客登录，游客登录则进行初始化帐号密码，非游客则查询数据库获取游客详情
        Customer customer = new Customer();
        if (customerId == -1) {

            customer.setPassword(phone);
            customer.setName(phone);
            customer.setAddress(address);
            customer.setStatus(0);
            customer.setPhone(phone);

            customerService.save(customer);

            for (Customer c :
                    customerService.list()) {
                if (c.getName().equals(customer.getName()) && c.getPassword().equals(MD5Util.getStringMD5(customer.getPassword()))) {
                    customer.setId(c.getId());
                    break;
                }
            }

            System.out.println(customer.getId());
            customerId = customer.getId();
        } else {
            customer.setId(customerId);
            customer = customerService.get(customerId);
        }
        json.addProperty("customer", gson.toJson(customer));//json传回用户信息

        //进行订单的相关设置，以及设置完成之后进行订单的提交
        String[] productIds = productId.split(",");
        String[] productNumbers = productNumber.split(",");

        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < productIds.length; i++) {
            OrderItem oi = new OrderItem();
            oi.setCstid(customerId);
            oi.setNumber(Integer.parseInt(productNumbers[i]));
            oi.setPid(Integer.parseInt(productIds[i]));
            orderItemService.save(oi);
            orderItems.add(oi);
        }

        Order order = new Order();
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setCode(orderCode);
        order.setAddress(address);
        order.setCstid(customerId);
        order.setStatus(0);
        orderService.save(order);

        List<Order> list = orderService.list();
        for (Order o :
                list) {
            if (o.getCode().equals(order.getCode())) {
                order.setId(o.getId());
                break;
            }
        }

        for (OrderItem oi :
                orderItems) {
            oi.setOid(order.getId());
            orderItemService.update(oi);
        }

        //json传回成功与否
        successOrFailure(response, json);
    }

    private void successOrFailure(HttpServletResponse response, JsonObject json) {
        PrintWriter out = null;
        response.setContentType("application/json;charset=UTF-8");
        try {
            out = response.getWriter();
            json.addProperty("status", 1);
            out.write(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            json.addProperty("status", -2);
            out.write(json.toString());
        } finally {
            out.flush();
            out.close();
        }
    }
}
