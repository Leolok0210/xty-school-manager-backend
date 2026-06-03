package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.SemesterUtils;
import com.xiaotiyun.school.manager.helper.WxHelper;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.StudentListResModel;
import com.xiaotiyun.school.manager.model.res.StudentPageResModel;
import com.xiaotiyun.school.manager.model.res.StudentResModel;
import com.xiaotiyun.school.manager.model.res.StudentScorePageResModel;
import com.xiaotiyun.school.manager.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

@Api(tags = "学生管理")
@RestController
@RequestMapping("/api/student")
public class StudentController extends BasicController {
    @Resource
    private StudentService studentService;


    @Autowired
    private LanguageUtil languageUtil;


    @Resource
    private WxHelper wxHelper;

    @ApiOperation("分页查询学生列表")
    @GetMapping("/page")
    @SaCheckPermission("student:page")
    public Result<PageInfo<StudentPageResModel>> page(@ApiParam("查询参数") @Validated StudentPageReqModel reqModel) {
        reqModel.setUserId(getUserId());
        PageInfo<StudentPageResModel> page = studentService.page(reqModel);
        List<StudentPageResModel> list = page.getList();
        if (!CollectionUtils.isEmpty(list))
        {
            for (StudentPageResModel studentPageResModel : list)
            {
                if (studentPageResModel.getNationality() != null && !studentPageResModel.getNationality().isEmpty())
                {
                    String currentLanguage = LanguageUtil.getCurrentLanguage();
                    SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
                    studentPageResModel.setNationality(NationalityEnum.getValue(studentPageResModel.getNationality(), languageEnum));
                }
            }
        }
        return Result.success(page);
    }

    @ApiOperation("新增学生")
    @PostMapping("/add")
    @SaCheckPermission("student:add")
    public Result<Long> save(@ApiParam("学生信息") @Validated @RequestBody StudentSaveReqModel reqModel) {
        Long save = studentService.save(reqModel);
        wxHelper.createOrUpdateStudents(reqModel.getSchoolId(), Lists.newArrayList( save), WechatBusinessTypeEnum.CREATE, reqModel.getSchoolYear());
        return Result.success(save);
    }

    @ApiOperation("修改学生")
    @PutMapping("/update/{id}")
    @SaCheckPermission("student:update")
    public Result<Long> update(
            @ApiParam("学生ID") @PathVariable Long id,
            @ApiParam("学生信息") @Validated @RequestBody StudentSaveReqModel reqModel) {
        studentService.update(id, reqModel);
        wxHelper.createOrUpdateStudents(reqModel.getSchoolId(), Lists.newArrayList( id), WechatBusinessTypeEnum.UPDATE, reqModel.getSchoolYear());
        return Result.success(id);
    }

    @ApiOperation("修改学生座位号")
    @PutMapping("/update")
    @SaCheckPermission("student:updateSeatNo")
    public Result<Void> update(
            @ApiParam("学生信息") @RequestBody List<StudentSaveBatchReqModel> reqModel) {
        reqModel.forEach(item -> {
            StudentSaveReqModel saveReqModel = new StudentSaveReqModel();
            saveReqModel.setSeatNo(item.getSeatNo());
            studentService.update(item.getId(), saveReqModel);
        });
        return Result.success();
    }

    @ApiOperation("获取学生信息")
    @GetMapping("/info/{id}")
    @SaCheckPermission("student:info")
    public Result<StudentResModel> info(
            @ApiParam("学生ID") @PathVariable Long id) {
        return Result.success(studentService.info(id));
    }


    @ApiOperation("获取学生信息-学生端(非鉴权)")//用于企微学生端
    @GetMapping("/wx/info")
    public Result<StudentResModel> wxInfo() {
        StudentEntity nowStudent = (StudentEntity) StpUtil.getSession().get("student");
        if (nowStudent == null) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        return Result.success(studentService.info(nowStudent.getId()));
    }

    @ApiOperation("删除学生")
    @DeleteMapping("/delete/{id}")
    @SaCheckPermission("student:delete")
    public Result<Void> delete(@ApiParam("学生ID") @PathVariable Long id) {
        studentService.delete(id);
        wxHelper.delete(getSchoolId(), Lists.newArrayList( id), EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_STUDENT, SemesterUtils.getCurrentSemesterName(LocalDate.now()));
        return Result.success();
    }

    @ApiOperation("导入学生")
    @PostMapping("/import")
    @SaCheckPermission("student:import")
    public Result<Long> importStudent(
            @ApiParam("Excel文件") @RequestPart("uploadFile") MultipartFile file,
            @ApiParam("学年") @RequestParam String schoolYear,
            @ApiParam("学校id") @RequestParam Long schoolId) {
        try {
            Long importId = studentService.importStudent(schoolYear, schoolId, file);
            return Result.success(importId);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.FILE_UPLOAD_FAILED.getMessageCode()) + ":" + e.getMessage());
        }
    }

    @ApiOperation("导出学生")
    @PostMapping("/export")
    @SaCheckPermission("student:export")
    public Result<String> exportStudent(@Validated @RequestBody StudentPageExportReqModel reqModel) {
        return Result.success(studentService.exportStudent(getUserId(), reqModel));
    }

    @ApiOperation("导出学生表头查询")
    @PostMapping("/export/header")
    @SaCheckPermission("student:export:header")
    public Result<String> exportStudentHeader() {
        return Result.success(studentService.exportStudentHeader(getSchoolId(), getUserId()));
    }

    //根据班级id查询学生
    @ApiOperation("根据班级id查询学生")
    @GetMapping("/listByClassId/{classId}")
    @SaCheckPermission("student:listByClassId")
    public Result<List<StudentListResModel>> listByClassId(@ApiParam("班级id") @PathVariable Long classId) {
        return Result.success(studentService.listByClassId(classId));
    }

    @ApiOperation(value = "学生照片上传(MultipartFile形式)")
    @PostMapping("/uploadImage")
    @SaCheckPermission("student:uploadImage")
    public Result<Void> uploadImage(@RequestPart("uploadFile") MultipartFile file, @RequestParam("schoolId") Long schoolId, @RequestParam("studentId") Long studentId) {
        try {
            studentService.uploadImage(file, schoolId, studentId);
        }catch (BusinessException e){
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(e.getMessage()));
        } catch (Exception e) {
            return Result.failed();
        }
        return Result.success();
    }

    @ApiOperation("批量照片导入")
    @PostMapping("/batchImageUpload")
    @SaCheckPermission("student:batchImageUpload")
    public Result<Long> batchImageUpload(@RequestBody @Validated StudentImageBatchUploadReqModel resModel) {
        return Result.success(studentService.batchImageUpload(resModel));
    }

    @ApiOperation("成绩记录")
    @GetMapping("/score")
    @SaCheckPermission("student:page")
    public Result<List<StudentScorePageResModel>> score(@ApiParam("查询参数") @Validated StudentScoreReqModel resModel) {
        return Result.success(studentService.score(resModel));
    }

    @ApiOperation("下载学生相片")
    @GetMapping("/downloadImage")
    @SaCheckPermission("student:downloadImage")
    public Result<String> downloadImage(@ApiParam("查询参数") @Validated StudentImageDownloadReqModel resModel) {
        return Result.success(studentService.downloadImage(getSchoolId(), resModel));
    }

    @ApiOperation("下载学生相片PDF")
    @GetMapping("/downloadImagePDF")
    @SaCheckPermission("student:downloadImagePDF")
    public Result<String> downloadImage(@Validated StudentImagePDFReqModel resModel) {
        return studentService.getStudentImagePdf(resModel);
    }
}