package com.sky.utils;

import com.alibaba.fastjson2.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Http工具类
 * 基于 Apache HttpClient 封装，提供 GET/POST（表单 & JSON）常用请求能力
 * 统一设置超时时间（5 秒），简化外部调用
 */
public class HttpClientUtil {

    /**
     * 默认超时时间：5 秒（毫秒）
     */
    private static final int TIMEOUT_MSEC = 5 * 1000;

    /**
     * 发送 GET 请求，参数拼接到 URL 后
     *
     * @param url      请求地址（不含参数）
     * @param paramMap 查询参数，可为 null
     * @return 响应正文，若请求失败或异常则返回空字符串
     */
    public static String doGet(String url, Map<String, String> paramMap) {
        // 创建 HttpClient 实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result = "";

        try {
            // 构造带参数的 URI
            URIBuilder builder = new URIBuilder(url);
            if (paramMap != null) {
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    builder.addParameter(entry.getKey(), entry.getValue());
                }
            }
            URI uri = builder.build();

            // 创建 GET 请求
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setConfig(builderRequestConfig());

            // 发送请求
            response = httpClient.execute(httpGet);

            // 仅当 200 才读取响应体
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            // 异常打印堆栈，不向上抛出，保证外部无感知
            e.printStackTrace();
        } finally {
            // 关闭资源，避免连接泄漏
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 发送 POST 请求，表单形式（application/x-www-form-urlencoded）
     *
     * @param url      请求地址
     * @param paramMap 表单参数，可为 null
     * @return 响应正文
     * @throws IOException 网络或 IO 异常向上抛出，由调用方处理
     */
    public static String doPost(String url, Map<String, String> paramMap) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";

        try {
            HttpPost httpPost = new HttpPost(url);

            // 封装表单参数
            if (paramMap != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (Map.Entry<String, String> param : paramMap.entrySet()) {
                    paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "UTF-8");
                httpPost.setEntity(entity);
            }

            // 统一超时配置
            httpPost.setConfig(builderRequestConfig());

            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            // 将异常原样抛出，方便调用方感知
            throw e;
        } finally {
            // 只关闭 response，HttpClient 由调用方决定是否复用
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultString;
    }

    /**
     * 发送 POST 请求，JSON 形式（application/json）
     *
     * @param url      请求地址
     * @param paramMap JSON 字段集合，可为 null
     * @return 响应正文
     * @throws IOException 网络或 IO 异常向上抛出
     */
    public static String doPost4Json(String url, Map<String, String> paramMap) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";

        try {
            HttpPost httpPost = new HttpPost(url);

            if (paramMap != null) {
                // 构造 JSON 字符串
                JSONObject jsonObject = new JSONObject();
                for (Map.Entry<String, String> param : paramMap.entrySet()) {
                    jsonObject.put(param.getKey(), param.getValue());
                }
                StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }

            httpPost.setConfig(builderRequestConfig());

            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultString;
    }

    /**
     * 构造统一超时配置
     *
     * @return RequestConfig 实例
     */
    private static RequestConfig builderRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(TIMEOUT_MSEC)      // 建立连接超时
                .setConnectionRequestTimeout(TIMEOUT_MSEC) // 从连接池获取连接超时
                .setSocketTimeout(TIMEOUT_MSEC)      // 读取数据超时
                .build();
    }
}
