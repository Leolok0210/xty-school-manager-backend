package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.enums.DataBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.DataOperationTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.StudetDisplayNameTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.dao.SemesterDao;
import com.xiaotiyun.school.manager.dao.StudentExamTaskMapper;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.dto.StudentExamPartakeCountDTO;
import com.xiaotiyun.school.manager.model.dto.StudentExamScoreDetailDTO;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentExamTaskServiceImpl extends ServiceImpl<StudentExamTaskMapper, StudentExamTaskEntity> implements StudentExamTaskService {
    private final StudentExamScoreService studentExamScoreService;
    private final GradeRecordSettingService gradeRecordSettingService;
    private final SysClassService sysClassService;
    private final GradeGroupService gradeGroupService;
    private final SemesterService semesterService;
    private final LanguageUtil languageUtil;
    private final SemesterDao semesterDao;

    private final UserAuthHelper userAuthHelper;

    @Override
    public PageInfo<StudentExamTaskPageResModel> page(StudentExamPageReqModel reqModel) {
        List<Long> taskIds = new ArrayList<>();
        if (StringUtils.isNotBlank(reqModel.getStudentInfo())) {
            //学生信息不为空，查询学生参与的考试id
            taskIds = studentExamScoreService.partakeTaskList(reqModel.getStudentInfo());
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
        List<StudentExamTaskPageResModel> list = this.getBaseMapper().page(taskIds, reqModel);
        //获取参与人数
        if (CollectionUtils.isNotEmpty(list)) {
            taskIds = list.stream().map(StudentExamTaskPageResModel::getId).collect(Collectors.toList());
            List<StudentExamPartakeCountDTO> partakeCountDTOS = studentExamScoreService.partakeCountList(taskIds);
            Map<Long, StudentExamPartakeCountDTO> partakeCountDTOMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(partakeCountDTOS)) {
                partakeCountDTOMap = partakeCountDTOS.stream().collect(Collectors.toMap(StudentExamPartakeCountDTO::getTaskId, partakeCountDTO -> partakeCountDTO));
            }
            for (StudentExamTaskPageResModel resModel : list) {
                StudentExamPartakeCountDTO partakeCountDTO = partakeCountDTOMap.get(resModel.getId());
                if (partakeCountDTO != null) {
                    resModel.setPartakeCount(partakeCountDTO.getPartakeCount());
                }
            }
        }
        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.EXAM_TASK)
    public StudentExamTaskEntity save(StudentExamTaskSaveReqModel reqModel) {
        StudentExamTaskEntity entity = BeanConvertUtil.convert(reqModel, StudentExamTaskEntity.class);
        this.save(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.EXAM_TASK)
    public StudentExamTaskEntity update(Long id, StudentExamTaskSaveReqModel reqModel) {
        StudentExamTaskEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.RECORD_NOT_EXIST));
        }
        // 使用BeanUtils替代BeanConvertUtil
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
        return entity;
    }

    @Override
    public StudentExamTaskResModel info(Long id) {
        StudentExamTaskEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.RECORD_NOT_EXIST));
        }
        return BeanConvertUtil.convert(entity, StudentExamTaskResModel.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        StudentExamTaskEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.RECORD_NOT_EXIST));
        }
        this.removeById(id);
    }

    @Override
    public String check(Long schoolId, StudentExamTaskCheckReqModel reqModel) {
        return this.getBaseMapper().check(schoolId, reqModel);
    }


    @Override
    public List<StudentExamScoreCheckResModel> scoreCheck(StudentExamScoreCheckReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if(CollectionUtils.isEmpty(classIds))
            {
                return new ArrayList<>();
            }
        }
        List<StudentExamScoreCheckResModel> result = new ArrayList<>();
        StudentExamPageReqModel studentExamPageReqModel = new StudentExamPageReqModel();
        BeanUtils.copyProperties(reqModel, studentExamPageReqModel);
        studentExamPageReqModel.setClassIds(classIds);
        List<StudentExamTaskPageResModel> list = this.getBaseMapper().page(null, studentExamPageReqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> taskIds = list.stream().map(StudentExamTaskPageResModel::getId).collect(Collectors.toList());
            //获取学生成绩
            List<StudentExamScoreDetailDTO> scoreEntities = studentExamScoreService.scoreDetailList(null, taskIds);
            if (CollectionUtils.isNotEmpty(scoreEntities)) {
                Map<Long, List<StudentExamScoreDetailDTO>> studentScoreMap = scoreEntities.stream().collect(Collectors.groupingBy(StudentExamScoreDetailDTO::getStudentId));
                if (reqModel.getSubjectId() != null && reqModel.getSubjectId() > 0) {
                    //按科目查询
                    for (Map.Entry<Long, List<StudentExamScoreDetailDTO>> studentScoreList : studentScoreMap.entrySet()) {
                        //按科目聚合计算平均分
                        StudentExamScoreCheckResModel resModel = new StudentExamScoreCheckResModel();
                        resModel.setStudentId(studentScoreList.getKey());
                        List<StudentExamScoreDetailDTO> scoreDetailDTOS = studentScoreList.getValue();
                        resModel.setSeatNo(scoreDetailDTOS.get(0).getSeatNo());
                        if (scoreDetailDTOS.get(0).getDisplayNameType().equals(StudetDisplayNameTypeEnum.ENGLISH.getCode()) && StringUtils.isNotBlank(scoreDetailDTOS.get(0).getEnglishName())) {
                            resModel.setStudentName(scoreDetailDTOS.get(0).getEnglishName());
                        } else {
                            resModel.setStudentName(scoreDetailDTOS.get(0).getChineseName());
                        }
                        Map<Long, List<StudentExamScoreDetailDTO>> subjectScoreMap = scoreDetailDTOS.stream().collect(Collectors.groupingBy(StudentExamScoreDetailDTO::getTaskId));
                        List<StudentExamScoreCheckDetailResModel> scoreList = new ArrayList<>();
                        for (Map.Entry<Long, List<StudentExamScoreDetailDTO>> subjectScoreList : subjectScoreMap.entrySet()) {
                            StudentExamScoreCheckDetailResModel model = new StudentExamScoreCheckDetailResModel();
                            model.setTaskName(subjectScoreList.getValue().get(0).getTaskName());
                            OptionalDouble average = subjectScoreList.getValue().stream().mapToInt(StudentExamScoreDetailDTO::getScore).average();
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
                    if (reqModel.getPeriodId() == null || reqModel.getPeriodId() == 0L ||
                            reqModel.getClassId() == null || reqModel.getClassId() == 0L) {
                        throw new BusinessException(LanguageConstants.PARAM_ERROR);
                    }
                    for (Map.Entry<Long, List<StudentExamScoreDetailDTO>> studentScoreList : studentScoreMap.entrySet()) {
                        //按任务聚合计算平均分
                        StudentExamScoreCheckResModel resModel = new StudentExamScoreCheckResModel();
                        resModel.setStudentId(studentScoreList.getKey());
                        List<StudentExamScoreDetailDTO> scoreDetailDTOS = studentScoreList.getValue();
                        resModel.setSeatNo(scoreDetailDTOS.get(0).getSeatNo());
                        if (scoreDetailDTOS.get(0).getDisplayNameType().equals(StudetDisplayNameTypeEnum.ENGLISH.getCode()) && StringUtils.isNotBlank(scoreDetailDTOS.get(0).getEnglishName())) {
                            resModel.setStudentName(scoreDetailDTOS.get(0).getEnglishName());
                        } else {
                            resModel.setStudentName(scoreDetailDTOS.get(0).getChineseName());
                        }
                        Map<Long, List<StudentExamScoreDetailDTO>> subjectScoreMap = scoreDetailDTOS.stream().collect(Collectors.groupingBy(StudentExamScoreDetailDTO::getSubjectId));
                        List<StudentExamScoreCheckDetailResModel> scoreList = new ArrayList<>();
                        for (Map.Entry<Long, List<StudentExamScoreDetailDTO>> subjectScoreList : subjectScoreMap.entrySet()) {
                            StudentExamScoreCheckDetailResModel model = new StudentExamScoreCheckDetailResModel();
                            model.setSubjectName(subjectScoreList.getValue().get(0).getSubjectName());
                            OptionalDouble average = subjectScoreList.getValue().stream().mapToInt(StudentExamScoreDetailDTO::getScore).average();
                            if (average.isPresent()) {
                                model.setScore((int) average.getAsDouble());
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
    public List<StudentExamScoreAnalysisResModel> scoreAnalysis(StudentExamScoreAnalysisReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if(CollectionUtils.isEmpty(classIds))
            {
                return new ArrayList<>();
            }
        }
        List<StudentExamScoreAnalysisResModel> result = new ArrayList<>();
        StudentExamPageReqModel studentExamPageReqModel = new StudentExamPageReqModel();
        BeanUtils.copyProperties(reqModel, studentExamPageReqModel);
        studentExamPageReqModel.setClassIds(classIds);
        List<StudentExamTaskPageResModel> list = this.getBaseMapper().page(null, studentExamPageReqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> taskIds = list.stream().map(StudentExamTaskPageResModel::getId).collect(Collectors.toList());
            //获取学生成绩
            List<StudentExamScoreDetailDTO> scoreEntities = studentExamScoreService.scoreDetailList(null, taskIds);
            if (CollectionUtils.isNotEmpty(scoreEntities)) {
                Map<Long, List<StudentExamScoreDetailDTO>> studentScoreMap = scoreEntities.stream().collect(Collectors.groupingBy(StudentExamScoreDetailDTO::getStudentId));
                for (Map.Entry<Long, List<StudentExamScoreDetailDTO>> studentScoreList : studentScoreMap.entrySet()) {
                    //聚合计算平均分
                    StudentExamScoreAnalysisResModel resModel = new StudentExamScoreAnalysisResModel();
                    resModel.setStudentId(studentScoreList.getKey());
                    List<StudentExamScoreDetailDTO> scoreDetailDTOS = studentScoreList.getValue();
                    if (scoreDetailDTOS.get(0).getDisplayNameType().equals(StudetDisplayNameTypeEnum.ENGLISH.getCode()) && StringUtils.isNotBlank(scoreDetailDTOS.get(0).getEnglishName())) {
                        resModel.setStudentName(scoreDetailDTOS.get(0).getEnglishName());
                    } else {
                        resModel.setStudentName(scoreDetailDTOS.get(0).getChineseName());
                    }
                    OptionalDouble average = scoreDetailDTOS.stream().mapToInt(StudentExamScoreDetailDTO::getScore).average();
                    if (average.isPresent()) {
                        resModel.setScore((int) average.getAsDouble());
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
        StudentExamPageReqModel studentExamPageReqModel = new StudentExamPageReqModel();
        BeanUtils.copyProperties(reqModel, studentExamPageReqModel);
        List<StudentExamTaskPageResModel> list = this.getBaseMapper().page(null, studentExamPageReqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> taskIds = list.stream().map(StudentExamTaskPageResModel::getId).collect(Collectors.toList());
            //获取学生成绩
            List<StudentExamScoreDetailDTO> scoreEntities = studentExamScoreService.scoreDetailList(reqModel.getStudentId(), taskIds);
            if (CollectionUtils.isNotEmpty(scoreEntities)) {
                for (StudentExamScoreDetailDTO scoreEntity : scoreEntities) {
                    StudentScorePageResModel resModel = new StudentScorePageResModel();
                    BeanUtils.copyProperties(scoreEntity, resModel);
                    result.add(resModel);
                }
            }
        }
        return result;
    }

    @Override
    public void scoreAdd(StudentExamScoreSaveReqModel reqModel) {
        StudentExamTaskEntity task = this.getById(reqModel.getTaskId());
        if (task != null) {
            SysClass sysClass = sysClassService.getSysClassById(task.getClassId());
            if (sysClass != null) {
                //获取成绩录入设定
                GradeRecordSettingResModel settingResModel = gradeRecordSettingService.getSetting(task.getSchoolId(), sysClass.getSid());
                if (settingResModel != null && settingResModel.getClassSettings() != null) {
                    long count = settingResModel.getClassSettings().stream().filter(classSettingItem -> classSettingItem.getClassId().equals(sysClass.getId()) && classSettingItem.getCanRecordExam()).count();
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
                studentExamScoreService.save(reqModel);
            }
        }
    }

    @Override
    public boolean hasScore(Long periodId) {
        if (periodId == null) {
            return false;
        }
        // 1. 先查询该学期下的任务ID
        LambdaQueryWrapper<StudentExamTaskEntity> taskQueryWrapper = new LambdaQueryWrapper<>();
        taskQueryWrapper.select(StudentExamTaskEntity::getId)
                .eq(StudentExamTaskEntity::getPeriodId, periodId);

        List<Long> taskIds = this.baseMapper.selectList(taskQueryWrapper)
                .stream()
                .map(StudentExamTaskEntity::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(taskIds)) {
            return false;
        }
        // 查询是否存在成绩记录
        LambdaQueryWrapper<StudentExamScoreEntity> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.in(StudentExamScoreEntity::getTaskId, taskIds);
        return studentExamScoreService.count(scoreWrapper) > 0;
    }
}