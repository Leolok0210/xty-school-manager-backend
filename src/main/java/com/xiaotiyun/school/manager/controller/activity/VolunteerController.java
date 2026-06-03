package com.xiaotiyun.school.manager.controller.activity;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.model.req.VolunteerPageReqModel;
import com.xiaotiyun.school.manager.model.req.VolunteerSaveReqModel;
import com.xiaotiyun.school.manager.model.res.VolunteerResModel;
import com.xiaotiyun.school.manager.model.res.VolunteerStudentSumResModel;
import com.xiaotiyun.school.manager.service.VolunteerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "义工服务管理")
@RestController
@RequestMapping("/api/volunteer")
@RequiredArgsConstructor
@Validated
public class VolunteerController extends BasicController {
    private final VolunteerService volunteerService;
    private final LanguageUtil languageUtil;

    @ApiOperation("分页查询")
    @PostMapping("/page")
    @SaCheckPermission("volunteer:view")
    public Result<PageInfo<VolunteerResModel>> page(
            @ApiParam(value = "分页查询参数", required = true)
            @Validated @RequestBody VolunteerPageReqModel reqModel) {
        reqModel.setUserId(getUserId());
        return Result.success(volunteerService.page(reqModel));
    }

    @ApiOperation("义工工作汇总-学生端(非鉴权)")
    @GetMapping("/student/sum")
    public Result<List<VolunteerStudentSumResModel>> summary(@RequestParam(value = "schoolYear") String schoolYear,
                                                             @RequestParam(value = "groupId") Long groupId) {
        return Result.success(volunteerService.sumByStudent(getSchoolId(),schoolYear,groupId));
    }

    @ApiOperation("义工明细查询-学生端(非鉴权)")
    @PostMapping("/student/page")
    public Result<PageInfo<VolunteerResModel>> pageByStudent(@Validated @RequestBody VolunteerPageReqModel reqModel) {
        return Result.success(volunteerService.pageByStudent(reqModel));
    }

    @ApiOperation("新增义工记录")
    @PostMapping
    @SaCheckPermission("volunteer:add")
    public Result<Void> save(@Validated @RequestBody VolunteerSaveReqModel reqModel) {
        volunteerService.save(reqModel);
        return Result.success();
    }

    @ApiOperation("修改义工记录")
    @PutMapping("/{id}")
    @SaCheckPermission("volunteer:edit")
    public Result<Void> update(
            @ApiParam(value = "记录ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "修改参数", required = true)
            @Valid @RequestBody VolunteerSaveReqModel reqModel) {
        volunteerService.update(id, reqModel);
        return Result.success();
    }

    @ApiOperation("删除义工记录")
    @DeleteMapping("/{id}")
    @SaCheckPermission("volunteer:delete")
    public Result<Void> delete(@PathVariable Long id) {
        volunteerService.delete(id);
        return Result.success();
    }

    @ApiOperation("导出Excel")
    @GetMapping("/export")
    @SaCheckPermission("volunteer:export")
    public Result<String> export(
            @ApiParam("导出查询参数") @Validated VolunteerPageReqModel reqModel) {
        return Result.success(volunteerService.export(reqModel));
    }

    @ApiOperation("导入")
    @PostMapping("/import")
    @SaCheckPermission("volunteer:import")
    public Result<Long> importVolunteer(
            @ApiParam("学年") @RequestParam("schoolYear") String schoolYear,
            @ApiParam("Excel文件") @RequestPart("uploadFile") MultipartFile file) {
        try {
            Long importId = volunteerService.importVolunteer(getSchoolId(), schoolYear, file);
            return Result.success(importId);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.FILE_UPLOAD_FAILED.getMessageCode()) + ":" + e.getMessage());
        }
    }
}