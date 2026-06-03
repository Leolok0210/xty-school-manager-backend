package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.PatrolRegistrationAddReqModel;
import com.xiaotiyun.school.manager.model.req.PatrolRegistrationQueryReqModel;
import com.xiaotiyun.school.manager.model.req.PatrolRegistrationUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.PatrolRegistrationResModel;
import com.xiaotiyun.school.manager.service.PatrolRegistrationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/rounds")
@Api(tags = "巡堂登记业务控制器")
@RequiredArgsConstructor
public class PatrolRegistrationController extends BasicController {

    private final PatrolRegistrationService patrolRegistrationService;

    @GetMapping("/page")
    @SaCheckPermission("rounds:page")
    @ApiOperation("巡堂登记-分页查询")
    public Result<PageInfo<PatrolRegistrationResModel>> listPatrolRegistrations(@Validated PatrolRegistrationQueryReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        reqModel.setUserId(getUserId());
        return patrolRegistrationService.listPatrolRegistrations(reqModel);
    }

    @PostMapping("/add")
    @ApiOperation("新增巡堂登记")
    @SaCheckPermission("rounds:add")
    public Result<String> addPatrolRegistration(@Validated @RequestBody List<PatrolRegistrationAddReqModel> entity) {
        return patrolRegistrationService.addPatrolRegistration(entity, getSchoolId());
    }

    @PostMapping("/update")
    @SaCheckPermission("rounds:update")
    @ApiOperation("更新巡堂登记")
    public Result<String> updatePatrolRegistration(@RequestBody PatrolRegistrationUpdateReqModel entity) {
        patrolRegistrationService.updatePatrolRegistration(entity);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @SaCheckPermission("rounds:delete")
    @ApiOperation("删除巡堂登记")
    public Result<String> deletePatrolRegistration(@PathVariable Long id) {
        return patrolRegistrationService.deletePatrolRegistration(id);
    }

    @GetMapping("/export")
    @SaCheckPermission("rounds:export")
    @ApiOperation("导出巡堂登记")
    public ResponseEntity<byte[]> exportPatrolRegistrations(PatrolRegistrationQueryReqModel reqModel) throws UnsupportedEncodingException {
        return patrolRegistrationService.exportPatrolRegistrations(reqModel);
    }
}