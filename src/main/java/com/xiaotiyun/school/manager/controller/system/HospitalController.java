package com.xiaotiyun.school.manager.controller.system;

import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.model.req.HospitalAddReqModel;
import com.xiaotiyun.school.manager.model.req.HospitalQueryReqModel;
import com.xiaotiyun.school.manager.model.res.HospitalResModel;
import com.xiaotiyun.school.manager.service.HospitalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckPermission;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/hospital")
@Api(tags = "医院管理")
public class HospitalController extends BasicController {
    @Resource
    private HospitalService hospitalService;

    @PostMapping
    @ApiOperation("新增医院")
    @SaCheckPermission("system:hospital:add")
    public Result<Void> add(HttpServletRequest request, @Valid @RequestBody HospitalAddReqModel reqModel) {
        hospitalService.addHospital(reqModel, getSchoolId(request));
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改医院")
    @SaCheckPermission("system:hospital:edit")
    public Result<Void> update(HttpServletRequest request, @PathVariable Long id, @Valid @RequestBody HospitalAddReqModel reqModel) {
        hospitalService.updateHospital(id, reqModel, getSchoolId(request));
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除医院")
    @SaCheckPermission("system:hospital:del")
    public Result<Void> delete(HttpServletRequest request, @PathVariable Long id) {
        hospitalService.deleteHospital(id, getSchoolId(request));
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("查看医院详情")
    @SaCheckPermission("system:hospital:query")
    public Result<HospitalResModel> getDetail(HttpServletRequest request, @PathVariable Long id) {
        return Result.success(hospitalService.getHospitalDetail(id, getSchoolId(request)));
    }

    @GetMapping
    @ApiOperation("查询医院列表")
    public Result<PageInfo<HospitalResModel>> list(HttpServletRequest request, @Valid HospitalQueryReqModel reqModel) {
        return Result.success(hospitalService.getHospitalList(reqModel, getSchoolId(request)));
    }
} 