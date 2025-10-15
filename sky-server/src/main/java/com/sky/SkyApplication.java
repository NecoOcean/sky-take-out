package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 主应用类
 * 启动SkyTakeOut应用，配置MyBatis扫描Mapper接口、开启事务管理、配置日志输出。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@SpringBootApplication
@EnableTransactionManagement // 开启注解方式的事务管理
@EnableCaching
@MapperScan("com.sky.mapper")
@Slf4j
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        log.info("server started");
    }
}
