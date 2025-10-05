package com.bot_system.common.utils;

import com.bot_system.exception.ApiRequestException;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.Map;

/**
 * @className: HttpUtil
 * @author: Java之父
 * @date: 2025/10/5 0:01
 * @version: 1.0.0
 * @description: HTTP请求工具类
 */
public final class HttpUtil {
    private static final RestTemplate restTemplate = createRestTemplate();

    /**
     * 创建带超时配置的 RestTemplate 实例。
     */
    private static RestTemplate createRestTemplate() {
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000); // 连接超时 10 秒
        factory.setReadTimeout(15_000);    // 读取超时 15 秒
        return new RestTemplate(factory);
    }

    /**
     * 简单 GET 请求。
     *
     * @param url 请求地址
     * @return 响应内容
     */
    public static String get(String url) {
        return get(url, null);
    }

    /**
     * 带 Header 的 GET 请求。
     *
     * @param url     请求地址
     * @param headers 请求头（可选）
     * @return 响应内容
     */
    public static String get(String url, Map<String, String> headers) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            if (headers != null) headers.forEach(httpHeaders::set);

            HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new ApiRequestException("GET 请求失败：" + e.getMessage(), e);
        }
    }

    /**
     * POST JSON 请求。
     *
     * @param url  请求地址
     * @param body 请求体对象（会自动转 JSON）
     * @return 响应字符串
     */
    public static String postJson(String url, Object body) {
        return postJson(url, body, null);
    }

    /**
     * 带 Header 的 POST JSON 请求。
     *
     * @param url     请求地址
     * @param body    请求体
     * @param headers 附加请求头
     * @return 响应字符串
     */
    public static String postJson(String url, Object body, Map<String, String> headers) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            if (headers != null) headers.forEach(httpHeaders::set);

            HttpEntity<Object> entity = new HttpEntity<>(body, httpHeaders);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new ApiRequestException("POST JSON 请求失败：" + e.getMessage(), e);
        }
    }

    /**
     * POST 表单请求（application/x-www-form-urlencoded）。
     *
     * @param url  请求地址
     * @param form 表单参数
     * @return 响应字符串
     */
    public static String postForm(String url, Map<String, Object> form) {
        try {
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            if (form != null) form.forEach(map::add);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new ApiRequestException("POST 表单请求失败：" + e.getMessage(), e);
        }
    }

    /**
     * 下载文件到指定路径（自动创建目录）。
     *
     * @param fileUrl  文件链接
     * @param savePath 保存路径（包含文件名）
     * @return 下载后的文件对象
     */
    public static File downloadFile(String fileUrl, String savePath) {
        try {
            URI uri = URI.create(fileUrl);
            ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET, null, byte[].class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new ApiRequestException("文件下载失败，HTTP状态：" + response.getStatusCode());
            }

            File targetFile = new File(savePath);
            Files.createDirectories(targetFile.getParentFile().toPath());

            try (FileOutputStream out = new FileOutputStream(targetFile)) {
                out.write(response.getBody());
            }

            return targetFile;
        } catch (Exception e) {
            throw new ApiRequestException("文件下载失败：" + e.getMessage(), e);
        }
    }

    /**
     * 校验响应码是否为成功。
     */
    public static boolean isSuccess(HttpStatus status) {
        return status.is2xxSuccessful();
    }

    /**
     * 将 Map 转换为标准的 URL 参数字符串。
     */
    public static String toQueryString(Map<String, ?> params) {
        if (params == null || params.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("?");
        params.forEach((k, v) -> {
            if (v != null) sb.append(k).append('=').append(v).append('&');
        });
        if (sb.charAt(sb.length() - 1) == '&') sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
