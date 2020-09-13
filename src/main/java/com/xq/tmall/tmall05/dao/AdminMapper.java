package com.xq.tmall.tmall05.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminMapper {
    Integer selectByLogin(@Param("admin_name") String admin_name, @Param("admin_password") String admin_password);
}