package com.xq.tmall.tmall05.controller.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.tmall.tmall05.controller.BaseController;
import com.xq.tmall.tmall05.entity.Address;
import com.xq.tmall.tmall05.entity.Product;
import com.xq.tmall.tmall05.entity.ProductOrderItem;
import com.xq.tmall.tmall05.entity.User;
import com.xq.tmall.tmall05.service.*;
import com.xq.tmall.tmall05.util.OrderUtil;
import com.xq.tmall.tmall05.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * 后台管理-用户页
 */
@Controller
public class UserController extends BaseController {


    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ProductOrderItemService productOrderItemService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductImageService productImageService;

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

    //转到后台管理-用户详情页-ajax
    @RequestMapping(value = "admin/user/{uid}", method = RequestMethod.GET)
    public String getUserById(HttpSession session, Map<String,Object> map, @PathVariable Integer uid/* 用户ID */) {
        logger.info("检查管理员权限");
        Object admin = checkAdmin(session);
        if(admin == null){
            return "admin/include/loginMessage";
        }
        User user = userService.get(uid);
        //获取用户详情-所在地地址信息
        Address address = addressService.get(user.getUser_address().getAddress_areaId());
        Stack<String> addressStack = new Stack<>();
        //最后一级地址
        addressStack.push(address.getAddress_name() + " ");
        //如果不是第一级地址
        while (!address.getAddress_areaId().equals(address.getAddress_regionId().getAddress_areaId())) {
            address = addressService.get(address.getAddress_regionId().getAddress_areaId());
            addressStack.push(address.getAddress_name() + " ");
        }
        StringBuilder builder = new StringBuilder();
        while (!addressStack.empty()) {
            builder.append(addressStack.pop());
        }
        //所在地地址字符串：---builder
        user.setUser_address(new Address().setAddress_name(builder.toString()));
        //获取用户详情的家乡地址信息
        address = addressService.get(user.getUser_homeplace().getAddress_areaId());
        //最后一级地址
        addressStack.push(address.getAddress_name() + " ");
        //如果不是第一级地址
        while (!address.getAddress_areaId().equals(address.getAddress_regionId().getAddress_areaId())) {
            address = addressService.get(address.getAddress_regionId().getAddress_areaId());
            addressStack.push(address.getAddress_name() + " ");
        }
        builder = new StringBuilder();
        while (!addressStack.empty()) {
            builder.append(addressStack.pop());
        }
        user.setUser_homeplace(new Address().setAddress_name(builder.toString()));
       //获取用户详情-购物车订单项信息
        List<ProductOrderItem> productOrderItemList = productOrderItemService.getListByUserId(user.getUser_id(), null);
        if (productOrderItemList != null) {
            //获取用户详情-购物车订单项对应的产品信息
            for (ProductOrderItem productOrderItem : productOrderItemList) {
                Integer productId = productOrderItem.getProductOrderItem_product().getProduct_id();
                Product product = productService.get(productId);
                if (product != null) {
                    product.setSingleProductImageList(productImageService.getList(productId, (byte) 0, new PageUtil(0, 1)));
                }
                productOrderItem.setProductOrderItem_product(product);
            }
        }
        user.setProductOrderItemList(productOrderItemList);


        map.put("user",user);

        return "admin/include/userDetails";


    }
    //按条件查询用户-ajax
    /**
     * RequestParam将请求参数绑定到你控制器的方法参数上
     *PathVariable 映射 URL 绑定的占位符
     */
    @ResponseBody
    @RequestMapping(value = "admin/user/{index}/{count}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getUserBySearch(@RequestParam(required = false) String user_name/* 用户名称 */,
                                  @RequestParam(required = false) Byte[] user_gender_array/* 用户性别数组 */,
                                  @RequestParam(required = false) String orderBy/* 排序字段 */,
                                  @RequestParam(required = false,defaultValue = "true") Boolean isDesc/* 是否倒序 */,
                                  @PathVariable Integer index/* 页数 */,
                                  @PathVariable Integer count/* 行数 */) throws UnsupportedEncodingException {
        //移除不必要条件
        Byte gender = null;
        if (user_gender_array != null && user_gender_array.length == 1) {
            gender = user_gender_array[0];
        }

        if (user_name != null) {
            //如果非空字符串则解决中文乱码：URLDecoder.decode(String,"UTF-8");
            user_name = "".equals(user_name) ? null : URLDecoder.decode(user_name, "UTF-8");
        }
        if (orderBy != null && "".equals(orderBy)) {
            orderBy = null;
        }
        //封装查询条件
        User user = new User()
                .setUser_name(user_name)
                .setUser_gender(gender);

        OrderUtil orderUtil = null;
        if (orderBy != null) {
            orderUtil = new OrderUtil(orderBy, isDesc);
        }

        //获取后面全部内容
        JSONObject object = new JSONObject();
        //按条件获取第多少页的第多少条用户
        PageUtil pageUtil = new PageUtil(index, count);
        List<User> userList = userService.getList(user, orderUtil, pageUtil);
        object.put("userList", JSONArray.parseArray(JSON.toJSONString(userList)));
        //按条件获取用户总数量
        Integer userCount = userService.getTotal(user);
        object.put("userCount", userCount);
        //获取分页信息
        pageUtil.setTotal(userCount);
        object.put("totalPage", pageUtil.getTotalPage());
        object.put("pageUtil", pageUtil);

        return object.toJSONString();
    }

}