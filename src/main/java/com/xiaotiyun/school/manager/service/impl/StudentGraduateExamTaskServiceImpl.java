package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.enums.DataBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.DataOperationTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.SemesterDao;
import com.xiaotiyun.school.manager.dao.StudentGraduateExamTaskMapper;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.dto.StudentGraduateExamPartakeCountDTO;
import com.xiaotiyun.school.manager.model.dto.StudentGraduateExamScoreDetailDTO;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.StudentGraduateExamPageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateExamScoreSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateExamTaskSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentScoreReqModel;
import com.xiaotiyun.school.manager.model.res.GradeRecordSettingResModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateExamTaskPageResModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateExamTaskResModel;
import com.xiaotiyun.school.manager.model.res.StudentScorePageResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentGraduateExamTaskServiceImpl extends ServiceImpl<StudentGraduateExamTaskMapper, StudentGraduateExamTaskEntity> implements StudentGraduateExamTaskService {
    private final StudentGraduateExamScoreService studentGraduateExamScoreService;
    private final GradeRecordSettingService gradeRecordSettingService;
    private final SysClassService sysClassService;
    private final GradeGroupService gradeGroupService;
    private final SemesterService semesterService;
    private final LanguageUtil languageUtil;
    private final SemesterDao semesterDao;

    private final UserAuthHelper userAuthHelper;


    @Override
    public PageInfo<StudentGraduateExamTaskPageResModel> page(StudentGraduateExamPageReqModel reqModel) {
        List<Long> taskIds = new ArrayList<>();
        if (StringUtils.isNotBlank(reqModel.getStudentInfo())) {
            //学生信息不为空，查询学生参与的考试id
            taskIds = studentGraduateExamScoreService.partakeTaskList(reqModel.getStudentInfo());
            if (CollectionUtils.isEmpty(taskIds)) {
                return new PageInfo<>(new ArrayList<>());
            }
        }
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if(CollectionUtils.isEmpty(classIds))
            {
                return new PageInfo<>(new ArrayList<>());
            }
            reqModel.setClassIds(classIds);
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<StudentGraduateExamTaskPageResModel> list = this.getBaseMapper().page(taskIds, reqModel);
        //获取参与人数
        if (CollectionUtils.isNotEmpty(list)) {
            taskIds = list.stream().map(StudentGraduateExamTaskPageResModel::getId).collect(Collectors.toList());
            List<StudentGraduateExamPartakeCountDTO> partakeCountDTOS = studentGraduateExamScoreService.partakeCountList(taskIds);
            Map<Long, StudentGraduateExamPartakeCountDTO> partakeCountDTOMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(partakeCountDTOS)) {
                partakeCountDTOMap = partakeCountDTOS.stream().collect(Collectors.toMap(StudentGraduateExamPartakeCountDTO::getTaskId, partakeCountDTO -> partakeCountDTO));
            }
            for (StudentGraduateExamTaskPageResModel resModel : list) {
                StudentGraduateExamPartakeCountDTO partakeCountDTO = partakeCountDTOMap.get(resModel.getId());
                if (partakeCountDTO != null) {
                    resModel.setPartakeCount(partakeCountDTO.getPartakeCount());
                }
            }
        }
        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.GRADUATE_EXAM_TASK)
    public StudentGraduateExamTaskEntity save(StudentGraduateExamTaskSaveReqModel reqModel) {
        StudentGraduateExamTaskEntity entity = BeanConvertUtil.convert(reqModel, StudentGraduateExamTaskEntity.class);
        this.save(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.GRADUATE_EXAM_TASK)
    public StudentGraduateExamTaskEntity update(Long id, StudentGraduateExamTaskSaveReqModel reqModel) {
        StudentGraduateExamTaskEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        // 使用BeanUtils替代BeanConvertUtil
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
        return entity;
    }

    @Override
    public StudentGraduateExamTaskResModel info(Long id) {
        StudentGraduateExamTaskEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        return BeanConvertUtil.convert(entity, StudentGraduateExamTaskResModel.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        StudentGraduateExamTaskEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        this.removeById(id);
    }

    @Override
    public List<StudentScorePageResModel> studentScore(StudentScoreReqModel reqModel) {
        List<StudentScorePageResModel> result = new ArrayList<>();
        StudentGraduateExamPageReqModel studentGraduateExamPageReqModel = new StudentGraduateExamPageReqModel();
        BeanUtils.copyProperties(reqModel, studentGraduateExamPageReqModel);
        List<StudentGraduateExamTaskPageResModel> list = this.getBaseMapper().page(null, studentGraduateExamPageReqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> taskIds = list.stream().map(StudentGraduateExamTaskPageResModel::getId).collect(Collectors.toList());
            //获取学生成绩
            List<StudentGraduateExamScoreDetailDTO> scoreEntities = studentGraduateExamScoreService.scoreDetailList(reqModel.getStudentId(), taskIds);
            if (CollectionUtils.isNotEmpty(scoreEntities)) {
                for (StudentGraduateExamScoreDetailDTO scoreEntity : scoreEntities) {
                    StudentScorePageResModel resModel = new StudentScorePageResModel();
                    BeanUtils.copyProperties(scoreEntity, resModel);
                    result.add(resModel);
                }
            }
        }
        return result;
    }

    @Override
    public void scoreAdd(StudentGraduateExamScoreSaveReqModel reqModel) {
        StudentGraduateExamTaskEntity task = this.getById(reqModel.getTaskId());
        if (task != null) {
            SysClass sysClass = sysClassService.getSysClassById(task.getClassId());
            if (sysClass != null) {
                //获取成绩录入设定
                GradeRecordSettingResModel settingResModel = gradeRecordSettingService.getSetting(task.getSchoolId(), sysClass.getSid());
                if (settingResModel != null && settingResModel.getClassSettings() != null) {
                    long count = settingResModel.getClassSettings().stream().filter(classSettingItem -> classSettingItem.getClassId().equals(sysClass.getId()) && classSettingItem.getCanRecordGraduation()).count();
                    if (count > 0) {
                        SemesterEntity semester = semesterDao.selectById(task.getPeriodId());
                        String errorMessage = null;
                        for (GradeRecordSettingResModel.TimeSettingItem timeSetting : settingResModel.getTimeSettings()) {
                            if (timeSetting.getDepartment().equals(sysClass.getDepartment()) && timeSetting.getSemesterId().equals(task.getPeriodId())) {
                                if (!timeSetting.getStartTime().isBefore(LocalDateTime.now()) || !timeSetting.getEndTime().isAfter(LocalDateTime.now())) {
                                    if (semester != null) {
                                        errorMessage = sysClass.getSid() + semester.getName();
                                    } else {
                                        errorMessage = sysClass.getSid();
                                    }
                                    errorMessage = String.format(languageUtil.getMessage(LanguageConstants.SCORE_INPUT_TIME_RANGE),errorMessage) + DateUtils.formatDateToString(timeSetting.getStartTime(), languageUtil.getMessage(LanguageConstants.YEAR_MONTH_DAY)) + "-" + DateUtils.formatDateToString(timeSetting.getEndTime(), languageUtil.getMessage(LanguageConstants.YEAR_MONTH_DAY));
                                }
                                break;
                            }
                        }
                        if (StringUtils.isNotBlank(errorMessage)) {
                            throw new BusinessMessageException(errorMessage);
                        }
                    }
                }
                studentGraduateExamScoreService.save(reqModel);
            }
        }
    }

    @Override
    public boolean hasScore(Long periodId) {
        if (periodId == null) {
            return false;
        }
        // 1. 先查询该学期下的任务ID
        LambdaQueryWrapper<StudentGraduateExamTaskEntity> taskQueryWrapper = new LambdaQueryWrapper<>();
        taskQueryWrapper.select(StudentGraduateExamTaskEntity::getId)
                .eq(StudentGraduateExamTaskEntity::getPeriodId, periodId);

        List<Long> taskIds = this.baseMapper.selectList(taskQueryWrapper)
                .stream()
                .map(StudentGraduateExamTaskEntity::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(taskIds)) {
            return false;
        }
        // 查询是否存在成绩记录
        LambdaQueryWrapper<StudentGraduateExamScoreEntity> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.in(StudentGraduateExamScoreEntity::getTaskId, taskIds);
        return studentGraduateExamScoreService.count(scoreWrapper) > 0;
    }


}