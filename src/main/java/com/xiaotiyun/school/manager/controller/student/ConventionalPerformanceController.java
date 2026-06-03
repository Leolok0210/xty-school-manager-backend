package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.req.ConventionalPerformancePageReqModel;
import com.xiaotiyun.school.manager.model.req.ConventionalPerformanceSaveReqModel;
import com.xiaotiyun.school.manager.model.req.ConventionalPerformanceUpdateReqModel;
import com.xiaotiyun.school.manager.model.req.StudentPerformanceTotalReqModel;
import com.xiaotiyun.school.manager.model.res.ConventionalPerformancePageResModel;
import com.xiaotiyun.school.manager.model.res.StudentPerformanceTotalResModel;
import com.xiaotiyun.school.manager.service.ConventionalPerformanceService;
import com.xiaotiyun.school.manager.service.StudentLeaveService;
import com.xiaotiyun.school.manager.service.UserRewardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = "常规表现管理")
@RequestMapping("/api/conventional/performance")
public class ConventionalPerformanceController extends BasicController {
    private final LanguageUtil languageUtil;
    private final ConventionalPerformanceService conventionalPerformanceService;
    private final UserRewardService userRewardService;
    private final StudentLeaveService studentLeaveService;


    @GetMapping("/page")
    @ApiOperation("分页列表")
    @SaCheckPermission("conventional:performance:page")
    public Result<PageInfo<ConventionalPerformancePageResModel>> page(@Validated ConventionalPerformancePageReqModel reqModel) {
        return Result.success(conventionalPerformanceService.page(getSchoolId(), reqModel));
    }

    @PostMapping
    @ApiOperation("新增")
    @SaCheckPermission("conventional:performance:add")
    public Result<Void> save(@Validated @RequestBody ConventionalPerformanceSaveReqModel reqModel) {
        conventionalPerformanceService.save(getSchoolId(), getUserId(), reqModel);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改")
    @SaCheckPermission("conventional:performance:edit")
    public Result<Void> update(@ApiParam(value = "记录ID", required = true) @PathVariable Long id,
                               @Validated @RequestBody ConventionalPerformanceUpdateReqModel reqModel) {
        conventionalPerformanceService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除")
    @SaCheckPermission("conventional:performance:delete")
    public Result<Void> delete(@PathVariable Long id) {
        conventionalPerformanceService.delete(id);
        return Result.success();
    }

    @ApiOperation("导入")
    @PostMapping("/import")
    @SaCheckPermission("conventional:performance:import")
    public Result<Long> importRecord(@ApiParam("Excel文件") @RequestPart("uploadFile") MultipartFile file,
                                     @ApiParam("学年") @RequestParam String sid,
                                     @ApiParam("学期") @RequestParam Long term,
                                     @ApiParam("班级id") @RequestParam Long classId) {
        try {
            return Result.success(conventionalPerformanceService.importRecord(getSchoolId(), getUserId(), sid, term, classId, file));
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.FILE_UPLOAD_FAILED.getMessageCode()) + ":" + e.getMessage());
        }
    }

    @GetMapping("/export")
    @ApiOperation("导出Excel")
    @SaCheckPermission("conventional:performance:export")
    public Result<String> export(@ApiParam("导出查询参数") @Validated ConventionalPerformancePageReqModel reqModel) {
        return Result.success(conventionalPerformanceService.export(getSchoolId(), reqModel));
    }

    @GetMapping("/student/total")
    @ApiOperation("在校表现统计-学生端(非鉴权)")
    public Result<List<StudentPerformanceTotalResModel>> total(@Valid StudentPerformanceTotalReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        StudentEntity nowStudent = (StudentEntity) StpUtil.getSession().get("student");
        if (nowStudent == null) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        reqModel.setStudentId(nowStudent.getId());
        List<StudentPerformanceTotalResModel> total = new ArrayList<>();
        total.addAll(conventionalPerformanceService.getTotal(reqModel));
        total.addAll(userRewardService.getTotal(reqModel));
        total.addAll(studentLeaveService.getTotal(reqModel));
        return Result.success(total);
    }
}