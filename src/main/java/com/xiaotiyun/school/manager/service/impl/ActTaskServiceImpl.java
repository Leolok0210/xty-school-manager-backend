package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.enums.ActProcessNodeTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.FileRelevanceTypeEnum;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActTaskServiceImpl implements ActTaskService {
    private final ActApprovalInstanceService actApprovalInstanceService;
    private final TeacherLeaveService teacherLeaveService;
    private final TeacherBusinessService teacherBusinessService;
    private final SysFileRelevanceService sysFileRelevanceService;
    private final SysFileService sysFileService;
    private final UserDeptRelService userDeptRelService;
    private final UserSchoolRelService userSchoolRelService;
    private final DeptService deptService;
    private final ActInstanceNodeService actInstanceNodeService;
    private final ActApprovalCcService actApprovalCcService;
    private final ActApprovalHistoryService actApprovalHistoryService;

    @Override
    public List<ActApprovalInstancePreviewNodeResModel> preview(Long schoolId, Long userId, ActApprovalInstancePreviewReqModel reqModel) {
        return actApprovalInstanceService.preview(schoolId, userId, reqModel);
    }

    @Override
    public boolean completeTask(Long schoolId, Long taskId, Long approverId, Integer approvalResult, String comment) {
        return actApprovalInstanceService.completeTask(schoolId, taskId, approverId, approvalResult, comment);
    }

    @Override
    public boolean revoke(Long schoolId, Long userId, ActApprovalInstanceRevokeReqModel reqModel) {
        return actApprovalInstanceService.revoke(schoolId, userId, reqModel);
    }

    @Override
    public PageInfo<ActApprovalInstanceInitiatedPageResModel> initiated(Long schoolId, Long userId, ActApprovalInstanceInitiatedReqModel reqModel) {
        return actApprovalInstanceService.initiated(schoolId, userId, reqModel);
    }

    @Override
    public PageInfo<ActApprovalInstancePendingPageResModel> pending(Long schoolId, Long userId, ActApprovalInstancePendingReqModel reqModel) {
        return actApprovalInstanceService.pending(schoolId, userId, reqModel);
    }

    @Override
    public PageInfo<ActApprovalInstanceApprovedPageResModel> approved(Long schoolId, Long userId, ActApprovalInstancePendingReqModel reqModel) {
        return actApprovalInstanceService.approved(schoolId, userId, reqModel);
    }

    @Override
    public PageInfo<ActApprovalInstanceApprovedPageResModel> approvalCompleted(Long schoolId, Long userId, ActApprovalInstancePendingReqModel reqModel) {
        return actApprovalInstanceService.approvalCompleted(schoolId, userId, reqModel);
    }

    @Override
    public ActApprovalInstanceInfoResModel info(Long schoolId, ActApprovalInstanceInfoReqModel reqModel) {
        ActApprovalInstanceInfoResModel resModel = new ActApprovalInstanceInfoResModel();
        FileRelevanceTypeEnum fileRelevanceTypeEnum = null;
        Long teacherId = null;
        switch (reqModel.getProcessType()) {
            case 1:
                fileRelevanceTypeEnum = FileRelevanceTypeEnum.TEACHER_LEAVE;
                //教师请假
                TeacherLeaveEntity teacherLeave = teacherLeaveService.getById(reqModel.getBusinessId());
                if (teacherLeave != null) {
                    resModel.setStartTime(teacherLeave.getStartTime());
                    resModel.setEndTime(teacherLeave.getEndTime());
                    resModel.setReason(teacherLeave.getReason());
                    resModel.setLeaveType(teacherLeave.getLeaveType());
                    teacherId = teacherLeave.getTeacherId();
                }
                break;
            case 2:
                //教师公务
                fileRelevanceTypeEnum = FileRelevanceTypeEnum.TEACHER_BUSINESS;
                TeacherBusinessEntity teacherBusiness = teacherBusinessService.getById(reqModel.getBusinessId());
                if (teacherBusiness != null) {
                    resModel.setStartTime(teacherBusiness.getStartTime());
                    resModel.setEndTime(teacherBusiness.getEndTime());
                    resModel.setReason(teacherBusiness.getReason());
                    teacherId = teacherBusiness.getTeacherId();
                }
                break;
        }
        if (teacherId != null) {
            QueryWrapper<UserSchoolRelEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(UserSchoolRelEntity::getUserId, teacherId)
                    .eq(UserSchoolRelEntity::getSchoolId, schoolId);
            UserSchoolRelEntity userSchoolRelEntity = userSchoolRelService.getOne(queryWrapper);
            if (userSchoolRelEntity != null) {
                resModel.setUserName(userSchoolRelEntity.getUsername());
                DeptEntity deptInfo = getDeptInfo(userSchoolRelEntity.getId(), schoolId);
                if (deptInfo != null) {
                    resModel.setDepName(deptInfo.getName());
                }
            }
        }
        if (fileRelevanceTypeEnum != null) {
            // 查询文件关联关系
            QueryWrapper<SysFileRelevanceEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SysFileRelevanceEntity::getBusinessId, reqModel.getBusinessId())
                    .eq(SysFileRelevanceEntity::getType, fileRelevanceTypeEnum.getType());
            List<SysFileRelevanceEntity> fileRelevanceList = sysFileRelevanceService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(fileRelevanceList)) {
                // 获取文件信息
                List<Long> fileIds = fileRelevanceList.stream().map(SysFileRelevanceEntity::getFileId).collect(Collectors.toList());
                List<SysFileEntity> fileList = sysFileService.listByIds(fileIds);
                if (CollectionUtils.isNotEmpty(fileList)) {
                    resModel.setFiles(fileList.stream().map(SysFileEntity::getPath).collect(Collectors.toList()));
                }
            }
        }
        //查询流程信息
        ActApprovalInstanceEntity approvalInstance = actApprovalInstanceService.getById(reqModel.getInstanceId());
        if (approvalInstance != null) {
            resModel.setStatus(approvalInstance.getStatus());
            resModel.setInitiatedStartTime(approvalInstance.getStartTime());
            //获取审批节点信息
            QueryWrapper<ActInstanceNodeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ActInstanceNodeEntity::getInstanceId, reqModel.getInstanceId());
            List<ActInstanceNodeEntity> nodeList = actInstanceNodeService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(nodeList)) {
                List<Long> userIds = nodeList.stream()
                        .flatMap(node -> JSON.parseArray(node.getApproverIds(), Long.class).stream())
                        .collect(Collectors.toList());
                Map<Long, UserSchoolRelEntity> userSchoolRelMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(userIds)) {
                    QueryWrapper<UserSchoolRelEntity> queryWrapperUserSchoolRel = new QueryWrapper<>();
                    queryWrapperUserSchoolRel.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId)
                            .in(UserSchoolRelEntity::getUserId, userIds);
                    List<UserSchoolRelEntity> list = userSchoolRelService.list(queryWrapperUserSchoolRel);
                    if (CollectionUtils.isNotEmpty(list)) {
                        userSchoolRelMap = list.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserId, userSchoolRelEntity -> userSchoolRelEntity));
                    }
                }
                List<ActApprovalInstanceInfoNodeResModel> nodes = new ArrayList<>();
                //获取审批历史信息
                QueryWrapper<ActApprovalHistoryEntity> queryWrapperHistory = new QueryWrapper<>();
                queryWrapperHistory.lambda().eq(ActApprovalHistoryEntity::getInstanceId, reqModel.getInstanceId());
                List<ActApprovalHistoryEntity> historyList = actApprovalHistoryService.list(queryWrapperHistory);
                Map<String, List<ActApprovalHistoryEntity>> historyMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(historyList)) {
                    historyMap = historyList.stream().collect(Collectors.groupingBy(ActApprovalHistoryEntity::getNodeCode));
                }
                //获取抄送信息
                QueryWrapper<ActApprovalCcEntity> queryWrapperCc = new QueryWrapper<>();
                queryWrapperCc.lambda().eq(ActApprovalCcEntity::getInstanceId, reqModel.getInstanceId());
                List<ActApprovalCcEntity> ccList = actApprovalCcService.list(queryWrapperCc);
                Map<String, List<ActApprovalCcEntity>> ccMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(ccList)) {
                    ccMap = ccList.stream().collect(Collectors.groupingBy(ActApprovalCcEntity::getNodeCode));
                }
                for (ActInstanceNodeEntity actInstanceNodeEntity : nodeList) {
                    ActApprovalInstanceInfoNodeResModel node = new ActApprovalInstanceInfoNodeResModel();
                    node.setNodeType(actInstanceNodeEntity.getNodeType());
                    node.setNodeCode(actInstanceNodeEntity.getNodeCode());
                    node.setNodeName(actInstanceNodeEntity.getNodeName());
                    if (StringUtils.isNotBlank(actInstanceNodeEntity.getNodeFrom())) {
                        List<String> nodeFroms = JSON.parseArray(actInstanceNodeEntity.getNodeFrom(), String.class);
                        if (CollectionUtils.isNotEmpty(nodeFroms)) {
                            node.setNodeFrom(String.join(",", nodeFroms));
                        }
                    }
                    List<ActApprovalInstanceInfoNodeApproverResModel> approver = new ArrayList<>();
                    ActProcessNodeTypeEnum nodeType = ActProcessNodeTypeEnum.getByCode(actInstanceNodeEntity.getNodeType());
                    switch (nodeType) {
                        case APPROVER: // 审批人节点
                            List<Long> approverIds = JSON.parseArray(actInstanceNodeEntity.getApproverIds(), Long.class);
                            List<ActApprovalHistoryEntity> historyEntityList = historyMap.get(actInstanceNodeEntity.getNodeCode());
                            if (CollectionUtils.isNotEmpty(approverIds)) {
                                Map<Long, ActApprovalHistoryEntity> historyEntityMap = new HashMap<>();
                                if (CollectionUtils.isNotEmpty(historyEntityList)) {
                                    historyEntityMap = historyEntityList.stream().collect(Collectors.toMap(ActApprovalHistoryEntity::getOperateUserId, actApprovalHistoryEntity -> actApprovalHistoryEntity));
                                }
                                //有审批人
                                for (Long approverId : approverIds) {
                                    ActApprovalInstanceInfoNodeApproverResModel approverResModel = new ActApprovalInstanceInfoNodeApproverResModel();
                                    UserSchoolRelEntity userSchoolRel = userSchoolRelMap.get(approverId);
                                    if (userSchoolRel != null) {
                                        approverResModel.setUserName(userSchoolRel.getUsername());
                                    }
                                    ActApprovalHistoryEntity actApprovalHistoryEntity = historyEntityMap.get(approverId);
                                    if (actApprovalHistoryEntity != null) {
                                        approverResModel.setApprovalResult(actApprovalHistoryEntity.getApprovalResult());
                                        approverResModel.setApprovalTime(actApprovalHistoryEntity.getOperateTime());
                                        approverResModel.setComment(actApprovalHistoryEntity.getComment());
                                    }
                                    approver.add(approverResModel);
                                }
                            } else {
                                //无审批人，自动审批
                                for (ActApprovalHistoryEntity actApprovalHistoryEntity : historyEntityList) {
                                    ActApprovalInstanceInfoNodeApproverResModel approverResModel = new ActApprovalInstanceInfoNodeApproverResModel();
                                    approverResModel.setUserName(actApprovalHistoryEntity.getOperateUserName());
                                    approverResModel.setApprovalResult(actApprovalHistoryEntity.getApprovalResult());
                                    approverResModel.setApprovalTime(actApprovalHistoryEntity.getOperateTime());
                                    approverResModel.setComment(actApprovalHistoryEntity.getComment());
                                    approver.add(approverResModel);
                                }
                            }
                            break;
                        case COPY: // 抄送人节点
                            List<Long> copyIds = JSON.parseArray(actInstanceNodeEntity.getApproverIds(), Long.class);
                            List<ActApprovalCcEntity> ccEntityList = ccMap.get(actInstanceNodeEntity.getNodeCode());
                            if (CollectionUtils.isNotEmpty(copyIds)) {
                                Map<Long, ActApprovalCcEntity> ccEntityMap = new HashMap<>();
                                if (CollectionUtils.isNotEmpty(ccEntityList)) {
                                    ccEntityMap = ccEntityList.stream().collect(Collectors.toMap(ActApprovalCcEntity::getCcUserId, actApprovalCcEntity -> actApprovalCcEntity));
                                }
                                //有审批人
                                for (Long copyId : copyIds) {
                                    ActApprovalInstanceInfoNodeApproverResModel approverResModel = new ActApprovalInstanceInfoNodeApproverResModel();
                                    UserSchoolRelEntity userSchoolRel = userSchoolRelMap.get(copyId);
                                    if (userSchoolRel != null) {
                                        approverResModel.setUserName(userSchoolRel.getUsername());
                                    }
                                    ActApprovalCcEntity actApprovalCcEntity = ccEntityMap.get(copyId);
                                    if (actApprovalCcEntity != null) {
                                        approverResModel.setApprovalTime(actApprovalCcEntity.getCcTime());
                                    }
                                    approver.add(approverResModel);
                                }
                            }
                            break;
                    }
                    node.setApprover(approver);
                    nodes.add(node);
                }
                resModel.setNodes(nodes);
            }
        }
        return resModel;
    }

    private DeptEntity getDeptInfo(Long userId, Long schoolId) {
        if (userId != null && schoolId != null) {
            QueryWrapper<UserDeptRelEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(UserDeptRelEntity::getSchoolId, schoolId)
                    .eq(UserDeptRelEntity::getUserId, userId)
                    .eq(UserDeptRelEntity::getIsMaster, 1);
            UserDeptRelEntity userDeptRel = userDeptRelService.getOne(queryWrapper);
            if (userDeptRel != null) {
                return deptService.getById(userDeptRel.getDeptId());
            }
        }
        return null;
    }
}