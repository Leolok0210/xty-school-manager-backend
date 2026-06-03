package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/userreward")
@Api(tags = "奖励管理")
public class UserRewardController extends BasicController {
    @Resource
    private UserRewardService userRewardService;
    @Resource
    private StudentService studentService;
    @Resource
    private SemesterService semesterService;
    @Resource
    private SysClassService sysClassService;
    @Resource
    private GradeGroupService gradeGroupService;
    @Resource
    private LanguageUtil languageUtil;

    @Resource
    private ExportRecordService exportRecordService;

    @PostMapping("/add")
    @ApiOperation("批量新增奖励")
    @SaCheckPermission("userreward:add")
    public Result<Void> addUserRewards(@Valid @RequestBody UserRewardAddReqModel reqModel) {
        userRewardService.addUserRewards(getSchoolId(), getUserId(), reqModel);
        return Result.success();
    }

    @PostMapping("/update")
    @ApiOperation("修改奖励")
    @SaCheckPermission("userreward:update")
    public Result<Void> updateUserReward(@Valid @RequestBody UserRewardUpdateReqModel reqModel) {
        int nonNullCount = 0;
        if (reqModel.getMaxReward() != null && reqModel.getMaxReward() > 0) {
            nonNullCount++;
        } else {
            reqModel.setMaxReward(0);
        }
        if (reqModel.getMidReward() != null && reqModel.getMidReward() > 0) {
            nonNullCount++;
        } else {
            reqModel.setMidReward(0);
        }
        if (reqModel.getMinReward() != null && reqModel.getMinReward() > 0) {
            nonNullCount++;
        } else {
            reqModel.setMinReward(0);
        }

        //校验大功小功有点只能有一个有值
        if (nonNullCount > 1) {
            return Result.failed(ResultCode.DATA_GRADE_FUNCTION_ONLY_ONE);
        }
        UserReward userReward = BeanConvertUtil.convert(reqModel, UserReward.class);
        userRewardService.updateUserReward(userReward);
        return Result.success();
    }

    @GetMapping("/delete")
    @ApiOperation("删除奖励")
    @SaCheckPermission("userreward:delete")
    public Result<Void> deleteUserReward(@RequestParam Long id) {
        userRewardService.deleteUserReward(id);
        return Result.success();
    }

    @GetMapping("/get")
    @ApiOperation("查看奖励详情")
    @SaCheckPermission("userreward:get")
    public Result<UserRewardDetailResModel> getUserRewardDetail(@RequestParam Long id) {
        UserReward userReward = userRewardService.getUserRewardById(id);
        UserRewardDetailResModel resModel = BeanConvertUtil.convert(userReward, UserRewardDetailResModel.class);
        StudentResModel info = studentService.getStudentById(userReward.getStudentId());
        if (info != null) {
            resModel.setStudentName(info.getChineseName());
            resModel.setSeatNo(info.getSeatNo());
            SysClass classById = sysClassService.getSysClassById(info.getClassId());
            if (classById != null) {
                GradeGroup gradeGroup = gradeGroupService.getById(classById.getGradeGroup());
                resModel.setGradeGroupName(gradeGroup == null ? "" : gradeGroup.getGradeGroupName());
                resModel.setClassName(classById.getClassName());
                resModel.setClassNumber(classById.getClassSerialNumber());
                resModel.setClassId(classById.getId());
            }
        }
        SemesterEntity semesterServiceById = semesterService.getById(userReward.getTerm());
        resModel.setTermName(semesterServiceById == null ? "" : semesterServiceById.getName());
        return Result.success(resModel);
    }

    @PostMapping("/list")
    @ApiOperation("查询奖励列表")
    @SaCheckPermission("userreward:list")
    public Result<PageInfo<UserRewardDetailResModel>> getUserRewardList(@RequestBody UserRewardQueryReqModel reqModel) {
        reqModel.setUserId(getUserId());
        PageInfo<UserRewardDetailResModel> userRewardList = userRewardService.getUserRewardList(reqModel);
        if (!CollectionUtils.isEmpty(userRewardList.getList())) {
            userRewardList.getList().forEach(item -> {
                SysClass classById = sysClassService.getSysClassById(item.getClassId());
                if (classById != null) {
                    GradeGroup gradeGroup = gradeGroupService.getById(classById.getGradeGroup());
                    item.setGradeGroupName(gradeGroup == null ? "" : gradeGroup.getGradeGroupName());
                }
                SemesterEntity semester = semesterService.getById(item.getTerm());
                if (semester != null) {
                    item.setTermName(semester.getName());
                }
            });
        }
        return Result.success(userRewardList);
    }

    @PostMapping("/auto/add")
    @ApiOperation("自动添加奖励")
    @SaCheckPermission("userreward:auto:add")
    public Result<Void> autoAddUserRewards() {
        userRewardService.autoUpdateUserRewards();
        return Result.success();
    }

    @ApiOperation("导入")
    @PostMapping("/import")
    @SaCheckPermission("userreward:import")
    public Result<Long> importRecord(@Valid @ModelAttribute UserRewardImportReqModel reqModel) {
        try {
            Long importId = userRewardService.importRecord(getSchoolId(), getUserId(), reqModel.getTemplateId(), reqModel.getDefinitionId(), reqModel.getSid(), reqModel.getTerm(), reqModel.getType(), reqModel.getApprover(), reqModel.getUploadFile());
            return Result.success(importId);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.FILE_UPLOAD_FAILED.getMessageCode()) + ":" + e.getMessage());
        }
    }

    @GetMapping("/pending")
    @ApiOperation("待审批列表")
    @SaCheckPermission("userreward:pending")
    public Result<PageInfo<UserRewardPendingPageResModel>> pending(@Valid UserRewardPendingReqModel reqModel) {
        return Result.success(userRewardService.pending(getSchoolId(), getUserId(), reqModel));
    }

    @GetMapping("/approved")
    @ApiOperation("已审批列表")
    @SaCheckPermission("userreward:approved")
    public Result<PageInfo<UserRewardPendingPageResModel>> approved(@Valid UserRewardPendingReqModel reqModel) {
        return Result.success(userRewardService.approved(getSchoolId(), getUserId(), reqModel));
    }

    @GetMapping("/list/all")
    @ApiOperation("全部列表")
    @SaCheckPermission("userreward:list:all")
    public Result<PageInfo<UserRewardAllListPageResModel>> allList(@Valid UserRewardAllListReqModel reqModel) {
        return Result.success(userRewardService.allList(getSchoolId(), getUserId(), reqModel));
    }

    @GetMapping("/info/{id}")
    @ApiOperation("详情")
    @SaCheckPermission("userreward:info")
    public Result<UserRewardInfoResModel> info(@ApiParam("奖惩id") @PathVariable Long id) {
        return Result.success(userRewardService.info(getSchoolId(), id));
    }

    /**
     * 惩罚导出
     */
    @PostMapping("/export/list")
    @ApiOperation("导出列表")
    @SaCheckPermission("userreward:export:list")
    public Result<PageInfo<ExportRecordResModel>> exportList(@RequestBody ExportRecordReqModel reqModel) {
        List<Long> classIds = new ArrayList<>();
        if (reqModel.getClassId() == null){
            //查询班级
            LambdaQueryWrapper<SysClass> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysClass::getSchoolId, getSchoolId());
            queryWrapper.eq(reqModel.getDepartment() != null, SysClass::getDepartment, reqModel.getDepartment());
            queryWrapper.eq(reqModel.getSchoolYear() != null, SysClass::getSid, reqModel.getSchoolYear());
            if (reqModel.getTerm() != null)
            {
                SemesterEntity semester = semesterService.getById(reqModel.getTerm());
                if (semester != null) {
                    queryWrapper.eq( SysClass::getSid, semester.getSchoolYear());
                }
            }
            List<SysClass> list = sysClassService.list(queryWrapper);
            if (list == null || list.isEmpty())
            {
                return Result.success(new PageInfo<>());
            }
            classIds = list.stream().map(SysClass::getId).collect(Collectors.toList());
        }else {
            classIds.add(reqModel.getClassId());
        }

        PageInfo<ExportRecordResModel> list = exportRecordService.list(reqModel.getPageNum(), reqModel.getPageSize(), classIds,
                getSchoolId(),1);
        return Result.success(list);
    }

    /**
    * 导出
     */
    @PostMapping("/export")
    @ApiOperation("导出")
    @SaCheckPermission("userreward:export")
    public Result<String> export(@RequestBody ExportRecordReqModel reqModel)
    {
        if (reqModel.getClassId() == null){
            return Result.failed(ResultCode.FAILED);
        }
        Long aLong = userRewardService.exportPdf(getSchoolId(), reqModel.getClassId(), new Date(reqModel.getStartTime()), new Date(reqModel.getEndTime()));
        ExportRecord exportRecord = exportRecordService.getById(aLong);
        if(exportRecord == null){
            throw new BusinessException(LanguageConstants.NO_INFORMATION_AVAILABLE_TO_EXPORT);
        }
        return Result.success(exportRecord.getUrl());
    }
}