package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.enums.DataBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.DataOperationTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.StudentUsuallyScoreMapper;
import com.xiaotiyun.school.manager.basic.enums.StudetDisplayNameTypeEnum;
import com.xiaotiyun.school.manager.model.dto.StudentUsuallyPartakeCountDTO;
import com.xiaotiyun.school.manager.model.dto.StudentUsuallyScoreDTO;
import com.xiaotiyun.school.manager.model.dto.StudentUsuallyScoreDetailDTO;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.entity.StudentUsuallyScoreEntity;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.StudentUsuallyScoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentUsuallyScoreServiceImpl extends ServiceImpl<StudentUsuallyScoreMapper, StudentUsuallyScoreEntity> implements StudentUsuallyScoreService {

    @Override
    public PageInfo<StudentUsuallyScorePageResModel> page(StudentUsuallyScorePageReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<StudentUsuallyScoreDTO> list = this.getBaseMapper().scoreList(reqModel.getTaskId());
        List<StudentUsuallyScorePageResModel> resList = list.stream().map((StudentUsuallyScoreDTO dto) -> {
            StudentUsuallyScorePageResModel resModel = new StudentUsuallyScorePageResModel();
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
    public PageInfo<StudentUsuallyScoreResModel> pageByStudent(StudentUsuallyScoreReqModel reqModel) {
        // 获取学生信息
        StudentEntity nowStudent = (StudentEntity) StpUtil.getSession().get("student");
        if (nowStudent == null){
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        reqModel.setStudentId(nowStudent.getId());
        // 分页查询
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<StudentUsuallyScoreResModel> list = this.getBaseMapper().getScoreListByStudent(reqModel);
        // 转换返回结果
        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.DAILY_GRADE)
    public List<StudentUsuallyScoreEntity> save(StudentUsuallyScoreSaveReqModel reqModel) {
        List<StudentUsuallyScoreEntity> scores = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(reqModel.getScoreList())) {
            reqModel.getScoreList().forEach((StudentUsuallyScoreDeatilSaveReqModel score) -> {
                StudentUsuallyScoreEntity entity = BeanConvertUtil.convert(score, StudentUsuallyScoreEntity.class);
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
    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.DAILY_GRADE)
    public StudentUsuallyScoreEntity update(Long id, StudentUsuallyScoreUpdateReqModel reqModel) {
        StudentUsuallyScoreEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.EXAM_SCORE_NOT_EXISTS);
        }
        // 使用BeanUtils替代BeanConvertUtil
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        StudentUsuallyScoreEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.EXAM_SCORE_NOT_EXISTS);
        }
        this.removeById(id);
    }

    @Override
    public List<StudentUsuallyPartakeCountDTO> partakeCountList(List<Long> taskIds) {
        return this.getBaseMapper().partakeCountList(taskIds);
    }

    @Override
    public List<Long> partakeTaskList(String studentInfo) {
        return this.getBaseMapper().partakeTaskList(studentInfo);
    }

    @Override
    public List<StudentUsuallyScoreDetailDTO> scoreDetailList(Long studentId, List<Long> taskIds) {
        return this.getBaseMapper().scoreDetailList(studentId, taskIds);
    }

    @Override
    public List<StudentPeriodScoreResModel> getUsuallyScores(Long classId, Long periodId) {
        // 调用mapper查询学生平时成绩
        List<StudentPeriodScoreResponseModel> scoreResponses = this.getBaseMapper().getStudentPeriodScores(classId, periodId);

        // 组装返回结果
        Map<Long, Map<Long, StudentPeriodScoreResModel>> resultMap = new HashMap<>();
        for (StudentPeriodScoreResponseModel response : scoreResponses) {
            Map<Long, StudentPeriodScoreResModel> scoreResModelMap = resultMap.get(response.getStudentId());
            if (scoreResModelMap == null) {
                scoreResModelMap = new HashMap<>();
                StudentPeriodScoreResModel resModel = new StudentPeriodScoreResModel();
                resModel.setStudentId(response.getStudentId());
                resModel.setPeriodId(response.getPeriodId());
                resModel.setSubjectScores(new ArrayList<>()); // 初始化科目成绩列表
                scoreResModelMap.put(response.getPeriodId(), resModel);
                resultMap.put(response.getStudentId(), scoreResModelMap);
            } else {
                StudentPeriodScoreResModel resModel = scoreResModelMap.get(response.getPeriodId());
                if (resModel == null) {
                    resModel = new StudentPeriodScoreResModel();
                    resModel.setStudentId(response.getStudentId());
                    resModel.setPeriodId(response.getPeriodId());
                    resModel.setSubjectScores(new ArrayList<>()); // 初始化科目成绩列表
                    scoreResModelMap.put(response.getPeriodId(), resModel);
                }
            }

            // 创建科目成绩对象并添加到列表
            StudentSubjectScoreResModel subjectScore = new StudentSubjectScoreResModel();
            subjectScore.setSubjectId(response.getSubjectId());
            subjectScore.setScore(response.getScore());
            subjectScore.setTypeId(response.getTypeId());
            scoreResModelMap.get(response.getPeriodId()).getSubjectScores().add(subjectScore);
        }

        // resultMap转list
        List<StudentPeriodScoreResModel> resModels = new ArrayList<>(100);
        Collection<Map<Long, StudentPeriodScoreResModel>> values = resultMap.values();
        for (Map<Long, StudentPeriodScoreResModel> value : values) {
            Collection<StudentPeriodScoreResModel> values1 = value.values();
            resModels.addAll(values1);
        }

        return resModels;
    }
}