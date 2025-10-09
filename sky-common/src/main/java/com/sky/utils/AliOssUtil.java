package com.sky.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.io.ByteArrayInputStream;

/**
 * 阿里云OSS工具类
 * 用于实现文件上传至阿里云对象存储服务
 *
 * @author sky
 */
@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {

    /**
     * OSS接入点（地域节点）
     */
    private String endpoint;

    /**
     * 阿里云账号AccessKey ID
     */
    private String accessKeyId;

    /**
     * 阿里云账号AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * OSS存储空间名称
     */
    private String bucketName;

    /**
     * 上传文件至阿里云OSS
     *
     * @param bytes      文件字节数组
     * @param objectName 文件在OSS中的存储路径及名称（例如：images/avatar/123.jpg）
     * @return 文件在OSS上的访问URL，格式：https://BucketName.Endpoint/ObjectName
     */
    public String upload(byte[] bytes, String objectName) {

        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 上传文件至指定bucket及路径
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            // OSS服务端异常
            log.error("OSS服务端异常: 错误信息={}, 错误码={}, 请求ID={}, 主机ID={}",
                    oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
        } catch (ClientException ce) {
            // 客户端异常，如网络问题
            log.error("OSS客户端异常: 错误信息={}", ce.getMessage());
        } finally {
            // 关闭OSSClient，释放资源
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // 拼接并返回文件访问URL
        StringBuilder stringBuilder = new StringBuilder("https://");
        stringBuilder
                .append(bucketName)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(objectName);

        log.info("文件已成功上传至: {}", stringBuilder.toString());

        return stringBuilder.toString();
    }
}
