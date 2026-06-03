package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.handler.ActivityCoursesImportTemplateHeaderHandler;
import com.xiaotiyun.school.manager.handler.UsualRuleImportTemplateHeaderHandler;
import com.xiaotiyun.school.manager.model.entity.StudentUsuallyTypeEntity;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyRuleDepartmentReqModel;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyRuleReqModel;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyRuleImportResModel;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyRuleResModel;
import com.xiaotiyun.school.manager.service.StudentUsuallyRuleService;
import com.xiaotiyun.school.manager.service.StudentUsuallyTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/studentUsuallyRule")
@Api(tags = "平时成绩权重配置管理")
@RequiredArgsConstructor
public class StudentUsuallyRuleController extends BasicController {

    private final StudentUsuallyRuleService studentUsuallyRuleService;

    private final StudentUsuallyTypeService studentUsuallyTypeService;

    private final LanguageUtil languageUtil;

    @Value("${file.templateUrl}")
    private String templateUrl;

    @PutMapping("/update")
    @SaCheckPermission("studentUsuallyRule:update")
    @ApiOperation("更新平时成绩权重配置")
    public Result<String> updateRule(@RequestBody List<StudentUsuallyRuleDepartmentReqModel> reqModels) {
        studentUsuallyRuleService.updateRule(getSchoolId(), reqModels);
        return Result.success();
    }

    @GetMapping("/get")
    @SaCheckPermission("studentUsuallyRule:get")
    @ApiOperation("获取平时成绩权重配置")
    public Result<List<StudentUsuallyRuleResModel>> getRuleById() {
        return Result.success(studentUsuallyRuleService.getRule(getSchoolId()));
    }

    @PostMapping("/list")
    @SaCheckPermission("studentUsuallyRule:list")
    @ApiOperation("获取平时成绩类型列表")
    public Result<List<StudentUsuallyRuleResModel>> list(@Valid @RequestBody StudentUsuallyRuleReqModel reqModel) {
        return Result.success(studentUsuallyRuleService.listByGroupId(getSchoolId(), reqModel));
    }

    @PostMapping("/student/list")
    @ApiOperation("获取平时成绩类型列表-学生端(非鉴权)")
    public Result<List<StudentUsuallyRuleResModel>> listByStudent(@Valid @RequestBody StudentUsuallyRuleReqModel reqModel) {
        return Result.success(studentUsuallyRuleService.listByGroupId(getSchoolId(), reqModel));
    }

    @ApiOperation("平时成绩规则导入")
    @PostMapping("/import")
    @SaCheckPermission("studentUsuallyRule:import")
    public Result<Long> importVolunteer(
            @ApiParam("Excel文件") @RequestPart("uploadFile") MultipartFile file) {
        try {
            Long importId = studentUsuallyRuleService.importRules(file, getSchoolId());
            return Result.success(importId);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.FILE_UPLOAD_FAILED.getMessageCode()) + ":" + e.getMessage());
        }
    }

    @ApiOperation("平时成绩规则导入模板下载")
    @GetMapping("/template/download")
    @SaCheckPermission("studentUsuallyRule:import")
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
            // 获取输出数据
            List<StudentUsuallyRuleImportResModel> ruleList = studentUsuallyRuleService.listImportData(getSchoolId());
            // 获取所有类型
            List<StudentUsuallyTypeEntity> typeList = studentUsuallyTypeService.list(Wrappers.<StudentUsuallyTypeEntity>lambdaQuery()
                    .eq(StudentUsuallyTypeEntity::getSchoolId, getSchoolId()));


            String templatePath;
            // 开始写入excel
            switch (languageEnum) {
                case EN_US:
                    // 拉取本地模板
                    templatePath = templateUrl + "Template for Importing Regular Grades Weighting.xlsx";
                    excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(templatePath).build();
                    response.setHeader("Content-Disposition", "attachment; filename=Template for Importing Regular Grades Weighting.xlsx");
                    writeSheet = EasyExcel.writerSheet(0) // 指定第一页
                            .registerWriteHandler(new UsualRuleImportTemplateHeaderHandler(ruleList, typeList))
                            .build();
                    break;
                case PT_PT:
                    // 拉取本地模板
                    templatePath = templateUrl + "Modelo de Importação de Peso de Notas Finais.xlsx";
                    excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(templatePath).build();
                    response.setHeader("Content-Disposition", "attachment; filename=Modelo de Importação de Peso de Notas Finais.xlsx");
                    writeSheet = EasyExcel.writerSheet(0) // 指定第一页
                            .registerWriteHandler(new UsualRuleImportTemplateHeaderHandler(ruleList, typeList))
                            .build();
                    break;
                default:
                    // 拉取本地模板
                    templatePath = templateUrl + "平時成績權重導入模板.xlsx";
                    excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(templatePath).build();
                    response.setHeader("Content-Disposition", "attachment; filename=平時成績權重導入模板.xlsx");
                    writeSheet = EasyExcel.writerSheet(0) // 指定第一页
                            .registerWriteHandler(new UsualRuleImportTemplateHeaderHandler(ruleList, typeList))
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