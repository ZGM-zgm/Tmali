package com.xq.tmall.tmall05.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.xq.tmall.tmall05.controller.BaseController;
import com.xq.tmall.tmall05.entity.Admin;
import com.xq.tmall.tmall05.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 后台管理-账户页
 */
@Controller
public class AccountController extends BaseController {
    @Autowired
    private AdminService adminService;

    //转到后台管理-账户页-ajax
    @RequestMapping("/admin/account")
    public String goToPage(HttpSession session, Map<String, Object> map){
        logger.info("检查管理员权限");
        Object adminId = checkAdmin(session);
        if(adminId == null){
            return "admin/include/loginMessage";
        }

        Admin admin = adminService.get(null,Integer.parseInt(adminId.toString()));
        map.put("admin",admin);
        return "admin/accountManagePage";
    }

    //退出当前账号
    @RequestMapping(value = "admin/account/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        Object o = session.getAttribute("adminId");
        if (o == null) {


        } else {
            session.removeAttribute("adminId");
            session.invalidate();
            logger.info("登录信息已清除，返回管理员登陆页");
        }
        return "redirect:/admin/login";
    }


    //更新管理员信息
    /**
     * RequestParam将请求参数绑定到你控制器的方法参数上
     *PathVariable 映射 URL 绑定的占位符
     */
    @ResponseBody
    @RequestMapping(value = "admin/account/{admin_id}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateAdmin(HttpSession session, @RequestParam String admin_nickname/*管理员昵称*/,
                              @RequestParam(required = false) String admin_password/*管理员当前密码*/,
                              @RequestParam(required = false) String admin_newPassword/*管理员新密码*/,
                              @RequestParam(required = false) String admin_profile_picture_src/*管理员头像路径*/,
                              @PathVariable("admin_id") String admin_id/*管理员编号*/) {
        //检查管理员权限
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }
        JSONObject jsonObject = new JSONObject();
        Admin putAdmin = new Admin();
        putAdmin.setAdmin_id(Integer.valueOf(admin_id));
        putAdmin.setAdmin_nickname(admin_nickname);

        //修改管理员账户的密码
        if (admin_password != null && !"".equals(admin_password) && admin_newPassword != null && !"".equals(admin_newPassword)) {
            Admin admin = adminService.get(null, Integer.valueOf(adminId.toString()));
            if (adminService.login(admin.getAdmin_name(), admin_password) != null) {
                putAdmin.setAdmin_password(admin_newPassword);
            } else {
                jsonObject.put("success", false);
                jsonObject.put("message", "原密码输入有误！");
                return jsonObject.toJSONString();
            }
        }

        //修改数据库的密码
        Boolean yn = adminService.update(putAdmin);
        //修改之后做条件判断，成功则返回登陆界面，并清除当前的登陆记录
        if (yn) {
            jsonObject.put("success", true);
            session.removeAttribute("adminId");
            session.invalidate();
            logger.info("更新成功,登录信息已清除");
        } else {
            jsonObject.put("success", false);
            logger.warn("更新失败！");
        }

        return jsonObject.toJSONString();
    }
}