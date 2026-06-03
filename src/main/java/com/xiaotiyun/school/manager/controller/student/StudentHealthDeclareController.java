package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.model.entity.StudentHealthDeclareEntity;
import com.xiaotiyun.school.manager.model.req.StudentHealthDeclareAddReqModel;
import com.xiaotiyun.school.manager.model.req.StudentHealthDeclarePageReqModel;
import com.xiaotiyun.school.manager.model.res.StudentHealthDeclareDetailResModel;
import com.xiaotiyun.school.manager.model.res.StudentHealthDeclarePageResModel;
import com.xiaotiyun.school.manager.service.StudentHealthDeclareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 学生健康申报表控制器
 * @author generated
 * @since 2023-09-01
 */
@RestController
@RequestMapping("/api/studentHealthDeclare")
@Api(tags = "学生健康申报管理")
public class StudentHealthDeclareController extends BasicController {

    @Autowired
    private StudentHealthDeclareService studentHealthDeclareService;

    /**
     * 分页查询学生健康申报记录
     */
    @GetMapping("/page")
    @ApiOperation("分页查询学生健康申报记录")
    @SaCheckPermission("studentHealthDeclare:page")
    public Result<PageInfo<StudentHealthDeclarePageResModel>> page(@Valid StudentHealthDeclarePageReqModel pageReqModel) {
        return Result.success(studentHealthDeclareService.page(pageReqModel));
    }

    /**
     * 新增学生健康申报记录
     */
    @PostMapping
    @ApiOperation("新增学生健康申报记录-学生端(非鉴权)")
    public Result save(@Valid @RequestBody StudentHealthDeclareAddReqModel reqModel) {
        return studentHealthDeclareService.addRecord(reqModel);
    }

    /**
     * 新增学生健康申报记录
     */
    @GetMapping("/lastDeclareId/{studentId}")
    @ApiOperation("获取历史最新的申报id-学生端(非鉴权)")
    public Result<Long> getLastDeclareId(@PathVariable Long studentId) {
        List<StudentHealthDeclareEntity> list = studentHealthDeclareService.list(Wrappers.<StudentHealthDeclareEntity>lambdaQuery()
                .eq(StudentHealthDeclareEntity::getSchoolId, getSchoolId())
                .eq(StudentHealthDeclareEntity::getStudentId, studentId)
                .orderByDesc(BaseEntity::getCreateTime));
        if (!CollectionUtils.isEmpty(list)) {
            return Result.success(list.get(0).getId());
        }
        return Result.success(0L);
    }

    /**
     * 查询学生健康申报记录详情
     */
    @GetMapping("/get/{id}")
    @ApiOperation("查询学生健康申报记录详情")
    @SaCheckPermission("studentHealthDeclare:detail")
    public Result<StudentHealthDeclareDetailResModel> detail(@PathVariable Long id) {
        StudentHealthDeclareEntity byId = studentHealthDeclareService.getById(id);
        if (byId == null) {
            return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.PARAM_ERROR);
        }
        StudentHealthDeclareDetailResModel resModel = new StudentHealthDeclareDetailResModel();
        BeanUtil.copyProperties(byId, resModel);
        return Result.success(resModel);
    }

    /**
     * 查询学生健康申报记录详情
     */
    @GetMapping("/student/get/{id}")
    @ApiOperation("查询学生健康申报记录详情-学生端(非鉴权)")
    public Result<StudentHealthDeclareDetailResModel> detailByStudent(@PathVariable Long id) {
        StudentHealthDeclareEntity byId = studentHealthDeclareService.getById(id);
        if (byId == null) {
            return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.PARAM_ERROR);
        }
        StudentHealthDeclareDetailResModel resModel = new StudentHealthDeclareDetailResModel();
        BeanUtil.copyProperties(byId, resModel);
        return Result.success(resModel);
    }
}
