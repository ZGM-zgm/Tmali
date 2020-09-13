package com.xq.tmall.tmall05.controller.admin;


import com.alibaba.fastjson.JSONObject;
import com.xq.tmall.tmall05.controller.BaseController;
import com.xq.tmall.tmall05.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 后台管理-登录页
 */

@Controller
public class AdminLoginController extends BaseController {

        @Autowired
    private AdminService adminService;

    @RequestMapping("/login")
    public String goToPage(){

        logger.info("转到后台管理-登录页");

        return "admin/loginPage";
    }

    //登陆验证-ajax
    @ResponseBody
    @RequestMapping(value = "admin/login/doLogin",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public String checkLogin(HttpSession session,  String username,  String password) {
        logger.info("管理员登录验证");
        Integer admin = adminService.login(username,password);

        JSONObject object = new JSONObject();
        if(admin == null){
            logger.info("登录验证失败");
            object.put("success",false);
        } else {
            logger.info("登录验证成功，管理员ID传入会话");
            session.setAttribute("adminId",admin);
            object.put("success",true);
        }

        return object.toJSONString();
    }
}