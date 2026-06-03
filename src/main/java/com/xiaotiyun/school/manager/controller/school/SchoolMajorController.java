package com.xiaotiyun.school.manager.controller.school;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.model.entity.SchoolMajor;
import com.xiaotiyun.school.manager.model.req.SchoolMajorAddReqModel;
import com.xiaotiyun.school.manager.model.req.SchoolMajorQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SchoolMajorDetailResModel;
import com.xiaotiyun.school.manager.model.res.SubjectDetailResModel;
import com.xiaotiyun.school.manager.model.res.SubjectRelResModel;
import com.xiaotiyun.school.manager.model.res.SubjectSimpleResModel;
import com.xiaotiyun.school.manager.service.SchoolMajorService;
import com.xiaotiyun.school.manager.service.SubjectRelService;
import com.xiaotiyun.school.manager.service.SubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/schoolmajor")
@Api(tags = "专业管理")
public class SchoolMajorController extends BasicController {

    @Resource
    private SchoolMajorService schoolMajorService;

    @Resource
    private SubjectService subjectService;


    @Resource
    private LanguageUtil languageUtil;

    @PostMapping("/add")
    @ApiOperation("批量新增专业")
    @SaCheckPermission("schoolmajor:add")
    public Result<Void> addSchoolMajors(HttpServletRequest request, @Valid @RequestBody List<SchoolMajorAddReqModel> reqModels) {
        long schoolId = getSchoolId(request);
        List<SchoolMajor> departmentAndSchoolId = schoolMajorService.getSchoolMajorByDepartmentAndSchoolId(reqModels.get(0).getDepartmentId(), schoolId);
        Map<String, SchoolMajor> majorMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(departmentAndSchoolId))
        {
            //List转map
            majorMap = departmentAndSchoolId.stream().collect(Collectors.toMap(SchoolMajor::getMajorName, item -> item));
        }
        List<SchoolMajor> schoolMajors = reqModels.stream()
                .map(reqModel -> {
                    SchoolMajor schoolMajor = BeanConvertUtil.convert(reqModel, SchoolMajor.class);
                    schoolMajor.setSchoolId(schoolId);
                    return schoolMajor;
                })
                .collect(Collectors.toList());
        for(SchoolMajor major : schoolMajors)
        {
            // 同一个学部下，专业名称不可重复
            if(majorMap.containsKey(major.getMajorName()))
            {
                return Result.failed(ResultCode.VALIDATE_FAILED.getCode(),languageUtil.getMessage(ResultCode.MAJOR_NAME_DUPLICATE.getMessageCode())+":"+major.getMajorName());
            }
            majorMap.put(major.getMajorName(),major);
        }
        schoolMajorService.createSchoolMajors(schoolMajors);
        return Result.success();
    }

    @PostMapping("/update")
    @ApiOperation("修改专业")
    @SaCheckPermission("schoolmajor:update")
    public Result<Void> updateSchoolMajor(HttpServletRequest request,@Valid @RequestBody SchoolMajorAddReqModel reqModel) {
        SchoolMajor schoolMajor = BeanConvertUtil.convert(reqModel, SchoolMajor.class);
        long schoolId = getSchoolId(request);
        schoolMajor.setSchoolId(schoolId);
        schoolMajorService.updateSchoolMajor(schoolMajor);
        return Result.success();
    }

    @GetMapping("/delete")
    @ApiOperation("删除专业")
    @SaCheckPermission("schoolmajor:delete")
    public Result<Void> deleteSchoolMajor(@RequestParam Long id) {
        schoolMajorService.deleteSchoolMajor(id);
        return Result.success();
    }

    @GetMapping("/get")
    @ApiOperation("查看专业详情")
    // @SaCheckPermission("schoolmajor:get")
    public Result<SchoolMajorDetailResModel> getSchoolMajorDetail(@RequestParam Long id) {
        SchoolMajor schoolMajor = schoolMajorService.getSchoolMajorById(id);
        SchoolMajorDetailResModel resModel = BeanConvertUtil.convert(schoolMajor, SchoolMajorDetailResModel.class);
        resModel.setDepartment(schoolMajor.getDepartmentId());
        if (!StringUtils.isEmpty(schoolMajor.getMajorSubjects()))
        {
            String[] split = schoolMajor.getMajorSubjects().split(",");
            //String转int list
            List<Long> ids = Arrays.stream(split).map(Long::parseLong).collect(Collectors.toList());
            List<SubjectDetailResModel> relResModels = subjectService.getSubjects(ids);
            List<SubjectSimpleResModel> subjectSimpleResModels = new ArrayList<>();
            relResModels.forEach(item -> {
                SubjectSimpleResModel subjectSimpleResModel = new SubjectSimpleResModel();
                subjectSimpleResModel.setId(item.getId());
                subjectSimpleResModel.setName(item.getSubjectName());
                subjectSimpleResModels.add(subjectSimpleResModel);
            });
            resModel.setMajorSubjects(subjectSimpleResModels);
        }
        return Result.success(resModel);
    }

    @PostMapping("/list")
    @ApiOperation("查询专业列表")
    @SaCheckPermission("schoolmajor:list")
    public Result<PageInfo<SchoolMajorDetailResModel>> getSchoolMajorList(HttpServletRequest request,@RequestBody SchoolMajorQueryReqModel reqModel) {
//        reqModel.setSchoolId(getSchoolId(request));
        PageInfo<SchoolMajorDetailResModel> schoolMajorList = schoolMajorService.getSchoolMajorList(reqModel);
        if(!CollectionUtils.isEmpty(schoolMajorList.getList()))
        {
            for (SchoolMajorDetailResModel schoolMajorDetailResModel : schoolMajorList.getList()) {
                SchoolMajor schoolMajor = schoolMajorService.getSchoolMajorById(schoolMajorDetailResModel.getId());
                if (!StringUtils.isEmpty(schoolMajor.getMajorSubjects()))
                {
                    String[] split = schoolMajor.getMajorSubjects().split(",");
                    //String转int list
                    List<Long> ids = Arrays.stream(split).map(Long::parseLong).collect(Collectors.toList());
                    List<SubjectDetailResModel> relResModels = subjectService.getSubjects(ids);
                    List<SubjectSimpleResModel> subjectSimpleResModels = new ArrayList<>();
                    relResModels.forEach(item -> {
                        SubjectSimpleResModel subjectSimpleResModel = new SubjectSimpleResModel();
                        subjectSimpleResModel.setId(item.getId());
                        subjectSimpleResModel.setName(item.getSubjectName());
                        subjectSimpleResModels.add(subjectSimpleResModel);
                    });
                    schoolMajorDetailResModel.setMajorSubjects(subjectSimpleResModels);
                }
            }
        }
        return Result.success(schoolMajorList);
    }
}