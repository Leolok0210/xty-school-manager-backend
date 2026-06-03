package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.ExternalCompetitionMapper;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.ExternalCompetitionDataDTO;
import com.xiaotiyun.school.manager.model.excel.ExternalCompetitionExportDTO;
import com.xiaotiyun.school.manager.model.excel.ExternalCompetitionExportEnDTO;
import com.xiaotiyun.school.manager.model.excel.ExternalCompetitionExportPtDTO;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionCreateReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionGroupCreateReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionQueryReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionRecordReqModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionGroupResModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionPageResModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionRecordResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExternalCompetitionServiceImpl extends ServiceImpl<ExternalCompetitionMapper, ExternalCompetitionEntity> implements ExternalCompetitionService {
    private final ExternalCompetitionRecordService externalCompetitionRecordService;
    private final ExternalCompetitionGroupService externalCompetitionGroupService;
    private final ExternalCompetitionExportRuleService externalCompetitionExportRuleService;
    private final UserSchoolRelService userSchoolRelService;
    private final StudentService studentService;
    private final SysClassService sysClassService;
    private final GradeGroupService gradeGroupService;
    private final ExportFileHandler exportFileHandler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdate(ExternalCompetitionCreateReqModel reqModel) {
        // 新增或修改比赛活动
        ExternalCompetitionEntity entity = BeanConvertUtil.convert(reqModel, ExternalCompetitionEntity.class);
        // 获取操作人
        long userId = StpUtil.getLoginIdAsLong();
        if (userId == 0L) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        entity.setCreateUserId(userId);
        // 处理地区
        ExternalCompetitionAreaEnum areaEnum = ExternalCompetitionAreaEnum.getByCode(reqModel.getArea());
        if (areaEnum == null) {
            throw new BusinessException(LanguageConstants.PARAM_ERROR);
        }
        entity.setArea(areaEnum.getCode());
        this.saveOrUpdate(entity);
        // 处理组别和学生信息
        if (CollectionUtils.isNotEmpty(reqModel.getGroups())) {
            List<Long> oldIds = new ArrayList<>();
            List<String> groupNames = new ArrayList<>();
            List<ExternalCompetitionGroupEntity> groupList = externalCompetitionGroupService.list(Wrappers.<ExternalCompetitionGroupEntity>lambdaQuery()
                    .eq(ExternalCompetitionGroupEntity::getCompetitionId, entity.getId()));
            if (CollectionUtils.isNotEmpty(groupList)) {
                oldIds = groupList.stream().map(ExternalCompetitionGroupEntity::getId).collect(Collectors.toList());
            }
            List<Long> recordIds = new ArrayList<>();
            List<ExternalCompetitionRecordEntity> recordEntityList = externalCompetitionRecordService.list(Wrappers.<ExternalCompetitionRecordEntity>lambdaQuery()
                    .eq(ExternalCompetitionRecordEntity::getCompetitionId, entity.getId()));
            if (CollectionUtils.isNotEmpty(recordEntityList)) {
                recordIds = recordEntityList.stream().map(ExternalCompetitionRecordEntity::getId).collect(Collectors.toList());
            }
            List<ExternalCompetitionRecordEntity> insertRecordList = new ArrayList<>();
            List<ExternalCompetitionRecordEntity> updateRecordList = new ArrayList<>();
            for (ExternalCompetitionGroupCreateReqModel record : reqModel.getGroups()) {
                // 检查组别名称是否重复
                if (groupNames.contains(record.getGroupName())) {
                    throw new BusinessException(LanguageConstants.EXTERNAL_COMPETITION_GROUP_NAME_EXISTS);
                }
                groupNames.add(record.getGroupName());
                if (record.getId() != null) {
                    oldIds.remove(record.getId());
                }
                // 新增或修改组别
                ExternalCompetitionGroupEntity groupEntity = BeanConvertUtil.convert(record, ExternalCompetitionGroupEntity.class);
                groupEntity.setCompetitionId(entity.getId());
                externalCompetitionGroupService.saveOrUpdate(groupEntity);
                // 处理学生信息
                if (CollectionUtils.isNotEmpty(record.getRecords())) {
                    List<ExternalCompetitionRecordReqModel> records = record.getRecords();
                    for (ExternalCompetitionRecordReqModel student : records) {
                        if (ObjectUtils.isNotEmpty(student.getId())) {
                            recordIds.remove(student.getId());
                        }
                        // 新增或修改学生记录
                        ExternalCompetitionRecordEntity recordEntity = BeanConvertUtil.convert(student, ExternalCompetitionRecordEntity.class);
                        recordEntity.setCompetitionId(entity.getId());
                        recordEntity.setGroupId(groupEntity.getId());
                        if (student.getId() == null) {
                            insertRecordList.add(recordEntity);
                        } else {
                            updateRecordList.add(recordEntity);
                        }
                    }
                }
            }
            // 删除被删除的组别
            if (!oldIds.isEmpty()) {
                externalCompetitionGroupService.removeByIds(oldIds);
            }
            // 新增或修改记录
            saveBatchRecord(insertRecordList);
            updateBatchRecord(updateRecordList);
            // 删除被删除的记录
            if (!recordIds.isEmpty()) {
                externalCompetitionRecordService.removeByIds(recordIds);
            }
        }
        return entity.getId();
    }

    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.OUTSIDE_COMPETITION)
    private List<ExternalCompetitionRecordEntity> saveBatchRecord(List<ExternalCompetitionRecordEntity> insertRecordList) {
        if (CollectionUtils.isNotEmpty(insertRecordList)) {
            externalCompetitionRecordService.saveBatch(insertRecordList);
            return insertRecordList;
        }
        return new ArrayList<>();
    }

    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.OUTSIDE_COMPETITION)
    public List<ExternalCompetitionRecordEntity> updateBatchRecord(List<ExternalCompetitionRecordEntity> updateRecordList) {
        if (CollectionUtils.isNotEmpty(updateRecordList)) {
            externalCompetitionRecordService.updateBatchById(updateRecordList);
            return updateRecordList;
        }
        return new ArrayList<>();
    }

    @Override
    public ExternalCompetitionPageResModel info(Long id) {
        ExternalCompetitionEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.COMPETITION_NOT_EXIST);
        }
        ExternalCompetitionPageResModel resModel = BeanConvertUtil.convert(entity, ExternalCompetitionPageResModel.class);
        //查询组别信息
        List<ExternalCompetitionGroupEntity> groupEntities = externalCompetitionGroupService.list(Wrappers.<ExternalCompetitionGroupEntity>lambdaQuery()
                .eq(ExternalCompetitionGroupEntity::getCompetitionId, id));
        if (CollectionUtils.isNotEmpty(groupEntities)) {
            resModel.setGroups(BeanConvertUtil.convertList(groupEntities, ExternalCompetitionGroupResModel.class));
        }
        //查询记录信息
        Map<Long, List<ExternalCompetitionRecordEntity>> groupIdRecordId = new HashMap<>();
        List<ExternalCompetitionRecordEntity> recordEntities = externalCompetitionRecordService.list(Wrappers.<ExternalCompetitionRecordEntity>lambdaQuery()
                .eq(ExternalCompetitionRecordEntity::getCompetitionId, id));
        if (CollectionUtils.isNotEmpty(recordEntities)) {
            groupIdRecordId = recordEntities.stream().collect(Collectors.groupingBy(ExternalCompetitionRecordEntity::getGroupId));
        }
        if (CollectionUtils.isNotEmpty(groupEntities)) {
            for (ExternalCompetitionGroupResModel group : resModel.getGroups()) {
                group.setRecords(BeanConvertUtil.convertList(groupIdRecordId.get(group.getId()),ExternalCompetitionRecordResModel.class));
            }
        }
        return resModel;
    }

    @Override
    public PageInfo<ExternalCompetitionPageResModel> page(ExternalCompetitionQueryReqModel reqModel) {
        List<Long> competitionIds = new ArrayList<>();
        if (reqModel.getClassId() != null || reqModel.getStudentId() != null) {
            //班级/学生信息不为空，查询参与的比赛id
            competitionIds = externalCompetitionRecordService.partakeCompetitionList(reqModel);
            if (CollectionUtils.isEmpty(competitionIds)) {
                //未查询到有关的比赛直接返回空
                return new PageInfo<>(new ArrayList<>());
            }
        }
        if (StringUtils.isNotEmpty(reqModel.getGroupType())) {
            // 查询这个级组名称的比赛活动id
            List<ExternalCompetitionGroupEntity> groupEntities = externalCompetitionGroupService.list(Wrappers.<ExternalCompetitionGroupEntity>lambdaQuery()
                    .like(ExternalCompetitionGroupEntity::getGroupName, reqModel.getGroupType())
                    .eq(ExternalCompetitionGroupEntity::getDeleted, 0));
            if (CollectionUtils.isNotEmpty(groupEntities)) {
                competitionIds.addAll(groupEntities.stream().map(ExternalCompetitionGroupEntity::getCompetitionId).collect(Collectors.toSet()));
            }
            if (CollectionUtils.isEmpty(competitionIds)) {
                //未查询到有关的比赛直接返回空
                return new PageInfo<>(new ArrayList<>());
            }
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<ExternalCompetitionPageResModel> list = this.getBaseMapper().page(competitionIds, reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            return new PageInfo<>(list);
        }
        return new PageInfo<>(new ArrayList<>());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ExternalCompetitionEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.COMPETITION_NOT_EXIST);
        }
        this.removeById(id);
        //删除记录
        externalCompetitionGroupService.remove(Wrappers.<ExternalCompetitionGroupEntity>lambdaQuery()
                .eq(ExternalCompetitionGroupEntity::getCompetitionId, id));
        externalCompetitionRecordService.remove(Wrappers.<ExternalCompetitionRecordEntity>lambdaQuery()
                .eq(ExternalCompetitionRecordEntity::getCompetitionId, id));
    }

    @Override
    public String export(ExternalCompetitionQueryReqModel reqModel) {
        List<Long> competitionIds = new ArrayList<>();
        if (reqModel.getClassId() != null || reqModel.getStudentId() != null) {
            //班级/学生信息不为空，查询参与的比赛id
            competitionIds = externalCompetitionRecordService.partakeCompetitionList(reqModel);
        }
        if (StringUtils.isNotEmpty(reqModel.getGroupType())) {
            // 查询这个级组名称的比赛活动id
            List<ExternalCompetitionGroupEntity> groupEntities = externalCompetitionGroupService.list(Wrappers.<ExternalCompetitionGroupEntity>lambdaQuery()
                    .like(ExternalCompetitionGroupEntity::getGroupName, reqModel.getGroupType())
                    .eq(ExternalCompetitionGroupEntity::getDeleted, 0));
            if (CollectionUtils.isNotEmpty(groupEntities)) {
                competitionIds.addAll(groupEntities.stream().map(ExternalCompetitionGroupEntity::getCompetitionId).collect(Collectors.toSet()));
            }
        }
        List<ExternalCompetitionPageResModel> list = this.getBaseMapper().page(competitionIds, reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            // 拼接数据
            List<ExternalCompetitionDataDTO> dataDTOS = getExternalCompetitionExportData(list);

            // 开始导出
            String fileName = "校外比赛导出.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();
            
            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                fileName = "externalCompetitions.xlsx";
                List<ExternalCompetitionExportEnDTO> exportEnModels = dataDTOS.stream()
                        .map(resModel -> {
                            ExternalCompetitionExportEnDTO exportDTO = new ExternalCompetitionExportEnDTO();
                            BeanUtils.copyProperties(resModel, exportDTO);
                            return exportDTO;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, ExternalCompetitionExportEnDTO.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                fileName = "competicoesExternas.xlsx";
                List<ExternalCompetitionExportPtDTO> exportPtModels = dataDTOS.stream()
                        .map(resModel -> {
                            ExternalCompetitionExportPtDTO exportDTO = new ExternalCompetitionExportPtDTO();
                            BeanUtils.copyProperties(resModel, exportDTO);
                            return exportDTO;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, ExternalCompetitionExportPtDTO.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            } else {
                List<ExternalCompetitionExportDTO> exportModels = dataDTOS.stream()
                        .map(resModel -> {
                            ExternalCompetitionExportDTO exportDTO = new ExternalCompetitionExportDTO();
                            BeanUtils.copyProperties(resModel, exportDTO);
                            return exportDTO;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportModels, fileName, ExternalCompetitionExportDTO.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());            }
        }
        return null;
    }

    private List<ExternalCompetitionDataDTO> getExternalCompetitionExportData(List<ExternalCompetitionPageResModel> list) {
        List<ExternalCompetitionDataDTO> dataDTOS = new ArrayList<>();
        Map<Long, ExternalCompetitionPageResModel> ECMap = list.stream().collect(Collectors.toMap(ExternalCompetitionPageResModel::getId, resModel -> resModel));
        List<Long> ids = list.stream().map(ExternalCompetitionPageResModel::getId).collect(Collectors.toList());
        // 获取组别信息
        Map<Long, ExternalCompetitionGroupEntity> groupMap = new HashMap<>();
        List<ExternalCompetitionGroupEntity> groupEntities = externalCompetitionGroupService.list(Wrappers.<ExternalCompetitionGroupEntity>lambdaQuery()
                .in(ExternalCompetitionGroupEntity::getCompetitionId, ids)
                .eq(ExternalCompetitionGroupEntity::getDeleted, 0));
        if (CollectionUtils.isNotEmpty(groupEntities)) {
            groupMap = groupEntities.stream().collect(Collectors.toMap(ExternalCompetitionGroupEntity::getId, Function.identity()));
        }
        // 获取学生信息
        List<ExternalCompetitionRecordEntity> recordEntities = externalCompetitionRecordService.list(Wrappers.<ExternalCompetitionRecordEntity>lambdaQuery()
                .in(ExternalCompetitionRecordEntity::getCompetitionId, ids)
                .eq(ExternalCompetitionRecordEntity::getDeleted, 0));
        // 获取教师信息
        Map<Long, UserSchoolRelEntity> userMap = new HashMap<>();
        List<Long> userIds = list.stream().map(ExternalCompetitionPageResModel::getCreateUserId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(userIds)) {
            List<UserSchoolRelEntity> userSchoolRelEntities = userSchoolRelService.list(Wrappers.<UserSchoolRelEntity>lambdaQuery()
                    .in(UserSchoolRelEntity::getUserId, userIds)
                    .eq(UserSchoolRelEntity::getSchoolId, list.get(0).getSchoolId())
                    .eq(UserSchoolRelEntity::getDeleted, 0));
            if (CollectionUtils.isNotEmpty(userSchoolRelEntities)) {
                userMap = userSchoolRelEntities.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserId, Function.identity()));
            }
        }
        if (CollectionUtils.isNotEmpty(recordEntities)) {
            // 获取学生详情
            Map<Long, StudentEntity> studentMap = new HashMap<>();
            List<Long> studentIds = recordEntities.stream().map(ExternalCompetitionRecordEntity::getStudentId).filter(Objects::nonNull).collect(Collectors.toList());
            List<StudentEntity> studentEntities = studentService.listByIds(studentIds);
            if (CollectionUtils.isNotEmpty(studentEntities)) {
                studentMap = studentEntities.stream().collect(Collectors.toMap(StudentEntity::getId, Function.identity()));
            }
            for (ExternalCompetitionRecordEntity recordEntity : recordEntities) {
                if (!ECMap.containsKey(recordEntity.getCompetitionId())) {
                    continue;
                }
                if (!groupMap.containsKey(recordEntity.getGroupId())) {
                    continue;
                }
                ExternalCompetitionDataDTO dataDTO = new ExternalCompetitionDataDTO();
                // 获取学生信息
                if (studentMap.containsKey(recordEntity.getStudentId())) {
                    StudentEntity studentEntity = studentMap.get(recordEntity.getStudentId());
                    dataDTO.setEducationNo(studentEntity.getEducationNo() == null ? "" : studentEntity.getEducationNo());
                    dataDTO.setStudentNo(studentEntity.getStudentNo() == null ? "" : studentEntity.getStudentNo());
                    dataDTO.setSeatNo(studentEntity.getSeatNo() == null ? "" : String.valueOf(studentEntity.getSeatNo()));
                }
                ExternalCompetitionPageResModel externalCompetition = ECMap.get(recordEntity.getCompetitionId());
                ExternalCompetitionGroupEntity externalCompetitionGroup = groupMap.get(recordEntity.getGroupId());

                BeanUtils.copyProperties(recordEntity, dataDTO);
                dataDTO.setClassName(recordEntity.getGradeName() + recordEntity.getClassName());
                // 拼接比赛信息
                dataDTO.setPrize(recordEntity.getPrizeName());
                dataDTO.setGroupName(externalCompetitionGroup.getGroupName());
                dataDTO.setName(externalCompetition.getName());
                dataDTO.setOrganizer(externalCompetition.getOrganizer());
                dataDTO.setAdvisor(externalCompetition.getAdvisor());
                dataDTO.setFinalAwardsPoints(recordEntity.getFinalAwardsPoints() == null ? "" : String.valueOf(recordEntity.getFinalAwardsPoints()));
                dataDTO.setStartTime(DateUtil.format(externalCompetition.getStartTime(), "yyyy-MM-dd"));
                dataDTO.setPrizeTime(DateUtil.format(externalCompetition.getPrizeTime(), "yyyy-MM-dd"));
                String currentLanguage = LanguageUtil.getCurrentLanguage();
                if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                    ExternalCompetitionAreaEnum byCode = ExternalCompetitionAreaEnum.getByCode(externalCompetition.getArea());
                    if (byCode != null) {
                        dataDTO.setArea(byCode.getEnglishName());
                    }
                    dataDTO.setCompetitionType(recordEntity.getOneOrTeam() == 0? "one" : "team");
                } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                    ExternalCompetitionAreaEnum byCode = ExternalCompetitionAreaEnum.getByCode(externalCompetition.getArea());
                    if (byCode != null) {
                        dataDTO.setArea(byCode.getPtName());
                    }
                    dataDTO.setCompetitionType(recordEntity.getOneOrTeam() == 0? "pessoal" : "grupos");
                } else {
                    ExternalCompetitionAreaEnum byCode = ExternalCompetitionAreaEnum.getByCode(externalCompetition.getArea());
                    if (byCode != null) {
                        dataDTO.setArea(byCode.getName());
                    }
                    dataDTO.setCompetitionType(recordEntity.getOneOrTeam() == 0? "個人" : "團體");
                }
                dataDTO.setActivityArea(externalCompetition.getActivityArea());
                dataDTO.setCategoryName(externalCompetition.getCategoryName());
                dataDTO.setRepresentative(externalCompetition.getRepresentative());
                // 获取教师姓名
                if (userMap.containsKey(externalCompetition.getCreateUserId())) {
                    dataDTO.setCreateUserName(userMap.get(externalCompetition.getCreateUserId()).getUsername());
                }
                // 根据规则计算最终评级建议
                getFinalAwardsRemark(dataDTO, recordEntity, externalCompetition);
                dataDTOS.add(dataDTO);
            }
        }
        // 拼接数据
        return dataDTOS;
    }

    private void getFinalAwardsRemark(ExternalCompetitionDataDTO dto, ExternalCompetitionRecordEntity recordEntity, ExternalCompetitionPageResModel entity) {
        // 查询规则是否存在
        List<ExternalCompetitionExportRuleEntity> ruleEntity = externalCompetitionExportRuleService.list(Wrappers.<ExternalCompetitionExportRuleEntity>lambdaQuery()
                .eq(ExternalCompetitionExportRuleEntity::getSchoolId, entity.getSchoolId()));
        if (ruleEntity == null || CollectionUtils.isEmpty(ruleEntity)) {
            return;
        }
        // 根据规则计算值
        Optional<ExternalCompetitionExportRuleEntity> first = ruleEntity.stream().filter(item -> item.getCategoryId().equals(entity.getCategoryId()) &&
                item.getAwardsId().equals(recordEntity.getAwardsId()) &&
                item.getRepresentative().equals(entity.getRepresentative()) &&
                Objects.equals(item.getType(), entity.getArea())).findFirst();
        dto.setAutoAwardsRemark(first.map(ExternalCompetitionExportRuleEntity::getRules).orElse(""));
    }
}