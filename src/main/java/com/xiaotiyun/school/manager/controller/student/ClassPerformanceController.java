package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.ClassPerformanceAddReqModel;
import com.xiaotiyun.school.manager.model.req.ClassPerformanceQueryReqModel;
import com.xiaotiyun.school.manager.model.req.StudentQualityScoreQueryReqModel;
import com.xiaotiyun.school.manager.model.res.ClassPerformanceDetailResModel;
import com.xiaotiyun.school.manager.model.res.StudentQualityScoreModel;
import com.xiaotiyun.school.manager.model.res.StudentResModel;
import com.xiaotiyun.school.manager.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/classperformance")
@Api(tags = "课堂表现管理")
public class ClassPerformanceController extends BasicController {

    @Resource
    private ClassPerformanceService classPerformanceService;

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

    @PostMapping("/add")
    @ApiOperation("批量新增课堂表现")
    @SaCheckPermission("classperformance:add")
    public Result<Void> addClassPerformances(HttpServletRequest request, @Valid @RequestBody List<ClassPerformanceAddReqModel> reqModels) {
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
        List<ClassPerformance> classPerformances = reqModels.stream()
                .map(reqModel -> {
                    ClassPerformance classPerformance = BeanConvertUtil.convert(reqModel, ClassPerformance.class);
                    classPerformance.setSchoolId(schoolId);
                    classPerformance.setUserId(id);
                    return classPerformance;
                })
                .collect(Collectors.toList());
        classPerformanceService.createClassPerformances(classPerformances);
        return Result.success();
    }

    @PostMapping("/update")
    @ApiOperation("修改课堂表现")
    @SaCheckPermission("classperformance:update")
    public Result<Void> updateClassPerformance(HttpServletRequest request,@Valid @RequestBody ClassPerformanceAddReqModel reqModel) {
        ClassPerformance classPerformance = BeanConvertUtil.convert(reqModel, ClassPerformance.class);
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
        classPerformance.setUserId(id);
        classPerformanceService.updateClassPerformance(classPerformance);
        return Result.success();
    }

    @GetMapping("/delete")
    @ApiOperation("删除课堂表现")
    @SaCheckPermission("classperformance:delete")
    public Result<Void> deleteClassPerformance(@RequestParam Long id) {
        classPerformanceService.deleteClassPerformance(id);
        return Result.success();
    }

    @GetMapping("/get")
    @ApiOperation("查看课堂表现详情")
    @SaCheckPermission("classperformance:get")
    public Result<ClassPerformanceDetailResModel> getClassPerformanceDetail(@RequestParam Long id) {
        ClassPerformance classPerformance = classPerformanceService.getClassPerformanceById(id);
        ClassPerformanceDetailResModel resModel = BeanConvertUtil.convert(classPerformance, ClassPerformanceDetailResModel.class);
        StudentResModel info = studentService.getStudentById(resModel.getStudentId());
        if(info!=null) {
            resModel.setStudentName(info.getChineseName());
            SysClass classById = sysClassService.getSysClassById(info.getClassId());
            if(classById!=null) {
                GradeGroup gradeGroup = gradeGroupService.getById(classById.getGradeGroup());
                resModel.setGradeGroupName(gradeGroup == null ? "" : gradeGroup.getGradeGroupName());
                resModel.setClassName(classById.getClassName());
            }
        }
        SemesterEntity semester = semesterService.getById(resModel.getTerm());
        if(semester!=null) {
            resModel.setTermName(semester.getName());
        }
        UserSchoolRelEntity userSchoolRelEntity = userSchoolRelDao.selectById(resModel.getUserId());
        if(userSchoolRelEntity!=null){
            resModel.setUserName(userSchoolRelEntity.getUsername());
        }
        return Result.success(resModel);
    }

    @PostMapping("/list")
    @ApiOperation("查询课堂表现列表")
    @SaCheckPermission("classperformance:list")
    public Result<PageInfo<ClassPerformanceDetailResModel>> getClassPerformanceList(@RequestBody ClassPerformanceQueryReqModel reqModel) {
        reqModel.setUserId(getUserId());
        PageInfo<ClassPerformanceDetailResModel> classPerformanceList = classPerformanceService.getClassPerformanceList(reqModel);
        if (CollectionUtils.isEmpty(classPerformanceList.getList()))
        {
            return Result.success(classPerformanceList);
        }
        classPerformanceList.getList().forEach(item -> {
            StudentResModel info = studentService.getStudentById(item.getStudentId());
            if(info!=null) {
                item.setStudentName(info.getChineseName());
                item.setSeatNo(info.getSeatNo());
                SysClass classById = sysClassService.getSysClassById(info.getClassId());
                if(classById!=null) {
                    GradeGroup gradeGroup = gradeGroupService.getById(classById.getGradeGroup());
                    item.setGradeGroupName(gradeGroup == null ? "" : gradeGroup.getGradeGroupName());
                    item.setClassName(classById.getClassName());
                }
            }
            SemesterEntity semester = semesterService.getById(item.getTerm());
            if(semester!=null) {
                item.setTermName(semester.getName());
            }
            UserSchoolRelEntity userSchoolRelEntity = userSchoolRelDao.selectById(item.getUserId());
            if(userSchoolRelEntity!=null){
                item.setUserName(userSchoolRelEntity.getUsername());
            }
        });
        return Result.success(classPerformanceList);
    }


    @PostMapping("/check/list")
    @ApiOperation("查询素质检查列表")
    @SaCheckPermission("classperformance:checkList")
    public Result<PageInfo<StudentQualityScoreModel>> getClassPerformanceCheckList(@Valid @RequestBody StudentQualityScoreQueryReqModel reqModel) {
        reqModel.setSid(null);
        reqModel.setTerm(null);
        reqModel.setUserId(getUserId());
        PageInfo<StudentQualityScoreModel> classPerformanceList = classPerformanceService.getClassPerformanceCheckList(reqModel);
        return Result.success(classPerformanceList);
    }

    @ApiOperation("导出查询素质检查列表")
    @PostMapping("/export")
    @SaCheckPermission("classperformance:checkExport")
    public Result<String> exportStudentQualityScoreList(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody StudentQualityScoreQueryReqModel reqModel) throws IOException {
        long schoolId = getSchoolId(request);
        reqModel.setSchoolId(schoolId);
        reqModel.setSid(null);
        reqModel.setTerm(null);
        return Result.success(classPerformanceService.exportStudentQualityScoreList(reqModel));
    }
}