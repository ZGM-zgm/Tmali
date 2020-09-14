package com.xq.tmall.tmall05.service;


import com.xq.tmall.tmall05.entity.Address;

import java.util.List;

public interface AddressService {
    boolean add(Address address);
    boolean update(Address address);

    List<Address> getList(String address_name, String address_regionId);
    Address get(String address_areaId);
    List<Address> getRoot();
}
