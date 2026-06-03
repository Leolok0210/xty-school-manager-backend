package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.service.SysFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/file")
@Api(tags = "文件管理")
public class SysFIleController extends BasicController {
    @Resource
    private SysFileService fileService;

    @PostMapping("/student/add")
    @ApiOperation("新增文件-学生端(非鉴权)")
    public Result<Long> addByStudent(@RequestPart("file") MultipartFile file) {
        return Result.success(fileService.addByStudent(file));
    }

    @PostMapping("/add")
    @ApiOperation("新增文件")
    @SaCheckPermission("file:add")
    public Result<Long> add(@RequestPart("file") MultipartFile file) {
        return Result.success(fileService.add(file,getSchoolId()));
    }
}