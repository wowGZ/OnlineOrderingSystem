package com.biyesheji.controller;

import com.biyesheji.pojo.Customer;
import com.biyesheji.pojo.Order;
import com.biyesheji.pojo.OrderItem;
import com.biyesheji.service.CustomerService;
import com.biyesheji.service.OrderItemService;
import com.biyesheji.service.OrderService;
import com.biyesheji.service.ProductService;

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

        response.setContentType("application/json");
        PrintWriter out = null;
        JsonObject json = new JsonObject();
        try {
            out = response.getWriter();
            Customer resultCustomer = customerService.foreLogin(customer);
            if (resultCustomer == null) {
                json.addProperty("status", -1);// -1 是登录失败,用户不存在
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

        response.setContentType("application/json");
        JsonObject json = new JsonObject();

        successOrFailure(response, json);
    }

    @RequestMapping("/forgetPassword")
    public void forgetPassword(HttpServletResponse response,
                               @RequestParam("name") String name,
                               @RequestParam("password") String password) {
        response.setContentType("application/json");
        PrintWriter out = null;
        JsonObject json = new JsonObject();

        try {
            out = response.getWriter();

            List<Customer> list = customerService.list();

            int flag = 0;
            for (Customer cst :
                    list) {
                if (cst.getName().equals(name)){

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
            if (flag >= 1){
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

        response.setContentType("application/json");
        JsonObject json = new JsonObject();

        successOrFailure(response,json);
    }


    @RequestMapping("/showCustomerAllOrder")
    public void showCustomerAllOrder(HttpServletResponse response,
                                     @RequestParam("id") Integer id){
        Gson gson = new Gson();

        response.setContentType("application/json");
        PrintWriter out = null;
        JsonObject json = new JsonObject();

        try {
            out = response.getWriter();

            Customer customer = customerService.get(id);
            List<Order> list = orderService.list(customer.getId());
            if (list.size() > 0) {
                orderItemService.fill(list);
                json.addProperty("status", 1);//成功
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

        response.setContentType("application/json");
        PrintWriter out = null;
        JsonObject json = new JsonObject();

        try {
            out = response.getWriter();
            json.addProperty("allProduct", gson.toJson(productService.list()));

            System.out.println(gson.toJson(productService.list()));

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

    @RequestMapping("/submitOrder")
    public void submitOrder(HttpServletResponse response,
                            @RequestParam("id") String productId,
                            @RequestParam("number") String productNumber,
                            @RequestParam("customerId") Integer customerId,
                            @RequestParam("address")String address){
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
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);order.setCode(orderCode);
        order.setCode(orderCode);
        order.setAddress(address);
        order.setCstid(customerId);
        order.setStatus(0);
        orderService.add(order, orderItems);

        response.setContentType("application/json");

        JsonObject json = new JsonObject();

        successOrFailure(response,json);
    }

    private void successOrFailure(HttpServletResponse response,JsonObject json) {
        PrintWriter out = null;
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
