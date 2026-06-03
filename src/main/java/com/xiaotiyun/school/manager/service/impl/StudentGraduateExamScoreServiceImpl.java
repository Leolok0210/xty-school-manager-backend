package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.enums.DataBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.DataOperationTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.StudentGraduateExamScoreMapper;
import com.xiaotiyun.school.manager.basic.enums.StudetDisplayNameTypeEnum;
import com.xiaotiyun.school.manager.model.dto.StudentGraduateExamPartakeCountDTO;
import com.xiaotiyun.school.manager.model.dto.StudentGraduateExamScoreDTO;
import com.xiaotiyun.school.manager.model.dto.StudentGraduateExamScoreDetailDTO;
import com.xiaotiyun.school.manager.model.entity.StudentGraduateExamScoreEntity;
import com.xiaotiyun.school.manager.model.req.StudentGraduateExamScoreDeatilSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateExamScorePageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateExamScoreSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateExamScoreUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateExamScorePageResModel;
import com.xiaotiyun.school.manager.service.StudentGraduateExamScoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentGraduateExamScoreServiceImpl extends ServiceImpl<StudentGraduateExamScoreMapper, StudentGraduateExamScoreEntity> implements StudentGraduateExamScoreService {

    @Override
    public PageInfo<StudentGraduateExamScorePageResModel> page(StudentGraduateExamScorePageReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<StudentGraduateExamScoreDTO> list = this.getBaseMapper().scoreList(reqModel.getTaskId());
        List<StudentGraduateExamScorePageResModel> resList = list.stream().map((StudentGraduateExamScoreDTO dto) -> {
            StudentGraduateExamScorePageResModel resModel = new StudentGraduateExamScorePageResModel();
            BeanUtils.copyProperties(dto, resModel);
            if (dto.getDisplayNameType().equals(StudetDisplayNameTypeEnum.ENGLISH.getCode()) && StringUtils.isNotBlank(dto.getEnglishName())) {
                resModel.setStudentName(dto.getEnglishName());
            } else {
                resModel.setStudentName(dto.getChineseName());
            }
            return resModel;
        }).collect(Collectors.toList());
        return new PageInfo<>(resList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.GRADUATION_EXAM)
    public List<StudentGraduateExamScoreEntity> save(StudentGraduateExamScoreSaveReqModel reqModel) {
        List<StudentGraduateExamScoreEntity> scores = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(reqModel.getScoreList())) {
            reqModel.getScoreList().forEach((StudentGraduateExamScoreDeatilSaveReqModel score) -> {
                StudentGraduateExamScoreEntity entity = BeanConvertUtil.convert(score, StudentGraduateExamScoreEntity.class);
                entity.setTaskId(reqModel.getTaskId());
                scores.add(entity);
            });
            if (CollectionUtils.isNotEmpty(scores)) {
                this.saveBatch(scores);
            }
        }
        return scores;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.GRADUATION_EXAM)
    public StudentGraduateExamScoreEntity update(Long id, StudentGraduateExamScoreUpdateReqModel reqModel) {
        StudentGraduateExamScoreEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.GRADUATE_EXAM_SCORE_NOT_EXISTS);
        }
        // 使用BeanUtils替代BeanConvertUtil
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        StudentGraduateExamScoreEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.GRADUATE_EXAM_SCORE_NOT_EXISTS);
        }
        this.removeById(id);
    }

    @Override
    public List<StudentGraduateExamPartakeCountDTO> partakeCountList(List<Long> taskIds) {
        return this.getBaseMapper().partakeCountList(taskIds);
    }

    @Override
    public List<Long> partakeTaskList(String studentInfo) {
        return this.getBaseMapper().partakeTaskList(studentInfo);
    }

    @Override
    public List<StudentGraduateExamScoreDetailDTO> scoreDetailList(Long studentId, List<Long> taskIds) {
        return this.getBaseMapper().scoreDetailList(studentId, taskIds);
    }
}