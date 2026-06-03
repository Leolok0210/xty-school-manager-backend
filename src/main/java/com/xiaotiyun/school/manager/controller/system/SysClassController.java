package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.secure.BCrypt;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.EnterpriseWxChatTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.GradeEnum;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.ClassUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.GradeGroupMapper;
import com.xiaotiyun.school.manager.dao.UserDao;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.helper.WxHelper;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.GraduateStudentsReqModel;
import com.xiaotiyun.school.manager.model.req.SysClassAddReqModel;
import com.xiaotiyun.school.manager.model.req.SysClassPromoteReqModel;
import com.xiaotiyun.school.manager.model.req.SysClassQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SysClassDetailResModel;
import com.xiaotiyun.school.manager.model.res.SysClassListResModel;
import com.xiaotiyun.school.manager.service.SchoolMajorService;
import com.xiaotiyun.school.manager.service.StudentService;
import com.xiaotiyun.school.manager.service.SysClassService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/sysclass")
@Api(tags = "班级管理")
public class SysClassController extends BasicController {

    @Resource
    private SysClassService sysClassService;

    @Resource
    private StudentService studentService;

    @Resource
    private SchoolMajorService schoolMajorService;

    @Resource
    private UserSchoolRelDao userSchoolRelDao;

    @Resource
    private GradeGroupMapper gradeGroupMapper;


    @Autowired
    private LanguageUtil languageUtil;

    @Autowired
    private ClassUtils classUtils;

    @Resource
    private UserDao userDao;


    @Resource
    private UserAuthHelper userAuthHelper;

    @Resource
    private WxHelper wxHelper;

    @SaCheckPermission("sysclass:add")
    @PostMapping("/add")
    @ApiOperation("批量新增班级")
    public Result<Boolean> addSysClasses(HttpServletRequest request, @Valid @RequestBody List<SysClassAddReqModel> reqModels) {
        long schoolId = getSchoolId(request);
        List<SysClass> sysClasses = BeanConvertUtil.convertList(reqModels, SysClass.class);

        return sysClassService.createSysClasses(sysClasses,schoolId);
    }

    @PostMapping("/update")
    @ApiOperation("修改班级")
    @SaCheckPermission("sysclass:update")
    public Result<Void> updateSysClass(HttpServletRequest request,@Valid @RequestBody SysClassAddReqModel reqModel) {
        long schoolId = getSchoolId(request);
        SysClass sysClass = BeanConvertUtil.convert(reqModel, SysClass.class);
        if(StringUtils.isEmpty(sysClass.getClassName()))
        {
            //最终展示的班级名称= 级组名称+班级序号：比如小六1班
            sysClass.setClassName(sysClass.getClassSerialNumber() +"班");
        }
        List<SysClass> oldClass = sysClassService.getSysClassBySchoolIdAndClassNameAndSidAndGradeGroupId(schoolId, sysClass.getClassName(), reqModel.getSid(), reqModel.getGradeGroup());
        if(oldClass != null && !oldClass.isEmpty())
        {
            List<SysClass> collect = oldClass.stream().filter(c -> !Objects.equals(c.getId(), sysClass.getId())).collect(Collectors.toList());
            if(!collect.isEmpty())
            {
                return Result.failed(ResultCode.VALIDATE_FAILED.getCode(),sysClass.getClassName()+":"+languageUtil.getMessage(ResultCode.FILE_UPLOAD_FAILED.getMessageCode()));
            }
        }
        SysClass oldSysClass = sysClassService.getSysClassById(sysClass.getId());
        GradeGroup gradeGroup = gradeGroupMapper.selectById(sysClass.getGradeGroup());
        String classNumber = classUtils.getClassNumber(schoolId, gradeGroup == null ? "" : gradeGroup.getGrade(), oldSysClass.getClassSerialNumber(), sysClass.getDepartment());
        sysClass.setClassNumber(classNumber);
        sysClassService.updateSysClass(sysClass);
        wxHelper.crateOrUpdateParents(schoolId, Lists.newArrayList(reqModel.getId()), null, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_CLASS);
        return Result.success();
    }

    @GetMapping("/delete")
    @ApiOperation("删除班级")
    @SaCheckPermission("sysclass:delete")
    public Result<Void> deleteSysClass(@RequestParam Long id) {
        long count = studentService.count(Wrappers.<StudentEntity>lambdaQuery()
                .eq(StudentEntity::getClassId, id));
        if(count > 0) {
            return Result.failed(ResultCode.FAILED.getCode(),languageUtil.getMessage(LanguageConstants.CLASS_HAS_STUDENT));
        }
        sysClassService.deleteSysClass(id);
        wxHelper.delete(getSchoolId(), Lists.newArrayList(id), EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_CLASS, null);
        return Result.success();
    }

    @GetMapping("/get")
    @ApiOperation("查看班级详情")
    public Result<SysClassDetailResModel> getSysClassDetail(@RequestParam Long id) {
        SysClass sysClass = sysClassService.getSysClassById(id);
        SysClassDetailResModel resModel = BeanConvertUtil.convert(sysClass, SysClassDetailResModel.class);
        if(resModel.getProfessionalId() != null){
            SchoolMajor schoolMajorById = schoolMajorService.getSchoolMajorById(resModel.getProfessionalId());
            resModel.setProfessionalName(schoolMajorById == null ? "" : schoolMajorById.getMajorName());
        }
        UserSchoolRelEntity userSchoolRelEntity = userSchoolRelDao.selectById(resModel.getHeadTeacher());
        if(userSchoolRelEntity != null)
        {
            resModel.setHeadTeacherName(userSchoolRelEntity.getUsername());
        }
        GradeGroup gradeGroup = gradeGroupMapper.selectById(resModel.getGradeGroup());
        resModel.setGradeGroupName(gradeGroup == null ? "" : gradeGroup.getGradeGroupName());
        resModel.setProfessionalSubject(gradeGroup == null ? 0 : gradeGroup.getProfessionalSubject());
        return Result.success(resModel);
    }

    @PostMapping("/list")
    @ApiOperation("查询班级列表")
    @SaCheckPermission("sysclass:list")
    public Result<PageInfo<SysClassDetailResModel>> getSysClassList(@RequestBody SysClassQueryReqModel reqModel) {
        Long userId = getUserId();
        reqModel.setUserId(userId);
        return Result.success(sysClassService.getSysClassList(reqModel));
    }

    @SaIgnore
    @PostMapping("/student/list")
    @ApiOperation("查询班级列表-学生端(非鉴权)")
    public Result<PageInfo<SysClassDetailResModel>> getSysClassListByStudent(@RequestBody SysClassQueryReqModel reqModel) {
        return Result.success(sysClassService.getSysClassListByStudent(reqModel));
    }

    @GetMapping("/listBySchool")
    @ApiOperation("根据学校ID查询班级列表")
    public Result<List<SysClassListResModel>> getSysClassListBySchool(@RequestParam Long schoolId) {
        List<SysClassListResModel> sysClassListBySchoolId = sysClassService.getSysClassListBySchoolId(schoolId);
        if (CollectionUtils.isEmpty(sysClassListBySchoolId)) {
            return Result.success(sysClassListBySchoolId);
        }
        List<Long> classIds = sysClassListBySchoolId.stream().map(SysClassListResModel::getClassId).collect(Collectors.toList());
        Map<Long, Boolean> hasClassPermission = userAuthHelper.hasClassPermission(getUserId(), schoolId, classIds);
        sysClassListBySchoolId.removeIf(classListResModel -> {
            Boolean aBoolean = hasClassPermission.get(classListResModel.getClassId());
            if (aBoolean == null) {
                return true;
            }
            return !aBoolean;
        });
        return Result.success(sysClassListBySchoolId);
    }

    // 新增API：根据学校ID和sid查询班级列表
    @GetMapping("/listBySchoolAndSid")
    @ApiOperation("根据学校ID和sid查询班级列表")
    public Result<List<SysClassListResModel>> getSysClassListBySchoolAndSid(@RequestParam Long schoolId, @RequestParam(required = false) String sid,
                                                                            @ApiParam("学部") @RequestParam(required = false) Integer department) {
        List<SysClassListResModel> schoolIdAndSid = sysClassService.getSysClassListBySchoolIdAndSid(schoolId, sid, department);
        if (CollectionUtils.isEmpty(schoolIdAndSid)) {
            return Result.success(schoolIdAndSid);
        }
        List<Long> classIds = schoolIdAndSid.stream().map(SysClassListResModel::getClassId).collect(Collectors.toList());
        Map<Long, Boolean> hasClassPermission = userAuthHelper.hasClassPermission(getUserId(), schoolId, classIds);
        schoolIdAndSid.removeIf(classListResModel -> {
            Boolean aBoolean = hasClassPermission.get(classListResModel.getClassId());
            if (aBoolean == null) {
                return true;
            }
            return !aBoolean;
        });
        return Result.success(schoolIdAndSid);
    }

    @GetMapping("/student/listBySchoolAndSid")
    @ApiOperation("根据学校ID和sid查询班级列表-学生端(非鉴权)")
    public Result<List<SysClassListResModel>> getSysClassListBySchoolAndSidStudent(@RequestParam Long schoolId, @RequestParam(required = false) String sid,
                                                                            @ApiParam("学部") @RequestParam(required = false) Integer department) {
        return Result.success(sysClassService.getSysClassListBySchoolIdAndSid(schoolId, sid,department));
    }

    // 新增API：批量升班
    @PostMapping("/promote")
    @ApiOperation("批量升班")
    @SaCheckPermission("sysclass:promote")
    public Result<List<String>> promoteClasses(HttpServletRequest request,@Valid @RequestBody SysClassPromoteReqModel reqModel) {
        long schoolId = getSchoolId(request);
        //密码校验
        UserEntity userEntity = userDao.selectById(reqModel.getUserId());
        if(userEntity == null)
        {
            return Result.failed(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }
        if (!BCrypt.checkpw(reqModel.getPassword(), userEntity.getPassword())) {
            return Result.failed(ResultCode.USER_ACCOUNT_PASSWORD_ERROR);
        }
        return Result.success(sysClassService.promoteClasses(schoolId));
    }

    // 新增API：将当前班级下的全部学生状态修改为“毕业”
    @PostMapping("/graduateStudents")
    @ApiOperation("将当前班级下的全部学生状态修改为“毕业”")
    @SaCheckPermission("sysclass:graduateStudents")
    public Result<Void> graduateStudents(@Valid @RequestBody GraduateStudentsReqModel reqModel) {
        //密码校验
        UserEntity userEntity = userDao.selectById(reqModel.getUserId());
        if(userEntity == null)
        {
            return Result.failed(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }
        if (!BCrypt.checkpw(reqModel.getPassword(), userEntity.getPassword())) {
            return Result.failed(ResultCode.USER_ACCOUNT_PASSWORD_ERROR);
        }
        reqModel.getClassIds().forEach(classId -> {
            sysClassService.graduateStudentsInClass(classId);
        });
        return Result.success();
    }

    // 新增API：根据组级名称查询班级列表
    @GetMapping("/listByGradeGroupNames")
    @ApiOperation("根据查询级组里面最大的级组对应的班级列表")
//    @SaCheckPermission("sysclass:listByGradeGroupNames")
    public Result<List<SysClassListResModel>> getSysClassListByGradeGroupNames(HttpServletRequest request, @ApiParam("学年") @RequestParam String sid) {
        long schoolId = getSchoolId(request);
        List<String> gradeGroupNames = new ArrayList<>();
        gradeGroupNames.add(GradeEnum.GRADE_6.getDesc());
        gradeGroupNames.add(GradeEnum.KINDERGARTEN_3.getDesc());
        gradeGroupNames.add(GradeEnum.MIDDLE_6.getDesc());
        List<SysClassListResModel> classListByGradeGroupNames = sysClassService.getSysClassListByGradeGroupNames(gradeGroupNames, sid, schoolId);
        if (CollectionUtils.isEmpty(classListByGradeGroupNames)) {
            return Result.success(classListByGradeGroupNames);
        }
        List<Long> classIds = classListByGradeGroupNames.stream().map(SysClassListResModel::getClassId).collect(Collectors.toList());
        Map<Long, Boolean> hasClassPermission = userAuthHelper.hasClassPermission(getUserId(), schoolId, classIds);
        classListByGradeGroupNames.removeIf(classListResModel -> {
            Boolean aBoolean = hasClassPermission.get(classListResModel.getClassId());
            if (aBoolean == null) {
                return true;
            }
            return !aBoolean;
        });
        return Result.success(classListByGradeGroupNames);
    }

    @ApiOperation("班级导入")
    @PostMapping("/import")
    @SaCheckPermission("sysclass:import")
    public Result<Long> importClass(
            @ApiParam("Excel文件") @RequestPart("uploadFile") MultipartFile file,
            @ApiParam("学校id") @RequestParam Long schoolId,
            @ApiParam("学年") @RequestParam String sid) {
        try {
            if(schoolId == null || sid == null)
            {
                return Result.failed(ResultCode.VALIDATE_FAILED);
            }
            Long importId = sysClassService.importClass(file, schoolId,sid);
            return Result.success(importId);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.FILE_UPLOAD_FAILED.getMessageCode()) + ":" +  e.getMessage());
        }
    }

    // 新增API：导出班级列表
    @PostMapping("/export")
    @ApiOperation("导出班级列表")
    @SaCheckPermission("sysclass:export")
    public Result<String> exportSysClassList(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody SysClassQueryReqModel reqModel) throws IOException {
        long schoolId = getSchoolId(request);
        reqModel.setSchoolId(schoolId);
        return Result.success(sysClassService.exportClassList(reqModel));
    }

    @ApiOperation("查询班级列表")
    @GetMapping("/list")
    public Result<List<SysClassListResModel>> listClasses(
            @ApiParam("学校ID") @RequestParam Long schoolId,
            @ApiParam("学年") @RequestParam(required = false) String schoolYear,
            //学部
            @ApiParam("学部") @RequestParam(required = false) Integer department,
            @ApiParam("级组ID") @RequestParam(required = false) Long gradeGroupId) {
        return Result.success(sysClassService.listClasses(schoolId, schoolYear, gradeGroupId, department,getUserId()));
    }
}