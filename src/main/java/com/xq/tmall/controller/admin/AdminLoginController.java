package com.xq.tmall.controller.admin;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 后台管理-登录页
 */

@Controller
@RequestMapping("/homePage")
public class AdminLoginController  {

    @RequestMapping("/home")
    public String homePage(){



        return "admin/homePage";
    }


}