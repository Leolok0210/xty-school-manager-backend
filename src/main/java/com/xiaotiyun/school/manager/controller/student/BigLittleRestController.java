package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.BigLittleRestAddReqModel;
import com.xiaotiyun.school.manager.model.req.BigLittleRestQueryReqModel;
import com.xiaotiyun.school.manager.model.req.BigLittleRestUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.BigLittleRestResModel;
import com.xiaotiyun.school.manager.service.BigLittleRestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/rest")
@Api(tags = "大小息表现业务控制器")
public class BigLittleRestController extends BasicController {

    @Autowired
    private BigLittleRestService bigLittleRestService;

    @GetMapping("/page")
    @SaCheckPermission("bigLittleRest:page")
    @ApiOperation("大小息表现记录-分页查询")
    public Result<PageInfo<BigLittleRestResModel>> listBigLittleRests(@Validated BigLittleRestQueryReqModel reqModel) {
        reqModel.setUserId(getUserId());
        return bigLittleRestService.listBigLittleRests(reqModel);
    }

    @PostMapping("/add")
    @SaCheckPermission("bigLittleRest:add")
    @ApiOperation("新增大小息表现记录")
    public Result<String> addBigLittleRest(@Validated @RequestBody List<BigLittleRestAddReqModel> entity) {
        return bigLittleRestService.addBigLittleRest(entity, getSchoolId());
    }

    @PostMapping("/update")
    @SaCheckPermission("bigLittleRest:update")
    @ApiOperation("更新大小息表现记录")
    public Result<String> updateBigLittleRest(@Validated @RequestBody BigLittleRestUpdateReqModel entity) {
        bigLittleRestService.updateBigLittleRest(entity);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @SaCheckPermission("bigLittleRest:delete")
    @ApiOperation("删除大小息表现记录")
    public Result<String> deleteBigLittleRest(@PathVariable Long id) {
        return bigLittleRestService.deleteBigLittleRest(id);
    }

    @GetMapping("/export")
    @SaCheckPermission("bigLittleRest:export")
    @ApiOperation("导出大小息表现记录")
    public ResponseEntity<byte[]> exportBigLittleRests(@Validated BigLittleRestQueryReqModel reqModel) throws UnsupportedEncodingException {
        return bigLittleRestService.exportBigLittleRests(reqModel);
    }
}