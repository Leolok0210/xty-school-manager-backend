package com.xiaotiyun.school.manager.controller.transcript;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.TranscriptRecordQueryReqModel;
import com.xiaotiyun.school.manager.model.req.TranscriptRecordReqModel;
import com.xiaotiyun.school.manager.model.req.TranscriptRecordUpdateReq;
import com.xiaotiyun.school.manager.model.res.TranscriptRecordResModel;
import com.xiaotiyun.school.manager.service.TranscriptRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transcriptRecord")
@Api(tags = "学年成绩单管理")
@RequiredArgsConstructor
public class TranscriptRecordController extends BasicController {

    private final TranscriptRecordService transcriptRecordService;

    @PostMapping("/add")
    @SaCheckPermission("transcriptRecord:add")
    @ApiOperation("生成学年成绩单")
    public Result<String> create(@Validated @RequestBody TranscriptRecordReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        transcriptRecordService.create(reqModel);
        return Result.success();
    }

    @PutMapping("/update")
    @SaCheckPermission("transcriptRecord:update")
    @ApiOperation("批量更新学年成绩单")
    public Result<String> update(@Validated @RequestBody TranscriptRecordReqModel reqModel) {
        transcriptRecordService.update(getSchoolId(), reqModel);
        return Result.success();
    }

    @GetMapping("/page")
    @SaCheckPermission("transcriptRecord:page")
    @ApiOperation("学年成绩单列表分页")
    public Result<PageInfo<TranscriptRecordResModel>> page(@Validated TranscriptRecordQueryReqModel reqModel) {
        reqModel.setUserId(getUserId());
        return Result.success(transcriptRecordService.page(reqModel));
    }

    @DeleteMapping("/delete/{id}")
    @SaCheckPermission("transcriptRecord:delete")
    @ApiOperation("删除学年成绩单记录")
    public Result<Void> delete(@PathVariable Long id) {
        transcriptRecordService.delete(id);
        return Result.success();
    }

    @SaIgnore
    @PostMapping("/updateById")
    @ApiOperation("更新成绩单状态和压缩包地址")
    public Result<Boolean> updateStatusAndZipUrl(@Validated @RequestBody TranscriptRecordUpdateReq req){
        transcriptRecordService.updateStatusAndZipUrl(req);
        return Result.success(true);
    }
}