package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 地址簿业务接口实现类
 */
@Service
public class AddressBookServiceImpl implements AddressBookService {
    /**
     * 地址簿映射器
     */
    @Resource
    private AddressBookMapper addressBookMapper;

    /**
     * 新增地址
     * @param addressBook 地址簿对象
     */
    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.insert(addressBook);
    }
    /**
     * 查询用户所有地址
     * @return 地址簿列表
     */
    @Override
    public List<AddressBook> list(AddressBook addressBook) {
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", addressBook.getUserId());
        return addressBookMapper.selectList(queryWrapper);
    }
    /**
     * 更新地址
     * @param addressBook 地址簿对象
     */
    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.updateById(addressBook);
    }

    /**
     * 根据id查询地址
     * @param id 地址簿id
     * @return 地址簿对象
     */
    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.selectById(id);
    }

     /**
      * 根据id删除地址
      * @param id 地址簿id
      */
    @Override
    public void removeById(Long id) {
        addressBookMapper.deleteById(id);
    }

    /**
     * 设置默认地址
     * @param addressBook 地址簿对象，包含要设为默认的地址ID
     */
    @Override
    public void setDefault(AddressBook addressBook) {
        // 先将所有地址设置为非默认
        UpdateWrapper<AddressBook> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", BaseContext.getCurrentId())
                   .set("is_default", 0);
        addressBookMapper.update(null, updateWrapper);
        
        // 设置指定id的地址为默认
        updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", addressBook.getId())
                   .set("is_default", 1);
        addressBookMapper.update(null, updateWrapper);
    }
}