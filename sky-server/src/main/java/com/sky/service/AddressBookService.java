package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;
/**
 * 地址簿业务接口
 */
public interface AddressBookService {
    /**
     * 新增地址
     * @param addressBook 地址簿对象
     */
    void save(AddressBook addressBook);
    /**
     * 查询用户所有地址
     * @return 地址簿列表
     */
    List<AddressBook> list(AddressBook addressBook);
    /**
     * 更新地址
     * @param addressBook 地址簿对象
     */
    void update(AddressBook addressBook);

    /**
     * 根据id查询地址
     * @param id 地址簿id
     * @return 地址簿对象
     */
    AddressBook getById(Long id);

     /**
      * 根据id删除地址
      * @param id 地址簿id
      */
    void removeById(Long id);

     /**
      * 根据id设置默认地址
      *
      * @param addressBook 地址簿id
      */
    void setDefault(AddressBook addressBook);
}
