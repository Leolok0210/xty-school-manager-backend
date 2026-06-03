package com.xiaotiyun.school.manager.controller.activity;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.handler.ActivityCoursesImportTemplateHeaderHandler;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityCoursesRecordEntity;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import com.xiaotiyun.school.manager.model.req.LeisureActivityCoursesAddRecordReq;
import com.xiaotiyun.school.manager.model.req.LeisureActivityCoursesQueryRecordReq;
import com.xiaotiyun.school.manager.model.res.LeisureActivityCoursesRecordRes;
import com.xiaotiyun.school.manager.service.LeisureActivityCoursesRecordService;
import com.xiaotiyun.school.manager.service.UserSchoolRelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/leisure/activity/courses")
@Api(tags = "余暇活动课程记录管理")
@RequiredArgsConstructor
public class LeisureActivityCoursesRecordController extends BasicController {
    private final LeisureActivityCoursesRecordService service;

    private final UserSchoolRelService userSchoolRelService;

    private final LanguageUtil languageUtil;

    @Value("${file.templateUrl}")
    private String templateUrl;

    @PostMapping("/list")
    @SaCheckPermission("leisureActivityCoursesRecord:list")
    @ApiOperation("获取所有余暇活动课程记录")
    public Result<List<LeisureActivityCoursesRecordRes>> list(@RequestBody @Validated LeisureActivityCoursesQueryRecordReq req) {
        req.setSchoolId(getSchoolId());
        return Result.success(service.listAndApply(req));
    }

    @PostMapping("/student/list")
    @ApiOperation("获取所有余暇活动课程记录-学生端(非鉴权)")
    public Result<List<LeisureActivityCoursesRecordRes>> listByStudent(@RequestBody @Validated LeisureActivityCoursesQueryRecordReq req) {
        req.setSchoolId(getSchoolId());
        return Result.success(service.pageAndApplyByStudent(req));
    }

    @PostMapping("/page")
    @SaCheckPermission("leisureActivityCoursesRecord:page")
    @ApiOperation("余暇管理-课程列表-分页")
    public Result<PageInfo<LeisureActivityCoursesRecordRes>> page(@RequestBody @Validated LeisureActivityCoursesQueryRecordReq req) {
        req.setSchoolId(getSchoolId());
        return Result.success(service.pageAndPre(req));
    }

    @PostMapping("/apply/page")
    @SaCheckPermission("leisureActivityCoursesRecord:applyPage")
    @ApiOperation("报名管理-课程列表-分页")
    public Result<PageInfo<LeisureActivityCoursesRecordRes>> applyPage(@RequestBody @Validated LeisureActivityCoursesQueryRecordReq req) {
        req.setSchoolId(getSchoolId());
        return Result.success(service.pageAndApply(req));
    }

    @GetMapping("/get/{id}")
    @SaCheckPermission("leisureActivityCoursesRecord:detail")
    @ApiOperation("根据ID获取余暇活动课程记录")
    public Result<LeisureActivityCoursesRecordRes> getLeisureActivityCoursesRecordById(@PathVariable Long id) {
        LeisureActivityCoursesRecordEntity entity = service.getById(id);
        if (entity != null) {
            LeisureActivityCoursesRecordRes res = new LeisureActivityCoursesRecordRes();
            BeanUtils.copyProperties(entity, res);
            return Result.success(res);
        }
        return Result.success();
    }

    @PostMapping("/add")
    @SaCheckPermission("leisureActivityCoursesRecord:add")
    @ApiOperation("创建新的余暇活动课程记录")
    public Result<List<String>> createLeisureActivityCoursesRecord(@RequestBody @Validated List<LeisureActivityCoursesAddRecordReq> req) {
        if (req != null && !req.isEmpty()) {
            List<String> names = req.stream().map(LeisureActivityCoursesAddRecordReq::getName).collect(Collectors.toList());
            List<LeisureActivityCoursesRecordEntity> oldNames = service.list(Wrappers.<LeisureActivityCoursesRecordEntity>lambdaQuery()
                    .eq(LeisureActivityCoursesRecordEntity::getSchoolId,getSchoolId())
                    .eq(LeisureActivityCoursesRecordEntity::getActivityId,req.get(0).getActivityId())
                    .in(LeisureActivityCoursesRecordEntity::getName, names));
            if (ObjectUtils.isNotEmpty(oldNames)) {
                Result<List<String>> failed = Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.COURSE_NAME_DUPLICATED));
                List<String> oldNameList = oldNames.stream().map(LeisureActivityCoursesRecordEntity::getName).collect(Collectors.toList());
                failed.setData(oldNameList);
                return failed;
            }
            List<String> newNames = new ArrayList<>();
            List<LeisureActivityCoursesRecordEntity> insertList = new ArrayList<>();
            for (LeisureActivityCoursesAddRecordReq coursesAddRecordReq : req) {
                if (newNames.contains(coursesAddRecordReq.getName())) {
                    Result<List<String>> failed = Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.COURSE_NAME_DUPLICATED));
                    failed.setData(newNames);
                    return failed;
                }
                LeisureActivityCoursesRecordEntity entity = new LeisureActivityCoursesRecordEntity();
                BeanUtils.copyProperties(coursesAddRecordReq, entity);
                entity.setSchoolId(getSchoolId());
                insertList.add(entity);
                newNames.add(entity.getName());
            }
            service.saveBatch(insertList);
        }
        return Result.success();
    }

    @PutMapping("/update")
    @SaCheckPermission("leisureActivityCoursesRecord:update")
    @ApiOperation("更新余暇活动课程记录")
    public Result<Boolean> updateLeisureActivityCoursesRecord(@RequestBody @Validated LeisureActivityCoursesAddRecordReq record) {
        if (record.getId() == null) {
            return Result.failed(ResultCode.FAILED.getCode(),languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        List<LeisureActivityCoursesRecordEntity> oldNames = service.list(Wrappers.<LeisureActivityCoursesRecordEntity>lambdaQuery()
                .eq(LeisureActivityCoursesRecordEntity::getSchoolId,getSchoolId())
                .eq(LeisureActivityCoursesRecordEntity::getActivityId,record.getActivityId())
                .eq(LeisureActivityCoursesRecordEntity::getName, record.getName()));
        if (ObjectUtils.isNotEmpty(oldNames) && !oldNames.get(0).getId().equals(record.getId())) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.COURSE_NAME_DUPLICATED));
        }
        LeisureActivityCoursesRecordEntity entity = new LeisureActivityCoursesRecordEntity();
        BeanUtils.copyProperties(record, entity);
        entity.setSchoolId(getSchoolId());
        return Result.success(service.updateById(entity));
    }

    @DeleteMapping("/delete/{id}")
    @SaCheckPermission("leisureActivityCoursesRecord:delete")
    @ApiOperation("删除余暇活动课程记录")
    public Result<Boolean> deleteLeisureActivityCoursesRecord(@PathVariable Long id) {
        return Result.success(service.removeById(id));
    }

    @PostMapping("/import")
    @SaCheckPermission("leisureActivityCoursesRecord:import")
    @ApiOperation("导入余暇活动课程")
    public Result<Long> importCourses(@ApiParam("Excel文件") @RequestPart("file") MultipartFile file,
                                                           @ApiParam("活动ID") @RequestPart("activityId") String activityId) {
        return Result.success(service.importCourses(file, Long.valueOf(activityId), getSchoolId()));
    }

    @ApiOperation("导入模板下载")
    @GetMapping("/template/download")
    @SaCheckPermission("leisureActivityCoursesRecord:import")
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        // 获取学校语言设置信息
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        ExcelWriter excelWriter = null;
        try {
            // 必须先设置响应头再获取输出流
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            // 获取第一页sheet
            WriteSheet writeSheet;
            // 获取授课教师列表
            List<UserSchoolRelEntity> teachers = userSchoolRelService.list(Wrappers.<UserSchoolRelEntity>lambdaQuery()
                    .eq(UserSchoolRelEntity::getSchoolId, getSchoolId()));
            List<String> teacherNames = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(teachers)) {
                teacherNames = teachers.stream().map(a -> {
                    return a.getUsername() + "(" + a.getUserNumber() + ")";
                }).collect(Collectors.toList());
            }
            String templatePath;
            // 开始写入excel
            switch (languageEnum) {
                case EN_US:
                case PT_PT:
                    // 拉取本地模板
                    templatePath = templateUrl + "CoursesEnImportTemplate.xlsx";
                    excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(templatePath).build();
                    response.setHeader("Content-Disposition", "attachment; filename=User Import Template.xlsx");
                    writeSheet = EasyExcel.writerSheet(0) // 指定第一页
                            .registerWriteHandler(new ActivityCoursesImportTemplateHeaderHandler(teacherNames))
                            .build();
                    break;
                default:
                    // 拉取本地模板
                    templatePath = templateUrl + "CoursesImportTemplate.xlsx";
                    excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(templatePath).build();
                    response.setHeader("Content-Disposition", "attachment; filename=余暇活动课程导入模板.xlsx");
                    writeSheet = EasyExcel.writerSheet(0) // 指定第一页
                            .registerWriteHandler(new ActivityCoursesImportTemplateHeaderHandler(teacherNames))
                            .build();
                    break;
            }
            writeSheet.setRelativeHeadRowIndex(0);
            excelWriter.write(new ArrayList<>(), writeSheet);
        } finally {
            // 千万别忘记finish 会帮忙关闭流
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }
}
