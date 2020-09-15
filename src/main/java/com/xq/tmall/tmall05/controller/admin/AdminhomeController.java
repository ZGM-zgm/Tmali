package com.xq.tmall.tmall05.controller.admin;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.tmall.tmall05.controller.BaseController;
import com.xq.tmall.tmall05.entity.Admin;
import com.xq.tmall.tmall05.entity.OrderGroup;
import com.xq.tmall.tmall05.service.AdminService;
import com.xq.tmall.tmall05.service.ProductOrderService;
import com.xq.tmall.tmall05.service.ProductService;
import com.xq.tmall.tmall05.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//后台管理-首页
@Controller
public class AdminhomeController extends BaseController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private ProductOrderService productOrderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @GetMapping("/admin")
    public String admin(HttpSession session, Map<String, Object> map) throws ParseException {
        Object adminId = checkAdmin(session);
        if (adminId == null){
            return "admin/loginPage";
        }
        Admin admin = adminService.get(null, Integer.parseInt(adminId.toString()));
        map.put("admin", admin);
        Integer productTotal = productService.getTotal(null, new Byte[]{0, 2});
        Integer userTotal = userService.getTotal(null);
        Integer orderTotal = productOrderService.getTotal(null, new Byte[]{3});
        map.put("jsonObject", getChartData(null,null));
        map.put("productTotal", productTotal);
        map.put("userTotal", userTotal);
        map.put("orderTotal", orderTotal);
        return "admin/homePage";
    }

    @GetMapping("admin/home")
    public String home(HttpSession session, Map<String, Object> map) throws ParseException {
        Integer productTotal = productService.getTotal(null, new Byte[]{0, 2});
        Integer userTotal = userService.getTotal(null);
        Integer orderTotal = productOrderService.getTotal(null, new Byte[]{3});
        map.put("jsonObject", getChartData(null,null));
        map.put("productTotal", productTotal);
        map.put("userTotal", userTotal);
        map.put("orderTotal", orderTotal);
        return "admin/homeManagePage";
    }

    @ResponseBody
    @GetMapping("admin/home/charts")
    public String charts(@Param("beginDate")String beginDate, @Param("endDate")String endDate) throws ParseException {
        System.out.println(beginDate+"---"+endDate);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return getChartData(format.parse(beginDate),format.parse(endDate)).toJSONString();
    }

    //获取图表的JSON数据
    private JSONObject getChartData(Date beginDate, Date endDate) throws ParseException {
        JSONObject object = new JSONObject();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        SimpleDateFormat timeSpecial = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        if (beginDate == null || endDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -7);
            beginDate = time.parse(time.format(cal.getTime()));
            cal = Calendar.getInstance();
            endDate = cal.getTime();
        } else {
            beginDate = time.parse(time.format(beginDate));
            endDate = timeSpecial.parse(time.format(endDate) + " 23:59:59");
        }
        String[] dateStr = new String[7];
        SimpleDateFormat time2 = new SimpleDateFormat("MM/dd", Locale.UK);
        for (int i = 0; i < dateStr.length; i++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(beginDate);
            cal.add(Calendar.DATE, i);
            dateStr[i] = time2.format(cal.getTime());
        }
        List<OrderGroup> orderGroupList = productOrderService.getTotalByDate(beginDate, endDate);
        //总交易订单数组
        int[] orderTotalArray = new int[7];
        //未付款订单数组
        int[] orderUnpaidArray = new int[7];
        //未发货订单叔祖
        int[] orderNotShippedArray = new int[7];
        //未确认订单数组
        int[] orderUnconfirmedArray = new int[7];
        //交易成功数组
        int[] orderSuccessArray = new int[7];
        for (OrderGroup orderGroup : orderGroupList) {
            int index = 0;
            for (int j = 0; j < dateStr.length; j++) {
                if (dateStr[j].equals(orderGroup.getProductOrder_pay_date())) {
                    index = j;
                }
            }
            switch (orderGroup.getProductOrder_status()) {
                case 0:
                    orderUnpaidArray[index] = orderGroup.getProductOrder_count();
                    break;
                case 1:
                    orderNotShippedArray[index] = orderGroup.getProductOrder_count();
                    break;
                case 2:
                    orderUnconfirmedArray[index] = orderGroup.getProductOrder_count();
                    break;
                case 3:
                    orderSuccessArray[index] = orderGroup.getProductOrder_count();
                    break;
                default:
                    throw new RuntimeException("错误的订单类型!");
            }
        }
        for (int i = 0; i < dateStr.length; i++) {
            orderTotalArray[i] = orderUnpaidArray[i] + orderNotShippedArray[i] + orderUnconfirmedArray[i] + orderSuccessArray[i];
        }
        object.put("orderTotalArray", JSONArray.parseArray(JSON.toJSONString(orderTotalArray)));
        object.put("orderUnpaidArray", JSONArray.parseArray(JSON.toJSONString(orderUnpaidArray)));
        object.put("orderNotShippedArray", JSONArray.parseArray(JSON.toJSONString(orderNotShippedArray)));
        object.put("orderUnconfirmedArray", JSONArray.parseArray(JSON.toJSONString(orderUnconfirmedArray)));
        object.put("orderSuccessArray", JSONArray.parseArray(JSON.toJSONString(orderSuccessArray)));
        object.put("dateStr",JSONArray.parseArray(JSON.toJSONString(dateStr)));
        return object;
    }
}
