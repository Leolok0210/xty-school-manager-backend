package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.DataBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.DataOperationTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.StudetDisplayNameTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.StudentExamScoreMapper;
import com.xiaotiyun.school.manager.model.dto.StudentExamPartakeCountDTO;
import com.xiaotiyun.school.manager.model.dto.StudentExamScoreDTO;
import com.xiaotiyun.school.manager.model.dto.StudentExamScoreDetailDTO;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.entity.StudentExamScoreEntity;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.StudentExamScoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentExamScoreServiceImpl extends ServiceImpl<StudentExamScoreMapper, StudentExamScoreEntity> implements StudentExamScoreService {

    @Resource
    private LanguageUtil languageUtil;

    @Override
    public PageInfo<StudentExamScorePageResModel> page(StudentExamScorePageReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<StudentExamScoreDTO> list = this.getBaseMapper().scoreList(reqModel.getTaskId());
        List<StudentExamScorePageResModel> resList = list.stream().map((StudentExamScoreDTO dto) -> {
            StudentExamScorePageResModel resModel = new StudentExamScorePageResModel();
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
    public PageInfo<StudentExamScoreResModel> pageByStudent(StudentExamScoreReqModel reqModel) {
        // 获取学生信息
        StudentEntity nowStudent = (StudentEntity) StpUtil.getSession().get("student");
        if (nowStudent == null){
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        reqModel.setStudentId(nowStudent.getId());
        // 分页查询
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<StudentExamScoreResModel> list = this.getBaseMapper().scoreListByStudent(reqModel);
        // 转换返回结果
        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.EXAM_GRADE)
    public List<StudentExamScoreEntity> save(StudentExamScoreSaveReqModel reqModel) {
        List<StudentExamScoreEntity> scores = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(reqModel.getScoreList())) {
            reqModel.getScoreList().forEach((StudentExamScoreDeatilSaveReqModel score) -> {
                StudentExamScoreEntity entity = BeanConvertUtil.convert(score, StudentExamScoreEntity.class);
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
    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.EXAM_GRADE)
    public StudentExamScoreEntity update(Long id, StudentExamScoreUpdateReqModel reqModel) {
        StudentExamScoreEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.EXAM_SCORE_NOT_EXISTS));
        }
        // 使用BeanUtils替代BeanConvertUtil
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        StudentExamScoreEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.EXAM_SCORE_NOT_EXISTS));
        }
        this.removeById(id);
    }

    @Override
    public List<StudentExamPartakeCountDTO> partakeCountList(List<Long> taskIds) {
        return this.getBaseMapper().partakeCountList(taskIds);
    }

    @Override
    public List<Long> partakeTaskList(String studentInfo) {
        return this.getBaseMapper().partakeTaskList(studentInfo);
    }

    @Override
    public List<StudentExamScoreDetailDTO> scoreDetailList(Long studentId, List<Long> taskIds) {
        return this.getBaseMapper().scoreDetailList(studentId, taskIds);
    }

    @Override
    public List<StudentPeriodScoreResModel> getStudentPeriodScores(Long classId, Long periodId) {

        // 调用mapper查询学生考试成绩
        List<StudentPeriodScoreResponseModel> scoreResponses = this.getBaseMapper().getStudentPeriodScores(classId, periodId);

        // 组装返回结果
            Map<Long, Map<Long,StudentPeriodScoreResModel>> resultMap = new HashMap<>();
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
            }else {
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
            scoreResModelMap.get(response.getPeriodId()).getSubjectScores().add(subjectScore);
        }
        //resultMap转list
        List<StudentPeriodScoreResModel> resModels = new ArrayList<>(100);
        Collection<Map<Long, StudentPeriodScoreResModel>> values = resultMap.values();
        for (Map<Long, StudentPeriodScoreResModel> value : values) {
            Collection<StudentPeriodScoreResModel> values1 = value.values();
            resModels.addAll(values1);
        }

        return resModels;
    }
}