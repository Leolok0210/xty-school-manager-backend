package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.dao.QualityEvaluationIndicatorDao;
import com.xiaotiyun.school.manager.dao.QualityEvaluationGradeStandardDao;
import com.xiaotiyun.school.manager.model.entity.QualityEvaluationIndicatorEntity;
import com.xiaotiyun.school.manager.model.entity.QualityEvaluationGradeStandardEntity;
import com.xiaotiyun.school.manager.basic.enums.DepartmentEnum;
import com.xiaotiyun.school.manager.model.res.QualityEvaluationGradeStandardResModel;
import com.xiaotiyun.school.manager.model.req.QualityIndicatorSaveReqModel;
import com.xiaotiyun.school.manager.model.req.QualityGradeStandardSaveReqModel;
import com.xiaotiyun.school.manager.model.res.QualityIndicatorListResModel;
import com.xiaotiyun.school.manager.model.req.QualityIndicatorBatchSaveReqModel;
import com.xiaotiyun.school.manager.model.req.QualityGradeStandardBatchSaveReqModel;
import com.xiaotiyun.school.manager.service.QualityEvaluationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QualityEvaluationServiceImpl extends ServiceImpl<QualityEvaluationIndicatorDao, QualityEvaluationIndicatorEntity> implements QualityEvaluationService {
    
    @Resource
    private QualityEvaluationGradeStandardDao gradeStandardDao;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveIndicators(Long schoolId, QualityIndicatorBatchSaveReqModel reqModel) {
        // 1. 校验权重总和是否为100
        Map<Integer, Integer> departmentWeightMap = new HashMap<>();
        for (QualityIndicatorSaveReqModel indicator : reqModel.getIndicators()) {
            departmentWeightMap.merge(indicator.getDepartment(), indicator.getWeight(), Integer::sum);
        }
        for (Map.Entry<Integer, Integer> entry : departmentWeightMap.entrySet()) {
            if (entry.getValue() != 100) {
                throw new BusinessException(LanguageConstants.DEPARTMENT_WEIGHT_MUST_BE_100);
            }
        }
        
        // 2. 处理新增和修改
        for (QualityIndicatorSaveReqModel indicator : reqModel.getIndicators()) {
            if (indicator.getId() != null) {
                // 修改
                QualityEvaluationIndicatorEntity entity = this.getById(indicator.getId());
                if (entity == null || !entity.getSchoolId().equals(schoolId)) {
                    throw new BusinessException(LanguageConstants.INDICATOR_NOT_EXISTS_OR_NO_PERMISSION);
                }
                
                entity.setContent(indicator.getContent());
                entity.setWeight(indicator.getWeight());
                entity.setDisplayType(indicator.getDisplayType());
                this.updateById(entity);
            } else {
                // 新增
                QualityEvaluationIndicatorEntity entity = new QualityEvaluationIndicatorEntity();
                entity.setSchoolId(schoolId);
                BeanUtils.copyProperties(indicator, entity);
                this.save(entity);
            }
        }
        
        // 3. 处理删除
        if (reqModel.getDeleteIds() != null && !reqModel.getDeleteIds().isEmpty()) {
            for (Long id : reqModel.getDeleteIds()) {
                QualityEvaluationIndicatorEntity entity = this.getById(id);
                if (entity != null && entity.getSchoolId().equals(schoolId)) {
                    this.removeById(id);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveGradeStandards(Long schoolId, QualityGradeStandardBatchSaveReqModel reqModel) {
        // 1. 获取数据库中现有的分数区间（排除要删除的）
        Set<Long> deleteIds = reqModel.getDeleteIds() != null ? new HashSet<>(reqModel.getDeleteIds()) : new HashSet<>();
        List<QualityEvaluationGradeStandardEntity> existingStandards = gradeStandardDao.selectList(
            new LambdaQueryWrapper<QualityEvaluationGradeStandardEntity>()
                .eq(QualityEvaluationGradeStandardEntity::getSchoolId, schoolId)
                .eq(QualityEvaluationGradeStandardEntity::getDeleted, 0)
                .notIn(deleteIds.size() > 0, QualityEvaluationGradeStandardEntity::getId, deleteIds)
        );

        // 2. 将现有记录转换为Map，方便查找和更新
        Map<Long, QualityEvaluationGradeStandardEntity> existingMap = existingStandards.stream()
            .collect(Collectors.toMap(QualityEvaluationGradeStandardEntity::getId, entity -> entity));

        // 3. 构建完整的待保存区间列表（包含新增和更新的记录）
        List<QualityEvaluationGradeStandardEntity> allStandards = new ArrayList<>();
        
        // 添加需要保留的现有记录，并更新修改的记录
        for (QualityEvaluationGradeStandardEntity existing : existingStandards) {
            // 查找是否有对应的更新请求
            Optional<QualityGradeStandardSaveReqModel> updateReq = reqModel.getStandards().stream()
                .filter(req -> req.getId() != null && req.getId().equals(existing.getId()))
                .findFirst();

            if (updateReq.isPresent()) {
                // 更新现有记录
                QualityGradeStandardSaveReqModel req = updateReq.get();
                existing.setGrade(req.getGrade());
                existing.setScoreMin(req.getScoreMin());
                existing.setScoreMax(req.getScoreMax());
                existing.setDepartment(req.getDepartment());
            }
            allStandards.add(existing);
        }

        // 添加新增的记录
        for (QualityGradeStandardSaveReqModel standard : reqModel.getStandards()) {
            if (standard.getId() == null) {
                QualityEvaluationGradeStandardEntity newEntity = new QualityEvaluationGradeStandardEntity();
                newEntity.setSchoolId(schoolId);
                BeanUtils.copyProperties(standard, newEntity);
                allStandards.add(newEntity);
            }
        }

        // 4. 校验所有区间（排除要删除的）
        Map<Integer, TreeMap<Integer, Integer>> departmentScoreRanges = new HashMap<>();
        for (QualityEvaluationGradeStandardEntity standard : allStandards) {
            // 如果是要删除的区间，跳过校验
            if (reqModel.getDeleteIds() != null && reqModel.getDeleteIds().contains(standard.getId())) {
                continue;
            }
            
            // 校验分数范围
            if (standard.getScoreMin() > standard.getScoreMax()) {
                throw new BusinessException(LanguageConstants.MIN_SCORE_GREATER_THAN_MAX);
            }

            // 获取当前学部的分数区间映射，如果不存在则创建
            TreeMap<Integer, Integer> scoreRanges = departmentScoreRanges.computeIfAbsent(
                standard.getDepartment(), 
                k -> new TreeMap<>()
            );

            // // 检查同一学部内的区间重叠
            // Integer floorKey = scoreRanges.floorKey(standard.getScoreMax());
            // if (floorKey != null && scoreRanges.get(floorKey) >= standard.getScoreMin()) {
            //     throw new BusinessException("分数区间存在重叠");
            // }
            //
            // // 检查是否有更高区间与当前区间重叠
            // Map.Entry<Integer, Integer> higherEntry = scoreRanges.higherEntry(standard.getScoreMin());
            // if (higherEntry != null && higherEntry.getKey() <= standard.getScoreMax()) {
            //     throw new BusinessException("分数区间存在重叠");
            // }

            scoreRanges.put(standard.getScoreMin(), standard.getScoreMax());
        }
        // 5. 检查每个学部是否完整覆盖1-100（只有当该学部有区间时才检查）
        // for (Map.Entry<Integer, TreeMap<Integer, Integer>> entry : departmentScoreRanges.entrySet()) {
        //     TreeMap<Integer, Integer> scoreRanges = entry.getValue();
        //     if (!scoreRanges.isEmpty() && (scoreRanges.firstKey() >= 0 || scoreRanges.get(scoreRanges.lastKey()) <= 100)) {
        //         throw new BusinessException("学部的分数区间必须完整覆盖0-100分");
        //     }
        // }

        // 6. 执行保存操作
        for (QualityGradeStandardSaveReqModel standard : reqModel.getStandards()) {
            if (standard.getId() != null) {
                // 修改
                QualityEvaluationGradeStandardEntity entity = existingMap.get(standard.getId());
                if (entity == null || !entity.getSchoolId().equals(schoolId)) {
                    throw new BusinessException(LanguageConstants.GRADE_STANDARD_NOT_EXISTS);
                }
                entity.setGrade(standard.getGrade());
                entity.setScoreMin(standard.getScoreMin());
                entity.setScoreMax(standard.getScoreMax());
                entity.setDepartment(standard.getDepartment());
                gradeStandardDao.updateById(entity);
            } else {
                // 新增
                QualityEvaluationGradeStandardEntity entity = new QualityEvaluationGradeStandardEntity();
                entity.setSchoolId(schoolId);
                BeanUtils.copyProperties(standard, entity);
                gradeStandardDao.insert(entity);
            }
        }
        
        // 7. 处理删除
        if (reqModel.getDeleteIds() != null && !reqModel.getDeleteIds().isEmpty()) {
            for (Long id : reqModel.getDeleteIds()) {
                QualityEvaluationGradeStandardEntity entity = gradeStandardDao.selectById(id);
                if (entity != null && entity.getSchoolId().equals(schoolId)) {
                    gradeStandardDao.deleteById(id);
                }
            }
        }
    }

    @Override
    public List<QualityIndicatorListResModel> listIndicator(Long schoolId, Integer department) {
        List<QualityEvaluationIndicatorEntity> indicators = this.list(new LambdaQueryWrapper<QualityEvaluationIndicatorEntity>()
                .eq(QualityEvaluationIndicatorEntity::getSchoolId, schoolId)
                .eq(QualityEvaluationIndicatorEntity::getDeleted, 0)
                .eq(QualityEvaluationIndicatorEntity::getDepartment, department));

        return indicators.stream().map(entity -> {
            QualityIndicatorListResModel resModel = new QualityIndicatorListResModel();
            BeanUtils.copyProperties(entity, resModel);
            return resModel;
        }).collect(Collectors.toList());
    }

    @Override
    public List<QualityIndicatorListResModel> listIndicators(Long schoolId) {
        List<QualityEvaluationIndicatorEntity> indicators = this.list(new LambdaQueryWrapper<QualityEvaluationIndicatorEntity>()
                .eq(QualityEvaluationIndicatorEntity::getSchoolId, schoolId)
                .eq(QualityEvaluationIndicatorEntity::getDeleted, 0)
                .orderByAsc(QualityEvaluationIndicatorEntity::getDepartment));
                
        return indicators.stream().map(entity -> {
            QualityIndicatorListResModel resModel = new QualityIndicatorListResModel();
            BeanUtils.copyProperties(entity, resModel);
            return resModel;
        }).collect(Collectors.toList());
    }

    @Override
    public List<QualityEvaluationGradeStandardResModel> listGradeStandards(Long schoolId) {
        List<QualityEvaluationGradeStandardEntity> standards = gradeStandardDao.selectList(
            new LambdaQueryWrapper<QualityEvaluationGradeStandardEntity>()
                .eq(QualityEvaluationGradeStandardEntity::getSchoolId, schoolId)
                .eq(QualityEvaluationGradeStandardEntity::getDeleted, 0)
                .orderByAsc(QualityEvaluationGradeStandardEntity::getScoreMin));
                
        return standards.stream().map(entity -> {
            QualityEvaluationGradeStandardResModel resModel = new QualityEvaluationGradeStandardResModel();
            BeanUtils.copyProperties(entity, resModel);
            return resModel;
        }).collect(Collectors.toList());
    }
} 