package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.ActApprovalInstanceDao;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActApprovalInstanceServiceImpl extends ServiceImpl<ActApprovalInstanceDao, ActApprovalInstanceEntity> implements ActApprovalInstanceService {
    private final ActProcessTemplateService actProcessTemplateService;
    private final ActProcessDefinitionService actProcessDefinitionService;
    private final ActProcessNodeService actProcessNodeService;
    private final UserSchoolRelService userSchoolRelService;
    private final DeptService deptService;
    private final UserDeptRelService userDeptRelService;
    private final UserGroupService userGroupService;
    private final LanguageUtil languageUtil;
    private final ActInstanceNodeService actInstanceNodeService;
    private final ActApprovalTaskService actApprovalTaskService;
    private final ActApprovalHistoryService actApprovalHistoryService;
    private final ActApprovalCcService actApprovalCcService;
    private final SysFileRelevanceService sysFileRelevanceService;
    private final SysFileService sysFileService;

    @Override
    public List<ActApprovalInstancePreviewNodeResModel> preview(Long schoolId, Long userId, ActApprovalInstancePreviewReqModel reqModel) {
        ActProcessTemplateEntity template = actProcessTemplateService.getById(reqModel.getTemplateId());
        if (template == null) {
            return Collections.emptyList();
        }
        ActProcessDefinitionEntity definition = actProcessDefinitionService.getById(reqModel.getDefinitionId());
        if (definition == null) {
            return Collections.emptyList();
        }
        QueryWrapper<ActProcessNodeEntity> nodeWrapper = new QueryWrapper<>();
        nodeWrapper.lambda().eq(ActProcessNodeEntity::getDefinitionId, definition.getId());
        List<ActProcessNodeEntity> nodes = actProcessNodeService.list(nodeWrapper);
        if (CollectionUtils.isEmpty(nodes)) {
            return Collections.emptyList();
        }
        QueryWrapper<UserSchoolRelEntity> userSchoolWrapper = new QueryWrapper<>();
        userSchoolWrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId)
                .eq(UserSchoolRelEntity::getUserId, userId);
        UserSchoolRelEntity userSchoolRel = userSchoolRelService.getOne(userSchoolWrapper);
        Long deptId = null;
        if (userSchoolRel != null) {
            //获取用户所在部门id
            QueryWrapper<UserDeptRelEntity> deptWrapper = new QueryWrapper<>();
            deptWrapper.lambda().eq(UserDeptRelEntity::getSchoolId, schoolId)
                    .eq(UserDeptRelEntity::getUserId, userSchoolRel.getId())
                    .eq(UserDeptRelEntity::getIsMaster, 1);
            UserDeptRelEntity userDeptRel = userDeptRelService.getOne(deptWrapper);
            if (userDeptRel != null) {
                deptId = userDeptRel.getDeptId();
            }
        }
        // 处理每个节点的实际流转逻辑
        return processNodeFlow(schoolId, deptId, userSchoolRel, nodes, reqModel);
    }

    @Override
    @Transactional
    public void startProcess(Long schoolId, Long userId, Long businessId, ActApprovalInstancePreviewReqModel reqModel) {
        //获取用户信息
        QueryWrapper<UserSchoolRelEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId)
                .eq(UserSchoolRelEntity::getUserId, userId);
        UserSchoolRelEntity userSchoolRelEntity = userSchoolRelService.getOne(wrapper);
        if (userSchoolRelEntity == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.USER_NOT_EXISTS));
        }
        // 1. 获取流程定义
        ActProcessTemplateEntity processTemplate = actProcessTemplateService.getById(reqModel.getTemplateId());
        if (processTemplate == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PROCESS_DEFINITION_NOT_EXISTS));
        }
        ActProcessDefinitionEntity processDefinition = actProcessDefinitionService.getById(reqModel.getDefinitionId());
        if (processDefinition == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PROCESS_DEFINITION_NOT_EXISTS));
        }
        // 2. 获取流程节点信息
        List<ActApprovalInstancePreviewNodeResModel> nodeResModelList = preview(schoolId, userId, reqModel);
        if (CollectionUtils.isNotEmpty(nodeResModelList)) {
            // 3. 创建流程实例
            ActApprovalInstanceEntity instance = new ActApprovalInstanceEntity();
            instance.setDefinitionId(processDefinition.getId());
            instance.setBusinessId(businessId);
            instance.setProcessType(processTemplate.getProcessType());
            instance.setTitle(userSchoolRelEntity.getUsername());
            ActProcessTemplateTypeEnum templateType = ActProcessTemplateTypeEnum.getByCode(processTemplate.getProcessType());
            if (templateType != null) {
                switch (templateType) {
                    case TEACHER_LEAVE:
                        instance.setTitle(instance.getTitle() + "的请假申请");
                        break;
                    case TEACHER_BUSINESS:
                        instance.setTitle(instance.getTitle() + "的公务申请");
                        break;
                    case STUDENT_REWARD_PUNISHMENT:
                        instance.setTitle(instance.getTitle() + "的奖惩审批申请");
                        break;
                }
            }
            instance.setStatus(ActApprovalInstanceStatusEnum.RUNNING.getCode());
            instance.setStartUserId(userId);
            instance.setStartUserName(userSchoolRelEntity.getUsername());
            instance.setStartTime(LocalDateTime.now());
            this.save(instance);
            // 4. 创建实例节点快照
            List<ActInstanceNodeEntity> instanceNodes = nodeResModelList.stream().map(node -> {
                ActInstanceNodeEntity instanceNode = new ActInstanceNodeEntity();
                instanceNode.setInstanceId(instance.getId());
                instanceNode.setNodeType(node.getNodeType());
                instanceNode.setNodeCode(node.getNodeCode());
                instanceNode.setNodeName(node.getNodeName());
                List<String> nodeForms = new ArrayList<>();
                if (StringUtils.isNotBlank(node.getNodeFrom())) {
                    nodeForms = Arrays.asList(node.getNodeFrom().split(","));
                }
                instanceNode.setNodeFrom(JSON.toJSONString(nodeForms));
                instanceNode.setApproverType(node.getApproverType());
                List<Long> approverIds = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(node.getApprover())) {
                    approverIds = node.getApprover().stream().map(ActApprovalInstancePreviewNodeApproverResModel::getUserId).filter(Objects::nonNull).collect(Collectors.toList());
                }
                instanceNode.setApproverIds(JSON.toJSONString(approverIds));
                instanceNode.setMultiApproveMode(1);
                return instanceNode;
            }).collect(Collectors.toList());
            actInstanceNodeService.saveBatch(instanceNodes);
            // 5. 创建第一个任务
            ActApprovalInstancePreviewNodeResModel firstNode = nodeResModelList.get(0);
            List<ActInstanceNodeEntity> instanceNodeEntities = instanceNodes.stream().filter(node -> node.getNodeCode().equals(firstNode.getNodeCode())).collect(Collectors.toList());
            ActInstanceNodeEntity nextStep = null;
            if (CollectionUtils.isNotEmpty(instanceNodeEntities)) {
                nextStep = instanceNodeEntities.get(0);
            }
            // 如果找到下一步骤，创建新任务
            if (nextStep != null) {
                // 处理所有连续的抄送节点（不创建任务，直接流转）
                while (nextStep != null && nextStep.getNodeType().equals(ActProcessNodeTypeEnum.COPY.getCode())) {
                    String nodeCode = nextStep.getNodeCode();
                    if (StringUtils.isNotBlank(nextStep.getApproverIds())) {
                        List<Long> ccUserIds = JSON.parseArray(nextStep.getApproverIds(), Long.class);
                        Map<Long, UserSchoolRelEntity> ccUserSchoolRelMap = new HashMap<>();
                        if (CollectionUtils.isNotEmpty(ccUserIds)) {
                            QueryWrapper<UserSchoolRelEntity> userWrapper = new QueryWrapper<>();
                            userWrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId)
                                    .in(UserSchoolRelEntity::getUserId, ccUserIds);
                            List<UserSchoolRelEntity> ccUserSchoolRels = userSchoolRelService.list(userWrapper);
                            if (CollectionUtils.isNotEmpty(ccUserSchoolRels)) {
                                ccUserSchoolRelMap = ccUserSchoolRels.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserId, user -> user));
                            }
                        }
                        List<ActApprovalCcEntity> ccList = new ArrayList<>();
                        for (Long ccUserId : ccUserIds) {
                            ActApprovalCcEntity cc = new ActApprovalCcEntity();
                            cc.setInstanceId(instance.getId());
                            cc.setNodeCode(nodeCode);
                            cc.setCcUserId(ccUserId);
                            UserSchoolRelEntity userSchoolRel = ccUserSchoolRelMap.get(ccUserId);
                            if (userSchoolRel != null) {
                                cc.setCcUserName(userSchoolRel.getUsername());
                            }
                            cc.setCcTime(LocalDateTime.now());
                            ccList.add(cc);
                        }
                        if (CollectionUtils.isNotEmpty(ccList)) {
                            actApprovalCcService.saveBatch(ccList);
                        }
                    }
                    // 流转到下一个节点
                    // 查找抄送节点的后续节点
                    List<ActInstanceNodeEntity> ccNextNodes = instanceNodes.stream()
                            .filter(node -> JSON.parseArray(node.getNodeFrom(), String.class).contains(nodeCode))
                            .collect(Collectors.toList());
                    nextStep = CollectionUtils.isNotEmpty(ccNextNodes) ? ccNextNodes.get(0) : null;
                }

                // 创建普通任务节点
                if (nextStep != null) {
                    ActApprovalTaskEntity newTask = new ActApprovalTaskEntity();
                    newTask.setInstanceId(instance.getId());
                    newTask.setNodeCode(nextStep.getNodeCode());
                    newTask.setNodeName(nextStep.getNodeName());
                    newTask.setApproverIds(nextStep.getApproverIds());
                    actApprovalTaskService.save(newTask);
                    if (nextStep.getApproverType().equals(NodeApproverTypeEnum.AUTO.getCode())) {
                        // 如果下一个节点为自动审批节点或者抄送节点则需要自动审批
                        completeTask(schoolId, newTask.getId(), -1L, ApprovalResultEnum.APPROVED.getCode(), "系统自动审批通过");
                    }
                } else {
                    // 没有下一步骤，流程结束
                    instance.setStatus(ActApprovalInstanceStatusEnum.COMPLETED.getCode());
                    instance.setEndTime(LocalDateTime.now());
                    this.updateById(instance);
                }
            } else {
                // 没有下一步骤，流程结束
                instance.setStatus(ActApprovalInstanceStatusEnum.COMPLETED.getCode());
                instance.setEndTime(LocalDateTime.now());
                this.updateById(instance);
            }
        }
    }

    @Override
    @Transactional
    public boolean completeTask(Long schoolId, Long taskId, Long approverId, Integer approvalResult, String comment) {
        //获取用户
        UserSchoolRelEntity approver = null;
        if (approverId != null && approverId > 0L) {
            QueryWrapper<UserSchoolRelEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId)
                    .eq(UserSchoolRelEntity::getUserId, approverId);
            approver = userSchoolRelService.getOne(wrapper);
            if (approver == null) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.USER_NOT_EXISTS));
            }
        }
        // 1. 获取当前任务
        ActApprovalTaskEntity currentTask = actApprovalTaskService.getById(taskId);
        if (currentTask == null || !ActApprovalTaskStatusEnum.PENDING.getCode().equals(currentTask.getStatus())) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.TASK_NOT_EXISTS_OR_PROCESSED));
        }

        //获取当前节点信息
        QueryWrapper<ActInstanceNodeEntity> nodeWrapper = new QueryWrapper<>();
        nodeWrapper.lambda().eq(ActInstanceNodeEntity::getInstanceId, currentTask.getInstanceId())
                .eq(ActInstanceNodeEntity::getNodeCode, currentTask.getNodeCode());
        ActInstanceNodeEntity instanceNode = actInstanceNodeService.getOne(nodeWrapper);
        if (instanceNode == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.NODE_INFO_GET_FAILED));
        }

        // 2. 处理自动审批节点
        // 如果是自动审批节点，自动设置审批结果
        if (instanceNode.getApproverType().equals(NodeApproverTypeEnum.AUTO.getCode())) {
            approvalResult = ApprovalResultEnum.APPROVED.getCode(); // 自动通过
            comment = "系统自动审批通过";
            approverId = -1L; // 系统用户ID
        } else {
            // 检查审批人是否在当前任务的审批人列表中
            List<Long> currentApproverIds = JSON.parseArray(currentTask.getApproverIds(), Long.class);
            if (approverId != null && approverId > 0L && !currentApproverIds.contains(approverId)) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.USER_NOT_IN_APPROVER_LIST));
            }
        }

        // 3. 更新当前任务状态为已处理
        currentTask.setStatus(ActApprovalTaskStatusEnum.PROCESSED.getCode());
        actApprovalTaskService.updateById(currentTask);

        // 3. 创建审批记录
        ActApprovalHistoryEntity history = new ActApprovalHistoryEntity();
        history.setInstanceId(currentTask.getInstanceId());
        history.setTaskId(currentTask.getId());
        history.setNodeCode(instanceNode.getNodeCode());
        history.setApprovalResult(approvalResult);
        history.setOperateUserId(approverId);
        history.setOperateUserName(approver != null ? approver.getUsername() : "自动通过");
        history.setOperateTime(LocalDateTime.now());
        history.setComment(comment);
        actApprovalHistoryService.save(history);

        // 4. 获取流程实例和流程定义
        ActApprovalInstanceEntity instance = this.getById(currentTask.getInstanceId());

        // 5. 根据审批结果处理流程
        if (approvalResult.equals(ApprovalResultEnum.REJECTED.getCode())) { // 拒绝
            instance.setStatus(ActApprovalInstanceStatusEnum.REJECTED.getCode());
            instance.setEndTime(LocalDateTime.now());
            this.updateById(instance);
            return true;
        }

        // 6. 解析流程步骤，获取下一步骤
        QueryWrapper<ActInstanceNodeEntity> instanceNodeWrapper = new QueryWrapper<>();
        instanceNodeWrapper.lambda().eq(ActInstanceNodeEntity::getInstanceId, instance.getId());
        List<ActInstanceNodeEntity> instanceNodes = actInstanceNodeService.list(instanceNodeWrapper);

        // 查找所有可能的后续节点 - 即nodeFrom包含当前节点code的节点
        List<ActInstanceNodeEntity> possibleNextNodes = instanceNodes.stream()
                .filter(node -> JSON.parseArray(node.getNodeFrom(), String.class).contains(instanceNode.getNodeCode()))
                .collect(Collectors.toList());

        ActInstanceNodeEntity nextStep = null;

        if (CollectionUtils.isNotEmpty(possibleNextNodes)) {
            // 非条件节点，默认取第一个（按优先级排序）
            nextStep = possibleNextNodes.get(0);
        }

        // 如果找到下一步骤，创建新任务
        if (nextStep != null) {
            // 处理所有连续的抄送节点（不创建任务，直接流转）
            while (nextStep != null && nextStep.getNodeType().equals(ActProcessNodeTypeEnum.COPY.getCode())) {
                String nodeCode = nextStep.getNodeCode();
                if (StringUtils.isNotBlank(nextStep.getApproverIds())) {
                    List<Long> ccUserIds = JSON.parseArray(nextStep.getApproverIds(), Long.class);
                    Map<Long, UserSchoolRelEntity> ccUserSchoolRelMap = new HashMap<>();
                    if (CollectionUtils.isNotEmpty(ccUserIds)) {
                        QueryWrapper<UserSchoolRelEntity> wrapper = new QueryWrapper<>();
                        wrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId)
                                .in(UserSchoolRelEntity::getUserId, ccUserIds);
                        List<UserSchoolRelEntity> ccUserSchoolRels = userSchoolRelService.list(wrapper);
                        if (CollectionUtils.isNotEmpty(ccUserSchoolRels)) {
                            ccUserSchoolRelMap = ccUserSchoolRels.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserId, user -> user));
                        }
                    }
                    List<ActApprovalCcEntity> ccList = new ArrayList<>();
                    for (Long ccUserId : ccUserIds) {
                        ActApprovalCcEntity cc = new ActApprovalCcEntity();
                        cc.setInstanceId(instance.getId());
                        cc.setNodeCode(nodeCode);
                        cc.setCcUserId(ccUserId);
                        UserSchoolRelEntity userSchoolRel = ccUserSchoolRelMap.get(ccUserId);
                        if (userSchoolRel != null) {
                            cc.setCcUserName(userSchoolRel.getUsername());
                        }
                        cc.setCcTime(LocalDateTime.now());
                        ccList.add(cc);
                    }
                    if (CollectionUtils.isNotEmpty(ccList)) {
                        actApprovalCcService.saveBatch(ccList);
                    }
                }
                // 流转到下一个节点
                // 查找抄送节点的后续节点
                List<ActInstanceNodeEntity> ccNextNodes = instanceNodes.stream()
                        .filter(node -> JSON.parseArray(node.getNodeFrom(), String.class).contains(nodeCode))
                        .collect(Collectors.toList());
                nextStep = CollectionUtils.isNotEmpty(ccNextNodes) ? ccNextNodes.get(0) : null;
            }

            // 创建普通任务节点
            if (nextStep != null) {
                ActApprovalTaskEntity newTask = new ActApprovalTaskEntity();
                newTask.setInstanceId(instance.getId());
                newTask.setNodeCode(nextStep.getNodeCode());
                newTask.setNodeName(nextStep.getNodeName());
                newTask.setApproverIds(nextStep.getApproverIds());
                actApprovalTaskService.save(newTask);
                if (nextStep.getApproverType().equals(NodeApproverTypeEnum.AUTO.getCode())) {
                    // 如果下一个节点为自动审批节点或者抄送节点则需要自动审批
                    completeTask(schoolId, newTask.getId(), -1L, ApprovalResultEnum.APPROVED.getCode(), "系统自动审批通过");
                } else {
                    List<Long> approverIds = JSON.parseArray(newTask.getApproverIds(), Long.class);
                    if (CollectionUtils.isNotEmpty(approverIds)) {
                        ActProcessDefinitionEntity processDefinition = actProcessDefinitionService.getById(instance.getDefinitionId());
                        Integer ruleSetting = processDefinition.getRuleSetting(); // 1-首个节点审批; 2-每个节点需审批; 3-连续审批自动同意
                        ActApprovalRuleSettingEnum ruleSettingEnum = ActApprovalRuleSettingEnum.getByCode(ruleSetting);
                        switch (ruleSettingEnum) {
                            case FIRST_NODE_ONLY:
                                QueryWrapper<ActApprovalHistoryEntity> historyWrapper = new QueryWrapper<>();
                                historyWrapper.lambda().eq(ActApprovalHistoryEntity::getInstanceId, instance.getId())
                                        .in(ActApprovalHistoryEntity::getOperateUserId, approverIds)
                                        .orderByDesc(ActApprovalHistoryEntity::getOperateTime);
                                List<ActApprovalHistoryEntity> userHistories = actApprovalHistoryService.list(historyWrapper);
                                if (CollectionUtils.isNotEmpty(userHistories)) {
                                    //审批人已审批过，自动调用审批逻辑，审批意见沿用历史审批意见
                                    ActApprovalHistoryEntity lastApprovalHistory = userHistories.get(0);
                                    completeTask(schoolId, newTask.getId(), lastApprovalHistory.getOperateUserId(), lastApprovalHistory.getApprovalResult(), lastApprovalHistory.getComment());
                                }
                                break;
                            case AUTO_APPROVE_CONTINUOUS:
                                if (approverIds.contains(approverId)) {
                                    //下一节点审批人相同时
                                    completeTask(schoolId, newTask.getId(), history.getOperateUserId(), history.getApprovalResult(), history.getComment());
                                }
                                break;
                        }
                    }
                }
            } else {
                // 没有下一步骤，流程结束
                instance.setStatus(ActApprovalInstanceStatusEnum.COMPLETED.getCode());
                instance.setEndTime(LocalDateTime.now());
                this.updateById(instance);
            }
        } else {
            // 没有下一步骤，流程结束
            instance.setStatus(ActApprovalInstanceStatusEnum.COMPLETED.getCode());
            instance.setEndTime(LocalDateTime.now());
            this.updateById(instance);
        }
        return true;
    }

    /**
     * 处理节点流转逻辑（参数改为ActProcessNodeEntity集合）
     */
    private List<ActApprovalInstancePreviewNodeResModel> processNodeFlow(Long schoolId, Long deptId, UserSchoolRelEntity userSchoolRel,
                                                                         List<ActProcessNodeEntity> nodes, ActApprovalInstancePreviewReqModel reqModel) {
        List<ActApprovalInstancePreviewNodeResModel> result = new ArrayList<>();
        Map<String, ActProcessNodeEntity> nodeMap = nodes.stream().collect(Collectors.toMap(ActProcessNodeEntity::getNodeCode, n -> n));
        // 处理起始节点（nodeFrom为空）
        ActProcessNodeEntity startNode = nodes.stream().filter(n -> !StringUtils.isNotBlank(n.getNodeFrom())).collect(Collectors.toList()).get(0);
        String nextNodeCode = startNode.getNodeCode();
        boolean isLoop = true;
        boolean isConditionNode = false;
        String managerNodeCode = "";
        while (isLoop) {
            //获取下一级节点
            String finalNextNodeCode = nextNodeCode;
            List<ActProcessNodeEntity> nextNodes = nodeMap.values().stream()
                    .filter(n -> Arrays.asList(n.getNodeFrom().split(",")).contains(finalNextNodeCode))
                    .sorted(Comparator.comparing(ActProcessNodeEntity::getPriority, Comparator.nullsLast(Integer::compareTo)))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(nextNodes)) {
                if (nextNodes.get(0).getNodeType().equals(ActProcessNodeTypeEnum.GATEWAY.getCode())) {
                    //网关节点不做任何处理，只记录上层节点code
                    nextNodeCode = nextNodes.get(0).getNodeCode();
                    if (!isConditionNode) {
                        //判断上一层是否为条件节点，不是条件节点才记录上层节点code，避免连续条件审批出错
                        managerNodeCode = finalNextNodeCode;
                    }
                } else if (nextNodes.get(0).getNodeType().equals(ActProcessNodeTypeEnum.CONDITION.getCode())) {
                    isConditionNode = true;
                    // 处理条件分支节点,先按优先级排序，然后按条件判断
                    List<ActProcessNodeEntity> selectNodes = nextNodes.stream().sorted(Comparator.comparingInt(ActProcessNodeEntity::getPriority)).collect(Collectors.toList());
                    for (ActProcessNodeEntity selectNode : selectNodes) {
                        if (processConditionNode(deptId, userSchoolRel, selectNode, reqModel)) {
                            //条件匹配
                            nextNodeCode = selectNode.getNodeCode();
                            break;
                        }
                    }
                } else {
                    //其他分支
                    for (ActProcessNodeEntity node : nextNodes) {
                        nextNodeCode = node.getNodeCode();
                        List<ActApprovalInstancePreviewNodeResModel> nodeResModels = new ArrayList<>();
                        ActProcessNodeTypeEnum nodeType = ActProcessNodeTypeEnum.getByCode(node.getNodeType());
                        ApproverNodeProcessResult approverResult = null;
                        switch (nodeType) {
                            case APPROVER: // 审批人节点
                                approverResult = processApproverNode(schoolId, deptId, node, reqModel, isConditionNode, managerNodeCode);
                                break;
                            case COPY: // 抄送人节点
                                approverResult = processCopyNode(schoolId, deptId, node, reqModel, isConditionNode, managerNodeCode);
                                break;
                        }
                        if (approverResult != null) {
                            nodeResModels = approverResult.getNodes();
                            managerNodeCode = approverResult.getUpdatedManagerNodeCode(); // 更新 managerNodeCode
                        }
                        if (CollectionUtils.isNotEmpty(nodeResModels)) {
                            result.addAll(nodeResModels);
                        }
                    }
                }
            } else {
                isLoop = false;
            }
        }
        return result;
    }

    /**
     * 审批节点处理结果类
     */
    private static class ApproverNodeProcessResult {
        private List<ActApprovalInstancePreviewNodeResModel> nodes;
        private String updatedManagerNodeCode;

        public ApproverNodeProcessResult(List<ActApprovalInstancePreviewNodeResModel> nodes, String updatedManagerNodeCode) {
            this.nodes = nodes;
            this.updatedManagerNodeCode = updatedManagerNodeCode;
        }

        // getters and setters
        public List<ActApprovalInstancePreviewNodeResModel> getNodes() {
            return nodes;
        }

        public void setNodes(List<ActApprovalInstancePreviewNodeResModel> nodes) {
            this.nodes = nodes;
        }

        public String getUpdatedManagerNodeCode() {
            return updatedManagerNodeCode;
        }

        public void setUpdatedManagerNodeCode(String updatedManagerNodeCode) {
            this.updatedManagerNodeCode = updatedManagerNodeCode;
        }
    }

    /**
     * 调整后的处理审批人节点（直接接收config字符串）
     */
    private ApproverNodeProcessResult processApproverNode(Long schoolId, Long deptId, ActProcessNodeEntity node,
                                                                             ActApprovalInstancePreviewReqModel reqModel, boolean isConditionNode,
                                                                             String managerNodeCode) {
        List<ActApprovalInstancePreviewNodeResModel> result = new ArrayList<>();
        String updatedManagerNodeCode = managerNodeCode;
        if (isConditionNode) {
            //上级节点为条件节点，处理到审批节点时需要设置节点部位条件节点
            isConditionNode = false;
        }
        if (node.getApproverType().equals(NodeApproverTypeEnum.AUTO.getCode())) {
            //自动审批
            ActApprovalInstancePreviewNodeResModel previewNode = new ActApprovalInstancePreviewNodeResModel();
            previewNode.setNodeCode(node.getNodeCode());
            previewNode.setNodeName(node.getNodeName());
            previewNode.setNodeFrom(updatedManagerNodeCode);
            previewNode.setNodeType(node.getNodeType());
            previewNode.setApproverType(node.getApproverType());
            List<ActApprovalInstancePreviewNodeApproverResModel> approvers = new ArrayList<>();
            ActApprovalInstancePreviewNodeApproverResModel approver = new ActApprovalInstancePreviewNodeApproverResModel();
            approver.setUserName(NodeApproverTypeEnum.AUTO.getDesc());
            approvers.add(approver);
            previewNode.setApprover(approvers);
            result.add(previewNode);
            updatedManagerNodeCode = node.getNodeCode();
        } else {
            //人工审批
            JSONObject config = JSON.parseObject(node.getConfig());
            JSONObject approverConfig = config.getJSONObject("approver");
            List<Long> approverIds = new ArrayList<>();
            switch (approverConfig.getInteger("type")) {
                case 1: // 指定成员
                    approverIds = approverConfig.getJSONArray("userIds").toJavaList(Long.class);
                    ActApprovalInstancePreviewNodeResModel userNode = new ActApprovalInstancePreviewNodeResModel();
                    userNode.setNodeCode(node.getNodeCode());
                    userNode.setNodeName(node.getNodeName());
                    userNode.setNodeFrom(updatedManagerNodeCode);
                    userNode.setNodeType(node.getNodeType());
                    userNode.setApproverType(node.getApproverType());
                    userNode.setApprover(getUserInfo(schoolId, approverIds));
                    result.add(userNode);
                    updatedManagerNodeCode = node.getNodeCode();
                    break;
                case 2: // 用户组
                    // 根据roleIds查询实际用户ID
                    ActApprovalInstancePreviewNodeResModel roleNode = new ActApprovalInstancePreviewNodeResModel();
                    roleNode.setNodeCode(node.getNodeCode());
                    roleNode.setNodeName(node.getNodeName());
                    roleNode.setNodeFrom(updatedManagerNodeCode);
                    roleNode.setNodeType(node.getNodeType());
                    roleNode.setApproverType(node.getApproverType());
                    List<Long> roleIds = approverConfig.getJSONArray("roleIds").toJavaList(Long.class);
                    if (CollectionUtils.isNotEmpty(roleIds)) {
                        List<UserGroupEntity> userGroupEntityList = userGroupService.listByIds(roleIds);
                        if (CollectionUtils.isNotEmpty(userGroupEntityList)) {
                            List<String> roleNames = userGroupEntityList.stream().map(UserGroupEntity::getName).collect(Collectors.toList());
                            roleNode.setNodeName(roleNode.getNodeName() + "(" + String.join(",", roleNames) + ")");
                        }
                        QueryWrapper<UserSchoolRelEntity> wrapper = new QueryWrapper<>();
                        wrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId);
                        List<UserSchoolRelEntity> userRelations = userSchoolRelService.list(wrapper);
                        if (CollectionUtils.isNotEmpty(userRelations)) {
                            approverIds = userRelations.stream()
                                    .filter(userRel -> {
                                        if (StringUtils.isEmpty(userRel.getUserGroupIds())) {
                                            return false;
                                        }
                                        // 将用户的userGroupIds拆分为Long集合
                                        List<Long> userGroupIds = Arrays.stream(userRel.getUserGroupIds().split(","))
                                                .map(Long::valueOf)
                                                .collect(Collectors.toList());
                                        // 检查是否有交集
                                        return !Collections.disjoint(userGroupIds, roleIds);
                                    })
                                    .map(UserSchoolRelEntity::getUserId)
                                    .distinct() // 去重
                                    .collect(Collectors.toList());
                        }
                    }
                    roleNode.setApprover(getUserInfo(schoolId, approverIds));
                    result.add(roleNode);
                    updatedManagerNodeCode = node.getNodeCode();
                    break;
                case 3: // 部门主管
                    // 根据发起人部门查询主管
                    ActApprovalInstancePreviewNodeResModel depNode = new ActApprovalInstancePreviewNodeResModel();
                    depNode.setNodeCode(node.getNodeCode());
                    depNode.setNodeName(node.getNodeName() + "(部门主管)");
                    depNode.setNodeType(node.getNodeType());
                    depNode.setApproverType(node.getApproverType());
                    depNode.setNodeFrom(updatedManagerNodeCode);
                    Long loopDeptId = deptId;
                    boolean isLoop = true;
                    while (isLoop) {
                        // 1. 获取当前部门信息
                        DeptEntity dept = deptService.getById(loopDeptId);
                        if (dept != null) {
                            // 2. 查询当前部门主管
                            approverIds = getDirectDeptLeaders(schoolId, loopDeptId);
                            if (CollectionUtils.isNotEmpty(approverIds)) {
                                isLoop = false;
                            }
                            // 3. 准备查询上级部门
                            if (dept.getParentId() == null || dept.getParentId() == 0) {
                                isLoop = false; // 没有上级部门了
                            }
                            loopDeptId = dept.getParentId();
                        } else {
                            isLoop = false;
                        }
                    }
                    if (!CollectionUtils.isNotEmpty(approverIds)) {
                        // 3. 查询学校管理员（最高级备选）
                        depNode.setNodeName(node.getNodeName() + "(学校管理员)");
                        approverIds = getSchoolAdmins(schoolId);
                    }
                    depNode.setApprover(getUserInfo(schoolId, approverIds));
                    result.add(depNode);
                    updatedManagerNodeCode = node.getNodeCode();
                    break;
                case 4: // 连续多级主管
                    // 根据层级查询多级主管
                    Integer level = approverConfig.getInteger("level");
                    if (deptId != null && level != null && level > 1) {
                        // 预先收集所有需要的部门ID，然后批量查询，避免N+1
                        List<Long> deptIdsToFetch = new ArrayList<>();
                        Long cursorId = deptId;
                        for (int i = 0; i < level && cursorId != null && cursorId != 0; i++) {
                            deptIdsToFetch.add(cursorId);
                            DeptEntity tempDept = deptService.getById(cursorId);
                            if (tempDept == null) break;
                            cursorId = tempDept.getParentId();
                        }
                        Map<Long, DeptEntity> deptMap = deptService.listByIds(deptIdsToFetch)
                                .stream().collect(Collectors.toMap(DeptEntity::getId, d -> d));

                        Long currentDeptId = deptId;
                        // 逐级向上查询主管
                        for (int i = 0; i < level; i++) {
                            // 1. 从批量查询结果中获取当前部门信息
                            DeptEntity dept = deptMap.get(currentDeptId);
                            if (dept == null) {
                                break;
                            }
                            // 2. 查询当前部门主管
                            List<Long> leaders = getDirectDeptLeaders(dept.getSchoolId(), currentDeptId);
                            if (CollectionUtils.isNotEmpty(leaders)) {
                                ActApprovalInstancePreviewNodeResModel depLevelNode = new ActApprovalInstancePreviewNodeResModel();
                                depLevelNode.setNodeCode(node.getNodeCode() + "_" + i);
                                depLevelNode.setNodeType(node.getNodeType());
                                depLevelNode.setApproverType(node.getApproverType());
                                if (i == 0) {
                                    depLevelNode.setNodeFrom(updatedManagerNodeCode);
                                    depLevelNode.setNodeName(node.getNodeName() + "(部门主管)");
                                } else {
                                    depLevelNode.setNodeFrom(node.getNodeCode() + "_" + (i - 1));
                                    depLevelNode.setNodeName(node.getNodeName() + "(第" + (i + 1) + "级部门主管)");
                                }
                                depLevelNode.setApprover(getUserInfo(schoolId, leaders));
                                updatedManagerNodeCode = depLevelNode.getNodeCode();
                                result.add(depLevelNode);
                                approverIds.addAll(leaders);
                            }
                            // 3. 准备查询上级部门
                            if (dept.getParentId() == null || dept.getParentId() == 0) {
                                break; // 没有上级部门了
                            }
                            currentDeptId = dept.getParentId();
                        }
                    }
                    // 如果没有找到任何主管，返回学校管理员
                    if (approverIds.isEmpty()) {
                        ActApprovalInstancePreviewNodeResModel depLevelNode = new ActApprovalInstancePreviewNodeResModel();
                        depLevelNode.setNodeCode(node.getNodeCode());
                        depLevelNode.setNodeName(node.getNodeName() + "(学校管理员)");
                        depLevelNode.setNodeType(node.getNodeType());
                        depLevelNode.setApproverType(node.getApproverType());
                        depLevelNode.setNodeFrom(updatedManagerNodeCode);
                        depLevelNode.setApprover(getUserInfo(schoolId, getSchoolAdmins(schoolId)));
                        result.add(depLevelNode);
                        updatedManagerNodeCode = node.getNodeCode();
                    }
                    break;
                case 5: // 发起人自选
                    ActApprovalInstancePreviewNodeResModel selectNode = new ActApprovalInstancePreviewNodeResModel();
                    selectNode.setNodeCode(node.getNodeCode());
                    selectNode.setNodeName(node.getNodeName());
                    selectNode.setNodeType(node.getNodeType());
                    selectNode.setApproverType(node.getApproverType());
                    selectNode.setNodeFrom(updatedManagerNodeCode);
                    if (CollectionUtils.isNotEmpty(reqModel.getApprover())) {
                        Map<String, ActApprovalInstancePreviewApproverReqModel> approverMap = reqModel.getApprover().stream().collect(Collectors.toMap(ActApprovalInstancePreviewApproverReqModel::getNodeCode, v -> v));
                        ActApprovalInstancePreviewApproverReqModel approverReqModel = approverMap.get(node.getNodeCode());
                        if (approverReqModel != null) {
                            approverIds.addAll(approverReqModel.getApproverIds());
                        }
                    }
                    selectNode.setApprover(getUserInfo(schoolId, approverIds));
                    result.add(selectNode);
                    updatedManagerNodeCode = node.getNodeCode();
                    break;
            }
        }
        return new ApproverNodeProcessResult(result, updatedManagerNodeCode);
    }

    /**
     * 调整后的处理抄送人节点（直接接收config字符串）
     */
    private ApproverNodeProcessResult processCopyNode(Long schoolId, Long deptId, ActProcessNodeEntity node,
                                                                         ActApprovalInstancePreviewReqModel reqModel, boolean isConditionNode,
                                                                         String managerNodeCode) {
        List<ActApprovalInstancePreviewNodeResModel> result = new ArrayList<>();
        String updatedManagerNodeCode = managerNodeCode;
        if (isConditionNode) {
            isConditionNode = false;
        }
        JSONObject config = JSON.parseObject(node.getConfig());
        JSONObject copyConfig = config.getJSONObject("copy");
        List<Long> copyUserIds = new ArrayList<>();
        switch (copyConfig.getInteger("type")) {
            case 1: // 指定成员
                copyUserIds = copyConfig.getJSONArray("userIds").toJavaList(Long.class);
                ActApprovalInstancePreviewNodeResModel userNode = new ActApprovalInstancePreviewNodeResModel();
                userNode.setNodeCode(node.getNodeCode());
                userNode.setNodeName(node.getNodeName());
                userNode.setNodeType(node.getNodeType());
                userNode.setApproverType(node.getApproverType());
                userNode.setNodeFrom(updatedManagerNodeCode);
                userNode.setApprover(getUserInfo(schoolId, copyUserIds));
                result.add(userNode);
                break;
            case 2: // 用户组
                // 根据roleIds查询实际用户ID
                ActApprovalInstancePreviewNodeResModel roleNode = new ActApprovalInstancePreviewNodeResModel();
                roleNode.setNodeCode(node.getNodeCode());
                roleNode.setNodeName(node.getNodeName());
                roleNode.setNodeType(node.getNodeType());
                roleNode.setApproverType(node.getApproverType());
                roleNode.setNodeFrom(updatedManagerNodeCode);
                List<Long> roleIds = copyConfig.getJSONArray("roleIds").toJavaList(Long.class);
                if (CollectionUtils.isNotEmpty(roleIds)) {
                    List<UserGroupEntity> userGroupEntityList = userGroupService.listByIds(roleIds);
                    if (CollectionUtils.isNotEmpty(userGroupEntityList)) {
                        List<String> roleNames = userGroupEntityList.stream().map(UserGroupEntity::getName).collect(Collectors.toList());
                        roleNode.setNodeName(roleNode.getNodeName() + "(" + String.join(",", roleNames) + ")");
                    }
                    QueryWrapper<UserSchoolRelEntity> wrapper = new QueryWrapper<>();
                    wrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId);
                    List<UserSchoolRelEntity> userRelations = userSchoolRelService.list(wrapper);
                    if (CollectionUtils.isNotEmpty(userRelations)) {
                        copyUserIds = userRelations.stream()
                                .filter(userRel -> {
                                    if (StringUtils.isEmpty(userRel.getUserGroupIds())) {
                                        return false;
                                    }
                                    // 将用户的userGroupIds拆分为Long集合
                                    List<Long> userGroupIds = Arrays.stream(userRel.getUserGroupIds().split(","))
                                            .map(Long::valueOf)
                                            .collect(Collectors.toList());
                                    // 检查是否有交集
                                    return !Collections.disjoint(userGroupIds, roleIds);
                                })
                                .map(UserSchoolRelEntity::getUserId)
                                .distinct() // 去重
                                .collect(Collectors.toList());
                    }
                }
                roleNode.setApprover(getUserInfo(schoolId, copyUserIds));
                result.add(roleNode);
                break;
            case 3: // 部门主管
                // 根据发起人部门查询主管
                ActApprovalInstancePreviewNodeResModel depNode = new ActApprovalInstancePreviewNodeResModel();
                depNode.setNodeCode(node.getNodeCode());
                depNode.setNodeType(node.getNodeType());
                depNode.setApproverType(node.getApproverType());
                depNode.setNodeFrom(updatedManagerNodeCode);
                Long loopDeptId = deptId;
                boolean isLoop = true;
                while (isLoop) {
                    // 1. 获取当前部门信息
                    DeptEntity dept = deptService.getById(loopDeptId);
                    if (dept != null) {
                        // 2. 查询当前部门主管
                        copyUserIds = getDirectDeptLeaders(schoolId, loopDeptId);
                        if (CollectionUtils.isNotEmpty(copyUserIds)) {
                            isLoop = false;
                        }
                        // 3. 准备查询上级部门
                        if (dept.getParentId() == null || dept.getParentId() == 0) {
                            isLoop = false; // 没有上级部门了
                        }
                        loopDeptId = dept.getParentId();
                    } else {
                        isLoop = false;
                    }
                }
                if (CollectionUtils.isNotEmpty(copyUserIds)) {
                    depNode.setNodeName(node.getNodeName() + "(部门主管)");
                } else {
                    depNode.setNodeName(node.getNodeName());
                }
                depNode.setApprover(getUserInfo(schoolId, copyUserIds));
                result.add(depNode);
                break;
            case 4: // 发起人自选
                ActApprovalInstancePreviewNodeResModel selectNode = new ActApprovalInstancePreviewNodeResModel();
                selectNode.setNodeCode(node.getNodeCode());
                selectNode.setNodeName(node.getNodeName());
                selectNode.setNodeType(node.getNodeType());
                selectNode.setApproverType(node.getApproverType());
                selectNode.setNodeFrom(updatedManagerNodeCode);
                if (CollectionUtils.isNotEmpty(reqModel.getApprover())) {
                    Map<String, ActApprovalInstancePreviewApproverReqModel> approverMap = reqModel.getApprover().stream().collect(Collectors.toMap(ActApprovalInstancePreviewApproverReqModel::getNodeCode, v -> v));
                    ActApprovalInstancePreviewApproverReqModel approverReqModel = approverMap.get(node.getNodeCode());
                    if (approverReqModel != null) {
                        copyUserIds.addAll(approverReqModel.getCopyIds());
                    }
                }
                selectNode.setApprover(getUserInfo(schoolId, copyUserIds));
                result.add(selectNode);
                break;
        }
        updatedManagerNodeCode = node.getNodeCode();
        return new ApproverNodeProcessResult(result, updatedManagerNodeCode);
    }

    /**
     * 查询学校管理员
     */
    private List<Long> getSchoolAdmins(Long schoolId) {
        QueryWrapper<UserGroupEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UserGroupEntity::getCode, UserGroupTypeEnum.SCHOOL_ADMIN.getCode());
        List<UserGroupEntity> list = userGroupService.list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 查询所有用户-学校关系记录
        QueryWrapper<UserSchoolRelEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId);
        List<UserSchoolRelEntity> userRelations = userSchoolRelService.list(queryWrapper);
        if (CollectionUtils.isEmpty(userRelations)) {
            return Collections.emptyList();
        }
        List<Long> roleIds = list.stream().map(UserGroupEntity::getId).collect(Collectors.toList());
        return userRelations.stream()
                .filter(userRel -> {
                    if (StringUtils.isEmpty(userRel.getUserGroupIds())) {
                        return false;
                    }
                    // 将用户的userGroupIds拆分为Long集合
                    List<Long> userGroupIds = Arrays.stream(userRel.getUserGroupIds().split(","))
                            .map(Long::valueOf)
                            .collect(Collectors.toList());
                    // 检查是否有交集
                    return !Collections.disjoint(userGroupIds, roleIds);
                })
                .map(UserSchoolRelEntity::getUserId)
                .distinct() // 去重
                .collect(Collectors.toList());
    }

    /**
     * 直接查询指定部门的主管
     */
    private List<Long> getDirectDeptLeaders(Long schoolId, Long deptId) {
        if (deptId == null) {
            return Collections.emptyList();
        }
        QueryWrapper<UserDeptRelEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(UserDeptRelEntity::getSchoolId, schoolId)
                .eq(UserDeptRelEntity::getDeptId, deptId)
                .eq(UserDeptRelEntity::getIsAdmin, 1); // 主管
        List<Long> teacherIds = userDeptRelService.list(wrapper).stream()
                .map(UserDeptRelEntity::getUserId)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(teacherIds)) {
            return Collections.emptyList();
        }
        QueryWrapper<UserSchoolRelEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId)
                .in(UserSchoolRelEntity::getId, teacherIds);
        return userSchoolRelService.list(queryWrapper).stream()
                .map(UserSchoolRelEntity::getUserId)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<ActApprovalInstancePreviewNodeApproverResModel> getUserInfo(Long schoolId, List<Long> userIds) {
        List<ActApprovalInstancePreviewNodeApproverResModel> approver = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userIds)) {
            QueryWrapper<UserSchoolRelEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(UserSchoolRelEntity::getUserId, userIds)
                    .eq(UserSchoolRelEntity::getSchoolId, schoolId);
            List<UserSchoolRelEntity> list = userSchoolRelService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(userSchoolRelEntity -> {
                    ActApprovalInstancePreviewNodeApproverResModel approverResModel = new ActApprovalInstancePreviewNodeApproverResModel();
                    approverResModel.setUserId(userSchoolRelEntity.getUserId());
                    approverResModel.setUserName(userSchoolRelEntity.getUsername());
                    approver.add(approverResModel);
                });
            }
        }
        return approver;
    }

    /**
     * 调整后的处理条件分支节点（直接接收config字符串）
     */
    private Boolean processConditionNode(Long deptId, UserSchoolRelEntity userSchoolRel, ActProcessNodeEntity node, ActApprovalInstancePreviewReqModel reqModel) {
        boolean result = true;
        if (StringUtils.isNotBlank(node.getConfig())) {
            JSONObject config = JSON.parseObject(node.getConfig());
            JSONArray conditions = config.getJSONArray("conditions");
            // 查找条件满足的分支
            for (Object condObj : conditions) {
                JSONObject condition = (JSONObject) condObj;
                result = result && checkCondition(deptId, userSchoolRel, condition, reqModel);
            }
        }
        return result;
    }

    /**
     * 检查条件是否满足
     */
    private boolean checkCondition(Long deptId, UserSchoolRelEntity userSchoolRel, JSONObject condition, ActApprovalInstancePreviewReqModel reqModel) {
        ConditionTypeEnum conditionType = ConditionTypeEnum.getByCode(condition.getInteger("type"));
        switch (conditionType) {
            case APPLICANT: // 发起人条件
                return checkApplicantCondition(deptId, userSchoolRel, condition);
            case DURATION: // 时长条件
                return checkDurationCondition(condition, reqModel.getApplyDays());
            case LEAVE_TYPE: // 请假类型
                return checkLeaveTypeCondition(condition, reqModel.getLeaveType());
            default:
                return false;
        }
    }

    /**
     * 检查发起人条件
     */
    private boolean checkApplicantCondition(Long deptId, UserSchoolRelEntity userSchoolRel, JSONObject condition) {
        // 检查用户ID
        if (condition.containsKey("userIds")) {
            List<Long> userIds = condition.getJSONArray("userIds").toJavaList(Long.class);
            if (userIds.contains(userSchoolRel.getUserId())) {
                return true;
            }
        }
        // 检查部门ID
        if (condition.containsKey("depIds")) {
            List<Long> depIds = condition.getJSONArray("depIds").toJavaList(Long.class);
            if (depIds.contains(deptId)) {
                return true;
            }
        }
        // 检查角色ID
        if (condition.containsKey("roleIds")) {
            List<Long> roleIds = condition.getJSONArray("roleIds").toJavaList(Long.class);
            List<Long> userRoleIds = Arrays.stream(userSchoolRel.getUserGroupIds().split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            if (userRoleIds.stream().anyMatch(roleIds::contains)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查时长条件
     */
    private boolean checkDurationCondition(JSONObject condition, Float applyDays) {
        if (applyDays == null) return false;
        JSONObject duration = condition.getJSONArray("leaveDuration").getJSONObject(0);
        Float value1 = duration.getFloatValue("value1");
        ComparisonOperatorEnum operator = ComparisonOperatorEnum.getByCode(duration.getString("operator"));
        switch (operator) {
            case LESS_THAN:
                return applyDays < value1;
            case LESS_EQUAL:
                return applyDays <= value1;
            case EQUAL:
                return applyDays.equals(value1);
            case GREATER_THAN:
                return applyDays > value1;
            case GREATER_EQUAL:
                return applyDays >= value1;
            case BETWEEN:
                float value2 = duration.getFloatValue("value2");
                return applyDays >= value1 && applyDays <= value2;
            default:
                return false;
        }
    }

    /**
     * 检查请假类型条件
     *
     * @param condition     条件配置
     * @param leaveTypeCode 请假类型编码(1-9)
     * @return 是否匹配
     */
    private boolean checkLeaveTypeCondition(JSONObject condition, Integer leaveTypeCode) {
        if (leaveTypeCode == null) {
            return false;
        }
        // 获取请假类型枚举
        LeaveTypeEnum leaveType = LeaveTypeEnum.getByCode(leaveTypeCode);
        if (leaveType == null) {
            return false;
        }
        JSONArray leaveTypeArray = condition.getJSONArray("leaveType");
        if (leaveTypeArray == null) {
            return false;
        }

        List<String> allowedTypes = new ArrayList<>();
        for (int i = 0; i < leaveTypeArray.size(); i++) {
            Object item = leaveTypeArray.get(i);
            String parsedType = null;
            if (item instanceof String) {
                parsedType = parseLeaveType((String) item);
            } else if (item instanceof Number) {
                parsedType = parseLeaveType(String.valueOf(item));
            } else {
                // 处理其他可能的类型
                parsedType = parseLeaveType(item.toString());
            }
            if (parsedType != null) {
                allowedTypes.add(parsedType);
            }
        }
        return allowedTypes.contains(leaveType.name());
    }

    /**
     * 解析请假类型配置值
     */
    private String parseLeaveType(String configValue) {
        // 尝试作为数字编码解析
        try {
            Integer code = Integer.parseInt(configValue);
            LeaveTypeEnum byCode = LeaveTypeEnum.getByCode(code);
            if (byCode != null) {
                return byCode.name();
            }
        } catch (NumberFormatException ignored) {
            // 不是数字编码，继续尝试作为枚举名解析
        }
        // 尝试作为枚举名解析
        LeaveTypeEnum byName = LeaveTypeEnum.getByName(configValue);
        return byName != null ? byName.name() : null;
    }

    @Override
    @Transactional
    public boolean revoke(Long schoolId, Long userId, ActApprovalInstanceRevokeReqModel reqModel) {
        ActApprovalInstanceEntity approvalInstance = this.getById(reqModel.getInstanceId());
        if (approvalInstance != null && approvalInstance.getStatus().equals(ActApprovalInstanceStatusEnum.RUNNING.getCode()) && approvalInstance.getStartUserId().equals(userId)) {
            //改变审批流成状态
            approvalInstance.setStatus(ActApprovalInstanceStatusEnum.REVOKE.getCode());
            this.updateById(approvalInstance);
            QueryWrapper<ActApprovalTaskEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ActApprovalTaskEntity::getInstanceId, reqModel.getInstanceId())
                    .eq(ActApprovalTaskEntity::getStatus, ActApprovalTaskStatusEnum.PENDING.getCode());
            List<ActApprovalTaskEntity> tasks = actApprovalTaskService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(tasks)) {
                //删除任务
                List<Long> taskIds = tasks.stream().map(ActApprovalTaskEntity::getId).collect(Collectors.toList());
                actApprovalTaskService.removeBatchByIds(taskIds);
            }
            return true;
        }
        return false;
    }

    @Override
    public PageInfo<ActApprovalInstanceInitiatedPageResModel> initiated(Long schoolId, Long userId, ActApprovalInstanceInitiatedReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<ActApprovalInstanceInitiatedPageResModel> list = this.getBaseMapper().initiated(schoolId, userId, reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            PageInfo<ActApprovalInstanceInitiatedPageResModel> pageInfo = new PageInfo<>(list);
            List<Long> businessIds = pageInfo.getList().stream()
                    .map(ActApprovalInstanceInitiatedPageResModel::getBusinessId)
                    .collect(Collectors.toList());
            List<Long> instanceIds = pageInfo.getList().stream().map(ActApprovalInstanceInitiatedPageResModel::getInstanceId).collect(Collectors.toList());
            //获取审批历史信息
            QueryWrapper<ActApprovalHistoryEntity> queryWrapperHistory = new QueryWrapper<>();
            queryWrapperHistory.lambda().in(ActApprovalHistoryEntity::getInstanceId, instanceIds)
                    .eq(ActApprovalHistoryEntity::getApprovalResult, ApprovalResultEnum.REJECTED.getCode());
            List<ActApprovalHistoryEntity> historyList = actApprovalHistoryService.list(queryWrapperHistory);
            Map<Long, ActApprovalHistoryEntity> historyMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(historyList)) {
                historyMap = historyList.stream().collect(Collectors.toMap(ActApprovalHistoryEntity::getInstanceId, actApprovalHistoryEntity -> actApprovalHistoryEntity));
            }
            for (ActApprovalInstanceInitiatedPageResModel resModel : pageInfo.getList()) {
                ActApprovalHistoryEntity historyEntity = historyMap.get(resModel.getInstanceId());
                if (historyEntity != null){
                    resModel.setComment(historyEntity.getComment());
                }
            }
            fillAttachmentInfo(
                    pageInfo,
                    businessIds,
                    reqModel.getProcessType(),
                    ActApprovalInstanceInitiatedPageResModel::getBusinessId,
                    ActApprovalInstanceInitiatedPageResModel::setFiles
            );
            return pageInfo;
        }
        return null;
    }

    @Override
    public PageInfo<ActApprovalInstancePendingPageResModel> pending(Long schoolId, Long userId, ActApprovalInstancePendingReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<ActApprovalInstancePendingPageResModel> list = this.getBaseMapper().pending(schoolId, userId, reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            PageInfo<ActApprovalInstancePendingPageResModel> pageInfo = new PageInfo<>(list);
            List<Long> businessIds = pageInfo.getList().stream()
                    .map(ActApprovalInstancePendingPageResModel::getBusinessId)
                    .collect(Collectors.toList());
            fillAttachmentInfo(
                    pageInfo,
                    businessIds,
                    reqModel.getProcessType(),
                    ActApprovalInstancePendingPageResModel::getBusinessId,
                    ActApprovalInstancePendingPageResModel::setFiles
            );
            return pageInfo;
        }
        return null;
    }

    @Override
    public PageInfo<ActApprovalInstanceApprovedPageResModel> approved(Long schoolId, Long userId, ActApprovalInstancePendingReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<ActApprovalInstanceApprovedPageResModel> list = this.getBaseMapper().approved(schoolId, userId, reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            PageInfo<ActApprovalInstanceApprovedPageResModel> pageInfo = new PageInfo<>(list);
            List<Long> businessIds = pageInfo.getList().stream()
                    .map(ActApprovalInstanceApprovedPageResModel::getBusinessId)
                    .collect(Collectors.toList());
            List<Long> instanceIds = pageInfo.getList().stream().map(ActApprovalInstanceApprovedPageResModel::getInstanceId).collect(Collectors.toList());
            //获取审批历史信息
            QueryWrapper<ActApprovalHistoryEntity> queryWrapperHistory = new QueryWrapper<>();
            queryWrapperHistory.lambda().in(ActApprovalHistoryEntity::getInstanceId, instanceIds)
                    .eq(ActApprovalHistoryEntity::getApprovalResult, ApprovalResultEnum.REJECTED.getCode());
            List<ActApprovalHistoryEntity> historyList = actApprovalHistoryService.list(queryWrapperHistory);
            Map<Long, ActApprovalHistoryEntity> historyMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(historyList)) {
                historyMap = historyList.stream().collect(Collectors.toMap(ActApprovalHistoryEntity::getInstanceId, actApprovalHistoryEntity -> actApprovalHistoryEntity));
            }
            for (ActApprovalInstanceInitiatedPageResModel resModel : pageInfo.getList()) {
                ActApprovalHistoryEntity historyEntity = historyMap.get(resModel.getInstanceId());
                if (historyEntity != null){
                    resModel.setComment(historyEntity.getComment());
                }
            }
            fillAttachmentInfo(
                    pageInfo,
                    businessIds,
                    reqModel.getProcessType(),
                    ActApprovalInstanceApprovedPageResModel::getBusinessId,
                    ActApprovalInstanceApprovedPageResModel::setFiles
            );
            return pageInfo;
        }
        return null;
    }

    @Override
    public PageInfo<ActApprovalInstanceApprovedPageResModel> approvalCompleted(Long schoolId, Long userId, ActApprovalInstancePendingReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<ActApprovalInstanceApprovedPageResModel> list = this.getBaseMapper().approvalCompleted(schoolId, userId, reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            PageInfo<ActApprovalInstanceApprovedPageResModel> pageInfo = new PageInfo<>(list);
            List<Long> businessIds = pageInfo.getList().stream()
                    .map(ActApprovalInstanceApprovedPageResModel::getBusinessId)
                    .collect(Collectors.toList());
            fillAttachmentInfo(
                    pageInfo,
                    businessIds,
                    reqModel.getProcessType(),
                    ActApprovalInstanceApprovedPageResModel::getBusinessId,
                    ActApprovalInstanceApprovedPageResModel::setFiles
            );
            return pageInfo;
        }
        return null;
    }


    /**
     * 补全附件信息
     *
     * @param pageInfo    分页结果对象
     * @param businessIds 业务ID集合
     * @param processType 流程类型
     * @param <T>         泛型类型，支持各种分页结果模型
     */
    private <T> void fillAttachmentInfo(PageInfo<T> pageInfo, List<Long> businessIds, Integer processType,
                                        Function<T, Long> businessIdGetter,
                                        BiConsumer<T, List<String>> filesSetter) {
        if (CollectionUtils.isEmpty(businessIds)) {
            return;
        }
        // 获取文件关联类型
        FileRelevanceTypeEnum fileType = getFileTypeByProcessType(processType);
        if (fileType == null) {
            return;
        }
        // 查询文件关联关系
        QueryWrapper<SysFileRelevanceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(SysFileRelevanceEntity::getBusinessId, businessIds)
                .eq(SysFileRelevanceEntity::getType, fileType.getType());
        List<SysFileRelevanceEntity> fileRelevanceList = sysFileRelevanceService.list(queryWrapper);
        if (CollectionUtils.isEmpty(fileRelevanceList)) {
            return;
        }
        // 获取文件信息
        List<Long> fileIds = fileRelevanceList.stream()
                .map(SysFileRelevanceEntity::getFileId)
                .collect(Collectors.toList());
        Map<Long, SysFileEntity> fileMap = sysFileService.listByIds(fileIds).stream()
                .collect(Collectors.toMap(SysFileEntity::getId, Function.identity()));
        // 按业务ID分组关联关系
        Map<Long, List<SysFileRelevanceEntity>> fileRelevanceMap = fileRelevanceList.stream()
                .collect(Collectors.groupingBy(SysFileRelevanceEntity::getBusinessId));
        // 补全附件信息
        for (T model : pageInfo.getList()) {
            Long businessId = businessIdGetter.apply(model);
            if (fileRelevanceMap.containsKey(businessId)) {
                List<String> files = fileRelevanceMap.get(businessId).stream()
                        .map(SysFileRelevanceEntity::getFileId)
                        .filter(fileMap::containsKey)
                        .map(fileId -> fileMap.get(fileId).getPath())
                        .collect(Collectors.toList());
                filesSetter.accept(model, files);
            }
        }
    }

    /**
     * 根据流程类型获取文件关联类型
     */
    private FileRelevanceTypeEnum getFileTypeByProcessType(Integer processType) {
        switch (processType) {
            case 1:
                return FileRelevanceTypeEnum.TEACHER_LEAVE;
            case 2:
                return FileRelevanceTypeEnum.TEACHER_BUSINESS;
            default:
                return null;
        }
    }

}