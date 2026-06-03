package com.xiaotiyun.school.manager.controller.grade;

import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.GradeRecordTimeSettingEntity;
import com.xiaotiyun.school.manager.model.entity.GradeRecordClassSettingEntity;
import com.xiaotiyun.school.manager.model.req.GradeRecordTimeSettingSaveReqModel;
import com.xiaotiyun.school.manager.model.req.GradeRecordClassSettingSaveReqModel;
import com.xiaotiyun.school.manager.model.req.GradeRecordSettingSaveReqModel;
import com.xiaotiyun.school.manager.model.res.GradeRecordSettingResModel;
import com.xiaotiyun.school.manager.service.GradeRecordSettingService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "成绩登记设定")
@RestController
@RequestMapping("/api/grade/record-setting")
public class GradeRecordSettingController extends BasicController {
    
    @Resource
    private GradeRecordSettingService gradeRecordSettingService;
    
    @ApiOperation("保存成绩录入设定")
    @SaCheckPermission("grade:record-setting:add")
    @PostMapping("/save")
    public Result<Void> saveSetting(HttpServletRequest request, 
            @Validated @RequestBody GradeRecordSettingSaveReqModel reqModel) {
        gradeRecordSettingService.saveSetting(getSchoolId(request), reqModel);
        return Result.success();
    }
    
    @ApiOperation("查询成绩录入设定")
    @SaCheckPermission("grade:record-setting:query")
    @GetMapping("/get")
    public Result<GradeRecordSettingResModel> getSetting(HttpServletRequest request,
            @ApiParam("学年") @RequestParam String schoolYear) {
        return Result.success(gradeRecordSettingService.getSetting(getSchoolId(request), schoolYear));
    }
} 