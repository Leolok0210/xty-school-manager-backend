package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.DressCodeViolationAddReqModel;
import com.xiaotiyun.school.manager.model.req.DressCodeViolationQueryReqModel;
import com.xiaotiyun.school.manager.model.req.DressCodeViolationUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.DressCodeViolationResModel;
import com.xiaotiyun.school.manager.service.DressCodeViolationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/appearance")
@Api(tags = "仪表不符业务控制器")
@RequiredArgsConstructor
public class DressCodeViolationController extends BasicController {

    private final DressCodeViolationService dressCodeViolationService;

    @GetMapping("/page")
    @SaCheckPermission("appearance:page")
    @ApiOperation("仪表不符记录-分页查询")
    public Result<PageInfo<DressCodeViolationResModel>> listDressCodeViolations(@Validated DressCodeViolationQueryReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        reqModel.setUserId(getUserId());
        return dressCodeViolationService.listDressCodeViolations(reqModel);
    }

    @PostMapping("/add")
    @SaCheckPermission("appearance:add")
    @ApiOperation("新增仪表不符记录")
    public Result<String> addDressCodeViolation(@Validated @RequestBody List<DressCodeViolationAddReqModel> entity) {
        return dressCodeViolationService.addDressCodeViolation(entity,getSchoolId());
    }

    @PostMapping("/update")
    @SaCheckPermission("appearance:update")
    @ApiOperation("更新仪表不符记录")
    public Result<String> updateDressCodeViolation(@Validated @RequestBody DressCodeViolationUpdateReqModel entity) {
        dressCodeViolationService.updateDressCodeViolation(entity);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @SaCheckPermission("appearance:delete")
    @ApiOperation("删除仪表不符记录")
    public Result<String> deleteDressCodeViolation(@PathVariable Long id) {
        return dressCodeViolationService.deleteDressCodeViolation(id);
    }

    @GetMapping("/export")
    @SaCheckPermission("appearance:export")
    @ApiOperation("导出仪表不符记录")
    public ResponseEntity<byte[]> exportDressCodeViolations(@Validated DressCodeViolationQueryReqModel reqModel) throws UnsupportedEncodingException {
        return dressCodeViolationService.exportDressCodeViolations(reqModel);
    }
}