package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.enums.DataBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.DataOperationTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.dao.ClassPerformanceDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.dto.ConditionGroupDTO;
import com.xiaotiyun.school.manager.model.dto.QualityEvaluationCommentRuleDTO;
import com.xiaotiyun.school.manager.model.entity.ClassPerformance;
import com.xiaotiyun.school.manager.model.entity.QualityEvaluationComment;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.model.req.ClassPerformanceQueryReqModel;
import com.xiaotiyun.school.manager.model.req.StudentQualityScoreQueryReqModel;
import com.xiaotiyun.school.manager.model.req.UserRewardCountReqModel;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;

@Service
public class ClassPerformanceServiceImpl extends ServiceImpl<ClassPerformanceDao, ClassPerformance> implements ClassPerformanceService {

    @Autowired
    private ClassPerformanceDao classPerformanceDao;


    @Autowired
    private StudentService studentService;


    @Autowired
    private StudentQualityScoreService studentQualityScoreService;


    @Autowired
    private UserRewardService userRewardService;


    @Autowired
    private QualityEvaluationCommentService qualityEvaluationCommentService;


    @Autowired
    private QualityCommentRuleService qualityCommentRuleService;

    @Autowired
    private QualityEvaluationService qualityEvaluationService;


    @Autowired
    private ExportFileHandler exportFileHandler;


    @Autowired
    private SysClassService sysClassService;

    @Autowired
    private StudentAttendanceService studentAttendanceService;

    @Autowired
    private StudentLeaveService studentLeaveService;

    @Autowired
    private LanguageUtil languageUtil;


    @Autowired
    private UserAuthHelper userAuthHelper;

    @Override
    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.CLASS_BEHAVIOR)
    public List<ClassPerformance> createClassPerformances(List<ClassPerformance> classPerformances) {
        classPerformances.forEach(classPerformance -> {
            classPerformance.setCreateTime(LocalDateTime.now());
            classPerformance.setUpdateTime(LocalDateTime.now());
            classPerformance.setDeleted(0L);
        });
        saveBatch(classPerformances);
        return classPerformances;
    }

    @Override
    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.CLASS_BEHAVIOR)
    public ClassPerformance updateClassPerformance(ClassPerformance classPerformance) {
        classPerformance.setUpdateTime(LocalDateTime.now());
        updateById(classPerformance);
        return classPerformance;
    }

    @Override
    public void deleteClassPerformance(Long id) {
        //逻辑删除
        removeById(id);
    }

    @Override
    public ClassPerformance getClassPerformanceById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public PageInfo<ClassPerformanceDetailResModel> getClassPerformanceList(ClassPerformanceQueryReqModel reqModel) {
        Long studentId = null;
        if(StringUtils.isNoneBlank(reqModel.getStudentName()))
        {
            //根据学校id和学生姓名查询
            StudentResModel studentIdByNameAndSchoolId = studentService.getStudentIdByNameAndSchoolId(reqModel.getStudentName(), reqModel.getSchoolId());
            if(studentIdByNameAndSchoolId != null){
                studentId = studentIdByNameAndSchoolId.getId();
            }else {
                PageInfo<ClassPerformanceDetailResModel> pageInfo = new PageInfo<>();
                pageInfo.setList(new ArrayList<>());
                return pageInfo;
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
        LambdaQueryWrapper<ClassPerformance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNoneBlank(reqModel.getSid()), ClassPerformance::getSid, reqModel.getSid())
                .eq(reqModel.getTerm() != null && reqModel.getTerm() > 0, ClassPerformance::getTerm, reqModel.getTerm())
                .eq(studentId != null && studentId > 0, ClassPerformance::getStudentId, studentId)
                .ge(reqModel.getStartDate() != null, ClassPerformance::getClassDate, reqModel.getStartDate())
                .le(reqModel.getEndDate() != null, ClassPerformance::getClassDate, reqModel.getEndDate())
                .eq(reqModel.getSchoolId() != null && reqModel.getSchoolId() > 0, ClassPerformance::getSchoolId, reqModel.getSchoolId())
                .eq(reqModel.getClassId() != null && reqModel.getClassId() > 0, ClassPerformance::getClassId, reqModel.getClassId())
                .in(classIds != null && !classIds.isEmpty(), ClassPerformance::getClassId, classIds)
                .orderByDesc(BaseEntity::getCreateTime);
        List<ClassPerformance> classPerformances = this.baseMapper.selectList(wrapper);
        PageInfo<ClassPerformance> pageInfo = new PageInfo<>(classPerformances);
        List<ClassPerformanceDetailResModel> classPerformanceDetailResModels = pageInfo.getList().stream()
                .map(item -> {
                    ClassPerformanceDetailResModel resModel = new ClassPerformanceDetailResModel();
                    BeanUtils.copyProperties(item, resModel);
                    return resModel;
                }).collect(Collectors.toList());
        PageInfo<ClassPerformanceDetailResModel> classPerformanceDetailResModelPageInfo = new PageInfo<>(classPerformanceDetailResModels);
        classPerformanceDetailResModelPageInfo.setTotal(pageInfo.getTotal());
        classPerformanceDetailResModelPageInfo.setPages(pageInfo.getPages());
        classPerformanceDetailResModelPageInfo.setList(classPerformanceDetailResModels);
        return classPerformanceDetailResModelPageInfo;
    }

    @Override
    public PageInfo<StudentQualityScoreModel> getClassPerformanceCheckList(StudentQualityScoreQueryReqModel reqModel) {
        PageInfo<StudentQualityScoreModel> result = new PageInfo<>();
        PageInfo<StudentQualityScoreListResModel> scoreList = studentQualityScoreService.getStudentQualityScoreList(reqModel);
        result.setPages(scoreList.getPages());
        result.setTotal(scoreList.getTotal());
        if(scoreList.getList().isEmpty())
        {
            result.setList(new ArrayList<>());
            return result;
        }
        //查询奖励、惩罚记录
        List<Long> studentIds = scoreList.getList().stream().map(StudentQualityScoreListResModel::getStudentId).collect(Collectors.toList());
        UserRewardCountReqModel countReqModel = new UserRewardCountReqModel();
        countReqModel.setSchoolId(reqModel.getSchoolId());
        countReqModel.setSid(reqModel.getSid());
        countReqModel.setStudentIds(studentIds);
        countReqModel.setType(1);
        List<UserRewardCountResModel> rewardCount = userRewardService.getUserRewardCount(countReqModel);
        //转map
        Map<Long, UserRewardCountResModel> rewardCountMap = rewardCount.stream().collect(Collectors.toMap(UserRewardCountResModel::getStudentId, item -> item));
        countReqModel.setType(2);
        List<UserRewardCountResModel> weakRewardCount = userRewardService.getUserRewardCount(countReqModel);
        //转map
        Map<Long, UserRewardCountResModel> weakRewardCountMap = weakRewardCount.stream().collect(Collectors.toMap(UserRewardCountResModel::getStudentId, item -> item));
        List<StudentQualityScoreListResModel> list = scoreList.getList();
        List<QualityEvaluationComment> qualityEvaluationComments = qualityEvaluationCommentService.getByClassIdAndStudentIds(reqModel.getClassId(), studentIds);
        //转map
        Map<Long, QualityEvaluationComment> qualityEvaluationCommentMap = qualityEvaluationComments.stream().collect(Collectors.toMap(QualityEvaluationComment::getStudentId, item -> item));
        
        // 查询迟到统计
        List<StudentLateCountResModel> lateCountList = studentAttendanceService.getStudentLateCount(
                reqModel.getSchoolId(), 
                reqModel.getClassId(), 
                studentIds
        );
        Map<Long, Integer> lateCountMap = lateCountList.stream()
                .collect(Collectors.toMap(
                        StudentLateCountResModel::getStudentId,
                        StudentLateCountResModel::getLateCount
                ));
                
        // 查询请假统计
        List<StudentLeaveStatisticsResModel> leaveStatsList = studentLeaveService.getStudentLeaveStatistics(
                reqModel.getSchoolId(),
                reqModel.getClassId(),
                studentIds
        );
        Map<Long, List<StudentLeaveStatisticsResModel>> leaveStatsMap = leaveStatsList.stream()
                .collect(Collectors.groupingBy(StudentLeaveStatisticsResModel::getStudentId));

        List<StudentQualityScoreModel> scoreModels = new ArrayList<>();
        List<QualityEvaluationCommentRuleDTO> ruleDTOS = qualityCommentRuleService.listRulesDTO(reqModel.getSchoolId());
        for (StudentQualityScoreListResModel studentQualityScoreListResModel : list) {
            StudentQualityScoreModel studentQualityScoreModel = new StudentQualityScoreModel();
            studentQualityScoreModel.setStudentId(studentQualityScoreListResModel.getStudentId());
            studentQualityScoreModel.setChineseName(studentQualityScoreListResModel.getStudentName());
            studentQualityScoreModel.setSeatNo(studentQualityScoreListResModel.getSeatNo());
            studentQualityScoreModel.setEnglishName(studentQualityScoreListResModel.getEnglishName());
            studentQualityScoreModel.setResModels(studentQualityScoreListResModel.getResModels());
            if(rewardCountMap.containsKey(studentQualityScoreListResModel.getStudentId()))
            {
                UserRewardCountResModel rewardCountResModel = rewardCountMap.get(studentQualityScoreListResModel.getStudentId());
                studentQualityScoreModel.setStrengths(rewardCountResModel.getMinRewardCount());
                studentQualityScoreModel.setMinorMerit(rewardCountResModel.getMidRewardCount());
                studentQualityScoreModel.setMajorMerit(rewardCountResModel.getMaxRewardCount());
            }
            if(weakRewardCountMap.containsKey(studentQualityScoreListResModel.getStudentId()))
            {
                UserRewardCountResModel rewardCountResModel = weakRewardCountMap.get(studentQualityScoreListResModel.getStudentId());
                studentQualityScoreModel.setMinorDemerit(rewardCountResModel.getMidRewardCount());
                studentQualityScoreModel.setMajorDemerit(rewardCountResModel.getMaxRewardCount());
                studentQualityScoreModel.setWeaknesses(rewardCountResModel.getMinRewardCount());
            }
            if(qualityEvaluationCommentMap.containsKey(studentQualityScoreListResModel.getStudentId()))
            {
                QualityEvaluationComment qualityEvaluationComment = qualityEvaluationCommentMap.get(studentQualityScoreListResModel.getStudentId());
                studentQualityScoreModel.setComments(qualityEvaluationComment.getComment());
                studentQualityScoreModel.setCommentsId(qualityEvaluationComment.getId());
            }else {
                //默认评语
                //如果全部成绩都为空，则不添加评语
                if(studentQualityScoreModel.getResModels().stream().allMatch(item -> item.getQualityProjectScore() == null) &&
                        studentQualityScoreModel.getMajorMerit() == null && studentQualityScoreModel.getMinorMerit() == null &&
                studentQualityScoreModel.getStrengths() == null && studentQualityScoreModel.getMinorDemerit() == null &&
                studentQualityScoreModel.getWeaknesses() == null && studentQualityScoreModel.getMajorDemerit() == null)
                {
                    studentQualityScoreModel.setComments(null);
                }else {
                    studentQualityScoreModel.setComments(getComment(ruleDTOS,studentQualityScoreModel));
                }
            }
            
            // 设置迟到次数
            studentQualityScoreModel.setLateCount(
                    lateCountMap.getOrDefault(studentQualityScoreListResModel.getStudentId(), null)
            );
            
            // 设置请假记录
            List<StudentLeaveStatisticsResModel> statsMapOrDefault = leaveStatsMap.getOrDefault(studentQualityScoreListResModel.getStudentId(), new ArrayList<>());
            for (StudentLeaveStatisticsResModel statsMapOrDefaultItem : statsMapOrDefault)
            {
                if (statsMapOrDefaultItem.getLeaveType() == 2) {
                    studentQualityScoreModel.setAbsenceCount(statsMapOrDefaultItem.getTotalPeriods());
                }
                if (statsMapOrDefaultItem.getLeaveType() == 1) {
                    studentQualityScoreModel.setLeaveRecords(statsMapOrDefaultItem.getTotalPeriods());
                }
            }
            
            scoreModels.add(studentQualityScoreModel);
        }
        result.setList(scoreModels);
        return result;
    }


    private List<StudentQualityScoreModel> getClassPerformanceCheckListExport(StudentQualityScoreQueryReqModel reqModel) {
        List<StudentQualityScoreListResModel> list = studentQualityScoreService.getStudentQualityScoreExportList(reqModel);
        //查询奖励、惩罚记录
        List<Long> studentIds = list.stream().map(StudentQualityScoreListResModel::getStudentId).collect(Collectors.toList());
        UserRewardCountReqModel countReqModel = new UserRewardCountReqModel();
        countReqModel.setSchoolId(reqModel.getSchoolId());
        countReqModel.setSid(reqModel.getSid());
        countReqModel.setStudentIds(studentIds);
        countReqModel.setType(1);
        List<UserRewardCountResModel> rewardCount = userRewardService.getUserRewardCount(countReqModel);
        //转map
        Map<Long, UserRewardCountResModel> rewardCountMap = rewardCount.stream().collect(Collectors.toMap(UserRewardCountResModel::getStudentId, item -> item));
        countReqModel.setType(2);
        List<UserRewardCountResModel> weakRewardCount = userRewardService.getUserRewardCount(countReqModel);
        //转map
        Map<Long, UserRewardCountResModel> weakRewardCountMap = weakRewardCount.stream().collect(Collectors.toMap(UserRewardCountResModel::getStudentId, item -> item));
        List<QualityEvaluationComment> qualityEvaluationComments = qualityEvaluationCommentService.getByClassIdAndStudentIds(reqModel.getClassId(), studentIds);
        //转map
        Map<Long, QualityEvaluationComment> qualityEvaluationCommentMap = qualityEvaluationComments.stream().collect(Collectors.toMap(QualityEvaluationComment::getStudentId, item -> item));

        // 查询迟到统计
        List<StudentLateCountResModel> lateCountList = studentAttendanceService.getStudentLateCount(
                reqModel.getSchoolId(),
                reqModel.getClassId(),
                studentIds
        );
        Map<Long, Integer> lateCountMap = lateCountList.stream()
                .collect(Collectors.toMap(
                        StudentLateCountResModel::getStudentId,
                        StudentLateCountResModel::getLateCount
                ));

        // 查询请假统计
        List<StudentLeaveStatisticsResModel> leaveStatsList = studentLeaveService.getStudentLeaveStatistics(
                reqModel.getSchoolId(),
                reqModel.getClassId(),
                studentIds
        );
        Map<Long, List<StudentLeaveStatisticsResModel>> leaveStatsMap = leaveStatsList.stream()
                .collect(Collectors.groupingBy(StudentLeaveStatisticsResModel::getStudentId));
        List<StudentQualityScoreModel> scoreModels = new ArrayList<>();
        for (StudentQualityScoreListResModel studentQualityScoreListResModel : list) {
            StudentQualityScoreModel studentQualityScoreModel = new StudentQualityScoreModel();
            studentQualityScoreModel.setStudentId(studentQualityScoreListResModel.getStudentId());
            studentQualityScoreModel.setChineseName(studentQualityScoreListResModel.getStudentName());
            studentQualityScoreModel.setSeatNo(studentQualityScoreListResModel.getSeatNo());
            studentQualityScoreModel.setEnglishName(studentQualityScoreListResModel.getEnglishName());
            studentQualityScoreModel.setResModels(studentQualityScoreListResModel.getResModels());
            if(rewardCountMap.containsKey(studentQualityScoreListResModel.getStudentId()))
            {
                UserRewardCountResModel rewardCountResModel = rewardCountMap.get(studentQualityScoreListResModel.getStudentId());
                studentQualityScoreModel.setStrengths(rewardCountResModel.getMinRewardCount());
                studentQualityScoreModel.setMinorMerit(rewardCountResModel.getMidRewardCount());
                studentQualityScoreModel.setMajorMerit(rewardCountResModel.getMaxRewardCount());
            }
            if(weakRewardCountMap.containsKey(studentQualityScoreListResModel.getStudentId()))
            {
                UserRewardCountResModel rewardCountResModel = weakRewardCountMap.get(studentQualityScoreListResModel.getStudentId());
                studentQualityScoreModel.setMinorDemerit(rewardCountResModel.getMidRewardCount());
                studentQualityScoreModel.setMajorDemerit(rewardCountResModel.getMaxRewardCount());
                studentQualityScoreModel.setWeaknesses(rewardCountResModel.getMinRewardCount());
            }
            if(qualityEvaluationCommentMap.containsKey(studentQualityScoreListResModel.getStudentId()))
            {
                QualityEvaluationComment qualityEvaluationComment = qualityEvaluationCommentMap.get(studentQualityScoreListResModel.getStudentId());
                studentQualityScoreModel.setComments(qualityEvaluationComment.getComment());
            }else {
                //默认评语
                //如果全部成绩都为空，则不添加评语
                if(studentQualityScoreModel.getResModels().stream().allMatch(item -> item.getQualityProjectScore() == null) &&
                        studentQualityScoreModel.getMajorMerit() == null && studentQualityScoreModel.getMinorMerit() == null &&
                        studentQualityScoreModel.getStrengths() == null && studentQualityScoreModel.getMinorDemerit() == null &&
                        studentQualityScoreModel.getWeaknesses() == null && studentQualityScoreModel.getMajorDemerit() == null)
                {
                    studentQualityScoreModel.setComments(null);
                }else {
                    List<QualityEvaluationCommentRuleDTO> ruleDTOS = qualityCommentRuleService.listRulesDTO(reqModel.getSchoolId());
                    studentQualityScoreModel.setComments(getComment(ruleDTOS,studentQualityScoreModel));
                }
            }

            // 设置迟到次数
            studentQualityScoreModel.setLateCount(
                    lateCountMap.getOrDefault(studentQualityScoreListResModel.getStudentId(), null)
            );

            // 设置请假记录
            List<StudentLeaveStatisticsResModel> statsMapOrDefault = leaveStatsMap.getOrDefault(studentQualityScoreListResModel.getStudentId(), new ArrayList<>());
            for (StudentLeaveStatisticsResModel statsMapOrDefaultItem : statsMapOrDefault)
            {
                if (statsMapOrDefaultItem.getLeaveType() == 2) {
                    studentQualityScoreModel.setAbsenceCount(statsMapOrDefaultItem.getTotalPeriods());
                }
                if (statsMapOrDefaultItem.getLeaveType() == 1) {
                    studentQualityScoreModel.setLeaveRecords(statsMapOrDefaultItem.getTotalPeriods());
                }
            }
            scoreModels.add(studentQualityScoreModel);
        }
        return scoreModels;
    }


    @Override
    public String exportStudentQualityScoreList(StudentQualityScoreQueryReqModel reqModel) {
        SysClass sysClass = sysClassService.getSysClassById(reqModel.getClassId());
        List<QualityIndicatorListResModel> indicators = qualityEvaluationService.listIndicator(reqModel.getSchoolId(),sysClass.getDepartment());
        indicators.sort(Comparator.comparing(QualityIndicatorListResModel::getId));
        List<List<String>> headers = getHeaders(indicators);
        List<List<String>> data = new ArrayList<>();
        //拼接成绩
        for (StudentQualityScoreModel studentQualityScoreModel : getClassPerformanceCheckListExport(reqModel)) {

            List<String> row = new ArrayList<>();
            row.add(String.valueOf(studentQualityScoreModel.getSeatNo() == null ? "" : studentQualityScoreModel.getSeatNo()));
            row.add(studentQualityScoreModel.getChineseName());
            List<StudentQualityScoreDetailResModel> resModels = studentQualityScoreModel.getResModels();
            //转map
            Map<Long, StudentQualityScoreDetailResModel> resModelsMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(resModels)){
                resModelsMap = resModels.stream()
                        .collect(Collectors.toMap(StudentQualityScoreDetailResModel::getQualityProjectId, item -> item));
            }
            StudentQualityScoreDetailResModel detailResModel = resModelsMap.get(0L);
            if(detailResModel != null && detailResModel.getQualityProjectScore() != null)
            {
                row.add(String.valueOf(detailResModel.getQualityProjectScore()/100));
            }else {
                row.add("");
            }
            for (QualityIndicatorListResModel indicator : indicators)
            {
                StudentQualityScoreDetailResModel scoreDetailResModel = resModelsMap.get(indicator.getId());
                if(scoreDetailResModel == null)
                {
                    row.add("");
                }else if(scoreDetailResModel.isDisplay())
                {
                    row.add(String.valueOf(scoreDetailResModel.getQualityProjectScore() == null ? ""
                            : scoreDetailResModel.getQualityProjectScore()/100));
                }else {
                    row.add(scoreDetailResModel.getQualityProjectLevel() == null ? "" : scoreDetailResModel.getQualityProjectLevel());
                }

            }
            row.add(String.valueOf(studentQualityScoreModel.getMajorMerit() == null ? "" : studentQualityScoreModel.getMajorMerit()));
            row.add(String.valueOf(studentQualityScoreModel.getMinorMerit() == null ? "" : studentQualityScoreModel.getMinorMerit()));
            row.add(String.valueOf(studentQualityScoreModel.getStrengths() == null ? "" : studentQualityScoreModel.getStrengths()));
            row.add(String.valueOf(studentQualityScoreModel.getMajorDemerit() == null ? "" : studentQualityScoreModel.getMajorDemerit()));
            row.add(String.valueOf(studentQualityScoreModel.getMinorDemerit() == null ? "" : studentQualityScoreModel.getMinorDemerit()));
            row.add(String.valueOf(studentQualityScoreModel.getWeaknesses() == null ? "" : studentQualityScoreModel.getWeaknesses()));
            row.add(studentQualityScoreModel.getComments() == null ? "" : studentQualityScoreModel.getComments());
            
            // 添加迟到次数
            row.add(String.valueOf(studentQualityScoreModel.getLateCount() == null ? "" : studentQualityScoreModel.getLateCount()));
            
            // 添加请假记录
            row.add(String.valueOf(studentQualityScoreModel.getLeaveRecords() == null ? "" : studentQualityScoreModel.getLeaveRecords()));
            row.add(String.valueOf(studentQualityScoreModel.getAbsenceCount() == null ? "" : studentQualityScoreModel.getAbsenceCount()));
            
            data.add(row);
        }
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        String fileName;
        if(languageEnum == SchoolLanguageEnum.ZH_MO)
        {
            fileName = "学生素质评分.xlsx";
        }else {
            fileName = "StudentQualityRating.xlsx";
        }

        return exportFileHandler.doExportExcelCommon(data, fileName, headers, FileTypeEnum.EXPORT, reqModel.getSchoolId());
    }

    @Override
    public boolean hasPerformance(Long periodId) {
        if(periodId == null){
            return false;
        }

        LambdaQueryWrapper<ClassPerformance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassPerformance::getTerm,periodId);
        return this.count(queryWrapper) > 0 ;
    }

    @Override
    public boolean canRemovePerformanceId(Long performanceId) {
        if(performanceId == null || performanceId == 0L){
            return false;
        }
        return this.count(Wrappers.<ClassPerformance>lambdaQuery().eq(ClassPerformance::getPerformanceId,performanceId)) == 0 ;
    }

    @Override
    public void updatePerformanceById(Long performanceId, String performance) {
        if(performanceId == null || performanceId == 0L){
            return ;
        }
        ClassPerformance classPerformance = new ClassPerformance();
        classPerformance.setPerformance(performance);
        this.update(classPerformance, Wrappers.<ClassPerformance>lambdaQuery().eq(ClassPerformance::getPerformanceId,performanceId));
    }

    @Override
    public List<StudentQualityScoreModel> getStudentQualityScoreList(Long periodId, Long classId,Long schoolId) {
        StudentQualityScoreQueryReqModel reqModel = new StudentQualityScoreQueryReqModel();
        reqModel.setTerm(periodId);
        reqModel.setClassId(classId);
        reqModel.setSchoolId(schoolId);
        List<StudentQualityScoreListResModel> list = studentQualityScoreService.getStudentQualityScoreExportList(reqModel);
        //查询奖励、惩罚记录
        List<Long> studentIds = list.stream().map(StudentQualityScoreListResModel::getStudentId).collect(Collectors.toList());
        UserRewardCountReqModel countReqModel = new UserRewardCountReqModel();
        countReqModel.setSchoolId(reqModel.getSchoolId());
        countReqModel.setTermId(reqModel.getTerm());
        countReqModel.setStudentIds(studentIds);
        countReqModel.setType(1);
        List<UserRewardCountResModel> rewardCount = userRewardService.getUserRewardCount(countReqModel);
        //转map
        Map<Long, UserRewardCountResModel> rewardCountMap = rewardCount.stream().collect(Collectors.toMap(UserRewardCountResModel::getStudentId, item -> item));
        countReqModel.setType(2);
        List<UserRewardCountResModel> weakRewardCount = userRewardService.getUserRewardCount(countReqModel);
        //转map
        Map<Long, UserRewardCountResModel> weakRewardCountMap = weakRewardCount.stream().collect(Collectors.toMap(UserRewardCountResModel::getStudentId, item -> item));
        // 查询迟到统计
        Map<Long, Integer> lateCountMap = studentAttendanceService.countFilterStudentLateDays(
                reqModel.getClassId(),
                reqModel.getTerm()
        );

        // 查询请假统计
        List<StudentLeaveStatisticsResModel> leaveStatsList = studentLeaveService.getStudentLeaveCountBySemester(
                reqModel.getTerm(),
                reqModel.getClassId()
        );
        Map<Long, List<StudentLeaveStatisticsResModel>> leaveStatsMap = leaveStatsList.stream()
                .collect(Collectors.groupingBy(StudentLeaveStatisticsResModel::getStudentId));
        List<QualityEvaluationCommentRuleDTO> ruleDTOS = qualityCommentRuleService.listRulesDTO(reqModel.getSchoolId());
        List<StudentQualityScoreModel> scoreModels = new ArrayList<>();
        for (StudentQualityScoreListResModel studentQualityScoreListResModel : list) {
            StudentQualityScoreModel studentQualityScoreModel = new StudentQualityScoreModel();
            studentQualityScoreModel.setStudentId(studentQualityScoreListResModel.getStudentId());
            studentQualityScoreModel.setChineseName(studentQualityScoreListResModel.getStudentName());
            studentQualityScoreModel.setSeatNo(studentQualityScoreListResModel.getSeatNo());
            studentQualityScoreModel.setEnglishName(studentQualityScoreListResModel.getEnglishName());
            studentQualityScoreModel.setResModels(studentQualityScoreListResModel.getResModels());
            if(rewardCountMap.containsKey(studentQualityScoreListResModel.getStudentId()))
            {
                UserRewardCountResModel rewardCountResModel = rewardCountMap.get(studentQualityScoreListResModel.getStudentId());
                studentQualityScoreModel.setStrengths(rewardCountResModel.getMinRewardCount());
                studentQualityScoreModel.setMinorMerit(rewardCountResModel.getMidRewardCount());
                studentQualityScoreModel.setMajorMerit(rewardCountResModel.getMaxRewardCount());
            }
            if(weakRewardCountMap.containsKey(studentQualityScoreListResModel.getStudentId()))
            {
                UserRewardCountResModel rewardCountResModel = weakRewardCountMap.get(studentQualityScoreListResModel.getStudentId());
                studentQualityScoreModel.setMinorDemerit(rewardCountResModel.getMidRewardCount());
                studentQualityScoreModel.setMajorDemerit(rewardCountResModel.getMaxRewardCount());
                studentQualityScoreModel.setWeaknesses(rewardCountResModel.getMinRewardCount());
            }
            //默认评语
            //如果全部成绩都为空，则不添加评语
            if(studentQualityScoreModel.getResModels().stream().allMatch(item -> item.getQualityProjectScore() == null) &&
                    studentQualityScoreModel.getMajorMerit() == null && studentQualityScoreModel.getMinorMerit() == null &&
                    studentQualityScoreModel.getStrengths() == null && studentQualityScoreModel.getMinorDemerit() == null &&
                    studentQualityScoreModel.getWeaknesses() == null && studentQualityScoreModel.getMajorDemerit() == null)
            {
                studentQualityScoreModel.setComments(null);
            }else {
                studentQualityScoreModel.setComments(getComment(ruleDTOS,studentQualityScoreModel));
            }

            // 设置迟到次数
            studentQualityScoreModel.setLateCount(
                    lateCountMap.getOrDefault(studentQualityScoreListResModel.getStudentId(), null)
            );

            // 设置请假记录
            List<StudentLeaveStatisticsResModel> statsMapOrDefault = leaveStatsMap.getOrDefault(studentQualityScoreListResModel.getStudentId(), new ArrayList<>());
            for (StudentLeaveStatisticsResModel statsMapOrDefaultItem : statsMapOrDefault)
            {
                if (statsMapOrDefaultItem.getLeaveType() == 2) {
                    studentQualityScoreModel.setAbsenceCount(statsMapOrDefaultItem.getTotalPeriods());
                }
                if (statsMapOrDefaultItem.getLeaveType() == 1) {
                    studentQualityScoreModel.setLeaveRecords(statsMapOrDefaultItem.getTotalPeriods());
                }
                if (statsMapOrDefaultItem.getLeaveType() == 3) {
                    studentQualityScoreModel.setLateCount(studentQualityScoreModel.getLateCount() == null ? statsMapOrDefaultItem.getTotalPeriods() :
                            statsMapOrDefaultItem.getTotalPeriods() + studentQualityScoreModel.getLateCount());
                }
            }
            scoreModels.add(studentQualityScoreModel);
        }
        return scoreModels;
    }

    private List<List<String>> getHeaders(List<QualityIndicatorListResModel> indicators) {
        //生成表头
        List<List<String>> headers = new ArrayList<>();
        // 添加基础列头
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_SEAT_NUMBER)));
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_STUDENT_NAME)));
        // 添加固定统计列
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_CONDUCT)));
        // 添加动态指标列
        for (QualityIndicatorListResModel indicator : indicators) {
            headers.add(Collections.singletonList(indicator.getContent()));
        }
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_MAJOR_MERIT)));
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_MINOR_MERIT)));
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_MERIT_POINT)));
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_MAJOR_DEMERIT)));
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_MINOR_DEMERIT)));
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_DEMERIT_POINT)));
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_COMMENTS)));
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_LATE_COUNT)));
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_LEAVE_COUNT)));
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_ABSENCE_COUNT)));
        return headers;
    }


    //获取评语
    private String getComment(List<QualityEvaluationCommentRuleDTO> ruleDTOS,StudentQualityScoreModel studentQualityScoreModel)
    {
        if(CollectionUtils.isEmpty(ruleDTOS))
        {
            return null;
        }
        for (QualityEvaluationCommentRuleDTO ruleDTO : ruleDTOS) {
            boolean flag = true;
            List<ConditionGroupDTO> conditions = ruleDTO.getConditions();
            for (ConditionGroupDTO conditionGroupDTO : conditions) {
                String combineType = conditionGroupDTO.getCombineType();
                if(combineType.equals("AND"))
                {
                    for (ConditionGroupDTO.ConditionItem item : conditionGroupDTO.getItems())
                    {
                        flag = flag && satisfy(studentQualityScoreModel,item);
                    }
                }else if(combineType.equals("OR"))
                {
                    for (ConditionGroupDTO.ConditionItem item1 : conditionGroupDTO.getItems())
                    {
                        flag = flag || satisfy(studentQualityScoreModel,item1);
                    }
                }
            }
            if(flag)
            {
                String commentTemplate = ruleDTO.getCommentTemplate();
                // 替换模板中的占位符
                return replacePlaceholders(commentTemplate, studentQualityScoreModel);
            }
        }
        return null;
    }
    // 替换模板中的占位符
    private String replacePlaceholders(String template, StudentQualityScoreModel studentQualityScoreModel) {
        // 使用正则表达式提取占位符 {2} 的值
        Pattern pattern = Pattern.compile("\\{(\\d+)\\}");
        Matcher matcher = pattern.matcher(template);
        // 创建一个 StringBuilder 来构建最终的评论字符串
        StringBuilder commentBuilder = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            // 添加占位符之前的文本
            commentBuilder.append(template, lastEnd, matcher.start());
            // 获取占位符的数字部分
            int placeholderIndex = Integer.parseInt(matcher.group(1));

            // 根据占位符的数字部分替换相应的值
            String replacement;
            switch (placeholderIndex) {
                case 1:
                    replacement = studentQualityScoreModel.getChineseName();
                    break;
                case 2:
                    replacement = studentQualityScoreModel.getEnglishName();
                    if(StringUtils.isBlank(replacement))
                    {
                        replacement = studentQualityScoreModel.getChineseName();
                    }
                    break;
                default:
                    replacement = matcher.group(0); // 如果没有匹配的占位符，保留原占位符
                    break;
            }
            // 添加替换后的值
            commentBuilder.append(replacement);

            // 更新 lastEnd 位置
            lastEnd = matcher.end();
        }

        // 添加剩余的文本
        commentBuilder.append(template.substring(lastEnd));
        return commentBuilder.toString();
    }

    //根据规则判断学生是否满足
private boolean satisfy(StudentQualityScoreModel studentQualityScoreModel, ConditionGroupDTO.ConditionItem item) {
    String itemType = item.getItem();

    switch (itemType) {
        case "CONDUCT":
            // 过滤出 qualityProjectId 为 0 的记录
            List<StudentQualityScoreDetailResModel> conductItems = studentQualityScoreModel.getResModels().stream()
                    .filter(item1 -> item1.getQualityProjectId() == 0)
                    .collect(Collectors.toList());
            if (conductItems.isEmpty()) {
                return false;
            }
            StudentQualityScoreDetailResModel detailResModel = conductItems.get(0);
            return equalsItem(item, detailResModel.getQualityProjectScore() == null ? 0 : detailResModel.getQualityProjectScore().intValue());

        case "MAJOR_MERIT":
            Integer majorMerit = studentQualityScoreModel.getMajorMerit();
            if (majorMerit == null) {
                return false;
            }
            return equalsItem(item, majorMerit);

        case "MINOR_MERIT":
            Integer minorMerit = studentQualityScoreModel.getMinorMerit();
            if (minorMerit == null) {
                return false;
            }
            return equalsItem(item, minorMerit);

        case "MERIT_POINT":
            Integer strengths = studentQualityScoreModel.getStrengths();
            if (strengths == null) {
                return false;
            }
            return equalsItem(item, strengths);

        case "TOTAL_MERIT":
            Integer totalMeritScore = (studentQualityScoreModel.getMajorMerit() == null ? 0 : studentQualityScoreModel.getMajorMerit()) +
                    (studentQualityScoreModel.getMinorMerit() == null ? 0 : studentQualityScoreModel.getMinorMerit()) +
                            (studentQualityScoreModel.getStrengths() == null ? 0 : studentQualityScoreModel.getStrengths());
            return equalsItem(item, totalMeritScore);

        case "MAJOR_DEMERIT":
            Integer majorDemerit = studentQualityScoreModel.getMajorDemerit();
            if (majorDemerit == null) {
                return false;
            }
            return equalsItem(item, majorDemerit);

        case "MINOR_DEMERIT":
            Integer minorDemerit = studentQualityScoreModel.getMinorDemerit();
            if (minorDemerit == null) {
                return false;
            }
            return equalsItem(item, minorDemerit);

        case "DEMERIT_POINT":
            Integer weaknesses = studentQualityScoreModel.getWeaknesses();
            if (weaknesses == null) {
                return false;
            }
            return equalsItem(item, weaknesses);

        case "TOTAL_DEMERIT":
            Integer totalDemeritScore = (studentQualityScoreModel.getMajorDemerit() == null ? 0 : studentQualityScoreModel.getMajorDemerit()) +
                    (studentQualityScoreModel.getMinorDemerit() == null ? 0 : studentQualityScoreModel.getMinorDemerit())+
                    (studentQualityScoreModel.getWeaknesses() == null ? 0 : studentQualityScoreModel.getWeaknesses());
            return equalsItem(item, totalDemeritScore);

        default:
            return false;
    }
}



    private boolean equalsItem(ConditionGroupDTO.ConditionItem item,Integer score)
    {
        if(item.getOperator().equals(">="))
        {
            return score>=item.getValue() * 100;
        }else if(item.getOperator().equals("<="))
        {
            return score<=item.getValue() * 100;
        }else if(item.getOperator().equals(">"))
        {
            return score>item.getValue() * 100;
        }else if(item.getOperator().equals("<")){
            return score<item.getValue() * 100;
        }
        return false;
    }
}