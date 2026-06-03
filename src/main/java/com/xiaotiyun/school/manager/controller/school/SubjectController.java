package com.xiaotiyun.school.manager.controller.school;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.model.entity.Subject;
import com.xiaotiyun.school.manager.model.req.SubjectAddReqModel;
import com.xiaotiyun.school.manager.model.req.SubjectQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SubjectDetailResModel;
import com.xiaotiyun.school.manager.model.res.SubjectSimpleResModel;
import com.xiaotiyun.school.manager.service.SubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/subject")
@Api(tags = "科目管理")
public class SubjectController extends BasicController {

    @Resource
    private SubjectService subjectService;

    @PostMapping("/add")
    @ApiOperation("批量新增科目")
    @SaCheckPermission("subject:add")
    public Result<Void> addSubjects(HttpServletRequest request, @Valid @RequestBody List<SubjectAddReqModel> reqModels) {
        long schoolId = getSchoolId(request);
        List<Subject> subjects = reqModels.stream()
                .map(reqModel -> {
                    Subject subject = BeanConvertUtil.convert(reqModel, Subject.class);
                    subject.setSchoolId(schoolId);
                    return subject;
                })
                .collect(Collectors.toList());
        subjectService.createSubjects(subjects);

        return Result.success();
    }
    
    @PostMapping("/update")
    @ApiOperation("修改科目")
    @SaCheckPermission("subject:update")
    public Result<Void> updateSubject(@Valid @RequestBody SubjectAddReqModel reqModel) {
        Subject subject = BeanConvertUtil.convert(reqModel, Subject.class);
        subjectService.updateSubject(subject);
        return Result.success();
    }
    
    @GetMapping("/delete")
    @ApiOperation("删除科目")
    @SaCheckPermission("subject:delete")
    public Result<Void> deleteSubject(@RequestParam Long id) {
        subjectService.deleteSubject(getSchoolId(), id);
        return Result.success();
    }
    
    @GetMapping("/get")
    @ApiOperation("查看科目详情")
    @SaCheckPermission("subject:get")
    public Result<SubjectDetailResModel> getSubjectDetail(@RequestParam Long id) {
        Subject subject = subjectService.getSubjectById(id);
        SubjectDetailResModel resModel = BeanConvertUtil.convert(subject, SubjectDetailResModel.class);
        return Result.success(resModel);
    }
    
    @PostMapping("/list")
    @ApiOperation("查询科目列表")
    @SaCheckPermission("subject:list")
    public Result<PageInfo<SubjectDetailResModel>> getSubjectList(@Valid @RequestBody SubjectQueryReqModel reqModel) {
        return Result.success(subjectService.getSubjectList(reqModel));
    }

    @GetMapping("/listBySchoolAndDepartment")
    @ApiOperation("根据学校ID和学部ID查询全部科目")
    public Result<List<SubjectSimpleResModel>> getSubjectsBySchoolAndDepartment(HttpServletRequest request,@RequestParam Integer departmentId) {
        long schoolId = getSchoolId(request);
        List<SubjectSimpleResModel> subjects = subjectService.getSubjectsBySchoolAndDepartment(schoolId, departmentId);
        return Result.success(subjects);
    }


    @ApiOperation("科目导入")
    @PostMapping("/import")
    @SaCheckPermission("subject:import")
    public Result<Long> importSubject(
            @ApiParam("Excel文件") @RequestPart("uploadFile") MultipartFile file,
            @ApiParam("学校id") @RequestParam Long schoolId) {
        try {
            Long importId = subjectService.importSubject(file, schoolId);
            return Result.success(importId);
        } catch (Exception e) {
            log.error("导入失败, 失败原因：{}", e.getMessage());
            return Result.failed(ResultCode.FILE_UPLOAD_FAILED);
        }
    }
}