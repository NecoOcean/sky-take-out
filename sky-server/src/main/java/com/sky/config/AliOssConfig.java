package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，用于创建AliOssUtil bean
 * 该类从AliOssProperties中获取OSS相关配置，并使用这些配置创建AliOssUtil实例。
 * 主要负责配置OSS客户端，用于上传、下载、删除等操作。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@Configuration
public class AliOssConfig {

    /**
     * 创建AliOssUtil bean
     *
     * @param aliOssProperties 从application.properties中读取的OSS配置属性
     * @return 配置好的AliOssUtil实例
     */
    @Bean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }

}