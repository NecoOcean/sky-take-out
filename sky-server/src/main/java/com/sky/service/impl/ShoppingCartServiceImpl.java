package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Resource
    private ShoppingCartMapper shoppingCartMapper;

    @Resource
    private DishMapper dishMapper;

    @Resource
    private SetmealMapper setmealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 1. 获取当前登录用户的ID
        Long userId = BaseContext.getCurrentId();

        // 2. 从DTO中获取菜品ID和套餐ID
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();

        // 3. 使用 LambdaQueryWrapper 查询购物车中是否已存在该商品
        //    我们需要查询的条件是：用户ID匹配，并且（菜品ID匹配 或 套餐ID匹配）
        ShoppingCart existingCart = shoppingCartMapper.selectOne(
                Wrappers.lambdaQuery(ShoppingCart.class)
                        .eq(ShoppingCart::getUserId, userId)
                        .and(wrapper -> wrapper
                                .eq(ShoppingCart::getDishId, dishId)
                                .or()
                                .eq(ShoppingCart::getSetmealId, setmealId)
                        )
        );

        // 4. 判断商品是否已存在
        if (existingCart != null) {
            // 如果已存在，只需将数量加一
            existingCart.setNumber(existingCart.getNumber() + 1);
            // 使用 BaseMapper 的 updateById 方法更新记录
            shoppingCartMapper.updateById(existingCart);

        } else {
            // 如果不存在，则需要插入一条新的购物车记录

            // 创建一个新的 ShoppingCart 对象
            ShoppingCart newCart = new ShoppingCart();
            // 从 DTO 复制属性（如 dishId, setmealId, dishFlavor）
            BeanUtils.copyProperties(shoppingCartDTO, newCart);

            // 设置用户ID
            newCart.setUserId(userId);

            // 根据是菜品还是套餐，设置名称、图片和单价
            if (dishId != null) {
                // 添加的是菜品
                Dish dish = dishMapper.selectById(dishId);
                newCart.setName(dish.getName());
                newCart.setImage(dish.getImage());
                newCart.setAmount(dish.getPrice());
            } else {
                // 添加的是套餐
                Setmeal setmeal = setmealMapper.selectById(setmealId);
                newCart.setName(setmeal.getName());
                newCart.setImage(setmeal.getImage());
                newCart.setAmount(setmeal.getPrice());
            }

            // 设置初始数量和创建时间
            newCart.setNumber(1);
            newCart.setCreateTime(LocalDateTime.now());

            // 使用 BaseMapper 的 insert 方法插入新记录
            shoppingCartMapper.insert(newCart);
        }
    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        //获取到当前微信用户的id
        Long userId = BaseContext.getCurrentId();
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return shoppingCartMapper.selectList(queryWrapper);
    }

    @Override
    public void cleanShoppingCart() {
        //获取到当前微信用户的id
        Long userId = BaseContext.getCurrentId();
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        shoppingCartMapper.delete(queryWrapper);
    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //设置查询条件，查询当前登录用户的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", shoppingCart.getUserId());
        List<ShoppingCart> list = shoppingCartMapper.selectList(queryWrapper);

        if(list != null && !list.isEmpty()){
            shoppingCart = list.get(0);

            Integer number = shoppingCart.getNumber();
            if(number == 1){
                //当前商品在购物车中的份数为1，直接删除当前记录
                shoppingCartMapper.deleteById(shoppingCart.getId());
            }else {
                //当前商品在购物车中的份数不为1，修改份数即可
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.updateById(shoppingCart);
            }
        }
    }
}
