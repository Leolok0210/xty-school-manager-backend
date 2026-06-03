package com.xiaotiyun.school.manager.controller.grade;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.EnterpriseWxChatTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.SemesterUtils;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.helper.WxHelper;
import com.xiaotiyun.school.manager.model.entity.GradeGroup;
import com.xiaotiyun.school.manager.model.req.GradeGroupAddReqModel;
import com.xiaotiyun.school.manager.model.req.GradeGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.res.GradeGroupDetailResModel;
import com.xiaotiyun.school.manager.service.GradeGroupService;
import com.xiaotiyun.school.manager.service.SysClassService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/gradegroup")
@Api(tags = "级组管理")
public class GradeGroupController extends BasicController {

    @Resource
    private GradeGroupService gradeGroupService;
    @Resource
    private SysClassService sysClassService;
    @Resource
    private UserAuthHelper userAuthHelper;
    @Autowired
    private LanguageUtil languageUtil;


    @Resource
    private WxHelper wxHelper;

    @PostMapping("/add")
    @ApiOperation("批量新增级组")
    @SaCheckPermission("gradegroup:add")
    public Result<Void> addGradeGroups(HttpServletRequest request, @Valid @RequestBody List<GradeGroupAddReqModel> reqModels) {
        long schoolId = getSchoolId(request);
        List<GradeGroup> gradeGroups = reqModels.stream()
                .map(reqModel -> {
                    GradeGroup gradeGroup = BeanConvertUtil.convert(reqModel, GradeGroup.class);
                    gradeGroup.setSchoolId(schoolId);
                    gradeGroup.setCreateTime(LocalDateTime.now());
                    gradeGroup.setUpdateTime(LocalDateTime.now());
                    gradeGroup.setDeleted(0L);
                    return gradeGroup;
                })
                .collect(Collectors.toList());
        gradeGroupService.createGradeGroups(gradeGroups);
        return Result.success();
    }
    
    @PostMapping("/update")
    @ApiOperation("修改级组")
    @SaCheckPermission("gradegroup:update")
    public Result<Void> updateGradeGroup(@Valid @RequestBody GradeGroupAddReqModel reqModel) {
        GradeGroup gradeGroup = BeanConvertUtil.convert(reqModel, GradeGroup.class);
        gradeGroup.setUpdateTime(LocalDateTime.now());
        GradeGroup groupByName = gradeGroupService.getGradeGroupByName(gradeGroup.getGradeGroupName(), gradeGroup.getSchoolId());
        if (groupByName == null) {
            return Result.failed(ResultCode.FAILED.getCode(),languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        if (!groupByName.getId().equals(gradeGroup.getId())) {
           return Result.failed(ResultCode.DATA_GROUP_NAME_EXIST);
        }
        if (sysClassService.checkGradeGroupCanUpdate(groupByName.getId())) {
            // 更新数据
            gradeGroupService.updateById(gradeGroup);
        } else {
            return Result.failed(ResultCode.FAILED.getCode(),languageUtil.getMessage(LanguageConstants.GRADE_GROUP_HAS_CLASS_CANNOT_MODIFY));
        }
        String currentSemesterName = SemesterUtils.getCurrentSemesterName(LocalDate.now());
        wxHelper.crateOrUpdateParents(gradeGroup.getSchoolId(), Lists.newArrayList(gradeGroup.getId()), currentSemesterName, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_LEVEL_GROUP);
        return Result.success();
    }
    
    @GetMapping("/delete")
    @ApiOperation("删除级组")
    @SaCheckPermission("gradegroup:delete")
    public Result<Void> deleteGradeGroup(@RequestParam Long id) {
        if (sysClassService.checkGradeGroupCanUpdate(id)) {
            gradeGroupService.removeById(id);
        } else {
            return Result.failed(ResultCode.FAILED.getCode(),languageUtil.getMessage(LanguageConstants.GRADE_GROUP_HAS_CLASS));
        }
        wxHelper.delete(getSchoolId(), Lists.newArrayList(id), EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_LEVEL_GROUP,
                SemesterUtils.getCurrentSemesterName(LocalDate.now()));
        return Result.success();
    }
    
    @GetMapping("/get")
    @ApiOperation("查看级组详情")
    @SaCheckPermission("gradegroup:get")
    public Result<GradeGroupDetailResModel> getGradeGroupDetail(@RequestParam Long id) {
        GradeGroup gradeGroup = gradeGroupService.getById(id);
        GradeGroupDetailResModel resModel = BeanConvertUtil.convert(gradeGroup, GradeGroupDetailResModel.class);
        return Result.success(resModel);
    }
    
    @PostMapping("/list")
    @ApiOperation("查询级组列表")
    @SaCheckPermission("gradegroup:list")
    public Result<PageInfo<GradeGroupDetailResModel>> getGradeGroupList(@RequestBody GradeGroupQueryReqModel reqModel) {
        return Result.success(gradeGroupService.getGradeGroupList(reqModel));
    }

    @PostMapping("/list/all")
    @ApiOperation("查询全部级组列表")
    public Result<List<GradeGroupDetailResModel>> getGradeGroupList(HttpServletRequest request, @RequestBody GradeGroupQueryReqModel reqModel) {
        Long userId = getUserId();
        Long schoolId = getSchoolId();
        if (userAuthHelper.getCommonUser(userId, schoolId)) {
            List<Long> gradeIds = userAuthHelper.getUserGrades(userId, schoolId);
            if (CollectionUtils.isEmpty(gradeIds)) {
                return null;
            }
            reqModel.setIds(gradeIds);
        }
        List<GradeGroup> gradeGroups = gradeGroupService.getGradeAllGroupList(reqModel);
        List<GradeGroupDetailResModel> resModels = gradeGroups.stream()
                .map(gradeGroup -> BeanConvertUtil.convert(gradeGroup, GradeGroupDetailResModel.class))
                .collect(Collectors.toList());
        return Result.success(resModels);
    }


    @SaIgnore
    @PostMapping("/student/list/all")
    @ApiOperation("查询全部级组列表-学生端(非鉴权)")
    public Result<List<GradeGroupDetailResModel>> getGradeGroupListByStudent(HttpServletRequest request, @RequestBody GradeGroupQueryReqModel reqModel) {
        List<GradeGroup> gradeGroups = gradeGroupService.getGradeAllGroupList(reqModel);
        List<GradeGroupDetailResModel> resModels = gradeGroups.stream()
                .map(gradeGroup -> BeanConvertUtil.convert(gradeGroup, GradeGroupDetailResModel.class))
                .collect(Collectors.toList());
        return Result.success(resModels);
    }
}