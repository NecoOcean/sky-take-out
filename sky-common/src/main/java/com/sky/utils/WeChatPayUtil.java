package com.sky.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sky.properties.WeChatProperties;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * 微信支付工具类
 * 提供微信支付相关接口的调用封装，包括下单、支付、退款等功能
 */
@Component
public class WeChatPayUtil {

    /**
     * 微信支付下单接口地址
     */
    public static final String JSAPI = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";

    /**
     * 申请退款接口地址
     */
    public static final String REFUNDS = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";

    /**
     * 微信配置属性
     */
    @Resource
    private WeChatProperties weChatProperties;

    /**
     * 获取调用微信接口的客户端工具对象
     * 该方法会加载商户私钥和微信支付平台证书，构建具有自动签名和验签功能的HttpClient
     *
     * @return CloseableHttpClient 微信支付专用HTTP客户端，如果加载失败则返回null
     */
    private CloseableHttpClient getClient() {
        PrivateKey merchantPrivateKey = null;
        try {
            // 加载商户API私钥，用于请求签名
            merchantPrivateKey = PemUtil.loadPrivateKey(new FileInputStream(new File(weChatProperties.getPrivateKeyFilePath())));
            // 加载微信支付平台证书，用于响应验签
            X509Certificate x509Certificate = PemUtil.loadCertificate(new FileInputStream(new File(weChatProperties.getWeChatPayCertFilePath())));
            // 构建微信支付平台证书列表
            List<X509Certificate> wechatPayCertificates = Arrays.asList(x509Certificate);

            // 创建微信支付HTTP客户端构建器
            WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                    .withMerchant(weChatProperties.getMchid(), weChatProperties.getMchSerialNo(), merchantPrivateKey)
                    .withWechatPay(wechatPayCertificates);

            // 构建具有自动签名和验签功能的HttpClient
            CloseableHttpClient httpClient = builder.build();
            return httpClient;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送POST方式请求
     * 该方法使用微信支付专用HTTP客户端发送POST请求，并自动处理签名和验签
     *
     * @param url  请求地址
     * @param body 请求体（JSON格式）
     * @return 响应内容（JSON格式字符串）
     * @throws Exception 发送请求或处理响应时可能抛出的异常
     */
    private String post(String url, String body) throws Exception {
        CloseableHttpClient httpClient = getClient();

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
        httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        httpPost.addHeader("Wechatpay-Serial", weChatProperties.getMchSerialNo());
        httpPost.setEntity(new StringEntity(body, "UTF-8"));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            return bodyAsString;
        } finally {
            httpClient.close();
            response.close();
        }
    }

    /**
     * 发送GET方式请求
     * 该方法使用微信支付专用HTTP客户端发送GET请求，并自动处理签名和验签
     *
     * @param url 请求地址
     * @return 响应内容（JSON格式字符串）
     * @throws Exception 发送请求或处理响应时可能抛出的异常
     */
    private String get(String url) throws Exception {
        CloseableHttpClient httpClient = getClient();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
        httpGet.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        httpGet.addHeader("Wechatpay-Serial", weChatProperties.getMchSerialNo());

        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            return bodyAsString;
        } finally {
            httpClient.close();
            response.close();
        }
    }

    /**
     * JSAPI下单
     * 调用微信支付JSAPI接口创建支付订单，适用于小程序支付场景
     *
     * @param orderNum    商户订单号，需保证唯一性
     * @param total       订单总金额，单位为元
     * @param description 商品描述，将展示给用户
     * @param openid      微信用户的openid，标识支付用户
     * @return 微信支付返回的JSON响应字符串
     * @throws Exception 调用接口时可能抛出的异常
     */
    private String jsapi(String orderNum, BigDecimal total, String description, String openid) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appid", weChatProperties.getAppid());
        jsonObject.put("mchid", weChatProperties.getMchid());
        jsonObject.put("description", description);
        jsonObject.put("out_trade_no", orderNum);
        jsonObject.put("notify_url", weChatProperties.getNotifyUrl());

        JSONObject amount = new JSONObject();
        // 将金额从元转换为分，并四舍五入保留两位小数
        amount.put("total", total.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).intValue());
        amount.put("currency", "CNY");

        jsonObject.put("amount", amount);

        JSONObject payer = new JSONObject();
        payer.put("openid", openid);

        jsonObject.put("payer", payer);

        String body = JSON.toJSONString(jsonObject);
        return post(JSAPI, body);
    }

    /**
     * 小程序支付
     * 统一下单并生成调起支付所需参数，返回给前端用于调起微信支付
     *
     * @param orderNum    商户订单号，需保证唯一性
     * @param total       订单金额，单位为元
     * @param description 商品描述，将展示给用户
     * @param openid      微信用户的openid，标识支付用户
     * @return JSONObject 包含调起支付所需参数的JSON对象，若下单失败则返回微信原始错误信息
     * @throws Exception 调用接口或签名过程中可能抛出的异常
     */
    public JSONObject pay(String orderNum, BigDecimal total, String description, String openid) throws Exception {
        // 统一下单，生成预支付交易单
        String bodyAsString = jsapi(orderNum, total, description, openid);
        // 解析返回结果
        JSONObject jsonObject = JSON.parseObject(bodyAsString);
        System.out.println(jsonObject);

        String prepayId = jsonObject.getString("prepay_id");
        if (prepayId != null) {
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
            String nonceStr = RandomStringUtils.randomNumeric(32);
            ArrayList<Object> list = new ArrayList<>();
            list.add(weChatProperties.getAppid());
            list.add(timeStamp);
            list.add(nonceStr);
            list.add("prepay_id=" + prepayId);
            // 二次签名，调起支付需要重新签名
            StringBuilder stringBuilder = new StringBuilder();
            for (Object o : list) {
                stringBuilder.append(o).append("\n");
            }
            String signMessage = stringBuilder.toString();
            byte[] message = signMessage.getBytes();

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(PemUtil.loadPrivateKey(new FileInputStream(new File(weChatProperties.getPrivateKeyFilePath()))));
            signature.update(message);
            String packageSign = Base64.getEncoder().encodeToString(signature.sign());

            // 构造数据给微信小程序，用于调起微信支付
            JSONObject jo = new JSONObject();
            jo.put("timeStamp", timeStamp);
            jo.put("nonceStr", nonceStr);
            jo.put("package", "prepay_id=" + prepayId);
            jo.put("signType", "RSA");
            jo.put("paySign", packageSign);

            return jo;
        }
        return jsonObject;
    }

    /**
     * 申请退款
     * 调用微信支付退款接口，对指定订单进行退款操作
     *
     * @param outTradeNo  商户订单号，需与支付时一致
     * @param outRefundNo 商户退款单号，需保证唯一性
     * @param refund      退款金额，单位为元，不能超过原订单金额
     * @param total       原订单金额，单位为元
     * @return 微信支付返回的JSON响应字符串
     * @throws Exception 调用接口时可能抛出的异常
     */
    public String refund(String outTradeNo, String outRefundNo, BigDecimal refund, BigDecimal total) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no", outTradeNo);
        jsonObject.put("out_refund_no", outRefundNo);

        JSONObject amount = new JSONObject();
        // 将金额从元转换为分，并四舍五入保留两位小数
        amount.put("refund", refund.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).intValue());
        amount.put("total", total.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).intValue());
        amount.put("currency", "CNY");

        jsonObject.put("amount", amount);
        jsonObject.put("notify_url", weChatProperties.getRefundNotifyUrl());

        String body = JSON.toJSONString(jsonObject);

        // 调用申请退款接口
        return post(REFUNDS, body);
    }
}
