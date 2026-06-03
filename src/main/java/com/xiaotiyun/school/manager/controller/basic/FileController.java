package com.xiaotiyun.school.manager.controller.basic;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.service.FileUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api(tags = "后台文件相关")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {
    private final FileUploadService fileUploadService;

    private final LanguageUtil languageUtil;

    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "uploadFile", value = "上传的文件", required = true, dataType = "MultipartFile"),
        @ApiImplicitParam(name = "schoolId", value = "学校ID", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "fileType", value = "文件类型：\n" +
            "1: 学生图片\n" +
            "2: 学生图片压缩包\n" +
            "3: 学校logo\n" +
            "5: 请假文件\n" +
            "6: 意向证明图片",
            required = true, dataType = "Integer")
    })
    @SaCheckPermission("file:upload")
    public Result<String> upload(@RequestPart("uploadFile") MultipartFile uploadFile, @RequestParam("schoolId") Long schoolId, @RequestParam("fileType") Integer fileType) {
        String originalFilename = uploadFile.getOriginalFilename();
        try {
            FileTypeEnum fileTypeEnum = FileTypeEnum.getEnum(fileType);
            return Result.success(fileUploadService.upload(uploadFile.getBytes(), fileTypeEnum, originalFilename, schoolId));
        } catch (IOException ex) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.FILE_UPLOAD_FAILED.getMessageCode()) + ":" + originalFilename);
        }
    }

    @PostMapping("/student/upload")
    @ApiOperation(value = "文件上传-学生端(非鉴权)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uploadFile", value = "上传的文件", required = true, dataType = "MultipartFile"),
            @ApiImplicitParam(name = "schoolId", value = "学校ID", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "fileType", value = "文件类型：\n" +
                    "1: 学生图片\n" +
                    "2: 学生图片压缩包\n" +
                    "3: 学校logo\n" +
                    "5: 请假文件\n" +
                    "6: 意向证明图片",
                    required = true, dataType = "Integer")
    })
    public Result<String> uploadByStudent(@RequestPart("uploadFile") MultipartFile uploadFile, @RequestParam("schoolId") Long schoolId, @RequestParam("fileType") Integer fileType) {
        String originalFilename = uploadFile.getOriginalFilename();
        try {
            FileTypeEnum fileTypeEnum = FileTypeEnum.getEnum(fileType);
            return Result.success(fileUploadService.upload(uploadFile.getBytes(), fileTypeEnum, originalFilename, schoolId));
        } catch (IOException ex) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.FILE_UPLOAD_FAILED.getMessageCode()) + ":" + originalFilename);
        }
    }
}
