package com.xq.tmall.tmall05.controller.admin;

import com.xq.tmall.tmall05.controller.BaseController;
import com.xq.tmall.tmall05.entity.User;
import com.xq.tmall.tmall05.service.UserService;
import com.xq.tmall.tmall05.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 后台管理-用户页
 */
@Controller
public class UserController extends BaseController {


    @Autowired
    private UserService userService;

    //转到后台管理-用户页-ajax
    @RequestMapping("admin/user")
    public String goUserManagePage(HttpSession session, Map<String, Object> map) {
        logger.info("检查管理员权限");
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }

        //用户详细信息


        logger.info("获取前十条用户信息");
        PageUtil pageUtil = new PageUtil(0, 10);
        List<User> userList = userService.getList(null, null, pageUtil);
        map.put("userList", userList);
        logger.info("获取用户总数量");
        Integer userCount = userService.getTotal(null);
        map.put("userCount", userCount);
        logger.info("获取分页信息");
        pageUtil.setTotal(userCount);
        map.put("pageUtil", pageUtil);

        logger.info("转到后台管理-用户页");






        return "admin/userManagePage";
    }
}