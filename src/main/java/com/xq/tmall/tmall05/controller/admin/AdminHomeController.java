package com.xq.tmall.tmall05.controller.admin;

import com.xq.tmall.tmall05.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.Map;

/*
 * 后台管理-主页
 */
@Controller
public class AdminHomeController extends BaseController {

    //转到后台管理-主页
    @RequestMapping("/admin")
    public String goToPage(HttpSession session, Map<String, Object> map) throws ParseException {
        logger.info("检查管理员权限");
        Object adminId = checkAdmin(session);
        System.out.println(adminId);
        if (adminId == null) {
            return "redirect:/login";
        }

        return "admin/homePage";
    }


}