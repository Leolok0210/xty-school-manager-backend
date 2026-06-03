package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.StudentParentAddReqModel;
import com.xiaotiyun.school.manager.model.res.StudentParentResModel;
import com.xiaotiyun.school.manager.service.StudentParentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 学生家长信息Controller
 */
@RestController
@RequestMapping("/api/studentParent")
@Api(tags = "学生家长信息管理")
public class StudentParentController extends BasicController {

    @Autowired
    private StudentParentService studentParentService;

    /**
     * 根据ID查询学生家长信息
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据ID查询学生家长信息")
    @SaCheckPermission("studentParent:getById")
    public Result<List<StudentParentResModel>> getById(@PathVariable Long id) {
        return studentParentService.listByStudentId(id, getSchoolId());
    }

    /**
     * 添加学生家长信息
     */
    @PostMapping("/add")
    @ApiOperation("添加学生家长信息")
    @SaCheckPermission("studentParent:add")
    public Result add(@Valid @RequestBody List<StudentParentAddReqModel> reqModels) {
        return studentParentService.addOrUpdate(reqModels, getSchoolId(), null);
    }
}
