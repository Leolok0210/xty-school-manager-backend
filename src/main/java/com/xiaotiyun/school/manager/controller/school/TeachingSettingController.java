package com.xiaotiyun.school.manager.controller.school;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.hash.Hash;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.TeachingSettingAddReqModel;
import com.xiaotiyun.school.manager.model.req.TeachingSettingQueryByRoleReqModel;
import com.xiaotiyun.school.manager.model.req.TeachingSettingQueryReqModel;
import com.xiaotiyun.school.manager.model.res.TeachingSettingDetailResModel;
import com.xiaotiyun.school.manager.model.res.TeachingSettingRoleResModel;
import com.xiaotiyun.school.manager.service.*;
import com.xiaotiyun.school.manager.model.res.SubjectRelResModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/teachingsetting")
@Api(tags = "任教设置管理")
public class TeachingSettingController extends BasicController {

    @Resource
    private TeachingSettingService teachingSettingService;


    @Resource
    private UserSchoolRelDao userSchoolRelDao;


    @Resource
    private SysClassService sysClassService;

    @Resource
    private GradeGroupService gradeGroupService;

    @Resource
    private UserService userService;

    @Resource
    private SubjectRelService subjectRelService;

    @PostMapping("/add")
    @ApiOperation("批量新增任教设置")
    @SaCheckPermission("teachingsetting:add")
    public Result<Void> addTeachingSettings(HttpServletRequest request, @Valid @RequestBody List<TeachingSettingAddReqModel> reqModels) {
        long schoolId = getSchoolId(request);
        List<TeachingSetting> teachingSettings = reqModels.stream()
                .map(reqModel -> {
                    TeachingSetting teachingSetting = BeanConvertUtil.convert(reqModel, TeachingSetting.class);
                    teachingSetting.setSchoolId(schoolId);
                    return teachingSetting;
                })
                .collect(Collectors.toList());
        teachingSettingService.createTeachingSettings(teachingSettings);
        return Result.success();
    }

    @PostMapping("/update")
    @ApiOperation("修改任教设置")
    @SaCheckPermission("teachingsetting:update")
    public Result<Void> updateTeachingSetting(@Valid @RequestBody TeachingSettingAddReqModel reqModel) {
        TeachingSetting teachingSetting = BeanConvertUtil.convert(reqModel, TeachingSetting.class);
        teachingSettingService.updateTeachingSetting(teachingSetting);
        return Result.success();
    }

    @GetMapping("/delete")
    @ApiOperation("删除任教设置")
    @SaCheckPermission("teachingsetting:delete")
    public Result<Void> deleteTeachingSetting(@RequestParam Long id) {
        teachingSettingService.deleteTeachingSetting(id);
        return Result.success();
    }

    @GetMapping("/get")
    @ApiOperation("查看任教设置详情")
    @SaCheckPermission("teachingsetting:get")
    public Result<TeachingSettingDetailResModel> getTeachingSettingDetail(HttpServletRequest request,@RequestParam Long id) {
        TeachingSetting teachingSetting = teachingSettingService.getTeachingSettingById(id);
        TeachingSettingDetailResModel resModel = new TeachingSettingDetailResModel();
        BeanUtils.copyProperties(teachingSetting, resModel, "subjectName", "teacherName","className");
        // 通过SubjectRelService查询科目信息
        List<SubjectRelResModel> subjectRelList = subjectRelService.listByIds(java.util.Collections.singletonList(teachingSetting.getSubjectId()));
        if(subjectRelList != null && !subjectRelList.isEmpty() && subjectRelList.get(0).getSubject() != null) {
            resModel.setSubjectName(subjectRelList.get(0).getSubject().getSubjectName());
        }
        UserSchoolRelEntity userDetail = userSchoolRelDao.selectById(teachingSetting.getTeacherId());
        if(userDetail != null) {
            resModel.setTeacherName(userDetail.getUsername());
        }
        SysClass classById = sysClassService.getSysClassById(resModel.getClassId());
        if(classById != null) {
            GradeGroup gradeGroup = gradeGroupService.getById(classById.getGradeGroup());
            if(gradeGroup != null)
            {
                resModel.setGradeGroupName(gradeGroup.getGradeGroupName());
            }
            resModel.setClassName(classById.getClassName());
        }
        return Result.success(resModel);
    }

    @PostMapping("/list")
    @ApiOperation("查询任教设置列表")
    @SaCheckPermission("teachingsetting:list")
    public Result<PageInfo<TeachingSettingDetailResModel>> getTeachingSettingList(@RequestBody TeachingSettingQueryReqModel reqModel) {
        PageInfo<TeachingSettingDetailResModel> pageInfo = teachingSettingService.getTeachingSettings(reqModel);
        if(pageInfo.getList() == null || pageInfo.getList().isEmpty())
            return Result.success(pageInfo);
        pageInfo.getList().forEach(resModel -> {
            UserSchoolRelEntity userDetail = userSchoolRelDao.selectById(resModel.getTeacherId());
            if(userDetail != null) {
                resModel.setTeacherName(userDetail.getUsername());
                UserEntity userEntity = userService.getById(userDetail.getUserId());
                if(userEntity != null) {
                    resModel.setTeacherPhoneNumber(userEntity.getMobile());
                }
            }
            SysClass classById = sysClassService.getSysClassById(resModel.getClassId());
            if(classById != null) {
                GradeGroup gradeGroup = gradeGroupService.getById(classById.getGradeGroup());
                if(gradeGroup != null)
                {
                    resModel.setGradeGroupName(gradeGroup.getGradeGroupName());
                }
                resModel.setClassName(classById.getClassName());
            }
        });
        return Result.success(pageInfo);
    }

    @PostMapping("/listByRole")
    @ApiOperation("查询任教设置列表-根据权限")
    @SaCheckPermission("teachingsetting:listRole")
    public Result<PageInfo<TeachingSettingRoleResModel>> getTeachingSettingListByRole(@Validated @RequestBody TeachingSettingQueryByRoleReqModel reqModel) {
        PageInfo<TeachingSettingRoleResModel> pageInfo = teachingSettingService.getTeachingSettingsByRole(reqModel);
        return Result.success(pageInfo);
    }
}