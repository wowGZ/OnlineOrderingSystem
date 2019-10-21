package com.biyesheji.controller;

import com.biyesheji.pojo.Customer;
import com.biyesheji.service.CustomerService;
import com.biyesheji.service.ProductService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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

    @RequestMapping("/login")
    public void loginFromClient(HttpServletRequest request, HttpServletResponse response, Model model,
                                  @RequestParam("name") String name,
                                  @RequestParam("password") String password){
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
    public void signIn(HttpServletRequest request, HttpServletResponse response, Model model,
                       @RequestParam("name") String name,
                       @RequestParam("password") String password,
                       @RequestParam("address") String address,
                       @RequestParam("phone")String phone){
        Customer customer = new Customer();
        customer.setName(name);
        customer.setPassword(password);
        customer.setAddress(address);
        customer.setPhone(phone);
        customer.setStatus(0);//默认为普通会员

        customerService.save(customer);

        response.setContentType("application/json");
        PrintWriter out = null;
        JsonObject json = new JsonObject();

        successOrFailure(response, out, json);
    }

    @RequestMapping("/forgetPassword")
    public void forgetPassword(HttpServletRequest request, HttpServletResponse response, Model model,
                               @RequestParam("name") String name,
                               @RequestParam("password") String password){
        Customer customer = new Customer();
        customer.setName(name);

        Customer resultCustomer = customerService.foreLogin(customer);
        resultCustomer.setPassword(password);

        customerService.update(resultCustomer);

        response.setContentType("application/json");
        PrintWriter out = null;
        JsonObject json = new JsonObject();

        successOrFailure(response, out, json);

    }

    @RequestMapping("/updatePersonalInfo")
    public void updatePersonalInfo(HttpServletRequest request, HttpServletResponse response, Model model,
                                   @RequestParam("id") Integer id,
                                   @RequestParam("name") String name,
                                   @RequestParam("password") String password,
                                   @RequestParam("address") String address,
                                   @RequestParam("phone")String phone){
        Customer customer = new Customer(id, name, password, address, phone, Integer.parseInt("0"));

        customerService.update(customer);

        response.setContentType("application/json");
        PrintWriter out = null;
        JsonObject json = new JsonObject();

        successOrFailure(response, out, json);
    }


    @RequestMapping("/showAllProduct")
    public void showAllProduct(HttpServletResponse response){
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
    private void successOrFailure(HttpServletResponse response, PrintWriter out, JsonObject json) {
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
