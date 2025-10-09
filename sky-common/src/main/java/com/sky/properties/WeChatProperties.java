package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信支付相关配置属性类
 * <p>
 * 用于从 application.yml 或 application.properties 中读取以 sky.wechat 为前缀的配置项，
 * 包括小程序、商户号、API 证书、支付回调等关键信息。
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "sky.wechat")
@Data
public class WeChatProperties {

    /**
     * 小程序的 appid
     * <p>
     * 微信公众平台为当前应用分配的唯一标识，用于调用微信开放接口。
     * </p>
     */
    private String appid;

    /**
     * 小程序的秘钥
     * <p>
     * 与 appid 配对使用，用于获取用户 openid、调用支付接口等敏感操作。
     * </p>
     */
    private String secret;

    /**
     * 微信支付商户号
     * <p>
     * 微信商户平台分配的 10 位数字编号，用于标识商户身份。
     * </p>
     */
    private String mchid;

    /**
     * 商户 API 证书的证书序列号
     * <p>
     * 在商户平台上传 API 证书后生成的唯一序列号，用于签名验证。
     * </p>
     */
    private String mchSerialNo;

    /**
     * 商户私钥文件路径
     * <p>
     * 本地磁盘上存放商户 API 私钥（apiclient_key.pem）的绝对或相对路径，
     * 用于对请求进行签名。
     * </p>
     */
    private String privateKeyFilePath;

    /**
     * 证书解密的密钥
     * <p>
     * 微信返回的敏感信息（如退款通知）使用该密钥进行 AES-256-GCM 解密，
     * 长度固定为 32 字节，由商户在商户平台设置。
     * </p>
     */
    private String apiV3Key;

    /**
     * 微信平台证书文件路径
     * <p>
     * 本地磁盘上存放微信官方平台证书（wechatpay_xxx.pem）的绝对或相对路径，
     * 用于验证微信服务器返回的签名。
     * </p>
     */
    private String weChatPayCertFilePath;

    /**
     * 支付成功的回调地址
     * <p>
     * 用户支付成功后，微信服务器会向该地址发送异步通知，
     * 需为公网可访问的 HTTPS 地址。
     * </p>
     */
    private String notifyUrl;

    /**
     * 退款成功的回调地址
     * <p>
     * 商户发起退款后，微信服务器会将退款结果异步通知到该地址，
     * 需为公网可访问的 HTTPS 地址。
     * </p>
     */
    private String refundNotifyUrl;

}
