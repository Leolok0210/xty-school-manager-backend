package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.ActApprovalInstanceEntity;
import com.xiaotiyun.school.manager.model.req.ActApprovalInstanceInitiatedReqModel;
import com.xiaotiyun.school.manager.model.req.ActApprovalInstancePendingReqModel;
import com.xiaotiyun.school.manager.model.res.ActApprovalInstanceApprovedPageResModel;
import com.xiaotiyun.school.manager.model.res.ActApprovalInstanceInitiatedPageResModel;
import com.xiaotiyun.school.manager.model.res.ActApprovalInstancePendingPageResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActApprovalInstanceDao extends BaseMapper<ActApprovalInstanceEntity> {
    List<ActApprovalInstanceInitiatedPageResModel> initiated(@Param("schoolId") Long schoolId, @Param("userId") Long userId, @Param("reqModel") ActApprovalInstanceInitiatedReqModel reqModel);

    List<ActApprovalInstancePendingPageResModel> pending(@Param("schoolId") Long schoolId, @Param("userId") Long userId, @Param("reqModel") ActApprovalInstancePendingReqModel reqModel);

    List<ActApprovalInstanceApprovedPageResModel> approved(@Param("schoolId") Long schoolId, @Param("userId") Long userId, @Param("reqModel") ActApprovalInstancePendingReqModel reqModel);

    List<ActApprovalInstanceApprovedPageResModel> approvalCompleted(@Param("schoolId") Long schoolId, @Param("userId") Long userId, @Param("reqModel") ActApprovalInstancePendingReqModel reqModel);
}