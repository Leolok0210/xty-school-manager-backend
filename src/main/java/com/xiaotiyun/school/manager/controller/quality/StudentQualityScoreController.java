package com.xiaotiyun.school.manager.controller.quality;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtils;
import com.xiaotiyun.school.manager.model.entity.GradeGroup;
import com.xiaotiyun.school.manager.model.entity.SemesterEntity;
import com.xiaotiyun.school.manager.model.entity.StudentQualityScore;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.model.req.StudentQualityScoreAddReqModel;
import com.xiaotiyun.school.manager.model.req.StudentQualityScoreQueryReqModel;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/studentqualityscore")
@Api(tags = "学生素质评分管理")
public class StudentQualityScoreController extends BasicController {

    @Resource
    private StudentQualityScoreService studentQualityScoreService;

    @Autowired
    private QualityEvaluationService qualityEvaluationService;

    @Resource
    private SystemSettingService systemSettingService;


    @Resource
    private SysClassService sysClassService;

    @Autowired
    private LanguageUtil languageUtil;

    @Autowired
    private GradeRecordSettingService gradeRecordSettingService;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private GradeGroupService gradeGroupService;

//    @PostMapping("/add")
//    @ApiOperation("批量新增学生素质评分")
//    public Result<Void> addStudentQualityScores(HttpServletRequest request, @Valid @RequestBody List<StudentQualityScoreAddReqModel> reqModels) {
//        long schoolId = getSchoolId(request);
//        List<StudentQualityScore> studentQualityScores = reqModels.stream()
//                .map(reqModel -> {
//                    StudentQualityScore studentQualityScore = BeanConvertUtil.convert(reqModel, StudentQualityScore.class);
//                    studentQualityScore.setSchoolId(schoolId);
//                    return studentQualityScore;
//                })
//                .collect(Collectors.toList());
//        studentQualityScoreService.createStudentQualityScores(studentQualityScores);
//        return Result.success();
//    }

//    @PostMapping("/update")
//    @ApiOperation("修改学生素质评分")
//    public Result<Void> updateStudentQualityScore(@Valid @RequestBody StudentQualityScoreAddReqModel reqModel) {
//        StudentQualityScore studentQualityScore = BeanConvertUtil.convert(reqModel, StudentQualityScore.class);
//        studentQualityScoreService.updateStudentQualityScore(studentQualityScore);
//        return Result.success();
//    }

//    @GetMapping("/delete")
//    @ApiOperation("删除学生素质评分")
//    public Result<Void> deleteStudentQualityScore(@RequestParam Long id) {
//        studentQualityScoreService.deleteStudentQualityScore(id);
//        return Result.success();
//    }

//    @GetMapping("/get")
//    @ApiOperation("查看学生素质评分详情")
//    public Result<StudentQualityScoreDetailResModel> getStudentQualityScoreDetail(@RequestParam Long id) {
//        StudentQualityScore studentQualityScore = studentQualityScoreService.getStudentQualityScoreById(id);
//        StudentQualityScoreDetailResModel resModel = BeanConvertUtil.convert(studentQualityScore, StudentQualityScoreDetailResModel.class);
//        return Result.success(resModel);
//    }

    @PostMapping("/list")
    @ApiOperation("查询学生素质评分列表")
    @SaCheckPermission("studentqualityscore:list")
    public Result<PageInfo<StudentQualityScoreListResModel>> getStudentQualityScoreList(@RequestBody StudentQualityScoreQueryReqModel reqModel) {
        reqModel.setUserId(getUserId());
        return Result.success(studentQualityScoreService.getStudentQualityScoreList(reqModel));
    }

    @ApiOperation("素质成绩导入")
    @PostMapping("/import")
    @SaCheckPermission("studentqualityscore:import")
    public Result<Long> importStudentQualityScore(
            @ApiParam("Excel文件") @RequestPart("uploadFile") MultipartFile file,
            @ApiParam("学校id") @RequestParam Long schoolId,
            @ApiParam("学年") @RequestParam String sid,
            @ApiParam("班级id") @RequestParam Long classId,
            @ApiParam("学段") @RequestParam Long term) {
        try {
            SysClass sysClass = sysClassService.getSysClassById(classId);
            if (sysClass != null) {
                //获取成绩录入设定
                GradeRecordSettingResModel settingResModel = gradeRecordSettingService.getSetting(schoolId, sysClass.getSid());
                if (settingResModel != null && settingResModel.getClassSettings() != null) {
                    long count = settingResModel.getClassSettings().stream().filter(classSettingItem -> classSettingItem.getClassId().equals(sysClass.getId()) && classSettingItem.getCanRecordConduct()).count();
                    if (count > 0) {
                        SemesterEntity semester = semesterService.getById(term);
                        String errorMessage = null;
                        for (GradeRecordSettingResModel.TimeSettingItem timeSetting : settingResModel.getTimeSettings()) {
                            if (timeSetting.getDepartment().equals(sysClass.getDepartment()) && timeSetting.getSemesterId().equals(term)) {
                                if (!timeSetting.getStartTime().isBefore(LocalDateTime.now()) || !timeSetting.getEndTime().isAfter(LocalDateTime.now())) {
                                    if (semester != null) {
                                        errorMessage = sysClass.getSid() + semester.getName();
                                    } else {
                                        errorMessage = sysClass.getSid();
                                    }
                                    errorMessage = String.format(languageUtil.getMessage(LanguageConstants.QUALITY_SCORE_INPUT_TIME_RANGE),errorMessage) + DateUtils.formatDateToString(timeSetting.getStartTime(), languageUtil.getMessage(LanguageConstants.YEAR_MONTH_DAY)) + "-" + DateUtils.formatDateToString(timeSetting.getEndTime(), languageUtil.getMessage(LanguageConstants.YEAR_MONTH_DAY));
                                }
                                break;
                            }
                        }
                        if (StringUtils.isNotBlank(errorMessage)) {
                            throw new BusinessMessageException(errorMessage);
                        }
                    }
                }
                Long importId = studentQualityScoreService.importStudentQualityScore(file, schoolId,classId,term,sid);
                return Result.success(importId);
            }
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.FILE_UPLOAD_FAILED.getMessageCode()) + ":" +  e.getMessage());
        }
        return null;
    }

    @ApiOperation("素质成绩导入模板下载")
    @GetMapping("/downloadtemplate")
    @SaCheckPermission("studentqualityscore:import")
    public void downloadImportTemplate(HttpServletRequest request,HttpServletResponse response,@RequestParam Long schoolId,@RequestParam Long classId) throws IOException {
        // 获取学校语言设置信息
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        // 必须先设置响应头再获取输出流
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=quality_score_import_template.xlsx");
        SysClass sysClass = sysClassService.getSysClassById(classId);
        List<QualityIndicatorListResModel> qualityIndicators = qualityEvaluationService.listIndicator(schoolId,sysClass.getDepartment());

        ExcelWriter excelWriter = null;
        // 创建二维表头结构
        List<List<String>> headers = new ArrayList<>(3 + qualityIndicators.size());
        // 添加基础列
        headers.add(Collections.singletonList(LanguageUtils.getSeatNumber(languageEnum)));
        headers.add(Collections.singletonList(LanguageUtils.getChineseName(languageEnum)));
        headers.add(Collections.singletonList(LanguageUtils.getStudentNumber(languageEnum)));
        // 添加动态指标列
        for (QualityIndicatorListResModel indicator : qualityIndicators) {
            headers.add(Collections.singletonList(indicator.getContent()));
        }
        try {
            excelWriter = EasyExcel.write(response.getOutputStream()).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().head(headers).build();
            excelWriter.write(new ArrayList<>(), writeSheet);
        } finally {
            // 千万别忘记finish 会帮忙关闭流
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

}