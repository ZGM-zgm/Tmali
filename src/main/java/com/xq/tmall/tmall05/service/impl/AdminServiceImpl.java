package com.xq.tmall.tmall05.service.impl;


import com.xq.tmall.tmall05.dao.AdminMapper;
import com.xq.tmall.tmall05.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Integer login(String admin_name, String admin_password) {
        return adminMapper.selectByLogin(admin_name,admin_password);
    }
}
