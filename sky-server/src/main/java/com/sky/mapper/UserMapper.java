package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.User;


/**
 * 用户映射器接口
 * 继承自 MyBatis-Plus 的 BaseMapper，提供基础的 CRUD 操作。
 * 用于操作 User 实体对应的数据库表。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
public interface UserMapper extends BaseMapper<User> {
}
