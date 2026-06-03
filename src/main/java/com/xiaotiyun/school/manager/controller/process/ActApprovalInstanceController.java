package com.xiaotiyun.school.manager.controller.process;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.ActTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "审批流程管理")
@RequiredArgsConstructor
@RequestMapping("/api/act/approval/instance")
public class ActApprovalInstanceController extends BasicController {
    private final ActTaskService actTaskService;

    @PostMapping("/preview")
    @ApiOperation("审批流程预览")
    @SaCheckPermission("act:approval:instance:preview")
    public Result<List<ActApprovalInstancePreviewNodeResModel>> preview(@Valid @RequestBody ActApprovalInstancePreviewReqModel reqModel) {
        return Result.success(actTaskService.preview(getSchoolId(), getUserId(), reqModel));
    }

    @PostMapping("/complete")
    @ApiOperation("完成任务（审批操作）")
    @SaCheckPermission("act:approval:instance:complete")
    public Result<Boolean> completeTask(@Valid @RequestBody ActApprovalInstanceCompleteReqModel reqModel) {
        return Result.success(actTaskService.completeTask(getSchoolId(), reqModel.getTaskId(), getUserId(), reqModel.getApprovalResult(), reqModel.getComment()));
    }

    @PostMapping("/revoke")
    @ApiOperation("撤回")
    @SaCheckPermission("act:approval:instance:revoke")
    public Result<Boolean> revoke(@Valid @RequestBody ActApprovalInstanceRevokeReqModel reqModel) {
        return Result.success(actTaskService.revoke(getSchoolId(), getUserId(), reqModel));
    }

    @GetMapping("/initiated")
    @ApiOperation("我发起的任务列表")
    @SaCheckPermission("act:approval:instance:initiated")
    public Result<PageInfo<ActApprovalInstanceInitiatedPageResModel>> initiated(@Valid ActApprovalInstanceInitiatedReqModel reqModel) {
        return Result.success(actTaskService.initiated(getSchoolId(), getUserId(), reqModel));
    }

    @GetMapping("/pending")
    @ApiOperation("待审批列表")
    @SaCheckPermission("act:approval:instance:pending")
    public Result<PageInfo<ActApprovalInstancePendingPageResModel>> pending(@Valid ActApprovalInstancePendingReqModel reqModel) {
        return Result.success(actTaskService.pending(getSchoolId(), getUserId(), reqModel));
    }

    @GetMapping("/approved")
    @ApiOperation("已审批列表")
    @SaCheckPermission("act:approval:instance:approved")
    public Result<PageInfo<ActApprovalInstanceApprovedPageResModel>> approved(@Valid ActApprovalInstancePendingReqModel reqModel) {
        return Result.success(actTaskService.approved(getSchoolId(), getUserId(), reqModel));
    }

    @GetMapping("/approval/completed")
    @ApiOperation("审批完成列表")
    @SaCheckPermission("act:approval:instance:approval:completed")
    public Result<PageInfo<ActApprovalInstanceApprovedPageResModel>> approvalCompleted(@Valid ActApprovalInstancePendingReqModel reqModel) {
        return Result.success(actTaskService.approvalCompleted(getSchoolId(), getUserId(), reqModel));
    }

    @GetMapping("/info")
    @ApiOperation("详情")
    @SaCheckPermission("act:approval:instance:info")
    public Result<ActApprovalInstanceInfoResModel> info(@Valid ActApprovalInstanceInfoReqModel reqModel) {
        return Result.success(actTaskService.info(getSchoolId(), reqModel));
    }
}
