package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 OSS 配置属性类
 * 用于读取 application.yml 或 application.properties 中前缀为 sky.alioss 的配置项
 */
@Component
@ConfigurationProperties(prefix = "sky.alioss")
@Data
public class AliOssProperties {

    /**
     * OSS 服务的 Endpoint（地域节点），例如：https://oss-cn-hangzhou.aliyuncs.com
     */
    private String endpoint;

    /**
     * 阿里云账号的 AccessKey ID
     */
    private String accessKeyId;

    /**
     * 阿里云账号的 AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * OSS 存储空间（Bucket）名称
     */
    private String bucketName;

}
