package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口控制器
 * 提供文件上传等通用功能
 * 访问路径前缀：/admin/common
 * 主要负责处理前端发送的通用请求，调用服务层进行业务逻辑处理，并返回结果。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@RestController
@RequestMapping("/admin/common")
@Tag(name = "通用接口", description = "后台管理系统通用功能接口，目前主要提供文件上传服务")
@Slf4j
public class CommonController {

    /**
     * 阿里云OSS工具类
     * 用于文件上传至阿里云OSS
     */
    @Resource
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传接口
     * 接收前端上传的文件，保存至阿里云OSS，并返回可访问的文件路径
     * 支持常见图片格式（如jpg、png、jpeg等），文件大小由全局配置限制
     *
     * @param file 前端上传的文件对象，不能为空，需为图片格式
     * @return Result<String> 包含上传成功后文件访问路径的成功响应，或上传失败的错误响应
     */
    @PostMapping("/upload")
    @Operation(
            summary = "文件上传",
            description = "上传图片文件到阿里云OSS，返回可访问的URL路径。支持jpg、png、jpeg等常见格式。"
    )
    public Result<String> upload(
            @Parameter(description = "待上传的图片文件", required = true)
            MultipartFile file
    ) {
        log.info("文件上传：原始文件名={}", file.getOriginalFilename());

        try {
            // 获取原始文件名，用于截取文件后缀
            String originalFilename = file.getOriginalFilename();
            // 截取原始文件名的后缀，例如：.png、.jpg
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 使用UUID生成唯一的新文件名，避免文件名冲突
            String objectName = UUID.randomUUID() + extension;

            // 将文件上传至阿里云OSS，并获取返回的可访问路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            // 返回成功响应，包含文件访问路径
            return Result.success(filePath);
        } catch (IOException e) {
            // 记录文件上传失败的异常信息
            log.error("文件上传失败，原始文件名：{}", file.getOriginalFilename(), e);
        }

        // 返回上传失败的错误响应
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
