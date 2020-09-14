package com.xq.tmall.tmall05.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface LastIDMapper {
    int selectLastID();
}
