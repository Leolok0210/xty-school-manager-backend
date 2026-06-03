package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.MissingAssignmentPerformanceAddReqModel;
import com.xiaotiyun.school.manager.model.req.MissingAssignmentPerformanceQueryReqModel;
import com.xiaotiyun.school.manager.model.res.MissingAssignmentPerformanceDetailResModel;
import com.xiaotiyun.school.manager.model.res.StudentResModel;
import com.xiaotiyun.school.manager.model.res.SubjectRelResModel;
import com.xiaotiyun.school.manager.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/missingassignmentperformance")
@Api(tags = "欠交作业表现管理")
public class MissingAssignmentPerformanceController extends BasicController {

    @Resource
    private MissingAssignmentPerformanceService missingAssignmentPerformanceService;



    @Resource
    private StudentService studentService;

    @Resource
    private SemesterService semesterService;


    @Resource
    private SysClassService sysClassService;

    @Resource
    private UserSchoolRelDao userSchoolRelDao;


    @Resource
    private GradeGroupService gradeGroupService;


    @Resource
    private SubjectRelService subjectRelService;
    @PostMapping("/add")
    @ApiOperation("批量新增欠交作业表现")
    @SaCheckPermission("missingassignmentperformance:add")
    public Result<Void> addMissingAssignmentPerformances(HttpServletRequest request, @Valid @RequestBody List<MissingAssignmentPerformanceAddReqModel> reqModels) {
        long schoolId = getSchoolId(request);
        Long userId = StpUtil.getLoginIdAsLong();
        if(CollectionUtils.isEmpty(reqModels))
        {
            return Result.failed(ResultCode.VALIDATE_FAILED);
        }
        LambdaQueryWrapper<UserSchoolRelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserSchoolRelEntity::getUserId, userId);
        queryWrapper.eq(UserSchoolRelEntity::getSchoolId, schoolId);
        UserSchoolRelEntity entity = userSchoolRelDao.selectOne(queryWrapper);
        Long id;
        if(entity != null)
        {
            id = entity.getId();
        }else {
            id = 0L;
        }
        List<MissingAssignmentPerformance> missingAssignmentPerformances = reqModels.stream()
                .map(reqModel -> {
                    MissingAssignmentPerformance missingAssignmentPerformance = BeanConvertUtil.convert(reqModel, MissingAssignmentPerformance.class);
                    missingAssignmentPerformance.setSchoolId(schoolId);
                    missingAssignmentPerformance.setUserId(id);
                    return missingAssignmentPerformance;
                })
                .collect(Collectors.toList());
        missingAssignmentPerformanceService.createMissingAssignmentPerformances(missingAssignmentPerformances);
        return Result.success();
    }

    @PostMapping("/update")
    @ApiOperation("修改欠交作业表现")
    @SaCheckPermission("missingassignmentperformance:update")
    public Result<Void> updateMissingAssignmentPerformance(HttpServletRequest request,@Valid @RequestBody MissingAssignmentPerformanceAddReqModel reqModel) {
        MissingAssignmentPerformance missingAssignmentPerformance = BeanConvertUtil.convert(reqModel, MissingAssignmentPerformance.class);
        Long userId = StpUtil.getLoginIdAsLong();
        long schoolId = getSchoolId(request);
        LambdaQueryWrapper<UserSchoolRelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserSchoolRelEntity::getUserId, userId);
        queryWrapper.eq(UserSchoolRelEntity::getSchoolId, schoolId);
        UserSchoolRelEntity entity = userSchoolRelDao.selectOne(queryWrapper);
        Long id;
        if(entity != null)
        {
            id = entity.getId();
        }else {
            id = 0L;
        }
        missingAssignmentPerformance.setUserId(id);
        missingAssignmentPerformanceService.updateMissingAssignmentPerformance(missingAssignmentPerformance);
        return Result.success();
    }

    @GetMapping("/delete")
    @ApiOperation("删除欠交作业表现")
    @SaCheckPermission("missingassignmentperformance:delete")
    public Result<Void> deleteMissingAssignmentPerformance(@RequestParam Long id) {
        missingAssignmentPerformanceService.deleteMissingAssignmentPerformance(id);
        return Result.success();
    }

    @GetMapping("/get")
    @ApiOperation("查看欠交作业表现详情")
    @SaCheckPermission("missingassignmentperformance:get")
    public Result<MissingAssignmentPerformanceDetailResModel> getMissingAssignmentPerformanceDetail(@RequestParam Long id) {
        MissingAssignmentPerformance missingAssignmentPerformance = missingAssignmentPerformanceService.getMissingAssignmentPerformanceById(id);
        MissingAssignmentPerformanceDetailResModel resModel = BeanConvertUtil.convert(missingAssignmentPerformance, MissingAssignmentPerformanceDetailResModel.class);
        StudentResModel info = studentService.getStudentById(resModel.getStudentId());
        if(info!=null) {
            resModel.setStudentName(info.getChineseName());
            resModel.setSeatNo(info.getSeatNo());
            SysClass classById = sysClassService.getSysClassById(info.getClassId());
            if(classById!=null) {
                GradeGroup gradeGroup = gradeGroupService.getById(classById.getGradeGroup());
                resModel.setGradeGroupName(gradeGroup == null ? "" : gradeGroup.getGradeGroupName());
                resModel.setClassName(classById.getClassName());
                resModel.setClassNumber(classById.getClassSerialNumber());
            }
        }
        SemesterEntity semesterEntity = semesterService.getById(resModel.getTerm());
        if(semesterEntity!=null) {
            resModel.setTermName(semesterEntity.getName());
        }
        UserSchoolRelEntity userSchoolRelEntity = userSchoolRelDao.selectById(resModel.getUserId());
        if(userSchoolRelEntity!=null){
            resModel.setUserName(userSchoolRelEntity.getUsername());
        }
        List<SubjectRelResModel> relResModels = subjectRelService.listByIds(Lists.newArrayList(resModel.getSubjectId()));
        if (!relResModels.isEmpty())
        {
            resModel.setSubjectName(relResModels.get(0).getSubject().getSubjectName());
        }

        return Result.success(resModel);
    }

    @PostMapping("/list")
    @ApiOperation("查询欠交作业表现列表")
    @SaCheckPermission("missingassignmentperformance:list")
    public Result<PageInfo<MissingAssignmentPerformanceDetailResModel>> getMissingAssignmentPerformanceList(@RequestBody MissingAssignmentPerformanceQueryReqModel reqModel) {

        reqModel.setUserId(getUserId());
        PageInfo<MissingAssignmentPerformanceDetailResModel> missingAssignmentPerformanceList = missingAssignmentPerformanceService.getMissingAssignmentPerformanceList(reqModel);
        if(CollectionUtils.isEmpty(missingAssignmentPerformanceList.getList()))
        {
            missingAssignmentPerformanceList.setList(new ArrayList<>());
            return Result.success(missingAssignmentPerformanceList);
        }
        List<Long> ids = missingAssignmentPerformanceList.getList().stream().map(MissingAssignmentPerformanceDetailResModel::getSubjectId).collect(Collectors.toList());
        List<SubjectRelResModel> relResModels = subjectRelService.listByIds(ids);
        //to mao
        Map<Long, SubjectRelResModel> relResModelMap = relResModels.stream().collect(Collectors.toMap(SubjectRelResModel::getSubjectId, item -> item));
        missingAssignmentPerformanceList.getList().forEach(
                item -> {
                    StudentResModel info = studentService.getStudentById(item.getStudentId());
                    if(info!=null) {
                        item.setStudentName(info.getChineseName());
                        item.setSeatNo(info.getSeatNo());
                        SysClass classById = sysClassService.getSysClassById(info.getClassId());
                        if(classById!=null) {
                            GradeGroup gradeGroup = gradeGroupService.getById(classById.getGradeGroup());
                            item.setGradeGroupName(gradeGroup == null ? "" : gradeGroup.getGradeGroupName());
                            item.setClassName(classById.getClassName());
                            item.setClassNumber(classById.getClassSerialNumber());
                        }
                    }
                    SemesterEntity semesterEntity = semesterService.getById(item.getTerm());
                    if(semesterEntity!=null) {
                        item.setTermName(semesterEntity.getName());
                    }
                    UserSchoolRelEntity userSchoolRelEntity = userSchoolRelDao.selectById(item.getUserId());
                    if(userSchoolRelEntity!=null){
                        item.setUserName(userSchoolRelEntity.getUsername());
                    }
                    SubjectRelResModel subjectById = relResModelMap.get(item.getSubjectId());
                    if(subjectById!=null){
                        item.setSubjectName(subjectById.getSubject().getSubjectName());
                    }
                }
        );
        return Result.success(missingAssignmentPerformanceList);
    }
}