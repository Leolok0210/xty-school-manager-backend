package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ActProcessTemplateTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.ActProcessTemplateDao;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.ActProcessTemplateSaveReqModel;
import com.xiaotiyun.school.manager.model.res.ActProcessTemplateInfoResModel;
import com.xiaotiyun.school.manager.model.res.ActProcessTemplateListResModel;
import com.xiaotiyun.school.manager.model.res.ActProcessTemplateNodeResModel;
import com.xiaotiyun.school.manager.model.res.ActProcessTemplatePageResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ActProcessTemplateServiceImpl extends ServiceImpl<ActProcessTemplateDao, ActProcessTemplateEntity> implements ActProcessTemplateService {
    private final LanguageUtil languageUtil;
    private final ActProcessDefinitionService actProcessDefinitionService;
    private final ActProcessNodeService actProcessNodeService;
    private final UserSchoolRelService userSchoolRelService;
    private final UserDeptRelService userDeptRelService;

    @Override
    public PageInfo<ActProcessTemplatePageResModel> page(Long schoolId, PageReqModel reqModel) {
        QueryWrapper<ActProcessTemplateEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ActProcessTemplateEntity::getSchoolId, schoolId)
                .orderByDesc(ActProcessTemplateEntity::getCreateTime);
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<ActProcessTemplateEntity> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            PageInfo<ActProcessTemplateEntity> pageInfo = new PageInfo<>(list);
            List<ActProcessTemplatePageResModel> resList = list.stream()
                    .map(entity -> {
                        ActProcessTemplatePageResModel resModel = new ActProcessTemplatePageResModel();
                        BeanUtils.copyProperties(entity, resModel);
                        return resModel;
                    })
                    .collect(Collectors.toList());
            PageInfo<ActProcessTemplatePageResModel> result = new PageInfo<>(resList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            return result;
        }
        return new PageInfo<>(Collections.emptyList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Long schoolId, ActProcessTemplateSaveReqModel reqModel) {
        checkDuplicate(null, schoolId, reqModel);
        ActProcessTemplateEntity entity = BeanConvertUtil.convert(reqModel, ActProcessTemplateEntity.class);
        entity.setSchoolId(schoolId);
        this.save(entity);
        saveProcessDefinitionAndNodes(entity, reqModel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long schoolId, Long id, ActProcessTemplateSaveReqModel reqModel) {
        checkDuplicate(id, schoolId, reqModel);
        ActProcessTemplateEntity entity = this.getById(id);
        if (entity != null) {
            BeanUtils.copyProperties(reqModel, entity);
            this.updateById(entity);
            actProcessDefinitionService.update(Wrappers.<ActProcessDefinitionEntity>lambdaUpdate()
                    .eq(ActProcessDefinitionEntity::getTemplateId, id)
                    .eq(ActProcessDefinitionEntity::getIsActive, true)
                    .set(ActProcessDefinitionEntity::getIsActive, false));
            saveProcessDefinitionAndNodes(entity, reqModel);
        }
    }

    /**
     * 保存流程定义和节点
     */
    private void saveProcessDefinitionAndNodes(ActProcessTemplateEntity templateEntity, ActProcessTemplateSaveReqModel reqModel) {
        if (templateEntity == null) return;
        ActProcessDefinitionEntity definitionEntity = new ActProcessDefinitionEntity();
        definitionEntity.setTemplateId(templateEntity.getId());
        definitionEntity.setRuleSetting(reqModel.getRuleSetting());
        actProcessDefinitionService.save(definitionEntity);
        if (CollectionUtils.isNotEmpty(reqModel.getNodes())) {
            List<ActProcessNodeEntity> nodeList = reqModel.getNodes().stream()
                    .map(node -> {
                        ActProcessNodeEntity nodeEntity = BeanConvertUtil.convert(node, ActProcessNodeEntity.class);
                        nodeEntity.setTemplateId(templateEntity.getId());
                        nodeEntity.setDefinitionId(definitionEntity.getId());
                        return nodeEntity;
                    })
                    .collect(Collectors.toList());
            actProcessNodeService.saveBatch(nodeList);
        }
    }

    /**
     * 校验名称是否重复
     */
    private void checkDuplicate(Long id, Long schoolId, ActProcessTemplateSaveReqModel reqModel) {
        QueryWrapper<ActProcessTemplateEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ActProcessTemplateEntity::getSchoolId, schoolId)
                .eq(ActProcessTemplateEntity::getProcessName, reqModel.getProcessName());
        if (id != null) {
            wrapper.lambda().ne(ActProcessTemplateEntity::getId, id);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACT_PROCESS_TEMPLATE_NAME_EXISTS));
        }
    }

    @Override
    public ActProcessTemplateInfoResModel info(Long id) {
        ActProcessTemplateEntity entity = this.getById(id);
        if (entity == null) {
            return null;
        }
        // 构建返回对象
        ActProcessTemplateInfoResModel resModel = new ActProcessTemplateInfoResModel();
        BeanUtils.copyProperties(entity, resModel);
        // 获取最新的流程定义
        QueryWrapper<ActProcessDefinitionEntity> definitionWrapper = new QueryWrapper<>();
        definitionWrapper.lambda().eq(ActProcessDefinitionEntity::getTemplateId, id)
                .orderByDesc(ActProcessDefinitionEntity::getCreateTime)
                .last("LIMIT 1");
        ActProcessDefinitionEntity definitionEntity = actProcessDefinitionService.getOne(definitionWrapper);
        if (definitionEntity != null) {
            resModel.setRuleSetting(definitionEntity.getRuleSetting());
            // 获取该流程定义下的所有节点
            QueryWrapper<ActProcessNodeEntity> nodeWrapper = new QueryWrapper<>();
            nodeWrapper.lambda().eq(ActProcessNodeEntity::getDefinitionId, definitionEntity.getId());
            List<ActProcessNodeEntity> nodes = actProcessNodeService.list(nodeWrapper);
            if (CollectionUtils.isNotEmpty(nodes)) {
                resModel.setNodes(nodes.stream()
                        .map(node -> {
                            ActProcessTemplateNodeResModel nodeInfo = new ActProcessTemplateNodeResModel();
                            BeanUtils.copyProperties(node, nodeInfo);
                            return nodeInfo;
                        })
                        .collect(Collectors.toList()));
            }
        }
        return resModel;
    }

    @Override
    public void delete(Long id) {
        ActProcessTemplateEntity entity = this.getById(id);
        if (entity != null) {
            this.removeById(id);
        }
    }

    @Override
    public List<ActProcessTemplateListResModel> list(Long schoolId, Long userId, Integer processType) {
        List<ActProcessTemplateListResModel> result = new ArrayList<>();
        // 获取用户信息
        QueryWrapper<UserSchoolRelEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId)
                .eq(UserSchoolRelEntity::getUserId, userId);
        UserSchoolRelEntity userSchoolRel = userSchoolRelService.getOne(wrapper);
        if (userSchoolRel != null) {
            List<Long> roleIds = new ArrayList<>();
            List<Long> deptIds = new ArrayList<>();
            // 获取用户角色
            if (StringUtils.isNotBlank(userSchoolRel.getUserGroupIds())) {
                roleIds.addAll(Stream.of(userSchoolRel.getUserGroupIds().split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList()));
            }
            // 获取用户部门
            QueryWrapper<UserDeptRelEntity> deptWrapper = new QueryWrapper<>();
            deptWrapper.lambda().eq(UserDeptRelEntity::getSchoolId, schoolId)
                    .eq(UserDeptRelEntity::getUserId, userSchoolRel.getId())
                    .eq(UserDeptRelEntity::getIsMaster, 1);
            UserDeptRelEntity userDeptRel = userDeptRelService.getOne(deptWrapper);
            if (userDeptRel != null) {
                deptIds.add(userDeptRel.getDeptId());
            }
            // 构建查询条件
            QueryWrapper<ActProcessTemplateEntity> templateWrapper = new QueryWrapper<>();
            templateWrapper.lambda().eq(ActProcessTemplateEntity::getSchoolId, schoolId)
                    .eq(ActProcessTemplateEntity::getProcessType, processType)
                    .and(w -> w.isNull(ActProcessTemplateEntity::getInitiatorScope) // 适用所有人
                            .or().apply("JSON_CONTAINS(initiator_scope->'$.userIds', CAST({0} AS JSON))", userId) // 指定用户
                            .or(!roleIds.isEmpty(), w2 -> w2.apply("JSON_CONTAINS(initiator_scope->'$.roleIds', JSON_ARRAY({0}))",
                                    roleIds.stream().map(String::valueOf).collect(Collectors.joining(","))) // 指定角色
                            )
                            .or(!deptIds.isEmpty(), w3 -> w3.apply("JSON_CONTAINS(initiator_scope->'$.depIds', JSON_ARRAY({0}))",
                                    deptIds.stream().map(String::valueOf).collect(Collectors.joining(","))) // 指定部门
                            )
                    );
            // 查询模板
            List<ActProcessTemplateEntity> templates = this.list(templateWrapper);
            if (CollectionUtils.isNotEmpty(templates)) {
                // 获取关联的流程定义并按ID倒序排序
                List<Long> templateIds = templates.stream().map(ActProcessTemplateEntity::getId).collect(Collectors.toList());
                QueryWrapper<ActProcessDefinitionEntity> definitionWrapper = new QueryWrapper<>();
                definitionWrapper.lambda().in(ActProcessDefinitionEntity::getTemplateId, templateIds)
                        .eq(ActProcessDefinitionEntity::getIsActive, true)
                        .orderByDesc(ActProcessDefinitionEntity::getId); // 添加排序条件
                List<ActProcessDefinitionEntity> definitions = actProcessDefinitionService.list(definitionWrapper);
                // 按照查询结果顺序(已排序)构建结果
                result = definitions.stream()
                        .map(definition -> {
                            ActProcessTemplateEntity template = templates.stream()
                                    .filter(t -> t.getId().equals(definition.getTemplateId()))
                                    .findFirst()
                                    .orElse(null);
                            if (template != null) {
                                ActProcessTemplateListResModel resModel = new ActProcessTemplateListResModel();
                                BeanUtils.copyProperties(template, resModel);
                                resModel.setTemplateId(template.getId());
                                resModel.setDefinitionId(definition.getId());
                                return resModel;
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initSchoolTemplates(Long schoolId) {
        // 1. 检查学校是否已有模板
        for (ActProcessTemplateTypeEnum value : ActProcessTemplateTypeEnum.values()) {
            if (this.count(Wrappers.<ActProcessTemplateEntity>lambdaQuery()
                    .eq(ActProcessTemplateEntity::getSchoolId, schoolId)
                    .eq(ActProcessTemplateEntity::getProcessType, value.getCode())) > 0) {
                continue; // 学校已有模板，无需初始化
            }
            // 2. 获取系统默认模板(schoolId=0)
            List<ActProcessTemplateEntity> systemTemplates = this.list(
                    Wrappers.<ActProcessTemplateEntity>lambdaQuery()
                            .eq(ActProcessTemplateEntity::getSchoolId, 0)
                            .eq(ActProcessTemplateEntity::getProcessType, value.getCode())
            );
            // 3. 为学校复制系统模板
            if (CollectionUtils.isNotEmpty(systemTemplates)) {
                systemTemplates.forEach(systemTemplate -> {
                    // 创建学校模板
                    ActProcessTemplateEntity schoolTemplate = new ActProcessTemplateEntity();
                    BeanUtils.copyProperties(systemTemplate, schoolTemplate);
                    schoolTemplate.setId(null); // 清除ID让数据库生成新ID
                    schoolTemplate.setSchoolId(schoolId);
                    this.save(schoolTemplate);
                    // 复制流程定义和节点(需要实现相应方法)
                    copyProcessDefinitionAndNodes(systemTemplate.getId(), schoolTemplate.getId());
                });
            }
        }
    }

    /**
     * 复制流程定义和节点
     */
    private void copyProcessDefinitionAndNodes(Long sourceTemplateId, Long targetTemplateId) {
        // 1. 复制最新流程定义
        ActProcessDefinitionEntity latestDefinition = actProcessDefinitionService.getOne(
                Wrappers.<ActProcessDefinitionEntity>lambdaQuery()
                        .eq(ActProcessDefinitionEntity::getTemplateId, sourceTemplateId)
                        .orderByDesc(ActProcessDefinitionEntity::getCreateTime)
                        .last("LIMIT 1")
        );
        if (latestDefinition != null) {
            ActProcessDefinitionEntity newDefinition = new ActProcessDefinitionEntity();
            BeanUtils.copyProperties(latestDefinition, newDefinition);
            newDefinition.setId(null);
            newDefinition.setTemplateId(targetTemplateId);
            actProcessDefinitionService.save(newDefinition);
            // 2. 复制节点
            List<ActProcessNodeEntity> nodes = actProcessNodeService.list(
                    Wrappers.<ActProcessNodeEntity>lambdaQuery()
                            .eq(ActProcessNodeEntity::getDefinitionId, latestDefinition.getId())
            );
            if (CollectionUtils.isNotEmpty(nodes)) {
                List<ActProcessNodeEntity> newNodes = nodes.stream().map(node -> {
                    ActProcessNodeEntity newNode = new ActProcessNodeEntity();
                    BeanUtils.copyProperties(node, newNode);
                    newNode.setId(null);
                    newNode.setDefinitionId(newDefinition.getId());
                    newNode.setTemplateId(targetTemplateId);
                    return newNode;
                }).collect(Collectors.toList());
                actProcessNodeService.saveBatch(newNodes);
            }
        }
    }
}