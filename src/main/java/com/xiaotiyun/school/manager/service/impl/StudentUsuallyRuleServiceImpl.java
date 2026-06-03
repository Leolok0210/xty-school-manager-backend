package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.StudentUsuallyRuleDao;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.UsuallyRuleImportModel;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyRuleDepartmentReqModel;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyRuleReqModel;
import com.xiaotiyun.school.manager.model.req.SubjectRelGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyRuleImportResModel;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyRuleResModel;
import com.xiaotiyun.school.manager.model.res.SubjectRelResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentUsuallyRuleServiceImpl extends ServiceImpl<StudentUsuallyRuleDao, StudentUsuallyRuleEntity> implements StudentUsuallyRuleService {

    @Resource
    private StudentUsuallyTypeService studentUsuallyTypeService;
    @Resource
    private GradeGroupService gradeGroupService;

    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private ImportRecordService importRecordService;
    @Resource(name = "importExecutor")
    private ThreadPoolTaskExecutor importExecutor;

    @Resource
    private SystemSettingService systemSettingService;

    @Resource
    private SubjectService subjectService;
    @Resource
    private SubjectRelService subjectRelService;

    @Resource
    private LanguageUtil languageUtil;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRule(Long schoolId, List<StudentUsuallyRuleDepartmentReqModel> reqModels) {
        if (ObjectUtils.isEmpty(reqModels)){
            throw new BusinessException(LanguageConstants.PARAM_ERROR);
        }
        // 删除原规则
        remove(Wrappers.<StudentUsuallyRuleEntity>lambdaQuery().eq(StudentUsuallyRuleEntity::getSchoolId, schoolId));
        List<StudentUsuallyRuleEntity> insertList = new ArrayList<>();
        reqModels.forEach(reqModel -> {
            StudentUsuallyRuleEntity entity = new StudentUsuallyRuleEntity();
            BeanUtils.copyProperties(reqModel, entity);
            entity.setSchoolId(schoolId);
            entity.setDeleted(0L);
            insertList.add(entity);
        });
        saveBatch(insertList);
    }

    @Override
    public List<StudentUsuallyRuleResModel> getRule(Long schoolId) {
        List<StudentUsuallyRuleEntity> list = list(Wrappers.<StudentUsuallyRuleEntity>lambdaQuery().eq(StudentUsuallyRuleEntity::getSchoolId, schoolId));
        return getStudentUsuallyRuleResModels(list);
    }

    @Override
    public List<StudentUsuallyRuleResModel> listByGroupId(Long schoolId, StudentUsuallyRuleReqModel reqModel) {
        List<StudentUsuallyRuleEntity> list = list(Wrappers.<StudentUsuallyRuleEntity>lambdaQuery()
                .eq(StudentUsuallyRuleEntity::getSchoolId, schoolId)
                .eq(StudentUsuallyRuleEntity::getGradeGroupId, reqModel.getGradeGroupId())
                .eq(ObjectUtils.isNotEmpty(reqModel.getSubjectId()) && reqModel.getSubjectId() > 0,
                        StudentUsuallyRuleEntity::getSubjectId, reqModel.getSubjectId()));
        return getStudentUsuallyRuleResModels(list);
    }

    @Override
    public List<StudentUsuallyRuleImportResModel> listImportData(Long schoolId) {
        // 获取已有的规则
        List<StudentUsuallyRuleEntity> list = list(Wrappers.<StudentUsuallyRuleEntity>lambdaQuery()
                .eq(StudentUsuallyRuleEntity::getSchoolId, schoolId));
        List<StudentUsuallyRuleResModel> resModels = getStudentUsuallyRuleResModels(list);
        Map<Long, List<StudentUsuallyRuleResModel>> groupRuleMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(resModels)){
            groupRuleMap.putAll(resModels.stream().collect(Collectors.groupingBy(StudentUsuallyRuleResModel::getGradeGroupId)));
        }
        // 获取学校配置
        boolean isRelSub = false;
        List<SystemSettingEntity> systemSettingEntityList = systemSettingService.list(Wrappers.<SystemSettingEntity>lambdaQuery()
                .eq(SystemSettingEntity::getSchoolId, schoolId)
                .eq(SystemSettingEntity::getSettingKey, SystemSettingKeyEnum.USUAL_TYPE_REL_SUB.getKey()));
        if (!CollectionUtils.isEmpty(systemSettingEntityList)){
            isRelSub = Objects.equals(systemSettingEntityList.get(0).getSettingValue(), "1");
        }
        // 获取所有需要配置的级组或科目
        List<GradeGroup> gradeGroupList = gradeGroupService.list(Wrappers.<GradeGroup>lambdaQuery()
                .eq(GradeGroup::getSchoolId, schoolId));
        if (CollectionUtils.isEmpty(gradeGroupList)){
            return new ArrayList<>();
        }
        // 排序，根据GradeEnum中的department和grade升序排序
        gradeGroupList.sort(Comparator.comparing(GradeGroup::getDepartment)
                .thenComparing(a -> {
                    // 先通过grade字段获取对应的GradeEnum枚举，然后获取其grade属性
                    GradeEnum gradeEnum = GradeEnum.getByDesc(a.getGrade());
                    return gradeEnum != null ? gradeEnum.getGrade() : Integer.MAX_VALUE;
                }));

        List<StudentUsuallyRuleImportResModel> resModelList = new ArrayList<>();
        if (isRelSub){
            // 关联科目
            List<SubjectRelEntity> subjectRelList = subjectRelService.list(Wrappers.<SubjectRelEntity>lambdaQuery()
                    .eq(SubjectRelEntity::getSchoolId, schoolId));
            if (CollectionUtils.isEmpty(subjectRelList)){
                return new ArrayList<>();
            }
            List<Long> subjectIds = subjectRelList.stream().map(SubjectRelEntity::getId).collect(Collectors.toList());
            List<SubjectRelResModel> subjectRelResModels = subjectRelService.listByIds(subjectIds);
            Map<Long, List<SubjectRelResModel>> groupSubMap = subjectRelResModels.stream().collect(Collectors.groupingBy(SubjectRelResModel::getGroupId));
            // 开始拼接
            for (GradeGroup gradeGroup : gradeGroupList) {
                if (groupSubMap.containsKey(gradeGroup.getId())){
                    Map<Long, List<StudentUsuallyRuleResModel>> subRuleMap = new HashMap<>();
                    if (groupRuleMap.containsKey(gradeGroup.getId())){
                        List<StudentUsuallyRuleResModel> studentUsuallyRuleResModels = groupRuleMap.get(gradeGroup.getId());
                        subRuleMap = studentUsuallyRuleResModels.stream().filter(a -> a.getSubjectId() != null && a.getSubjectId() > 0)
                                .collect(Collectors.groupingBy(StudentUsuallyRuleResModel::getSubjectId));
                    }
                    List<SubjectRelResModel> subList = groupSubMap.get(gradeGroup.getId());
                    for (SubjectRelResModel subjectRelResModel : subList) {
                        if (subRuleMap.containsKey(subjectRelResModel.getId())){
                            List<StudentUsuallyRuleResModel> rules = subRuleMap.get(subjectRelResModel.getId());
                            for (StudentUsuallyRuleResModel rule : rules) {
                                StudentUsuallyRuleImportResModel resModel = new StudentUsuallyRuleImportResModel();
                                resModel.setGradeGroupName(gradeGroup.getGradeGroupName());
                                resModel.setSubjectName(subjectRelResModel.getSubject().getSubjectName());
                                resModel.setTypeName(rule.getTypeName());
                                resModel.setWeight(rule.getWeight() != null && rule.getWeight() >= 100 ? rule.getWeight()/100 : 0);
                                resModelList.add(resModel);
                            }
                        } else {
                            StudentUsuallyRuleImportResModel resModel = new StudentUsuallyRuleImportResModel();
                            resModel.setGradeGroupName(gradeGroup.getGradeGroupName());
                            resModel.setSubjectName(subjectRelResModel.getSubject().getSubjectName());
                            resModelList.add(resModel);
                        }
                    }
                }
            }
        } else {
            // 非关联科目
            for (GradeGroup gradeGroup : gradeGroupList) {
                if (groupRuleMap.containsKey(gradeGroup.getId())){
                    List<StudentUsuallyRuleResModel> rules = groupRuleMap.get(gradeGroup.getId());
                    for (StudentUsuallyRuleResModel rule : rules) {
                        StudentUsuallyRuleImportResModel resModel = new StudentUsuallyRuleImportResModel();
                        resModel.setGradeGroupName(gradeGroup.getGradeGroupName());
                        resModel.setTypeName(rule.getTypeName());
                        resModel.setWeight(rule.getWeight() != null && rule.getWeight() >= 100 ? rule.getWeight()/100 : 0);
                        resModelList.add(resModel);
                    }
                } else {
                    StudentUsuallyRuleImportResModel resModel = new StudentUsuallyRuleImportResModel();
                    resModel.setGradeGroupName(gradeGroup.getGradeGroupName());
                    resModelList.add(resModel);
                }
            }
        }
        return resModelList;
    }

    @Override
    public Long importRules(MultipartFile file, Long schoolId) {
        ImportTaskEntity taskEntity = new ImportTaskEntity();
        taskEntity.setSchoolId(schoolId);
        taskEntity.setFileName(file.getOriginalFilename());
        taskEntity.setType(ImportTaskTypeEnum.STUDENT_USUALLY_RULE.getCode());
        taskEntity.setTotalCount(0);
        taskEntity.setSuccessCount(0);
        taskEntity.setFailCount(0);
        taskEntity.setStatus(ImportTaskStatusEnum.UNTREATED.getCode());
        // 异步发起任务
        // 获取学校语言设置信息
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        // 验证数据格式，转换数据格式，使用 EasyExcel 读取 Excel 文件并转换为 LeisureActivityCourseImportModel 列表
        List<UsuallyRuleImportModel> importRuleList = null;
        try{
            importRuleList = EasyExcel.read(file.getInputStream())
                    .head(UsuallyRuleImportModel.class)
                    .sheet()
                    .headRowNumber(2)
                    .doReadSync();
        } catch (IOException e){
            log.error("导入平时成绩规则错误！文件读取失败", e);
            taskEntity.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            importTaskService.save(taskEntity);
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.FILE_FORMAT_ERROR));
        } catch (Exception e) {
            log.error("导入平时成绩规则错误，系统错误！", e);
            taskEntity.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            importTaskService.save(taskEntity);
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.FILE_FORMAT_ERROR));
        }
        importTaskService.save(taskEntity);
        Long taskId = taskEntity.getId();
        // 发起异步任务
        List<UsuallyRuleImportModel> tmpList = importRuleList;
        CompletableFuture.runAsync(() -> {
                    languageUtil.setLanguage(languageEnum.getCode());
                    importRuleRecord(tmpList, taskId, schoolId);
                }, importExecutor)
                .exceptionally(ex -> {
                    // 打印异常信息
                    log.error("异步导入余暇活动课程任务发生异常", ex);
                    taskEntity.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
                    importTaskService.save(taskEntity);
                    return null;
                })
                .thenRun(LanguageUtil::clearLanguage);
        // 返回导入任务id
        return taskId;
    }

    private void importRuleRecord(List<UsuallyRuleImportModel> importModelList, Long taskId, Long schoolId) {
        ImportTaskEntity taskEntity = new ImportTaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
        // 验证数据
        if (CollectionUtils.isEmpty(importModelList)) {
            log.error("导入平时成绩规则错误！数据为空");
            importTaskService.updateById(taskEntity);
            return;
        }
        // 获取级组、科目、类型id
        List<GradeGroup> list = gradeGroupService.list(new LambdaQueryWrapper<GradeGroup>()
                .eq(GradeGroup::getSchoolId, schoolId));
        if (CollectionUtils.isEmpty(list)) {
            log.error("导入平时成绩规则错误！级组数据为空");
            importTaskService.updateById(taskEntity);
            return;
        }
        SubjectRelGroupQueryReqModel subjectRelGroupQueryReqModel = new SubjectRelGroupQueryReqModel();
        subjectRelGroupQueryReqModel.setSchoolId(schoolId);
        List<SubjectRelResModel> subjectRelList = subjectRelService.listByGroup(subjectRelGroupQueryReqModel);
        if (CollectionUtils.isEmpty(subjectRelList)) {
            log.error("导入平时成绩规则错误！科目数据为空");
            importTaskService.updateById(taskEntity);
            return;
        }
        List<StudentUsuallyTypeEntity> typeEntityList = studentUsuallyTypeService.list(new LambdaQueryWrapper<StudentUsuallyTypeEntity>()
                .eq(StudentUsuallyTypeEntity::getSchoolId, schoolId));
        if (CollectionUtils.isEmpty(typeEntityList)) {
            log.error("导入平时成绩规则错误！平时成绩类型数据为空");
            importTaskService.updateById(taskEntity);
            return;
        }
        // 转换为map
        Map<String, Long> subMap = subjectRelList.stream().collect(
                Collectors.toMap(a -> a.getGroupId()+ "-" + a.getSubject().getSubjectName(), SubjectRelResModel::getId));
        Map<String, Long> groupMap = list.stream().collect(Collectors.toMap(GradeGroup::getGradeGroupName, GradeGroup::getId));
        Map<String, Long> typeMap = typeEntityList.stream().collect(Collectors.toMap(StudentUsuallyTypeEntity::getTypeName, StudentUsuallyTypeEntity::getId));
        // 获取学校配置
        boolean isRelSub = false;
        List<SystemSettingEntity> systemSettingEntityList = systemSettingService.list(new LambdaQueryWrapper<SystemSettingEntity>()
                .eq(SystemSettingEntity::getSchoolId, schoolId)
                .eq(SystemSettingEntity::getSettingKey, SystemSettingKeyEnum.USUAL_TYPE_REL_SUB.getKey()));
        if (!CollectionUtils.isEmpty(systemSettingEntityList)) {
            isRelSub = systemSettingEntityList.get(0).getSettingValue().equals("1");
        }
        // 校验数据
        List<ImportRecordEntity> failList = new ArrayList<>();
        List<StudentUsuallyRuleEntity> entityList = new ArrayList<>();
        List<UsuallyRuleImportModel> checkSuccess = new ArrayList<>();
        int i = 3;
        for (UsuallyRuleImportModel model : importModelList) {
            model.setRowNum(i);
            if (!checkImportData(model, taskId, subMap, groupMap, typeMap, failList, i, isRelSub)) {
                continue;
            }
            model.setSubjectId(subMap.get(model.getGroupId() + "-" + model.getSubjectName()));
            model.setTypeId(typeMap.get(model.getType()));
            checkSuccess.add(model);
            i++;
        }
        // 根据配置校验级组或科目下是否为100%
        List<UsuallyRuleImportModel> successList = getWeightSuccess(checkSuccess, failList, taskId, isRelSub);

        successList.forEach(model -> {
            // 转换数据格式
            StudentUsuallyRuleEntity entity = new StudentUsuallyRuleEntity();
            BeanUtils.copyProperties(model, entity);
            entity.setGradeGroupId(model.getGroupId());
            entity.setSchoolId(schoolId);
            entity.setWeight(Integer.parseInt(model.getWeight()) * 100);
            entityList.add(entity);
        });
        // 保存任务信息
        taskEntity.setFailCount(failList.size());
        taskEntity.setSuccessCount(successList.size());
        importTaskService.updateById(taskEntity);
        // 保存失败记录
        if (!CollectionUtils.isEmpty(failList)) {
            importRecordService.saveBatch(failList);
        }
        // 保存数据
        if (!CollectionUtils.isEmpty(entityList)) {
            // 清空老数据
            this.remove(Wrappers.<StudentUsuallyRuleEntity>lambdaQuery().eq(StudentUsuallyRuleEntity::getSchoolId, schoolId));
            // 保存数据
            this.saveBatch(entityList);
        }
    }

    private List<UsuallyRuleImportModel> getWeightSuccess(List<UsuallyRuleImportModel> checkSuccess, List<ImportRecordEntity> failList, Long taskId, boolean isRelSub) {
        Map<Long, List<UsuallyRuleImportModel>> groupEntityMap = checkSuccess.stream().collect(Collectors.groupingBy(UsuallyRuleImportModel::getGroupId));
        List<UsuallyRuleImportModel> successList = new ArrayList<>();
        if (isRelSub) {
            // 校验科目下是否为100%
            for (Map.Entry<Long, List<UsuallyRuleImportModel>> entry : groupEntityMap.entrySet()) {
                List<UsuallyRuleImportModel> v = entry.getValue();
                Map<Long, List<UsuallyRuleImportModel>> subjectEntityMap = v.stream().collect(Collectors.groupingBy(UsuallyRuleImportModel::getSubjectId));
                for (Map.Entry<Long, List<UsuallyRuleImportModel>> subjectEntry : subjectEntityMap.entrySet()) {
                    List<UsuallyRuleImportModel> subjectRules = subjectEntry.getValue();
                    int totalWeight = subjectRules.stream().mapToInt(item -> Integer.parseInt(item.getWeight())).sum();
                    if (totalWeight != 100) {
                        for (UsuallyRuleImportModel failRule : subjectRules) {
                            ImportRecordEntity recordEntity = new ImportRecordEntity();
                            recordEntity.setTaskId(taskId);
                            recordEntity.setIncorrectLineno(String.valueOf(failRule.getRowNum()));
                            recordEntity.setIncorrectReason(String.format(languageUtil.getMessage(LanguageConstants.SUBJECT_WEIGHT_ERROR), failRule.getSubjectName()));
                            failList.add(recordEntity);
                        }
                    } else {
                        successList.addAll(subjectRules);
                    }
                }
            }
        } else {
            // 校验级组下是否为100%
            for (Map.Entry<Long, List<UsuallyRuleImportModel>> entry : groupEntityMap.entrySet()) {
                List<UsuallyRuleImportModel> v = entry.getValue();
                int totalWeight = v.stream().mapToInt(item -> Integer.parseInt(item.getWeight())).sum();
                if (totalWeight != 100) {
                    for (UsuallyRuleImportModel failRule : v) {
                        ImportRecordEntity recordEntity = new ImportRecordEntity();
                        recordEntity.setTaskId(taskId);
                        recordEntity.setIncorrectLineno(String.valueOf(failRule.getRowNum()));
                        recordEntity.setIncorrectReason(String.format(languageUtil.getMessage(LanguageConstants.GROUP_WEIGHT_ERROR), failRule.getGroupName()));
                        failList.add(recordEntity);
                    }
                } else {
                    successList.addAll(v);
                }
            }
        }
        return successList;
    }

    private boolean checkImportData(UsuallyRuleImportModel model, Long taskId,
                                    Map<String, Long> subMap, Map<String, Long> groupMap, Map<String, Long> typeMap,
                                    List<ImportRecordEntity> failList, int num, boolean isRelSub) {
        String failMessage = "";
        // 级组名称
        if (StringUtils.isEmpty(model.getGroupName())){
            failMessage = languageUtil.getMessage(LanguageConstants.GROUP_NAME_REQUIRED);
        }
        if (!groupMap.containsKey(model.getGroupName())){
            failMessage = languageUtil.getMessage(LanguageConstants.GROUP_NAME_NOT_EXIST);
        } else {
            model.setGroupId(groupMap.get(model.getGroupName()));
        }
        // 科目名称
        if (isRelSub){
            if (StringUtils.isEmpty(model.getSubjectName())){
                failMessage = languageUtil.getMessage(LanguageConstants.SUBJECT_NAME_REQUIRED);
            }
            if (model.getGroupId() != null && model.getGroupId() > 0 &&
                    !subMap.containsKey(model.getGroupId() + "-" + model.getSubjectName())){
                failMessage = languageUtil.getMessage(LanguageConstants.SUBJECT_NAME_NOT_EXIST);
            }
        }
        // 类型
        if (StringUtils.isEmpty(model.getType())){
            failMessage = languageUtil.getMessage(LanguageConstants.TYPE_REQUIRED);
        }
        if (!typeMap.containsKey(model.getType())){
            failMessage = languageUtil.getMessage(LanguageConstants.TYPE_NOT_EXIST);
        }
        // 权重
        if (StringUtils.isEmpty(model.getWeight())){
            failMessage = languageUtil.getMessage(LanguageConstants.WEIGHT_REQUIRED);
        }
        try {
            int weight = Integer.parseInt(model.getWeight());
            if (weight < 0 || weight > 100){
                failMessage = languageUtil.getMessage(LanguageConstants.WEIGHT_ERROR);
            }
        } catch (Exception e) {
            failMessage = languageUtil.getMessage(LanguageConstants.WEIGHT_ERROR);
        }
        if (StringUtils.isNotEmpty(failMessage)) {
            ImportRecordEntity recordEntity = new ImportRecordEntity();
            recordEntity.setIncorrectLineno(num + "");
            recordEntity.setTaskId(taskId);
            recordEntity.setIncorrectReason(failMessage);
            failList.add(recordEntity);
            return false;
        }
        return true;
    }

    private List<StudentUsuallyRuleResModel> getStudentUsuallyRuleResModels(List<StudentUsuallyRuleEntity> list) {
        if (CollectionUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        // 查询类型名称
        List<Long> ids = list.stream().map(StudentUsuallyRuleEntity::getTypeId).collect(Collectors.toList());
        List<StudentUsuallyTypeEntity> typeList = studentUsuallyTypeService.listByIds(ids);
        Map<Long, StudentUsuallyTypeEntity> IdTypeMap = typeList.stream().collect(Collectors.toMap(StudentUsuallyTypeEntity::getId, Function.identity()));
        // 查询科目名称
        List<Long> subjectIds = list.stream().map(StudentUsuallyRuleEntity::getSubjectId).collect(Collectors.toList());
        List<SubjectRelResModel> subjectRelResModels = subjectRelService.listByIds(subjectIds);
        Map<Long, SubjectRelResModel> subjectIdSubjectMap = subjectRelResModels.stream().collect(Collectors.toMap(SubjectRelResModel::getId, Function.identity()));
        // 查询级组名称
        List<Long> gradeGroupIds = list.stream().map(StudentUsuallyRuleEntity::getGradeGroupId).collect(Collectors.toList());
        List<GradeGroup> gradeGroupList = gradeGroupService.listByIds(gradeGroupIds);
        Map<Long, GradeGroup> gradeGroupIdGradeGroupMap = gradeGroupList.stream().collect(Collectors.toMap(GradeGroup::getId, Function.identity()));
        // 拼接数据
        return list.stream().map(entity -> {
            StudentUsuallyRuleResModel resModel = new StudentUsuallyRuleResModel();
            BeanUtils.copyProperties(entity, resModel);
            if (IdTypeMap.containsKey(entity.getTypeId())){
                StudentUsuallyTypeEntity typeEntity = IdTypeMap.get(entity.getTypeId());
                resModel.setTypeName(typeEntity.getTypeName());
            }
            if (subjectIdSubjectMap.containsKey(entity.getSubjectId())){
                SubjectRelResModel subjectRelResModel = subjectIdSubjectMap.get(entity.getSubjectId());
                resModel.setSubjectName(subjectRelResModel.getSubject().getSubjectName());
            }
            if (gradeGroupIdGradeGroupMap.containsKey(entity.getGradeGroupId())){
                GradeGroup gradeGroup = gradeGroupIdGradeGroupMap.get(entity.getGradeGroupId());
                resModel.setGradeGroupName(gradeGroup.getGradeGroupName());
            }
            return resModel;
        }).collect(Collectors.toList());
    }
}