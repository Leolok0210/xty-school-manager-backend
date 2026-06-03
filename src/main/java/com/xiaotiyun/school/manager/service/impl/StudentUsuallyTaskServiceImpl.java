package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.DataBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.DataOperationTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.StudetDisplayNameTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.SystemSettingKeyEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.StudentUsuallyTaskMapper;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.dto.StudentUsuallyPartakeCountDTO;
import com.xiaotiyun.school.manager.model.dto.StudentUsuallyScoreDetailDTO;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentUsuallyTaskServiceImpl extends ServiceImpl<StudentUsuallyTaskMapper, StudentUsuallyTaskEntity> implements StudentUsuallyTaskService {
    private final StudentUsuallyScoreService studentUsuallyScoreService;

    private final StudentUsuallyRuleService studentUsuallyRuleService;

    private final StudentUsuallyTypeService studentUsuallyTypeService;

    private final SemesterService semesterService;

    private final SysClassService sysClassService;

    private final SystemSettingService systemSettingService;

    private final UserAuthHelper userAuthHelper;

    @Override
    public PageInfo<StudentUsuallyTaskPageResModel> page(StudentUsuallyPageReqModel reqModel) {
        List<Long> taskIds = new ArrayList<>();
        if (StringUtils.isNotBlank(reqModel.getStudentInfo())) {
            //学生信息不为空，查询学生参与的考试id
            taskIds = studentUsuallyScoreService.partakeTaskList(reqModel.getStudentInfo());
            if (CollectionUtils.isEmpty(taskIds)) {
                return new PageInfo<>(new ArrayList<>());
            }
        }
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if (CollectionUtils.isEmpty(classIds)) {
                return new PageInfo<>(new ArrayList<>());
            }
            reqModel.setClassIds(classIds);
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<StudentUsuallyTaskPageResModel> list = this.getBaseMapper().page(taskIds, reqModel);

        if (CollectionUtils.isNotEmpty(list)) {
            //获取参与人数
            taskIds = list.stream().map(StudentUsuallyTaskPageResModel::getId).collect(Collectors.toList());
            List<StudentUsuallyPartakeCountDTO> partakeCountDTOS = studentUsuallyScoreService.partakeCountList(taskIds);
            Map<Long, StudentUsuallyPartakeCountDTO> partakeCountDTOMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(partakeCountDTOS)) {
                partakeCountDTOMap = partakeCountDTOS.stream().collect(Collectors.toMap(StudentUsuallyPartakeCountDTO::getTaskId, partakeCountDTO -> partakeCountDTO));
            }
            // 获取平时成绩类型名称
            List<StudentUsuallyTypeEntity> typeResModels = studentUsuallyTypeService.list(Wrappers.<StudentUsuallyTypeEntity>lambdaQuery()
                    .eq(StudentUsuallyTypeEntity::getSchoolId, reqModel.getSchoolId()));
            Map<Long, StudentUsuallyTypeEntity> typeResModelMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(typeResModels)) {
                typeResModelMap = typeResModels.stream().collect(Collectors.toMap(StudentUsuallyTypeEntity::getId, Function.identity()));
            }
            for (StudentUsuallyTaskPageResModel resModel : list) {
                StudentUsuallyPartakeCountDTO partakeCountDTO = partakeCountDTOMap.get(resModel.getId());
                if (partakeCountDTO != null) {
                    resModel.setPartakeCount(partakeCountDTO.getPartakeCount());
                }
                StudentUsuallyTypeEntity typeResModel = typeResModelMap.get(resModel.getTypeId());
                if (typeResModel != null) {
                    resModel.setTypeName(typeResModel.getTypeName());
                }
            }
        }
        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.USUALLY_TASK)
    public StudentUsuallyTaskEntity save(StudentUsuallyTaskSaveReqModel reqModel) {
        StudentUsuallyTaskEntity entity = BeanConvertUtil.convert(reqModel, StudentUsuallyTaskEntity.class);
        this.save(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.USUALLY_TASK)
    public StudentUsuallyTaskEntity update(Long id, StudentUsuallyTaskSaveReqModel reqModel) {
        StudentUsuallyTaskEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        // 使用BeanUtils替代BeanConvertUtil
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
        return entity;
    }

    @Override
    public StudentUsuallyTaskResModel info(Long id) {
        StudentUsuallyTaskEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        StudentUsuallyTaskResModel convert = BeanConvertUtil.convert(entity, StudentUsuallyTaskResModel.class);
        // 获取平时成绩类型名称
        StudentUsuallyTypeEntity typeResModel = studentUsuallyTypeService.getById(entity.getTypeId());
        if (typeResModel != null) {
            convert.setTypeName(typeResModel.getTypeName());
        }
        return convert;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        StudentUsuallyTaskEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        this.removeById(id);
    }

    @Override
    public String check(Long schoolId, StudentUsuallyTaskCheckReqModel reqModel) {
        return this.getBaseMapper().check(schoolId, reqModel);
    }

    @Override
    public List<StudentUsuallyScoreCheckResModel> scoreCheck(StudentUsuallyScoreCheckReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if (CollectionUtils.isEmpty(classIds)) {
                return new ArrayList<>();
            }
        }
        List<StudentUsuallyScoreCheckResModel> result = new ArrayList<>();
        StudentUsuallyPageReqModel studentUsuallyPageReqModel = new StudentUsuallyPageReqModel();
        BeanUtils.copyProperties(reqModel, studentUsuallyPageReqModel);
        studentUsuallyPageReqModel.setClassIds(classIds);
        List<StudentUsuallyTaskPageResModel> list = this.getBaseMapper().page(null, studentUsuallyPageReqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> taskIds = list.stream().map(StudentUsuallyTaskPageResModel::getId).collect(Collectors.toList());
            //获取学生成绩
            List<StudentUsuallyScoreDetailDTO> scoreEntities = studentUsuallyScoreService.scoreDetailList(null, taskIds);
            if (CollectionUtils.isNotEmpty(scoreEntities)) {
                Map<Long, List<StudentUsuallyScoreDetailDTO>> studentScoreMap = scoreEntities.stream().collect(Collectors.groupingBy(StudentUsuallyScoreDetailDTO::getStudentId));
                if (reqModel.getSubjectId() != null && reqModel.getSubjectId() > 0) {
                    //按科目查询
                    for (Map.Entry<Long, List<StudentUsuallyScoreDetailDTO>> studentScoreList : studentScoreMap.entrySet()) {
                        //按科目聚合计算平均分
                        StudentUsuallyScoreCheckResModel resModel = new StudentUsuallyScoreCheckResModel();
                        resModel.setStudentId(studentScoreList.getKey());
                        List<StudentUsuallyScoreDetailDTO> scoreDetailDTOS = studentScoreList.getValue();
                        resModel.setSeatNo(scoreDetailDTOS.get(0).getSeatNo());
                        if (scoreDetailDTOS.get(0).getDisplayNameType().equals(StudetDisplayNameTypeEnum.ENGLISH.getCode()) && StringUtils.isNotBlank(scoreDetailDTOS.get(0).getEnglishName())) {
                            resModel.setStudentName(scoreDetailDTOS.get(0).getEnglishName());
                        } else {
                            resModel.setStudentName(scoreDetailDTOS.get(0).getChineseName());
                        }
                        Map<Long, List<StudentUsuallyScoreDetailDTO>> subjectScoreMap = scoreDetailDTOS.stream().collect(Collectors.groupingBy(StudentUsuallyScoreDetailDTO::getTaskId));
                        List<StudentUsuallyScoreCheckDetailResModel> scoreList = new ArrayList<>();
                        for (Map.Entry<Long, List<StudentUsuallyScoreDetailDTO>> subjectScoreList : subjectScoreMap.entrySet()) {
                            StudentUsuallyScoreCheckDetailResModel model = new StudentUsuallyScoreCheckDetailResModel();
                            model.setTaskName(subjectScoreList.getValue().get(0).getTaskName());
                            model.setTestDate(subjectScoreList.getValue().get(0).getTestDate());
                            OptionalDouble average = subjectScoreList.getValue().stream().mapToInt(StudentUsuallyScoreDetailDTO::getScore).average();
                            if (average.isPresent()) {
                                model.setScore((int) average.getAsDouble());
                            }
                            scoreList.add(model);
                        }
                        resModel.setScoreList(scoreList);
                        result.add(resModel);
                    }
                } else {
                    //按班级查询
                    //获取学部信息
                    if (reqModel.getPeriodId() == null || reqModel.getPeriodId() == 0L ||
                            reqModel.getClassId() == null || reqModel.getClassId() == 0L) {
                        throw new BusinessException(LanguageConstants.PARAM_ERROR);
                    }
                    SysClass sysClass = sysClassService.getSysClassById(reqModel.getClassId());
                    //获取平时成绩权重规则
                    List<StudentUsuallyRuleEntity> ruleEntityList = studentUsuallyRuleService.list(Wrappers.<StudentUsuallyRuleEntity>lambdaQuery()
                            .eq(StudentUsuallyRuleEntity::getGradeGroupId, sysClass.getGradeGroup())
                            .eq(StudentUsuallyRuleEntity::getSchoolId, reqModel.getSchoolId()));
                    if (ObjectUtils.isEmpty(ruleEntityList)) {
                        throw new BusinessException(LanguageConstants.SCORE_RULE_NOT_EXISTS);
                    }
                    // 根据平时成绩是否关联类型，分组
                    // 平时成绩类型科目关联开关
                    boolean isUsualTypeRelSub = false;
                    List<SystemSettingEntity> setting = systemSettingService.list(Wrappers.<SystemSettingEntity>lambdaQuery()
                            .eq(SystemSettingEntity::getSettingKey, SystemSettingKeyEnum.USUAL_TYPE_REL_SUB.getKey())
                            .eq(SystemSettingEntity::getSchoolId, reqModel.getSchoolId()));
                    if (!CollectionUtils.isEmpty(setting)) {
                        isUsualTypeRelSub = setting.get(0).getSettingValue().equals("1");
                    }
                    // 根据开关封装map
                    Map<Long, List<StudentUsuallyRuleEntity>> usuallyRuleMapBySub = new HashMap<>();// 关联用这个  科目id-类型id-规则zh
                    if (isUsualTypeRelSub){
                        try {
                            usuallyRuleMapBySub = ruleEntityList.stream().filter(item -> item.getSubjectId() != null)
                                    .collect(Collectors.groupingBy(StudentUsuallyRuleEntity::getSubjectId));
                        } catch (Exception e) {
                            log.error("学校Id{},平时成绩科目占比配置错误2！", reqModel.getSchoolId(), e);
                        }
                    }
                    // 按学生聚合
                    for (Map.Entry<Long, List<StudentUsuallyScoreDetailDTO>> studentScoreList : studentScoreMap.entrySet()) {
                        //按任务聚合计算平均分
                        StudentUsuallyScoreCheckResModel resModel = new StudentUsuallyScoreCheckResModel();
                        resModel.setStudentId(studentScoreList.getKey());
                        List<StudentUsuallyScoreDetailDTO> scoreDetailDTOS = studentScoreList.getValue();
                        resModel.setSeatNo(scoreDetailDTOS.get(0).getSeatNo());
                        if (scoreDetailDTOS.get(0).getDisplayNameType().equals(StudetDisplayNameTypeEnum.ENGLISH.getCode()) && StringUtils.isNotBlank(scoreDetailDTOS.get(0).getEnglishName())) {
                            resModel.setStudentName(scoreDetailDTOS.get(0).getEnglishName());
                        } else {
                            resModel.setStudentName(scoreDetailDTOS.get(0).getChineseName());
                        }
                        Map<Long, List<StudentUsuallyScoreDetailDTO>> subjectScoreMap = scoreDetailDTOS.stream().collect(Collectors.groupingBy(StudentUsuallyScoreDetailDTO::getSubjectId));
                        List<StudentUsuallyScoreCheckDetailResModel> scoreList = new ArrayList<>();
                        for (Map.Entry<Long, List<StudentUsuallyScoreDetailDTO>> subjectScoreList : subjectScoreMap.entrySet()) {
                            StudentUsuallyScoreCheckDetailResModel model = new StudentUsuallyScoreCheckDetailResModel();
                            model.setSubjectName(subjectScoreList.getValue().get(0).getSubjectName());
                            BigDecimal totalScore = BigDecimal.ZERO;
                            List<StudentUsuallyRuleEntity> thisRuleList = ruleEntityList;
                            // 开启开关时，需要获取当前科目下的类型规则占比
                            if (isUsualTypeRelSub && usuallyRuleMapBySub.containsKey(subjectScoreList.getKey())){
                                thisRuleList = usuallyRuleMapBySub.get(subjectScoreList.getKey());
                            }
                            for (StudentUsuallyRuleEntity rule : thisRuleList) {
                                OptionalDouble avgScore = subjectScoreList.getValue().stream().filter(a -> a.getTypeId().equals(rule.getTypeId())).mapToDouble(StudentUsuallyScoreDetailDTO::getScore).average();
                                if (avgScore.isPresent()) {
                                    totalScore = totalScore.add(BigDecimal.valueOf(avgScore.getAsDouble()).multiply(BigDecimal.valueOf(rule.getWeight())).divide(BigDecimal.valueOf(10000), 0, RoundingMode.HALF_UP));
                                }
                            }
                            if (totalScore.compareTo(BigDecimal.ZERO) > 0) {
                                model.setScore(totalScore.intValue());
                            }
                            scoreList.add(model);
                        }
                        resModel.setScoreList(scoreList);
                        result.add(resModel);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<StudentUsuallyScoreAnalysisResModel> scoreAnalysis(StudentUsuallyScoreAnalysisReqModel reqModel) {
        SysClass sysClass = sysClassService.getSysClassById(reqModel.getClassId());
        //获取平时成绩权重规则
        List<StudentUsuallyRuleEntity> ruleEntityList = studentUsuallyRuleService.list(Wrappers.<StudentUsuallyRuleEntity>lambdaQuery()
                .eq(StudentUsuallyRuleEntity::getGradeGroupId, sysClass.getGradeGroup())
                .eq(StudentUsuallyRuleEntity::getSchoolId, reqModel.getSchoolId()));
        if (ObjectUtils.isEmpty(ruleEntityList)) {
            throw new BusinessException(LanguageConstants.SCORE_RULE_NOT_EXISTS);
        }
        // 根据平时成绩是否关联类型，分组
        // 平时成绩类型科目关联开关
        boolean isUsualTypeRelSub = false;
        List<SystemSettingEntity> setting = systemSettingService.list(Wrappers.<SystemSettingEntity>lambdaQuery()
                .eq(SystemSettingEntity::getSettingKey, SystemSettingKeyEnum.USUAL_TYPE_REL_SUB.getKey())
                .eq(SystemSettingEntity::getSchoolId, reqModel.getSchoolId()));
        if (!CollectionUtils.isEmpty(setting)) {
            isUsualTypeRelSub = setting.get(0).getSettingValue().equals("1");
        }
        // 根据开关封装map
        if (isUsualTypeRelSub){
            try {
                ruleEntityList = ruleEntityList.stream()
                        .filter(item -> item.getSubjectId() != null && item.getSubjectId().equals(reqModel.getSubjectId()))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("学校Id{},平时成绩科目占比配置错误2！", reqModel.getSchoolId(), e);
            }
        }
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if (CollectionUtils.isEmpty(classIds)) {
                return new ArrayList<>();
            }
        }
        List<StudentUsuallyScoreAnalysisResModel> result = new ArrayList<>();
        StudentUsuallyPageReqModel studentUsuallyPageReqModel = new StudentUsuallyPageReqModel();
        BeanUtils.copyProperties(reqModel, studentUsuallyPageReqModel);
        studentUsuallyPageReqModel.setClassIds(classIds);
        List<StudentUsuallyTaskPageResModel> list = this.getBaseMapper().page(null, studentUsuallyPageReqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> taskIds = list.stream().map(StudentUsuallyTaskPageResModel::getId).collect(Collectors.toList());
            //获取学生成绩
            List<StudentUsuallyScoreDetailDTO> scoreEntities = studentUsuallyScoreService.scoreDetailList(null, taskIds);
            if (CollectionUtils.isNotEmpty(scoreEntities)) {
                Map<Long, List<StudentUsuallyScoreDetailDTO>> studentScoreMap = scoreEntities.stream().collect(Collectors.groupingBy(StudentUsuallyScoreDetailDTO::getStudentId));
                for (Map.Entry<Long, List<StudentUsuallyScoreDetailDTO>> studentScoreList : studentScoreMap.entrySet()) {
                    //聚合计算平均分
                    StudentUsuallyScoreAnalysisResModel resModel = new StudentUsuallyScoreAnalysisResModel();
                    resModel.setStudentId(studentScoreList.getKey());
                    List<StudentUsuallyScoreDetailDTO> scoreDetailDTOS = studentScoreList.getValue();
                    if (scoreDetailDTOS.get(0).getDisplayNameType().equals(StudetDisplayNameTypeEnum.ENGLISH.getCode()) && StringUtils.isNotBlank(scoreDetailDTOS.get(0).getEnglishName())) {
                        resModel.setStudentName(scoreDetailDTOS.get(0).getEnglishName());
                    } else {
                        resModel.setStudentName(scoreDetailDTOS.get(0).getChineseName());
                    }
                    // 根据类型和权重累加平时分总分
                    BigDecimal totalScore = BigDecimal.ZERO;
                    for (StudentUsuallyRuleEntity rule : ruleEntityList) {
                        OptionalDouble avgScore = scoreDetailDTOS.stream().filter(a -> a.getTypeId().equals(rule.getTypeId())).mapToDouble(StudentUsuallyScoreDetailDTO::getScore).average();
                        if (avgScore.isPresent()) {
                            totalScore = totalScore.add(BigDecimal.valueOf(avgScore.getAsDouble()).multiply(BigDecimal.valueOf(rule.getWeight())).divide(BigDecimal.valueOf(10000), 0, RoundingMode.HALF_UP));
                        }
                    }
                    if (totalScore.compareTo(BigDecimal.ZERO) > 0) {
                        resModel.setScore(totalScore.intValue());
                    }
                    result.add(resModel);
                }
            }
        }
        return result;
    }

    @Override
    public List<StudentScorePageResModel> studentScore(StudentScoreReqModel reqModel) {
        List<StudentScorePageResModel> result = new ArrayList<>();
        StudentUsuallyPageReqModel studentUsuallyPageReqModel = new StudentUsuallyPageReqModel();
        BeanUtils.copyProperties(reqModel, studentUsuallyPageReqModel);
        List<StudentUsuallyTaskPageResModel> list = this.getBaseMapper().page(null, studentUsuallyPageReqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> taskIds = list.stream().map(StudentUsuallyTaskPageResModel::getId).collect(Collectors.toList());
            //获取学生成绩
            List<StudentUsuallyScoreDetailDTO> scoreEntities = studentUsuallyScoreService.scoreDetailList(reqModel.getStudentId(), taskIds);
            if (CollectionUtils.isNotEmpty(scoreEntities)) {
                for (StudentUsuallyScoreDetailDTO scoreEntity : scoreEntities) {
                    StudentScorePageResModel resModel = new StudentScorePageResModel();
                    BeanUtils.copyProperties(scoreEntity, resModel);
                    result.add(resModel);
                }
            }
        }
        return result;
    }

    @Override
    public boolean hasScore(Long periodId) {
        if (periodId == null) {
            return false;
        }

        // 1. 先查询该学期下的任务ID
        LambdaQueryWrapper<StudentUsuallyTaskEntity> taskQueryWrapper = new LambdaQueryWrapper<>();
        taskQueryWrapper.select(StudentUsuallyTaskEntity::getId)
                .eq(StudentUsuallyTaskEntity::getPeriodId, periodId)
                .eq(StudentUsuallyTaskEntity::getDeleted, false);

        List<Long> taskIds = this.baseMapper.selectList(taskQueryWrapper)
                .stream()
                .map(StudentUsuallyTaskEntity::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(taskIds)) {
            return false;
        }

        // 查询是否存在成绩记录
        LambdaQueryWrapper<StudentUsuallyScoreEntity> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.in(StudentUsuallyScoreEntity::getTaskId, taskIds);
        return studentUsuallyScoreService.count(scoreWrapper) > 0;
    }

    @Override
    public boolean checkTask(Long schoolId) {
        // 1. 先查询该学期下的任务ID
        LambdaQueryWrapper<StudentUsuallyTaskEntity> taskQueryWrapper = new LambdaQueryWrapper<>();
        taskQueryWrapper.select(StudentUsuallyTaskEntity::getId)
                .eq(StudentUsuallyTaskEntity::getSchoolId, schoolId)
                .eq(StudentUsuallyTaskEntity::getDeleted, false);

        return this.baseMapper.selectCount(taskQueryWrapper) > 0;
    }
}