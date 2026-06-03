package com.xiaotiyun.school.manager.service;

import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;

import java.util.List;

public interface ActTaskService {

    /**
     * 预览审批流程
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    List<ActApprovalInstancePreviewNodeResModel> preview(Long schoolId, Long userId, ActApprovalInstancePreviewReqModel reqModel);

    /**
     * 完成任务（审批操作）
     *
     * @param schoolId
     * @param taskId
     * @param approverId
     * @param approvalResult
     * @param comment
     * @return
     */
    boolean completeTask(Long schoolId, Long taskId, Long approverId, Integer approvalResult, String comment);

    /**
     * 撤回
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    boolean revoke(Long schoolId, Long userId, ActApprovalInstanceRevokeReqModel reqModel);

    /**
     * 我发起的任务列表
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    PageInfo<ActApprovalInstanceInitiatedPageResModel> initiated(Long schoolId, Long userId, ActApprovalInstanceInitiatedReqModel reqModel);

    /**
     * 待审批列表
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    PageInfo<ActApprovalInstancePendingPageResModel> pending(Long schoolId, Long userId, ActApprovalInstancePendingReqModel reqModel);

    /**
     * 已审批列表
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    PageInfo<ActApprovalInstanceApprovedPageResModel> approved(Long schoolId, Long userId, ActApprovalInstancePendingReqModel reqModel);

    /**
     * 审批完成列表
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    PageInfo<ActApprovalInstanceApprovedPageResModel> approvalCompleted(Long schoolId, Long userId, ActApprovalInstancePendingReqModel reqModel);

    /**
     * 详情
     *
     * @param schoolId
     * @param reqModel
     * @return
     */
    ActApprovalInstanceInfoResModel info(Long schoolId, ActApprovalInstanceInfoReqModel reqModel);
}