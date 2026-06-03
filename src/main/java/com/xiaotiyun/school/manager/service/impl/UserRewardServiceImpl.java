package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.TempFileUtils;
import com.xiaotiyun.school.manager.dao.StudentLeaveDao;
import com.xiaotiyun.school.manager.dao.SystemSettingHistoryDao;
import com.xiaotiyun.school.manager.dao.UserRewardDao;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.listener.*;
import com.xiaotiyun.school.manager.model.dto.*;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class UserRewardServiceImpl extends ServiceImpl<UserRewardDao, UserReward> implements UserRewardService {
    @Autowired
    private UserRewardDao userRewardDao;
    @Resource
    private SystemSettingService systemSettingService;
    @Resource
    private SystemSettingHistoryDao systemSettingHistoryDao;
    @Resource
    private SysClassService sysClassService;
    @Resource
    private SemesterService semesterService;
    @Resource
    private StudentAttendanceService studentAttendanceService;
    @Resource
    private StudentLeaveDao studentLeaveDao;
    @Autowired
    private UserAuthHelper userAuthHelper;
    @Resource
    private LanguageUtil languageUtil;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private ImportRecordService importRecordService;
    @Resource
    private StudentService studentService;
    @Resource
    private GradeRecordSettingService gradeRecordSettingService;
    @Resource
    private ActApprovalInstanceService actApprovalInstanceService;
    @Resource
    private ActApprovalHistoryService actApprovalHistoryService;
    @Resource
    private ActInstanceNodeService actInstanceNodeService;
    @Resource
    private ActApprovalCcService actApprovalCcService;
    @Resource
    private ActApprovalTaskService actApprovalTaskService;
    @Resource
    private UserSchoolRelService userSchoolRelService;
    @Resource
    private GradeGroupService gradeGroupService;
    @Resource
    private ConventionalPerformanceService conventionalPerformanceService;
    @Resource
    private ExternalCompetitionRecordService externalCompetitionRecordService;
    @Resource(name = "importExecutor")
    private ThreadPoolTaskExecutor importPool;
    @Resource
    private TempFileUtils tempFileUtils;
    @Resource
    private FileUploadService fileUploadService;

    @Resource
    private ExportRecordService exportRecordService;

    @Resource
    private PdfService pdfService;


    @Resource
    private StudentUsuallyScoreService studentUsuallyScoreService;

    @Resource
    private StudentUsuallyTaskService studentUsuallyTaskService;

    @Resource
    private StudentUsuallyTypeService studentUsuallyTypeService;
    @Resource
    private SubjectRelService subjectRelService;


    private static final String DEFAULT_PATTERN = "dd/MM/yyyy";

    private static final String OFFICE_PATTERN = "dd/MM/yyyy HH:mm:ss";

    private static final String PATTERN = "dd/MM";

    @Resource
    private StudentLeaveService studentLeaveService;


    @Autowired
    private StudentBusinessService studentBusinessService;

    @Override
    public void addUserRewards(Long schoolId, Long userId, UserRewardAddReqModel reqModel) {
        if (!CollectionUtils.isEmpty(reqModel.getStudentInfos())) {
            List<Long> studentIds = reqModel.getStudentInfos().stream().map(UserRewardAddStudentReqModel::getStudentId).collect(Collectors.toList());
            QueryWrapper<StudentEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(StudentEntity::getSchoolId, schoolId)
                    .in(StudentEntity::getId, studentIds);
            List<StudentEntity> students = studentService.list(queryWrapper);
            if (!CollectionUtils.isEmpty(students)) {
                List<Long> classIds = students.stream().map(StudentEntity::getClassId).collect(Collectors.toList());
                QueryWrapper<SysClass> wrapper = new QueryWrapper<>();
                wrapper.lambda().in(SysClass::getId, classIds);
                List<SysClass> sysClasses = sysClassService.list(wrapper);
                if (!CollectionUtils.isEmpty(sysClasses)) {
                    SemesterEntity semester = semesterService.getById(reqModel.getSid());
                    //获取成绩录入设定
                    GradeRecordSettingResModel settingResModel = gradeRecordSettingService.getSetting(schoolId, reqModel.getSid());
                    if (settingResModel != null && settingResModel.getClassSettings() != null) {
                        for (SysClass sysClass : sysClasses) {
                            long count = settingResModel.getClassSettings().stream().filter(classSettingItem -> classSettingItem.getClassId().equals(sysClass.getId()) && classSettingItem.getCanRecordMoralEducation()).count();
                            if (count > 0) {
                                String errorMessage = null;
                                for (GradeRecordSettingResModel.TimeSettingItem timeSetting : settingResModel.getTimeSettings()) {
                                    if (timeSetting.getDepartment().equals(sysClass.getDepartment()) && timeSetting.getSemesterId().equals(reqModel.getTerm())) {
                                        if (!timeSetting.getStartTime().isBefore(LocalDateTime.now()) || !timeSetting.getEndTime().isAfter(LocalDateTime.now())) {
                                            if (semester != null) {
                                                errorMessage = sysClass.getSid() + semester.getName();
                                            } else {
                                                errorMessage = sysClass.getSid();
                                            }
                                            errorMessage = String.format(languageUtil.getMessage(LanguageConstants.REWARD_INPUT_TIME_RANGE), errorMessage) + DateUtils.formatDateToString(timeSetting.getStartTime(), languageUtil.getMessage(LanguageConstants.YEAR_MONTH_DAY)) + "-" + DateUtils.formatDateToString(timeSetting.getEndTime(), languageUtil.getMessage(LanguageConstants.YEAR_MONTH_DAY));
                                        }
                                        break;
                                    }
                                }
                                if (StringUtils.isNotBlank(errorMessage)) {
                                    throw new BusinessMessageException(errorMessage);
                                }
                            }
                        }
                    }
                }
            }
            List<UserReward> userRewards = new ArrayList<>();
            List<ExternalCompetitionRecordEntity> externalCompetitionRecords = new ArrayList<>();
            for (UserRewardAddStudentReqModel studentInfo : reqModel.getStudentInfos()) {
                UserReward userReward = BeanConvertUtil.convert(studentInfo, UserReward.class);
                userReward.setSid(reqModel.getSid());
                userReward.setSchoolId(schoolId);
                userReward.setTerm(reqModel.getTerm());
                userReward.setDate(LocalDateTime.now());
                userReward.setType(reqModel.getType());
                userReward.setRegisterType(reqModel.getRegisterType());
                // 校外活动关联信息
                ExternalCompetitionRecordEntity externalCompetitionRecord = null;
                if (userReward.getRegisterType() == 1 && userReward.getExternalCompetitionRecordId() != null) {
                    //外部竞赛记录
                    // 分数规则写死：优点=3 小功=9 大功=27
                    externalCompetitionRecord = externalCompetitionRecordService.getById(userReward.getExternalCompetitionRecordId());
                }
                switch (studentInfo.getType()) {
                    case 1:
                    case 4:
                        userReward.setMaxReward(studentInfo.getFrequency());
                        if (externalCompetitionRecord != null) {
                            externalCompetitionRecord.setFinalAwards(userReward.getRewardReason());
                            externalCompetitionRecord.setFinalAwardsPoints(studentInfo.getFrequency() * 27);
                        }
                        break;
                    case 2:
                    case 5:
                        userReward.setMidReward(studentInfo.getFrequency());
                        if (externalCompetitionRecord != null) {
                            externalCompetitionRecord.setFinalAwards(userReward.getRewardReason());
                            externalCompetitionRecord.setFinalAwardsPoints(studentInfo.getFrequency() * 9);
                        }
                        break;
                    case 3:
                    case 6:
                        userReward.setMinReward(studentInfo.getFrequency());
                        if (externalCompetitionRecord != null) {
                            externalCompetitionRecord.setFinalAwards(userReward.getRewardReason());
                            externalCompetitionRecord.setFinalAwardsPoints(studentInfo.getFrequency() * 3);
                        }
                        break;
                }
                userRewards.add(userReward);
                if (externalCompetitionRecord != null) {
                    externalCompetitionRecords.add(externalCompetitionRecord);
                }
            }
            if (!CollectionUtils.isEmpty(userRewards)) {
                List<UserReward> userRewardList = this.createUserRewards(userRewards);
                if (!CollectionUtils.isEmpty(userRewardList)) {
                    userRewardList.forEach(userReward -> {
                        //发起审批流程
                        ActApprovalInstancePreviewReqModel previewReqModel = new ActApprovalInstancePreviewReqModel();
                        previewReqModel.setTemplateId(reqModel.getTemplateId());
                        previewReqModel.setDefinitionId(reqModel.getDefinitionId());
                        previewReqModel.setApprover(reqModel.getApprover());
                        actApprovalInstanceService.startProcess(schoolId, userId, userReward.getId(), previewReqModel);
                    });
                }
            }
            if (!CollectionUtils.isEmpty(externalCompetitionRecords)) {
                externalCompetitionRecordService.updateBatchById(externalCompetitionRecords);
            }
        }
    }

    @Override
    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.REWARD)
    public List<UserReward> createUserRewards(List<UserReward> userRewards) {
        userRewards.forEach(userReward -> {
            userReward.setCreateTime(LocalDateTime.now());
            userReward.setUpdateTime(LocalDateTime.now());
            userReward.setDeleted(0L);
        });
        saveBatch(userRewards);
        return userRewards;
    }

    @Override
    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.REWARD)
    public UserReward updateUserReward(UserReward userReward) {
        // 判空
        UserReward oldReward = getById(userReward.getId());
        if (oldReward == null) {
            throw new BusinessException(LanguageConstants.USER_REWARD_NOT_EXIST);
        }
        userReward.setUpdateTime(LocalDateTime.now());
        updateById(userReward);
        // 修改时，更新外部活动奖励信息
        if (userReward.getRegisterType() == 1 && userReward.getExternalCompetitionRecordId() != null) {
            ExternalCompetitionRecordEntity externalCompetitionRecord = externalCompetitionRecordService.getById(userReward.getExternalCompetitionRecordId());
            if (externalCompetitionRecord != null) {
                externalCompetitionRecord.setFinalAwards(userReward.getRewardReason());
                externalCompetitionRecord.setFinalAwardsPoints(userReward.getMaxReward() * 27 + userReward.getMidReward() * 9 + userReward.getMinReward() * 3);
                externalCompetitionRecordService.updateById(externalCompetitionRecord);
            }
            if (oldReward.getRegisterType() == 1 &&
                    oldReward.getExternalCompetitionRecordId() != null &&
                    !oldReward.getExternalCompetitionRecordId().equals(userReward.getExternalCompetitionRecordId())) {
                ExternalCompetitionRecordEntity oldExternalCompetitionRecord = externalCompetitionRecordService.getById(oldReward.getExternalCompetitionRecordId());
                if (oldExternalCompetitionRecord != null) {
                    oldExternalCompetitionRecord.setFinalAwards(null);
                    oldExternalCompetitionRecord.setFinalAwardsPoints(0);
                    externalCompetitionRecordService.updateById(oldExternalCompetitionRecord);
                }
            }
        }
        return userReward;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserReward(Long id) {
        // 判空
        UserReward userReward = getById(id);
        if (userReward == null) {
            throw new BusinessException(LanguageConstants.USER_REWARD_NOT_EXIST);
        }
        //删除审批流程相关内容
        QueryWrapper<ActApprovalInstanceEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ActApprovalInstanceEntity::getBusinessId, id)
                        .eq(ActApprovalInstanceEntity::getProcessType, ActProcessTemplateTypeEnum.STUDENT_REWARD_PUNISHMENT.getCode());
        ActApprovalInstanceEntity approvalInstance = actApprovalInstanceService.getOne(wrapper);
        if (approvalInstance != null) {
            //删除审批任务
            actApprovalTaskService.remove(Wrappers.<ActApprovalTaskEntity>lambdaQuery()
                    .eq(ActApprovalTaskEntity::getInstanceId, approvalInstance.getId()));
            //删除抄送信息
            actApprovalCcService.remove(Wrappers.<ActApprovalCcEntity>lambdaQuery()
                    .eq(ActApprovalCcEntity::getInstanceId, approvalInstance.getId()));
            //删除审批历史信息
            actApprovalHistoryService.remove(Wrappers.<ActApprovalHistoryEntity>lambdaQuery()
                    .eq(ActApprovalHistoryEntity::getInstanceId, approvalInstance.getId()));
            //删除审批实例信息
            actApprovalInstanceService.removeById(approvalInstance.getId());
        }
        //删除奖惩信息
        removeById(id);
        // 删除外部活动奖励信息
        if (userReward.getRegisterType() == 1 && userReward.getExternalCompetitionRecordId() != null) {
            ExternalCompetitionRecordEntity byId = externalCompetitionRecordService.getById(userReward.getExternalCompetitionRecordId());
            byId.setFinalAwards(null);
            byId.setFinalAwardsPoints(null);
            externalCompetitionRecordService.updateById(byId);
        }
    }

    @Override
    public UserReward getUserRewardById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public PageInfo<UserRewardDetailResModel> getUserRewardList(UserRewardQueryReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if (CollectionUtils.isEmpty(classIds)) {
                return PageInfo.emptyPageInfo();
            }
            reqModel.setClassIds(classIds);
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<UserRewardDetailResModel> userRewardDetailResModels = this.baseMapper.getUserReward(reqModel);
        return new PageInfo<>(userRewardDetailResModels);
    }

    @Override
    public List<UserRewardCountResModel> getUserRewardCount(UserRewardCountReqModel reqModel) {
        if (CollectionUtils.isEmpty(reqModel.getStudentIds())) {
            return new ArrayList<>();
        }
        return userRewardDao.getUserRewardCount(reqModel);
    }

    @Override
    public boolean hasReward(Long periodId) {
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoUpdateUserRewards() {
        log.info("=======================开始自动计算学生奖惩=======================");
        //获取昨天的日期，当天00:00:00
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0);
        //获取所有满足时间的学段
        List<SemesterEntity> semesterList = semesterService.list(Wrappers.<SemesterEntity>lambdaQuery()
                .le(SemesterEntity::getStartTime, yesterday)
                .ge(SemesterEntity::getEndTime, yesterday));
        if (ObjectUtils.isEmpty(semesterList)) {
            log.error("=======================自动计算学生奖惩结束，没有满足时间的学段=======================");
            return;
        }
        // 获取所有学段中的最大结束时间和最小开始时间
        LocalDateTime maxEndTime = semesterList.stream().map(SemesterEntity::getEndTime).max(LocalDateTime::compareTo).orElse(null);
        LocalDateTime minStartTime = semesterList.stream().map(SemesterEntity::getStartTime).min(LocalDateTime::compareTo).orElse(null);
        // 配置查询已移至学期循环内部，使用CTE查询按学期时间范围获取
        //获取所有类型惩罚数据
        // 上课违规、欠作业、仪表不符、欠课本、欠回条
        List<ConventionalPerformanceEntity> conventionalPerformances = conventionalPerformanceService.list(Wrappers.<ConventionalPerformanceEntity>lambdaQuery()
                .in(ConventionalPerformanceEntity::getTerm, semesterList.stream().map(SemesterEntity::getId).collect(Collectors.toList())));
        Map<Long, List<ConventionalPerformanceEntity>> conventionalPerformanceMap = new HashMap<>();
        if (ObjectUtils.isNotEmpty(conventionalPerformances)) {
            conventionalPerformanceMap = conventionalPerformances.stream().collect(Collectors.groupingBy(ConventionalPerformanceEntity::getTerm));
        }
        // 入校迟到
        List<StudentAttendanceEntity> attendanceList = studentAttendanceService.list(Wrappers.<StudentAttendanceEntity>lambdaQuery()
                .like(StudentAttendanceEntity::getStatus, "1")
                .between(StudentAttendanceEntity::getAttendanceDate, minStartTime, maxEndTime));
        if (ObjectUtils.isEmpty(attendanceList)) {
            attendanceList = new ArrayList<>();
        } else {
            Map<Long, List<StudentAttendanceEntity>> listMap = attendanceList.stream()
                    .collect(Collectors.groupingBy(StudentAttendanceEntity::getStudentId));
            attendanceList = studentAttendanceService.getFilterRecords(listMap);
        }
        // 学生课堂迟到
        List<StudentLeaveEntity> studentLeaveList = studentLeaveDao.selectList(Wrappers.<StudentLeaveEntity>lambdaQuery()
                .eq(StudentLeaveEntity::getLeaveType, StudentLeaveTypeEnum.LATE.getCode())
                .between(StudentLeaveEntity::getLeaveDate, minStartTime, maxEndTime));
        if (ObjectUtils.isEmpty(attendanceList)) {
            attendanceList = new ArrayList<>();
        }

        // 学生缺席记录
        List<StudentLeaveEntity> studentAbsenceList = studentLeaveDao.selectList(Wrappers.<StudentLeaveEntity>lambdaQuery()
                .eq(StudentLeaveEntity::getLeaveType, StudentLeaveTypeEnum.ABSENT.getCode())
                .between(StudentLeaveEntity::getLeaveDate, minStartTime, maxEndTime));
        if (ObjectUtils.isEmpty(studentAbsenceList)) {
            studentAbsenceList = new ArrayList<>();
        }
        //获取学段内所有已生成的惩罚数据
        List<UserReward> saveOrUpdateUserRewards = new ArrayList<>();
        //遍历学段，根据惩罚规则自动生成惩罚记录
        for (SemesterEntity semester : semesterList) {
            log.info("开始计算学期【{}】的惩罚记录", JSONObject.toJSONString(semester));
            // 获取该学段内所有学生的入校迟到记录
            Map<Long, List<StudentAttendanceEntity>> studentAttendanceMap = attendanceList.stream()
                    .filter(a -> a.getAttendanceDate().isAfter(ChronoLocalDate.from(semester.getStartTime().minusDays(1))) &&
                            a.getAttendanceDate().isBefore(ChronoLocalDate.from(semester.getEndTime().plusDays(1)))).collect(Collectors.groupingBy(StudentAttendanceEntity::getStudentId));
            // 获取该学段内所有学生的课堂迟到记录
            Map<Long, List<StudentLeaveEntity>> studentLeaveMap = studentLeaveList.stream()
                    .filter(a -> a.getLeaveDate().isAfter(ChronoLocalDate.from(semester.getStartTime().minusDays(1))) &&
                            a.getLeaveDate().isBefore(ChronoLocalDate.from(semester.getEndTime().plusDays(1)))).collect(Collectors.groupingBy(StudentLeaveEntity::getStudentId));
            // 获取该学段内所有学生的缺席记录
            Map<Long, List<StudentLeaveEntity>> studentAbsenceMap = studentAbsenceList.stream()
                    .filter(a -> a.getLeaveDate().isAfter(ChronoLocalDate.from(semester.getStartTime().minusDays(1))) &&
                            a.getLeaveDate().isBefore(ChronoLocalDate.from(semester.getEndTime().plusDays(1)))).collect(Collectors.groupingBy(StudentLeaveEntity::getStudentId));
            Long schoolId = semester.getSchoolId();

            // 获取学段内所有的惩罚记录
            List<UserReward> userRewards = userRewardDao.selectList(Wrappers.<UserReward>lambdaQuery().eq(UserReward::getTerm, semester.getId()).eq(UserReward::getDeleted, 0));
            // 转成Map<Long,Map<Integer,UserReward>>，第一个分组是按学生分组，第二个分组是按autoType分组
            Map<Long, Map<Integer, UserReward>> existingRewardMap = userRewards.stream()
                    .collect(Collectors.groupingBy(
                            UserReward::getStudentId,
                            Collectors.toMap(UserReward::getAutoType, Function.identity(), (existing, replacement) -> existing)
                    ));
            log.info("学期【{}】历史惩罚记录数量：{}，按学生和类型分组后的Map：{}", semester.getId(), userRewards.size(), existingRewardMap);


            // 根据学期查历史配置（包含最新配置）
            List<SystemSettingHistoryDTO> historyConfigs = systemSettingService.getHistoryConfigsBySemesterTime(
                    schoolId, "penaltyRules", semester.getStartTime(), semester.getEndTime());
            log.info("学期【{}】历史配置数量：{}，配置详情：{}", semester.getId(), historyConfigs.size(),historyConfigs);

            if (ObjectUtils.isEmpty(historyConfigs)) {
                log.warn("学期【{}】未找到任何配置，跳过处理", semester.getId());
                continue;
            }

            // 获取最新配置，直接从system_setting表查询最新的配置记录
            SystemSettingEntity latestConfig = systemSettingService.getLatestConfig(schoolId, "penaltyRules");

            if(latestConfig == null || StringUtils.isBlank(latestConfig.getSettingValue())){
                log.warn("学期【{}】未找到最新配置，跳过处理", semester.getId());
                continue;
            }

            List<PenaltyRuleDTO> currentPenaltyRules = JSONArray.parseArray(latestConfig.getSettingValue(),PenaltyRuleDTO.class);
            // 最新规则type作为key转map
            Map<String,PenaltyRuleDTO> currentPenaltyRuleMap = currentPenaltyRules.stream().collect(Collectors.toMap(PenaltyRuleDTO::getType, v -> v));

            log.info("最新规则={}",currentPenaltyRules);

            //遍历所有规则，计算惩罚，结果累加后更新
            // 学生该学段下各种类型惩罚记录
            Map<Long, UserReward> stuClassViolationMap = new HashMap<>();
            Map<Long, UserReward> stuMissingHomeworkMap = new HashMap<>();
            Map<Long, UserReward> stuUniformNonComplianceMap = new HashMap<>();
            Map<Long, UserReward> stuMissingTextbookMap = new HashMap<>();
            Map<Long, UserReward> stuMissingBacksheetMap = new HashMap<>();
            Map<Long, UserReward> stuLateMap = new HashMap<>();
            Map<Long, UserReward> stuAbsenceMap = new HashMap<>();
            for (int i = 0; i < historyConfigs.size(); i++) {
                SystemSettingHistoryDTO entity = historyConfigs.get(i);
                LocalDateTime startTime = entity.getStartTime();
                LocalDateTime endTime = entity.getEndTime();
                List<PenaltyRuleDTO> penaltyRuleDTOS = JSON.parseArray(entity.getNewValue(), PenaltyRuleDTO.class);

                // 时间边界检查：如果开始时间小于学期开始时间，则开始时间取学期开始时间
                if (startTime.isBefore(semester.getStartTime())) {
                    startTime = semester.getStartTime();
                }
                // 如果结束时间大于学期结束时间，则结束时间取学期结束时间
                if (endTime.isAfter(semester.getEndTime())) {
                    endTime = semester.getEndTime();
                }
                
                // 创建final副本供lambda表达式使用
                final LocalDateTime finalStartTime = startTime;
                final LocalDateTime finalEndTime = endTime;
                
                // 开始遍历规则
                for (PenaltyRuleDTO penaltyRuleDTO : penaltyRuleDTOS) {
                    if(StringUtils.isBlank(penaltyRuleDTO.getPenaltyType())
                            || StringUtils.isBlank(penaltyRuleDTO.getFrequency())
                            || StringUtils.isBlank(penaltyRuleDTO.getQuantity())
                            || StringUtils.isBlank(penaltyRuleDTO.getType())){
                        log.warn("规则【{}】缺少必要字段，跳过处理", JSONObject.toJSONString(penaltyRuleDTO));
                        continue;
                    }
                    // 规则内计算后有剩余的次数，不累加到新规则内计算
                    switch (penaltyRuleDTO.getType()) {
                        case "1":
                            // 上课违规
                            if (conventionalPerformanceMap.containsKey(semester.getId())) {
                                List<ConventionalPerformanceEntity> conventionalPerformanceList = conventionalPerformanceMap.get(semester.getId());
                                List<ConventionalPerformanceEntity> handleList = conventionalPerformanceList.stream()
                                        .filter(conventionalPerformance -> conventionalPerformance.getType().equals(ConventionalPerformanceTypeEnum.CLASS_VIOLATION.getCode()))
                                        .collect(Collectors.toList());
                                if (!CollectionUtils.isEmpty(handleList)) {
                                    Map<Long, List<ConventionalPerformanceEntity>> collect = handleList.stream()
                                            .collect(Collectors.groupingBy(ConventionalPerformanceEntity::getStudentId));
                                    for (Long studentId : collect.keySet()) {
                                        List<ConventionalPerformanceEntity> entityList = collect.get(studentId);

                                        UserReward studentReward;
                                        if (stuClassViolationMap.containsKey(studentId)) {
                                            studentReward = stuClassViolationMap.get(studentId);
                                        } else {
                                            // 创建新记录
                                            studentReward = new UserReward();
                                            createUserRecord(semester, studentReward, studentId, 0);

                                            // 如果存在历史记录，则复用其主键ID用于更新
                                            if (existingRewardMap.containsKey(studentId) &&
                                                existingRewardMap.get(studentId).containsKey(0)) {
                                                Long existingId = existingRewardMap.get(studentId).get(0).getId();
                                                studentReward.setId(existingId);
                                                log.info("复用学生【{}】课堂违规历史记录ID：{}，创建新对象进行更新", studentId, existingId);
                                            } else {
                                                log.info("为学生【{}】创建新的课堂违规记录", studentId);
                                            }
                                            stuClassViolationMap.put(studentId, studentReward);
                                        }
                                        // 使用新的计算方法，特殊配置使用最新规则
                                        PenaltyRuleDTO latestRule = currentPenaltyRuleMap.get("1");
                                        UserReward calculatedReward = calculatePenaltyReward(penaltyRuleDTO, latestRule,
                                                entityList, finalStartTime, finalEndTime, studentReward);
                                        log.info("规则【{}】处理结果：{}", JSONObject.toJSONString(penaltyRuleDTO), JSONObject.toJSONString(calculatedReward));
                                    }
                                }
                            }
                            break;
                        case "2":
                            // 欠作业
                            if (conventionalPerformanceMap.containsKey(semester.getId())) {
                                List<ConventionalPerformanceEntity> conventionalPerformanceList = conventionalPerformanceMap.get(semester.getId());
                                List<ConventionalPerformanceEntity> handleList = conventionalPerformanceList.stream()
                                        .filter(conventionalPerformance -> conventionalPerformance.getType().equals(ConventionalPerformanceTypeEnum.MISSING_HOMEWORK.getCode()))
                                        .collect(Collectors.toList());
                                if (!CollectionUtils.isEmpty(handleList)) {
                                    Map<Long, List<ConventionalPerformanceEntity>> collect = handleList.stream()
                                            .collect(Collectors.groupingBy(ConventionalPerformanceEntity::getStudentId));
                                    for (Long studentId : collect.keySet()) {
                                        List<ConventionalPerformanceEntity> entityList = collect.get(studentId);

                                        UserReward studentReward;
                                        if (stuMissingHomeworkMap.containsKey(studentId)) {
                                            studentReward = stuMissingHomeworkMap.get(studentId);
                                        } else {
                                            // 创建新记录
                                            studentReward = new UserReward();
                                            createUserRecord(semester, studentReward, studentId, 1);

                                            // 如果存在历史记录，则复用其主键ID用于更新
                                            if (existingRewardMap.containsKey(studentId) &&
                                                existingRewardMap.get(studentId).containsKey(1)) {
                                                Long existingId = existingRewardMap.get(studentId).get(1).getId();
                                                studentReward.setId(existingId);
                                                log.info("复用学生【{}】欠作业历史记录ID：{}，创建新对象进行更新", studentId, existingId);
                                            } else {
                                                log.info("为学生【{}】创建新的欠作业记录", studentId);
                                            }
                                            stuMissingHomeworkMap.put(studentId, studentReward);
                                        }

                                        // 使用新的计算方法，特殊配置使用最新规则
                                        PenaltyRuleDTO latestRule = currentPenaltyRuleMap.get("2");
                                        UserReward calculatedReward = calculatePenaltyReward(penaltyRuleDTO, latestRule,
                                                entityList, finalStartTime, finalEndTime, studentReward);
                                    }
                                }
                            }
                            break;
                        case "3":
                            // 仪表不符
                            if (conventionalPerformanceMap.containsKey(semester.getId())) {
                                List<ConventionalPerformanceEntity> conventionalPerformanceList = conventionalPerformanceMap.get(semester.getId());
                                List<ConventionalPerformanceEntity> handleList = conventionalPerformanceList.stream()
                                        .filter(conventionalPerformance -> conventionalPerformance.getType().equals(ConventionalPerformanceTypeEnum.UNIFORM_NON_COMPLIANCE.getCode()))
                                        .collect(Collectors.toList());
                                if (!CollectionUtils.isEmpty(handleList)) {
                                    Map<Long, List<ConventionalPerformanceEntity>> collect = handleList.stream()
                                            .collect(Collectors.groupingBy(ConventionalPerformanceEntity::getStudentId));
                                    for (Long studentId : collect.keySet()) {
                                        List<ConventionalPerformanceEntity> entityList = collect.get(studentId);

                                        UserReward studentReward;
                                        if (stuUniformNonComplianceMap.containsKey(studentId)) {
                                            studentReward = stuUniformNonComplianceMap.get(studentId);
                                        } else {
                                            // 创建新记录
                                            studentReward = new UserReward();
                                            createUserRecord(semester, studentReward, studentId, 2);

                                            // 如果存在历史记录，则复用其主键ID用于更新
                                            if (existingRewardMap.containsKey(studentId) &&
                                                existingRewardMap.get(studentId).containsKey(2)) {
                                                Long existingId = existingRewardMap.get(studentId).get(2).getId();
                                                studentReward.setId(existingId);
                                                log.info("复用学生【{}】仪表不符历史记录ID：{}，创建新对象进行更新", studentId, existingId);
                                            } else {
                                                log.info("为学生【{}】创建新的仪表不符记录", studentId);
                                            }
                                            stuUniformNonComplianceMap.put(studentId, studentReward);
                                        }

                                        // 使用新的计算方法，特殊配置使用最新规则
                                        PenaltyRuleDTO latestRule = currentPenaltyRuleMap.get("3");
                                        UserReward calculatedReward = calculatePenaltyReward(penaltyRuleDTO, latestRule,
                                                entityList, finalStartTime, finalEndTime, studentReward);
                                    }
                                }
                            }
                            break;
                        case "4":
                            // 入校迟到
                            Set<Long> studentIds = new HashSet<>();
                            if (ObjectUtils.isNotEmpty(studentAttendanceMap)) {
                                studentIds.addAll(studentAttendanceMap.keySet());
                            }
                            if (ObjectUtils.isNotEmpty(studentLeaveMap)) {
                                studentIds.addAll(studentLeaveMap.keySet());
                            }
                            for (Long studentId : studentIds) {
                                List<StudentAttendanceEntity> studentAttendanceEntities = studentAttendanceMap.get(studentId);
                                List<StudentLeaveEntity> studentLeaveEntityList = studentLeaveMap.get(studentId);

                                UserReward studentReward;
                                if (stuLateMap.containsKey(studentId)) {
                                    studentReward = stuLateMap.get(studentId);
                                } else {
                                    // 创建新记录
                                    studentReward = new UserReward();
                                    createUserRecord(semester, studentReward, studentId, 3);

                                    // 如果存在历史记录，则复用其主键ID用于更新
                                    if (existingRewardMap.containsKey(studentId) &&
                                        existingRewardMap.get(studentId).containsKey(3)) {
                                        Long existingId = existingRewardMap.get(studentId).get(3).getId();
                                        studentReward.setId(existingId);
                                        log.info("复用学生【{}】迟到历史记录ID：{}，创建新对象进行更新", studentId, existingId);
                                    } else {
                                        log.info("为学生【{}】创建新的迟到记录", studentId);
                                    }
                                    stuLateMap.put(studentId, studentReward);
                                }

                                // 使用新的迟到惩罚计算方法，特殊配置使用最新规则
                                PenaltyRuleDTO latestRule = currentPenaltyRuleMap.get("4");
                                UserReward calculatedReward = calculateLatePenaltyReward(penaltyRuleDTO, latestRule,
                                        studentAttendanceEntities, studentLeaveEntityList, finalStartTime, finalEndTime, studentReward);
                            }
                            break;
                        case "5":
                            // 欠课本
                            if (conventionalPerformanceMap.containsKey(semester.getId())) {
                                List<ConventionalPerformanceEntity> conventionalPerformanceList = conventionalPerformanceMap.get(semester.getId());
                                List<ConventionalPerformanceEntity> handleList = conventionalPerformanceList.stream()
                                        .filter(conventionalPerformance -> conventionalPerformance.getType().equals(ConventionalPerformanceTypeEnum.MISSING_TEXTBOOK.getCode()))
                                        .collect(Collectors.toList());
                                if (!CollectionUtils.isEmpty(handleList)) {
                                    Map<Long, List<ConventionalPerformanceEntity>> collect = handleList.stream()
                                            .collect(Collectors.groupingBy(ConventionalPerformanceEntity::getStudentId));
                                    for (Long studentId : collect.keySet()) {
                                        List<ConventionalPerformanceEntity> entityList = collect.get(studentId);

                                        UserReward studentReward;
                                        if (stuMissingTextbookMap.containsKey(studentId)) {
                                            studentReward = stuMissingTextbookMap.get(studentId);
                                        } else {
                                            // 创建新记录
                                            studentReward = new UserReward();
                                            createUserRecord(semester, studentReward, studentId, 4);

                                            // 如果存在历史记录，则复用其主键ID用于更新
                                            if (existingRewardMap.containsKey(studentId) &&
                                                existingRewardMap.get(studentId).containsKey(4)) {
                                                Long existingId = existingRewardMap.get(studentId).get(4).getId();
                                                studentReward.setId(existingId);
                                                log.info("复用学生【{}】欠课本历史记录ID：{}，创建新对象进行更新", studentId, existingId);
                                            } else {
                                                log.info("为学生【{}】创建新的欠课本记录", studentId);
                                            }
                                            stuMissingTextbookMap.put(studentId, studentReward);
                                        }
                                        
                                        // 使用新的计算方法，特殊配置使用最新规则
                                        PenaltyRuleDTO latestRule = currentPenaltyRuleMap.get("5");
                                        UserReward calculatedReward = calculatePenaltyReward( penaltyRuleDTO, latestRule,
                                                entityList, finalStartTime, finalEndTime, studentReward);
                                    }
                                }
                            }
                            break;
                        case "6":
                            // 缺席
                            if (!studentAbsenceMap.isEmpty()) {
                                for (Long studentId : studentAbsenceMap.keySet()) {
                                    List<StudentLeaveEntity> absenceList = studentAbsenceMap.get(studentId);
                                    if (CollectionUtils.isEmpty(absenceList)) {
                                        continue;
                                    }

                                    UserReward studentReward;
                                    if (stuAbsenceMap.containsKey(studentId)) {
                                        studentReward = stuAbsenceMap.get(studentId);
                                    } else {
                                        // 创建新记录
                                        studentReward = new UserReward();
                                        createUserRecord(semester, studentReward, studentId, 5); // 使用正确的缺席类型

                                        // 如果存在历史记录，则复用其主键ID用于更新
                                        if (existingRewardMap.containsKey(studentId) &&
                                                existingRewardMap.get(studentId).containsKey(5)) {
                                            Long existingId = existingRewardMap.get(studentId).get(5).getId();
                                            studentReward.setId(existingId);
                                            log.info("复用学生【{}】缺席历史记录ID：{}，创建新对象进行更新", studentId, existingId);
                                        } else {
                                            log.info("为学生【{}】创建新的缺席记录", studentId);
                                        }

                                        stuAbsenceMap.put(studentId, studentReward);
                                    }

                                    // 使用新的计算方法，特殊配置使用最新规则
                                    PenaltyRuleDTO latestRule = currentPenaltyRuleMap.get("6");
                                    UserReward calculatedReward = calculateLatePenaltyReward( penaltyRuleDTO, latestRule,
                                            new ArrayList<>(), absenceList, finalStartTime, finalEndTime, studentReward);
                                }
                            }
                            break;
                        case "7":
                            // 欠回条
                            if (conventionalPerformanceMap.containsKey(semester.getId())) {
                                List<ConventionalPerformanceEntity> conventionalPerformanceList = conventionalPerformanceMap.get(semester.getId());
                                List<ConventionalPerformanceEntity> handleList = conventionalPerformanceList.stream()
                                        .filter(conventionalPerformance -> conventionalPerformance.getType().equals(ConventionalPerformanceTypeEnum.MISSING_RETURN_STICKER.getCode()))
                                        .collect(Collectors.toList());
                                if (!CollectionUtils.isEmpty(handleList)) {
                                    Map<Long, List<ConventionalPerformanceEntity>> collect = handleList.stream()
                                            .collect(Collectors.groupingBy(ConventionalPerformanceEntity::getStudentId));
                                    for (Long studentId : collect.keySet()) {
                                        List<ConventionalPerformanceEntity> entityList = collect.get(studentId);

                                        UserReward studentReward;
                                        if (stuMissingBacksheetMap.containsKey(studentId)) {
                                            studentReward = stuMissingBacksheetMap.get(studentId);
                                        } else {
                                            // 创建新记录
                                            studentReward = new UserReward();
                                            createUserRecord(semester, studentReward, studentId, 6);

                                            // 如果存在历史记录，则复用其主键ID用于更新
                                            if (existingRewardMap.containsKey(studentId) &&
                                                existingRewardMap.get(studentId).containsKey(6)) {
                                                Long existingId = existingRewardMap.get(studentId).get(6).getId();
                                                studentReward.setId(existingId);
                                                log.info("复用学生【{}】欠回条历史记录ID：{}，创建新对象进行更新", studentId, existingId);
                                            } else {
                                                log.info("为学生【{}】创建新的欠回条记录", studentId);
                                            }
                                            stuMissingBacksheetMap.put(studentId, studentReward);
                                        }

                                        // 使用新的计算方法，特殊配置使用最新规则
                                        PenaltyRuleDTO latestRule = currentPenaltyRuleMap.get("7");
                                        UserReward calculatedReward = calculatePenaltyReward( penaltyRuleDTO, latestRule,
                                                entityList, finalStartTime, finalEndTime, studentReward);
                                    }
                                }
                            }
                            break;
                    }
                }
            }
            // 加入该学段下的记录
            saveOrUpdateUserRewards.addAll(stuClassViolationMap.values());
            saveOrUpdateUserRewards.addAll(stuMissingHomeworkMap.values());
            saveOrUpdateUserRewards.addAll(stuUniformNonComplianceMap.values());
            saveOrUpdateUserRewards.addAll(stuMissingTextbookMap.values());
            saveOrUpdateUserRewards.addAll(stuMissingBacksheetMap.values());
            saveOrUpdateUserRewards.addAll(stuLateMap.values());
            saveOrUpdateUserRewards.addAll(stuAbsenceMap.values());
        }
        // 过滤列表：如果maxReward、midReward、minReward这三个字段都是0的话，只做更新，不做新增
        List<UserReward> filteredUserRewards = new ArrayList<>();

        for (UserReward userReward : saveOrUpdateUserRewards) {
            // 检查三个字段是否都为0
            boolean allZero = (userReward.getMaxReward() == null || userReward.getMaxReward() <= 0) &&
                             (userReward.getMidReward() == null || userReward.getMidReward() <= 0) &&
                             (userReward.getMinReward() == null || userReward.getMinReward() <= 0);

            if (allZero) {
                // 如果三个字段都是0，只做更新操作（必须有ID）
                if (userReward.getId() != null) {
                    filteredUserRewards.add(userReward);
                }
                // 如果没有ID且三个字段都是0，则跳过不处理
            } else {
                // 如果至少有一个字段不为0，正常处理
                filteredUserRewards.add(userReward);
            }
        }
        // 再处理需要新增或更新的记录
        if (!filteredUserRewards.isEmpty()) {
            saveOrUpdateBatch(filteredUserRewards);
            log.info("新增或更新了{}条记录", filteredUserRewards.size());
        }

        log.info("=======================结束自动计算学生奖惩=======================");
    }


    public List<UserRewardDTO> calculateUserRewards(List<Long> studentIds,Date startTimeDay, Date endTimeDay,Long schoolId) {
        LocalDateTime startTimeLocal = DateUtils.toLocalDateTime(startTimeDay);
        LocalDateTime endTimeLocal = DateUtils.toLocalDateTime(endTimeDay);
        // 配置查询已移至学期循环内部，使用CTE查询按学期时间范围获取
        //获取所有类型惩罚数据
        // 上课违规、欠作业、仪表不符、欠课本、欠回条
        List<ConventionalPerformanceEntity> conventionalPerformances = conventionalPerformanceService.list(Wrappers.<ConventionalPerformanceEntity>lambdaQuery()
                .in(ConventionalPerformanceEntity::getStudentId, studentIds)
                .ge(ConventionalPerformanceEntity::getDate, startTimeDay)
                .le(ConventionalPerformanceEntity::getDate, endTimeDay));
        if (CollectionUtils.isEmpty(conventionalPerformances))
        {
            conventionalPerformances = new ArrayList<>();
        }
        // 入校迟到
        List<StudentAttendanceEntity> attendanceList = studentAttendanceService.list(Wrappers.<StudentAttendanceEntity>lambdaQuery()
                //筛选1,2,3中的1 FIND_IN_SET
                .apply("FIND_IN_SET(1,status)")
                .in(StudentAttendanceEntity::getStudentId, studentIds)
                .between(StudentAttendanceEntity::getAttendanceDate, startTimeDay, endTimeDay));
        if (ObjectUtils.isEmpty(attendanceList)) {
            attendanceList = new ArrayList<>();
        } else {
            Map<Long, List<StudentAttendanceEntity>> listMap = attendanceList.stream()
                    .collect(Collectors.groupingBy(StudentAttendanceEntity::getStudentId));
            attendanceList = studentAttendanceService.getFilterRecords(listMap);
        }
        // 学生课堂迟到
        List<StudentLeaveEntity> studentLeaveList = studentLeaveDao.selectList(Wrappers.<StudentLeaveEntity>lambdaQuery()
                .eq(StudentLeaveEntity::getLeaveType, "3")
                .in(StudentLeaveEntity::getStudentId, studentIds)
                .between(StudentLeaveEntity::getLeaveDate, startTimeDay, endTimeDay));
        if (ObjectUtils.isEmpty(studentLeaveList)) {
            studentLeaveList = new ArrayList<>();
        }
        
        // 学生缺席记录
        List<StudentLeaveEntity> studentAbsenceList = studentLeaveDao.selectList(Wrappers.<StudentLeaveEntity>lambdaQuery()
                .eq(StudentLeaveEntity::getLeaveType, StudentLeaveTypeEnum.ABSENT.getCode())
                .in(StudentLeaveEntity::getStudentId, studentIds)
                .between(StudentLeaveEntity::getLeaveDate, startTimeDay, endTimeDay));
        if (ObjectUtils.isEmpty(studentAbsenceList)) {
            studentAbsenceList = new ArrayList<>();
        }
        //获取学段内所有已生成的惩罚数据
        List<UserRewardDTO> saveOrUpdateUserRewards = new ArrayList<>();
        //遍历学段，根据惩罚规则自动生成惩罚记录
        // 获取该学段内所有学生的入校迟到记录
        Map<Long, List<StudentAttendanceEntity>> studentAttendanceMap = attendanceList.stream()
                .filter(a -> a.getAttendanceDate().isAfter(ChronoLocalDate.from(startTimeLocal.minusDays(1))) &&
                        a.getAttendanceDate().isBefore(ChronoLocalDate.from(endTimeLocal.plusDays(1)))).collect(Collectors.groupingBy(StudentAttendanceEntity::getStudentId));
        // 获取该学段内所有学生的课堂迟到记录
        Map<Long, List<StudentLeaveEntity>> studentLeaveMap = studentLeaveList.stream()
                .filter(a -> a.getLeaveDate().isAfter(ChronoLocalDate.from(startTimeLocal.minusDays(1))) &&
                        a.getLeaveDate().isBefore(ChronoLocalDate.from(endTimeLocal.plusDays(1)))).collect(Collectors.groupingBy(StudentLeaveEntity::getStudentId));
        
        // 获取该学段内所有学生的缺席记录
        Map<Long, List<StudentLeaveEntity>> studentAbsenceMap = studentAbsenceList.stream()
                .filter(a -> a.getLeaveDate().isAfter(ChronoLocalDate.from(startTimeLocal.minusDays(1))) &&
                        a.getLeaveDate().isBefore(ChronoLocalDate.from(endTimeLocal.plusDays(1)))).collect(Collectors.groupingBy(StudentLeaveEntity::getStudentId));

        // 获取学段内所有的惩罚记录
        // 转成Map<Long,Map<Integer,UserReward>>，第一个分组是按学生分组，第二个分组是按autoType分组
//        Map<Long, Map<Integer, UserReward>> existingRewardMap = new HashMap<>();

        // 根据学期查历史配置（包含最新配置）
        List<SystemSettingHistoryDTO> historyConfigs = systemSettingService.getHistoryConfigsBySemesterTime(
                schoolId, "penaltyRules", startTimeLocal,endTimeLocal);
        log.info("历史配置数量：{}，配置详情：{}",historyConfigs.size(),historyConfigs);

        if (ObjectUtils.isEmpty(historyConfigs)) {
            log.warn("未找到任何配置，跳过处理");
            return new ArrayList<>();
        }


        // 获取最新配置，直接从system_setting表查询最新的配置记录
        SystemSettingEntity latestConfig = systemSettingService.getLatestConfig(schoolId, "penaltyRules");

        if(latestConfig == null || StringUtils.isBlank(latestConfig.getSettingValue())){
            log.warn("未找到最新配置，跳过处理");
            return new ArrayList<>();
        }

        List<PenaltyRuleDTO> currentPenaltyRules = JSONArray.parseArray(latestConfig.getSettingValue(),PenaltyRuleDTO.class);
        // 最新规则type作为key转map
        Map<String,PenaltyRuleDTO> currentPenaltyRuleMap = currentPenaltyRules.stream().collect(Collectors.toMap(PenaltyRuleDTO::getType, v -> v));

        log.info("最新规则={}",currentPenaltyRules);

        //遍历所有规则，计算惩罚，结果累加后更新
        // 学生该学段下各种类型惩罚记录
        Map<Long, UserRewardDTO> stuClassViolationMap = new HashMap<>();
        Map<Long, UserRewardDTO> stuMissingHomeworkMap = new HashMap<>();
        Map<Long, UserRewardDTO> stuUniformNonComplianceMap = new HashMap<>();
        Map<Long, UserRewardDTO> stuMissingTextbookMap = new HashMap<>();
        Map<Long, UserRewardDTO> stuMissingBacksheetMap = new HashMap<>();
        Map<Long, UserRewardDTO> stuLateMap = new HashMap<>();
        Map<Long, UserRewardDTO> stuAbsenceMap = new HashMap<>();
        for (int i = 0; i < historyConfigs.size(); i++) {
            SystemSettingHistoryDTO entity = historyConfigs.get(i);
            LocalDateTime startTime = entity.getStartTime();
            LocalDateTime endTime = entity.getEndTime();
            List<PenaltyRuleDTO> penaltyRuleDTOS = JSON.parseArray(entity.getNewValue(), PenaltyRuleDTO.class);

            // 时间边界检查：如果开始时间小于学期开始时间，则开始时间取学期开始时间
            if (startTime.isBefore(startTimeLocal)) {
                startTime = startTimeLocal;
            }
            // 如果结束时间大于学期结束时间，则结束时间取学期结束时间
            if (endTime.isAfter(endTimeLocal)) {
                endTime = endTimeLocal;
            }

            // 创建final副本供lambda表达式使用
            final LocalDateTime finalStartTime = startTime;
            final LocalDateTime finalEndTime = endTime;

            // 开始遍历规则
            for (PenaltyRuleDTO penaltyRuleDTO : penaltyRuleDTOS) {
                if(StringUtils.isBlank(penaltyRuleDTO.getPenaltyType())
                        || StringUtils.isBlank(penaltyRuleDTO.getFrequency())
                        || StringUtils.isBlank(penaltyRuleDTO.getQuantity())
                        || StringUtils.isBlank(penaltyRuleDTO.getType())){
                    log.warn("规则【{}】缺少必要字段，跳过处理", JSONObject.toJSONString(penaltyRuleDTO));
                    continue;
                }
                List<ConventionalPerformanceEntity> handleList = null;
                        // 规则内计算后有剩余的次数，不累加到新规则内计算
                switch (penaltyRuleDTO.getType()) {
                    case "1":
                        // 上课违规
                        handleList = conventionalPerformances.stream()
                                .filter(conventionalPerformance -> conventionalPerformance.getType().equals(ConventionalPerformanceTypeEnum.CLASS_VIOLATION.getCode()))
                                .collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(handleList)) {
                            Map<Long, List<ConventionalPerformanceEntity>> collect = handleList.stream()
                                    .collect(Collectors.groupingBy(ConventionalPerformanceEntity::getStudentId));
                            for (Long studentId : collect.keySet()) {
                                List<ConventionalPerformanceEntity> entityList = collect.get(studentId);

                                UserRewardDTO studentReward;
                                if (stuClassViolationMap.containsKey(studentId)) {
                                    studentReward = stuClassViolationMap.get(studentId);
                                }else {
                                    // 创建新记录
                                    studentReward = new UserRewardDTO();
                                    createUserRecord(studentReward, studentId, 0);
                                    stuClassViolationMap.put(studentId, studentReward);
                                }

                                // 使用新的计算方法，特殊配置使用最新规则
                                PenaltyRuleDTO latestRule = currentPenaltyRuleMap.get("1");
                                UserRewardDTO calculatedReward = calculatePenaltyRewardV2(penaltyRuleDTO, latestRule,
                                        entityList, finalStartTime, finalEndTime, studentReward);
                                log.info("规则【{}】处理结果：{}", JSONObject.toJSONString(penaltyRuleDTO), JSONObject.toJSONString(calculatedReward));
                            }
                        }
                        break;
                    case "2":
                        // 欠作业
                        handleList = conventionalPerformances.stream()
                                .filter(conventionalPerformance -> conventionalPerformance.getType().equals(ConventionalPerformanceTypeEnum.MISSING_HOMEWORK.getCode()))
                                .collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(handleList)) {
                            Map<Long, List<ConventionalPerformanceEntity>> collect = handleList.stream()
                                    .collect(Collectors.groupingBy(ConventionalPerformanceEntity::getStudentId));
                            for (Long studentId : collect.keySet()) {
                                List<ConventionalPerformanceEntity> entityList = collect.get(studentId);

                                UserRewardDTO studentReward;
                                if (stuMissingHomeworkMap.containsKey(studentId)) {
                                    studentReward = stuMissingHomeworkMap.get(studentId);
                                }else {
                                    // 创建新记录
                                    studentReward = new UserRewardDTO();
                                    createUserRecord(studentReward, studentId, 1);
                                    stuMissingHomeworkMap.put(studentId, studentReward);
                                }

                                // 使用新的计算方法，特殊配置使用最新规则
                                PenaltyRuleDTO latestRule = currentPenaltyRuleMap.get("2");
                                UserRewardDTO calculatedReward = calculatePenaltyRewardV2(penaltyRuleDTO, latestRule,
                                        entityList, finalStartTime, finalEndTime, studentReward);
                            }
                        }
                        break;
                    case "3":
                        // 仪表不符
                        handleList = conventionalPerformances.stream()
                                .filter(conventionalPerformance -> conventionalPerformance.getType().equals(ConventionalPerformanceTypeEnum.UNIFORM_NON_COMPLIANCE.getCode()))
                                .collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(handleList)) {
                            Map<Long, List<ConventionalPerformanceEntity>> collect = handleList.stream()
                                    .collect(Collectors.groupingBy(ConventionalPerformanceEntity::getStudentId));
                            for (Long studentId : collect.keySet()) {
                                List<ConventionalPerformanceEntity> entityList = collect.get(studentId);

                                UserRewardDTO studentReward;
                                if (stuUniformNonComplianceMap.containsKey(studentId)) {
                                    studentReward = stuUniformNonComplianceMap.get(studentId);
                                }else {
                                    // 创建新记录
                                    studentReward = new UserRewardDTO();
                                    createUserRecord(studentReward, studentId, 2);
                                    stuUniformNonComplianceMap.put(studentId, studentReward);
                                }

                                // 使用新的计算方法，特殊配置使用最新规则
                                PenaltyRuleDTO latestRule = currentPenaltyRuleMap.get("3");
                                UserRewardDTO calculatedReward = calculatePenaltyRewardV2(penaltyRuleDTO, latestRule,
                                        entityList, finalStartTime, finalEndTime, studentReward);
                            }
                        }
                        break;
                    case "4":
                        // 入校迟到
                        Set<Long> studentIdsSchool = new HashSet<>();
                        if (ObjectUtils.isNotEmpty(studentAttendanceMap)) {
                            studentIdsSchool.addAll(studentAttendanceMap.keySet());
                        }
                        if (ObjectUtils.isNotEmpty(studentLeaveMap)) {
                            studentIdsSchool.addAll(studentLeaveMap.keySet());
                        }
                        for (Long studentId : studentIdsSchool) {
                            List<StudentAttendanceEntity> studentAttendanceEntities = studentAttendanceMap.get(studentId);
                            List<StudentLeaveEntity> studentLeaveEntityList = studentLeaveMap.get(studentId);

                            UserRewardDTO studentReward;
                            if (stuLateMap.containsKey(studentId)) {
                                studentReward = stuLateMap.get(studentId);
                            } else {
                                // 创建新记录
                                studentReward = new UserRewardDTO();
                                createUserRecord(studentReward, studentId, 3);
                                stuLateMap.put(studentId, studentReward);
                            }

                            // 使用新的迟到惩罚计算方法，特殊配置使用最新规则
                            PenaltyRuleDTO latestRule = currentPenaltyRuleMap.get("4");
                            UserRewardDTO calculatedReward = calculateLatePenaltyRewardV2(penaltyRuleDTO, latestRule,
                                    studentAttendanceEntities, studentLeaveEntityList, finalStartTime, finalEndTime, studentReward);
                        }
                        break;
                    case "5":
                        // 欠课本
                        handleList = conventionalPerformances.stream()
                                .filter(conventionalPerformance -> conventionalPerformance.getType().equals(ConventionalPerformanceTypeEnum.MISSING_TEXTBOOK.getCode()))
                                .collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(handleList)) {
                            Map<Long, List<ConventionalPerformanceEntity>> collect = handleList.stream()
                                    .collect(Collectors.groupingBy(ConventionalPerformanceEntity::getStudentId));
                            for (Long studentId : collect.keySet()) {
                                List<ConventionalPerformanceEntity> entityList = collect.get(studentId);

                                UserRewardDTO studentReward;
                                if (stuMissingTextbookMap.containsKey(studentId)) {
                                    studentReward = stuMissingTextbookMap.get(studentId);
                                } else {
                                    // 创建新记录
                                    studentReward = new UserRewardDTO();
                                    createUserRecord(studentReward, studentId, 4);
                                    stuMissingTextbookMap.put(studentId, studentReward);
                                }

                                // 使用新的计算方法，特殊配置使用最新规则
                                PenaltyRuleDTO latestRule = currentPenaltyRuleMap.get("5");
                                UserRewardDTO calculatedReward = calculatePenaltyRewardV2( penaltyRuleDTO, latestRule,
                                        entityList, finalStartTime, finalEndTime, studentReward);
                            }
                        }
                        break;
                    case "6":
                        // 缺席
                        if (!studentAbsenceMap.isEmpty()) {
                            for (Long studentId : studentAbsenceMap.keySet()) {
                                List<StudentLeaveEntity> absenceList = studentAbsenceMap.get(studentId);
                                if (CollectionUtils.isEmpty(absenceList)) {
                                    continue;
                                }

                                UserRewardDTO studentReward;
                                if (stuAbsenceMap.containsKey(studentId)) {
                                    studentReward = stuAbsenceMap.get(studentId);
                                } else {
                                    // 创建新记录
                                    studentReward = new UserRewardDTO();
                                    createUserRecord(studentReward, studentId, 5); // 使用正确的缺席类型
                                    stuAbsenceMap.put(studentId, studentReward);
                                }

                                // 使用缺席惩罚规则计算缺席惩罚
                                PenaltyRuleDTO latestRule = currentPenaltyRuleMap.get("6"); // 使用缺席规则
                                UserRewardDTO calculatedReward = calculateLatePenaltyRewardV2(penaltyRuleDTO, latestRule,
                                        new ArrayList<>(), absenceList, finalStartTime, finalEndTime, studentReward);
                            }
                        }
                        break;
                    case "7":
                        // 欠回条
                        handleList = conventionalPerformances.stream()
                                .filter(conventionalPerformance -> conventionalPerformance.getType().equals(ConventionalPerformanceTypeEnum.MISSING_RETURN_STICKER.getCode()))
                                .collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(handleList)) {
                            Map<Long, List<ConventionalPerformanceEntity>> collect = handleList.stream()
                                    .collect(Collectors.groupingBy(ConventionalPerformanceEntity::getStudentId));
                            for (Long studentId : collect.keySet()) {
                                List<ConventionalPerformanceEntity> entityList = collect.get(studentId);

                                UserRewardDTO studentReward;
                                if (stuMissingBacksheetMap.containsKey(studentId)) {
                                    studentReward = stuMissingBacksheetMap.get(studentId);
                                } else {
                                    // 创建新记录
                                    studentReward = new UserRewardDTO();
                                    createUserRecord(studentReward, studentId, 6);
                                    stuMissingBacksheetMap.put(studentId, studentReward);
                                }

                                // 使用新的计算方法，特殊配置使用最新规则
                                PenaltyRuleDTO latestRule = currentPenaltyRuleMap.get("7");
                                UserRewardDTO calculatedReward = calculatePenaltyRewardV2(penaltyRuleDTO, latestRule,
                                        entityList, finalStartTime, finalEndTime, studentReward);
                            }
                        }
                        break;
                }
            }
        }
        // 加入该学段下的记录
        saveOrUpdateUserRewards.addAll(stuClassViolationMap.values());
        saveOrUpdateUserRewards.addAll(stuMissingHomeworkMap.values());
        saveOrUpdateUserRewards.addAll(stuUniformNonComplianceMap.values());
        saveOrUpdateUserRewards.addAll(stuMissingTextbookMap.values());
        saveOrUpdateUserRewards.addAll(stuMissingBacksheetMap.values());
        saveOrUpdateUserRewards.addAll(stuLateMap.values());
        saveOrUpdateUserRewards.addAll(stuAbsenceMap.values());
        // 过滤列表：如果maxReward、midReward、minReward这三个字段都是0的话，只做更新，不做新增
        List<UserRewardDTO> filteredUserRewards = new ArrayList<>();

        for (UserRewardDTO userReward : saveOrUpdateUserRewards) {
            // 检查三个字段是否都为0
            boolean allZero = (userReward.getMaxReward() == null || userReward.getMaxReward() <= 0) &&
                    (userReward.getMidReward() == null || userReward.getMidReward() <= 0) &&
                    (userReward.getMinReward() == null || userReward.getMinReward() <= 0);

            if (!allZero) {
                filteredUserRewards.add(userReward);
            }
        }
//        // 再处理需要新增或更新的记录
//        if (!filteredUserRewards.isEmpty()) {
//            saveOrUpdateBatch(filteredUserRewards);
//            log.info("新增或更新了{}条记录", filteredUserRewards.size());
//        }
        return filteredUserRewards;
    }
    /**
     * 计算违规转惩罚次数（支持特殊配置和时间范围过滤，逐条处理违规记录）
     *
     * @param penaltyRuleDTO 惩罚规则配置
     * @param violationRecords 违规记录列表
     * @param startTime 规则生效开始时间
     * @param endTime 规则生效结束时间
     * @param studentReward 学生现有奖励记录
     * @return 计算后的奖励记录
     */
    private UserReward calculatePenaltyReward(PenaltyRuleDTO penaltyRuleDTO, PenaltyRuleDTO latestRule,List<ConventionalPerformanceEntity> violationRecords,
                                           LocalDateTime startTime, LocalDateTime endTime,
                                           UserReward studentReward) {
        // 1. 按时间范围过滤违规记录
        List<ConventionalPerformanceEntity> filteredRecords = violationRecords.stream()
                .filter(record -> !record.getDate().isBefore(startTime.toLocalDate()) && 
                                 !record.getDate().isAfter(endTime.toLocalDate()))
                .sorted(Comparator.comparing(ConventionalPerformanceEntity::getDate)) // 按日期排序
                .collect(Collectors.toList());
        
        if (CollectionUtils.isEmpty(filteredRecords)) {
            return null;
        }
        
        // 2. 累计违规次数，动态检查特殊配置
        int currentTotalPenalty =0 ; // 当前已计算的总惩罚次数
        int currentRulePenaltyCount = 0; // 本次规则计算出的惩罚次数（兑换次数）
        int accumulatedViolationCount = 0; // 累计违规次数
        
        for (ConventionalPerformanceEntity record : filteredRecords) {
            int recordFrequency = record.getFrequency() == null ? 0 : record.getFrequency();
            if (recordFrequency <= 0) {
                continue;
            }
            
            // 将每个record的frequency分解为单独的违规次数进行处理
            for (int i = 0; i < recordFrequency; i++) {
                accumulatedViolationCount += 1; // 每次违规+1

                // 3. 检查是否需要使用特殊配置（基于当前兑换次数，使用最新规则）
                PenaltyRuleSpecialConfigDTO specialConfig = findApplicableSpecialConfig(latestRule, currentRulePenaltyCount);

                // 4. 根据配置判断是否达到处罚阈值
                String frequency = specialConfig != null ? specialConfig.getFrequency() : penaltyRuleDTO.getFrequency();
                String quantity = specialConfig != null ? specialConfig.getQuantity() : penaltyRuleDTO.getQuantity();

                int frequencyThreshold = Integer.parseInt(frequency);

                // 当累计违规次数达到阈值时，计算处罚
                if (accumulatedViolationCount >= frequencyThreshold) {
                    currentRulePenaltyCount += 1; // 计算一次处罚（兑换次数+1）
                    currentTotalPenalty += Integer.parseInt(quantity); // 更新总惩罚次数
                    accumulatedViolationCount -= frequencyThreshold; // 重置累计次数
                }
            }
        }
        
        if (currentTotalPenalty <= 0) {
            return null;
        }
        
        // 5. 创建或更新奖励记录
        return createOrUpdateRewardRecord(penaltyRuleDTO,  currentTotalPenalty
                                        , studentReward);
    }


    /**
     * 计算违规转惩罚次数（支持特殊配置和时间范围过滤，逐条处理违规记录）
     *
     * @param penaltyRuleDTO 惩罚规则配置
     * @param violationRecords 违规记录列表
     * @param startTime 规则生效开始时间
     * @param endTime 规则生效结束时间
     * @param studentReward 学生现有奖励记录
     * @return 计算后的奖励记录
     */
    private UserRewardDTO calculatePenaltyRewardV2(PenaltyRuleDTO penaltyRuleDTO, PenaltyRuleDTO latestRule,List<ConventionalPerformanceEntity> violationRecords,
                                              LocalDateTime startTime, LocalDateTime endTime,
                                              UserRewardDTO studentReward) {
        // 1. 按时间范围过滤违规记录
        List<ConventionalPerformanceEntity> filteredRecords = violationRecords.stream()
                .filter(record -> !record.getDate().isBefore(startTime.toLocalDate()) &&
                        !record.getDate().isAfter(endTime.toLocalDate()))
                .sorted(Comparator.comparing(ConventionalPerformanceEntity::getDate)) // 按日期排序
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(filteredRecords)) {
            return null;
        }
        filteredRecords.sort(Comparator.comparing(ConventionalPerformanceEntity::getDate));

        // 2. 累计违规次数，动态检查特殊配置
        int currentRulePenaltyCount = 0; // 本次规则计算出的惩罚次数（兑换次数）
        int accumulatedViolationCount = 0; // 累计违规次数

        List<UserRewardDetailsDTO> details = new ArrayList<>();

        for (ConventionalPerformanceEntity record : filteredRecords) {
            int recordFrequency = record.getFrequency() == null ? 0 : record.getFrequency();
            if (recordFrequency <= 0) {
                continue;
            }

            // 将每个record的frequency分解为单独的违规次数进行处理
            for (int i = 0; i < recordFrequency; i++) {
                accumulatedViolationCount += 1; // 每次违规+1

                // 3. 检查是否需要使用特殊配置（基于当前兑换次数，使用最新规则）
                PenaltyRuleSpecialConfigDTO specialConfig = findApplicableSpecialConfig(latestRule, currentRulePenaltyCount);

                // 4. 根据配置判断是否达到处罚阈值
                String frequency = specialConfig != null ? specialConfig.getFrequency() : penaltyRuleDTO.getFrequency();
                String quantity = specialConfig != null ? specialConfig.getQuantity() : penaltyRuleDTO.getQuantity();
                // 惩罚类型：特殊配置时使用最新规则，否则使用常规规则
                String penaltyType;
                if (specialConfig != null && latestRule != null) {
                    penaltyType = latestRule.getPenaltyType();
                    log.info("使用特殊配置 - 最新规则惩罚类型: {}, 常规规则惩罚类型: {}", 
                            latestRule.getPenaltyType(), penaltyRuleDTO.getPenaltyType());
                } else {
                    penaltyType = penaltyRuleDTO.getPenaltyType();
                    log.info("使用常规配置 - 惩罚类型: {}", penaltyRuleDTO.getPenaltyType());
                }

                int frequencyThreshold = Integer.parseInt(frequency);

                // 当累计违规次数达到阈值时，计算处罚
                if (accumulatedViolationCount >= frequencyThreshold) {
                    currentRulePenaltyCount += 1; // 计算一次处罚（兑换次数+1）
                    accumulatedViolationCount -= frequencyThreshold; // 重置累计次数
                    
                    // 直接更新UserRewardDTO的对应字段
                    int penaltyQuantity = Integer.parseInt(quantity);
                    updateRewardByPenaltyType(studentReward, penaltyType, penaltyQuantity);
                    
                    UserRewardDetailsDTO detailsDTO = new UserRewardDetailsDTO();
                    detailsDTO.setDate(record.getDate());
                    detailsDTO.setReportNumber(penaltyQuantity);
                    detailsDTO.setNumber(frequencyThreshold);
                    detailsDTO.setPenaltyType(penaltyType);
                    details.add(detailsDTO);
                }
            }
        }

        // 检查是否有任何惩罚
        if (studentReward.getMaxReward() == null && studentReward.getMidReward() == null && studentReward.getMinReward() == null) {
            return null;
        }

        // 5. 记录日志
        log.info("仪表不符惩罚计算后 - maxReward: {}, midReward: {}, minReward: {}", 
                studentReward.getMaxReward(), studentReward.getMidReward(), studentReward.getMinReward());

        if (CollectionUtils.isEmpty(studentReward.getDetails()))
        {
            studentReward.setDetails(details);
        }else {
            studentReward.getDetails().addAll(details);
        }
        return studentReward;
    }

    /**
     * 根据惩罚类型直接更新UserRewardDTO的对应字段
     */
    private void updateRewardByPenaltyType(UserRewardDTO studentReward, String penaltyType, int penaltyQuantity) {
        if (studentReward == null) {
            return;
        }
        
        switch (penaltyType) {
            case "1":
                // 大过
                studentReward.setMaxReward((studentReward.getMaxReward() != null ? studentReward.getMaxReward() : 0) + penaltyQuantity);
                break;
            case "2":
                // 小过
                studentReward.setMidReward((studentReward.getMidReward() != null ? studentReward.getMidReward() : 0) + penaltyQuantity);
                break;
            case "3":
                // 缺点
                studentReward.setMinReward((studentReward.getMinReward() != null ? studentReward.getMinReward() : 0) + penaltyQuantity);
                break;
        }
    }

    /**
     * 查找适用的特殊配置
     */
    private PenaltyRuleSpecialConfigDTO findApplicableSpecialConfig(PenaltyRuleDTO penaltyRuleDTO, int currentExchangeCount) {
        if (penaltyRuleDTO ==null || CollectionUtils.isEmpty(penaltyRuleDTO.getSpecialConfigs())) {
            return null;
        }
        
        // 找到times等于下次兑换次数的特殊配置
        return penaltyRuleDTO.getSpecialConfigs().stream()
                .filter(config -> config.getTimes() != null && config.getTimes().equals(currentExchangeCount + 1))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据UserRewardDTO的奖励字段判断惩罚类型并获取对应的名称
     */
    private String getPenaltyTypeNameFromReward(UserRewardDTO userReward, SchoolLanguageEnum languageEnum) {
        if (userReward == null) {
            return "";
        }
        
        log.info("获取惩罚类型名称 - maxReward: {}, midReward: {}, minReward: {}", 
                userReward.getMaxReward(), userReward.getMidReward(), userReward.getMinReward());
        
        // 根据maxReward、midReward、minReward字段判断惩罚类型
        if (userReward.getMaxReward() != null && userReward.getMaxReward() > 0) {
            // 大过
            switch (languageEnum) {
                case ZH_MO:
                    return "大过";
                case EN_US:
                    return "Major Demerit";
                case PT_PT:
                    return "Falta Grave";
                default:
                    return "大过";
            }
        } else if (userReward.getMidReward() != null && userReward.getMidReward() > 0) {
            // 小过
            switch (languageEnum) {
                case ZH_MO:
                    return "小过";
                case EN_US:
                    return "Minor Demerit";
                case PT_PT:
                    return "Falta Menor";
                default:
                    return "小过";
            }
        } else if (userReward.getMinReward() != null && userReward.getMinReward() > 0) {
            // 缺点
            switch (languageEnum) {
                case ZH_MO:
                    return "缺点";
                case EN_US:
                    return "Demerit Point";
                case PT_PT:
                    return "Ponto de Falta";
                default:
                    return "缺点";
            }
        }
        
        return "";
    }

    /**
     * 根据UserRewardDetailsDTO的penaltyType字段获取惩罚类型名称
     */
    private String getPenaltyTypeNameFromDetail(UserRewardDetailsDTO detail, SchoolLanguageEnum languageEnum) {
        if (detail == null || detail.getPenaltyType() == null) {
            return "";
        }
        
        switch (detail.getPenaltyType()) {
            case "1":
                // 大过
                switch (languageEnum) {
                    case ZH_MO:
                        return "大过";
                    case EN_US:
                        return "Major Demerit";
                    case PT_PT:
                        return "Falta Grave";
                    default:
                        return "大过";
                }
            case "2":
                // 小过
                switch (languageEnum) {
                    case ZH_MO:
                        return "小过";
                    case EN_US:
                        return "Minor Demerit";
                    case PT_PT:
                        return "Falta Menor";
                    default:
                        return "小过";
                }
            case "3":
                // 缺点
                switch (languageEnum) {
                    case ZH_MO:
                        return "缺点";
                    case EN_US:
                        return "Demerit Point";
                    case PT_PT:
                        return "Ponto de Falta";
                    default:
                        return "缺点";
                }
        }
        
        return "";
    }


    /**
     * 根据UserRewardDetailsDTO的penaltyType字段获取惩罚类型名称
     */
    private String getTypeNameFromDetail(String type, SchoolLanguageEnum languageEnum) {
        if (type == null) {
            return "";
        }

        switch (type) {
            case "1":
                // 大过
                switch (languageEnum) {
                    case ZH_MO:
                        return "大过";
                    case EN_US:
                        return "Major Demerit";
                    case PT_PT:
                        return "Falta Grave";
                    default:
                        return "大过";
                }
            case "2":
                // 小过
                switch (languageEnum) {
                    case ZH_MO:
                        return "小过";
                    case EN_US:
                        return "Minor Demerit";
                    case PT_PT:
                        return "Falta Menor";
                    default:
                        return "小过";
                }
            case "3":
                // 缺点
                switch (languageEnum) {
                    case ZH_MO:
                        return "缺点";
                    case EN_US:
                        return "Demerit Point";
                    case PT_PT:
                        return "Ponto de Falta";
                    default:
                        return "缺点";
                }
        }

        return "";
    }

    /**
     * 根据基础规则的惩罚类型获取对应的名称
     */
    private String getPenaltyTypeNameFromRule(PenaltyRuleDTO penaltyRuleDTO, SchoolLanguageEnum languageEnum) {
        if (penaltyRuleDTO == null || penaltyRuleDTO.getPenaltyType() == null) {
            return "";
        }
        
        switch (penaltyRuleDTO.getPenaltyType()) {
            case "1":
                // 大过
                switch (languageEnum) {
                    case ZH_MO:
                        return "大过";
                    case EN_US:
                        return "Major Demerit";
                    case PT_PT:
                        return "Falta Grave";
                    default:
                        return "大过";
                }
            case "2":
                // 小过
                switch (languageEnum) {
                    case ZH_MO:
                        return "小过";
                    case EN_US:
                        return "Minor Demerit";
                    case PT_PT:
                        return "Falta Menor";
                    default:
                        return "小过";
                }
            case "3":
                // 缺点
                switch (languageEnum) {
                    case ZH_MO:
                        return "缺点";
                    case EN_US:
                        return "Demerit Point";
                    case PT_PT:
                        return "Ponto de Falta";
                    default:
                        return "缺点";
                }
        }
        
        return "";
    }

    /**
     * 计算迟到惩罚次数（支持特殊配置和时间范围过滤，逐条处理记录）
     *
     * @param penaltyRuleDTO 惩罚规则配置
     * @param attendanceRecords 出勤记录列表
     * @param leaveRecords 请假记录列表
     * @param startTime 规则生效开始时间
     * @param endTime 规则生效结束时间
     * @param studentReward 学生现有奖励记录
     * @return 计算后的奖励记录
     */
    private UserReward calculateLatePenaltyReward(PenaltyRuleDTO penaltyRuleDTO, PenaltyRuleDTO latestRule,List<StudentAttendanceEntity> attendanceRecords,
                                                List<StudentLeaveEntity> leaveRecords,
                                                LocalDateTime startTime, LocalDateTime endTime,
                                                UserReward studentReward) {
        // 1. 按时间范围过滤出勤记录
        List<StudentAttendanceEntity> filteredAttendanceRecords = new ArrayList<>();
        if (!CollectionUtils.isEmpty(attendanceRecords)) {
            filteredAttendanceRecords = attendanceRecords.stream()
                    .filter(record -> record.getAttendanceDate().isAfter(ChronoLocalDate.from(startTime.minusDays(1))) &&
                                     record.getAttendanceDate().isBefore(ChronoLocalDate.from(endTime.plusDays(1))))
                    .sorted(Comparator.comparing(StudentAttendanceEntity::getAttendanceDate))
                    .collect(Collectors.toList());
        }
        
        // 2. 按时间范围过滤请假记录
        List<StudentLeaveEntity> filteredLeaveRecords = new ArrayList<>();
        if (!CollectionUtils.isEmpty(leaveRecords)) {
            filteredLeaveRecords = leaveRecords.stream()
                    .filter(record -> record.getLeaveDate().isAfter(ChronoLocalDate.from(startTime.minusDays(1))) &&
                                     record.getLeaveDate().isBefore(ChronoLocalDate.from(endTime.plusDays(1))))
                    .sorted(Comparator.comparing(StudentLeaveEntity::getLeaveDate))
                    .collect(Collectors.toList());
        }
        
        if (CollectionUtils.isEmpty(filteredAttendanceRecords) && CollectionUtils.isEmpty(filteredLeaveRecords)) {
            return null;
        }
        
        // 3. 累计违规次数，动态检查特殊配置
        int currentTotalPenalty = 0; // 当前已计算的总惩罚次数（包括历史惩罚次数）
        int currentRulePenaltyCount = 0; // 本次规则计算出的惩罚次数（兑换次数）
        int accumulatedViolationCount = 0; // 累计违规次数
        
        // 处理出勤记录
        for (StudentAttendanceEntity record : filteredAttendanceRecords) {
            // 累计违规次数（每次出勤记录算1次）
            accumulatedViolationCount += 1;
            
            // 检查是否需要使用特殊配置（基于当前兑换次数，使用最新规则）
            PenaltyRuleSpecialConfigDTO specialConfig = findApplicableSpecialConfig(latestRule, currentRulePenaltyCount);
            
            // 根据配置判断是否达到处罚阈值
            String frequency = specialConfig != null ? specialConfig.getFrequency() : penaltyRuleDTO.getFrequency();
            String quantity = specialConfig != null ? specialConfig.getQuantity() : penaltyRuleDTO.getQuantity();
            
            int frequencyThreshold = Integer.parseInt(frequency);
            
            // 当累计违规次数达到阈值时，计算处罚
            while (accumulatedViolationCount >= frequencyThreshold) {
                currentRulePenaltyCount += 1; // 计算一次处罚（兑换次数+1）
                currentTotalPenalty += Integer.parseInt(quantity); // 更新总惩罚次数
                accumulatedViolationCount -= frequencyThreshold; // 重置累计次数
                
                // 重新检查特殊配置（因为兑换次数已更新，使用最新规则）
                specialConfig = findApplicableSpecialConfig(latestRule, currentRulePenaltyCount);
                frequency = specialConfig != null ? specialConfig.getFrequency() : penaltyRuleDTO.getFrequency();
                quantity = specialConfig != null ? specialConfig.getQuantity() : penaltyRuleDTO.getQuantity();
                frequencyThreshold = Integer.parseInt(frequency);
            }
        }
        
        // 处理请假记录
        for (StudentLeaveEntity record : filteredLeaveRecords) {
            int periods = record.getPeriods();
            if (periods <= 0) {
                continue;
            }
            
            // 累计违规次数
            accumulatedViolationCount += periods;
            
            // 检查是否需要使用特殊配置（基于当前兑换次数，使用最新规则）
            PenaltyRuleSpecialConfigDTO specialConfig = findApplicableSpecialConfig(latestRule, currentRulePenaltyCount);
            
            // 根据配置判断是否达到处罚阈值
            String frequency = specialConfig != null ? specialConfig.getFrequency() : penaltyRuleDTO.getFrequency();
            String quantity = specialConfig != null ? specialConfig.getQuantity() : penaltyRuleDTO.getQuantity();
            
            int frequencyThreshold = Integer.parseInt(frequency);
            
            // 当累计违规次数达到阈值时，计算处罚
            while (accumulatedViolationCount >= frequencyThreshold) {
                currentRulePenaltyCount += 1; // 计算一次处罚（兑换次数+1）
                currentTotalPenalty += Integer.parseInt(quantity); // 更新总惩罚次数
                accumulatedViolationCount -= frequencyThreshold; // 重置累计次数
                
                // 重新检查特殊配置（因为兑换次数已更新，使用最新规则）
                specialConfig = findApplicableSpecialConfig(latestRule, currentRulePenaltyCount);
                frequency = specialConfig != null ? specialConfig.getFrequency() : penaltyRuleDTO.getFrequency();
                quantity = specialConfig != null ? specialConfig.getQuantity() : penaltyRuleDTO.getQuantity();
                frequencyThreshold = Integer.parseInt(frequency);
            }
        }
        
        if (currentRulePenaltyCount <= 0) {
            return null;
        }
        
        // 4. 创建或更新奖励记录
        return createOrUpdateRewardRecord(penaltyRuleDTO, currentTotalPenalty, studentReward);
    }



    /**
     * 计算迟到惩罚次数（支持特殊配置和时间范围过滤，逐条处理记录）
     *
     * @param penaltyRuleDTO 惩罚规则配置
     * @param attendanceRecords 出勤记录列表
     * @param leaveRecords 请假记录列表
     * @param startTime 规则生效开始时间
     * @param endTime 规则生效结束时间
     * @param studentReward 学生现有奖励记录
     * @return 计算后的奖励记录
     */
    private UserRewardDTO calculateLatePenaltyRewardV2(PenaltyRuleDTO penaltyRuleDTO, PenaltyRuleDTO latestRule,List<StudentAttendanceEntity> attendanceRecords,
                                                  List<StudentLeaveEntity> leaveRecords,
                                                  LocalDateTime startTime, LocalDateTime endTime,
                                                  UserRewardDTO studentReward) {
        // 1. 按时间范围过滤出勤记录
        List<StudentAttendanceEntity> filteredAttendanceRecords = new ArrayList<>();
        if (!CollectionUtils.isEmpty(attendanceRecords)) {
            filteredAttendanceRecords = attendanceRecords.stream()
                    .filter(record -> record.getAttendanceDate().isAfter(ChronoLocalDate.from(startTime.minusDays(1))) &&
                            record.getAttendanceDate().isBefore(ChronoLocalDate.from(endTime.plusDays(1))))
                    .sorted(Comparator.comparing(StudentAttendanceEntity::getAttendanceDate))
                    .collect(Collectors.toList());
        }

        // 2. 按时间范围过滤请假记录
        List<StudentLeaveEntity> filteredLeaveRecords = new ArrayList<>();
        if (!CollectionUtils.isEmpty(leaveRecords)) {
            filteredLeaveRecords = leaveRecords.stream()
                    .filter(record -> record.getLeaveDate().isAfter(ChronoLocalDate.from(startTime.minusDays(1))) &&
                            record.getLeaveDate().isBefore(ChronoLocalDate.from(endTime.plusDays(1))))
                    .sorted(Comparator.comparing(StudentLeaveEntity::getLeaveDate))
                    .collect(Collectors.toList());
        }

        if (CollectionUtils.isEmpty(filteredLeaveRecords)) {
            return null;
        }
        filteredAttendanceRecords.sort(Comparator.comparing(StudentAttendanceEntity::getAttendanceDate));
        filteredLeaveRecords.sort(Comparator.comparing(StudentLeaveEntity::getLeaveDate));

        // 3. 累计违规次数，动态检查特殊配置
        int currentTotalPenalty = 0; // 当前已计算的总惩罚次数（包括历史惩罚次数）
        int currentRulePenaltyCount = 0; // 本次规则计算出的惩罚次数（兑换次数）
        int accumulatedViolationCount = 0; // 累计违规次数
        List<UserRewardDetailsDTO> details = new ArrayList<>();
        List<UserRewardDetailsDTO> lateDetails = new ArrayList<>();

        // 处理出勤记录
        for (StudentAttendanceEntity record : filteredAttendanceRecords) {
            // 累计违规次数（每次出勤记录算1次）
            accumulatedViolationCount += 1;

            // 检查是否需要使用特殊配置（基于当前兑换次数，使用最新规则）
            PenaltyRuleSpecialConfigDTO specialConfig = findApplicableSpecialConfig(latestRule, currentRulePenaltyCount);

            // 根据配置判断是否达到处罚阈值
            String frequency = specialConfig != null ? specialConfig.getFrequency() : penaltyRuleDTO.getFrequency();
            String quantity = specialConfig != null ? specialConfig.getQuantity() : penaltyRuleDTO.getQuantity();

            int frequencyThreshold = Integer.parseInt(frequency);

            // 当累计违规次数达到阈值时，计算处罚
            while (accumulatedViolationCount >= frequencyThreshold) {
                currentRulePenaltyCount += 1; // 计算一次处罚（兑换次数+1）
                currentTotalPenalty += Integer.parseInt(quantity); // 更新总惩罚次数
                accumulatedViolationCount -= frequencyThreshold; // 重置累计次数

                // 惩罚类型：特殊配置时使用最新规则，否则使用常规规则
                String penaltyType;
                if (specialConfig != null && latestRule != null) {
                    penaltyType = latestRule.getPenaltyType();
                    log.info("使用特殊配置 - 最新规则惩罚类型: {}, 常规规则惩罚类型: {}",
                            latestRule.getPenaltyType(), penaltyRuleDTO.getPenaltyType());
                } else {
                    penaltyType = penaltyRuleDTO.getPenaltyType();
                    log.info("使用常规配置 - 惩罚类型: {}", penaltyRuleDTO.getPenaltyType());
                }
                UserRewardDetailsDTO detail = new UserRewardDetailsDTO();
                detail.setDate(record.getAttendanceDate());
                detail.setReportNumber(Integer.parseInt(quantity));
                detail.setNumber(frequencyThreshold);
                detail.setPenaltyType(penaltyType);
                details.add(detail);

                // 重新检查特殊配置（因为兑换次数已更新，使用最新规则）
                specialConfig = findApplicableSpecialConfig(latestRule, currentRulePenaltyCount);
                frequency = specialConfig != null ? specialConfig.getFrequency() : penaltyRuleDTO.getFrequency();
                quantity = specialConfig != null ? specialConfig.getQuantity() : penaltyRuleDTO.getQuantity();
                frequencyThreshold = Integer.parseInt(frequency);
            }
        }

        // 处理请假记录
        for (StudentLeaveEntity record : filteredLeaveRecords) {
            int periods = record.getPeriods();
            if (periods <= 0) {
                continue;
            }

            // 累计违规次数
            accumulatedViolationCount += periods;

            // 检查是否需要使用特殊配置（基于当前兑换次数，使用最新规则）
            PenaltyRuleSpecialConfigDTO specialConfig = findApplicableSpecialConfig(latestRule, currentRulePenaltyCount);

            // 根据配置判断是否达到处罚阈值
            String frequency = specialConfig != null ? specialConfig.getFrequency() : penaltyRuleDTO.getFrequency();
            String quantity = specialConfig != null ? specialConfig.getQuantity() : penaltyRuleDTO.getQuantity();

            int frequencyThreshold = Integer.parseInt(frequency);

            // 当累计违规次数达到阈值时，计算处罚
            while (accumulatedViolationCount >= frequencyThreshold) {
                currentRulePenaltyCount += 1; // 计算一次处罚（兑换次数+1）
                currentTotalPenalty += Integer.parseInt(quantity); // 更新总惩罚次数
                accumulatedViolationCount -= frequencyThreshold; // 重置累计次数

                // 惩罚类型：特殊配置时使用最新规则，否则使用常规规则
                String penaltyType;
                if (specialConfig != null && latestRule != null) {
                    penaltyType = latestRule.getPenaltyType();
                    log.info("使用特殊配置 - 最新规则惩罚类型: {}, 常规规则惩罚类型: {}",
                            latestRule.getPenaltyType(), penaltyRuleDTO.getPenaltyType());
                } else {
                    penaltyType = penaltyRuleDTO.getPenaltyType();
                    log.info("使用常规配置 - 惩罚类型: {}", penaltyRuleDTO.getPenaltyType());
                }
                UserRewardDetailsDTO detail = new UserRewardDetailsDTO();
                detail.setDate(record.getLeaveDate());
                detail.setReportNumber(Integer.parseInt(quantity));
                detail.setNumber(frequencyThreshold);
                detail.setPenaltyType(penaltyType);
                lateDetails.add(detail);

                // 重新检查特殊配置（因为兑换次数已更新，使用最新规则）
                specialConfig = findApplicableSpecialConfig(latestRule, currentRulePenaltyCount);
                frequency = specialConfig != null ? specialConfig.getFrequency() : penaltyRuleDTO.getFrequency();
                quantity = specialConfig != null ? specialConfig.getQuantity() : penaltyRuleDTO.getQuantity();
                frequencyThreshold = Integer.parseInt(frequency);
            }
        }

        if (currentRulePenaltyCount <= 0) {
            return null;
        }

        // 4. 创建或更新奖励记录
        UserRewardDTO orUpdateRewardRecord = createOrUpdateRewardRecord(penaltyRuleDTO, currentTotalPenalty, studentReward);
        if (CollectionUtils.isEmpty(orUpdateRewardRecord.getDetails())){
            orUpdateRewardRecord.setDetails(details);
        }else{
            orUpdateRewardRecord.getDetails().addAll(details);
        }
        if (CollectionUtils.isEmpty(orUpdateRewardRecord.getDetails1()))
        {
            orUpdateRewardRecord.setDetails1(lateDetails);
        }else {
            orUpdateRewardRecord.getDetails1().addAll(lateDetails);
        }
        return orUpdateRewardRecord;
    }

    private void createUserRecord(SemesterEntity semester,UserReward studentReward,long studentId, int autoType){
        studentReward.setSchoolId(semester.getSchoolId());
        studentReward.setSid(semester.getSchoolYear());
        studentReward.setTerm(semester.getId());
        studentReward.setStudentId(studentId);
        studentReward.setDate(LocalDateTime.now());
        studentReward.setType(2);
        studentReward.setIsAuto(1);
        studentReward.setAutoType(autoType);
        studentReward.setRewardReason("自动计算惩罚");
        studentReward.setMinReward(0);
        studentReward.setMidReward(0);
        studentReward.setMaxReward(0);
    }

    private void createUserRecord(UserRewardDTO studentReward,long studentId, int autoType){
        studentReward.setStudentId(studentId);
        studentReward.setDate(LocalDateTime.now());
        studentReward.setAutoType(autoType);
        studentReward.setMinReward(0);
        studentReward.setMidReward(0);
        studentReward.setMaxReward(0);
    }

    /**
     * 创建或更新奖励记录
     */
    private UserReward createOrUpdateRewardRecord(PenaltyRuleDTO penaltyRuleDTO,
                                               int currentTotalPenalty,
                                                UserReward studentReward) {
        switch (penaltyRuleDTO.getPenaltyType()) {
            case "1":
                studentReward.setMaxReward(studentReward.getMaxReward()+currentTotalPenalty);
                break;
            case "2":
                studentReward.setMidReward(studentReward.getMidReward()+currentTotalPenalty);
                break;
            case "3":
                studentReward.setMinReward(studentReward.getMinReward()+currentTotalPenalty);
                break;
        }
        studentReward.setUpdateTime(LocalDateTime.now());
        return studentReward;
    }

    private UserRewardDTO createOrUpdateRewardRecord(PenaltyRuleDTO penaltyRuleDTO,
                                                  int currentTotalPenalty,
                                                  UserRewardDTO studentReward) {
        switch (penaltyRuleDTO.getPenaltyType()) {
            case "1":
                studentReward.setMaxReward(studentReward.getMaxReward()+currentTotalPenalty);
                break;
            case "2":
                studentReward.setMidReward(studentReward.getMidReward()+currentTotalPenalty);
                break;
            case "3":
                studentReward.setMinReward(studentReward.getMinReward()+currentTotalPenalty);
                break;
        }
        return studentReward;
    }


    @Override
    public Long importRecord(Long schoolId, Long userId, Long templateId, Long definitionId, String sid, Long term, Integer type, List<ActApprovalInstancePreviewApproverReqModel> approver, MultipartFile file) {
        if (schoolId == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.NO_SCHOOL_FILE_CONTENT_EMPTY));
        }
        //获取学校语言设置信息
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        // 读取Excel文件
        List<UserRewardImportModel> list = readExcelData(file, languageEnum, type);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
            // 创建导入任务
            ImportTaskEntity task = new ImportTaskEntity();
            task.setSchoolId(schoolId);
            task.setFileName(file.getOriginalFilename());
            if (type == 1) {
                task.setType(ImportTaskTypeEnum.STUDENT_REWARD.getCode());
            } else {
                task.setType(ImportTaskTypeEnum.STUDENT_PUNISHMENT.getCode());
            }
            task.setTotalCount(0);
            task.setSuccessCount(0);
            task.setFailCount(0);
            importTaskService.save(task);
            CompletableFuture.runAsync(() -> {
                languageUtil.setLanguage(languageEnum.getCode());
                log.info("当前使用的语言是:{}", LanguageUtil.getCurrentLanguage());
                handleImportData(task, list, schoolId, userId, templateId, definitionId, sid, term, type, approver, languageEnum);
                LanguageUtil.clearLanguage();
            }, importPool).whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("导入学生奖惩任务执行结束taskId=【{}】异常={}",task.getId(),ex);
                } else {
                    log.info("导入学生奖惩完成，任务ID={}",task.getId());
                }
                task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
                importTaskService.updateById(task);
            });
            return task.getId();
        }
        return null;
    }

    private void handleImportData(ImportTaskEntity task, List<UserRewardImportModel> list, Long schoolId, Long userId, Long templateId, Long definitionId, String sid, Long term, Integer type, List<ActApprovalInstancePreviewApproverReqModel> approver, SchoolLanguageEnum languageEnum) {
        task.setTotalCount(list.size());
        task.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
        importTaskService.updateById(task);
        log.info("开始处理数据导入...");
        Iterator<UserRewardImportModel> iterator = list.iterator();
        //每500个处理一次
        List<UserRewardImportModel> batchExcelLine = new ArrayList<>(500);
        int correctCount = 0;
        List<ImportRecordSaveDTO> importRecordSaveDTOS = new ArrayList<>();
        List<UserRewardImportDTO> correctList = new ArrayList<>();
        while (iterator.hasNext()) {
            UserRewardImportModel importModel = iterator.next();
            batchExcelLine.add(importModel);
            if (batchExcelLine.size() >= 500) {
                //处理数据 插入数据库
                correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, correctList, type, languageEnum);
                batchExcelLine.clear();
            }
        }
        if (!batchExcelLine.isEmpty()) {
            correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, correctList, type, languageEnum);
            batchExcelLine.clear();
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(correctList)) {
            //将正确的记录处理后导入数据库
            handleImportData(correctList, schoolId, userId, templateId, definitionId, sid, term, type, approver);
        }
        //当前处理进度写入数据库
        task.setSuccessCount(correctCount);
        task.setFailCount(list.size() - correctCount);
        task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
        importTaskService.updateById(task);
        //错误信息写入数据库
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(importRecordSaveDTOS)) {
            List<ImportRecordEntity> entityList = importRecordSaveDTOS.stream().map(dto -> {
                ImportRecordEntity importRecordEntity = new ImportRecordEntity();
                BeanUtils.copyProperties(dto, importRecordEntity);
                importRecordEntity.setTaskId(task.getId());
                return importRecordEntity;
            }).collect(Collectors.toList());
            importRecordService.saveBatch(entityList);
        }
    }

    private void handleImportData(List<UserRewardImportDTO> correctList, Long schoolId, Long userId, Long templateId, Long definitionId, String sid, Long term, Integer type, List<ActApprovalInstancePreviewApproverReqModel> approver) {
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(correctList)) {
            UserRewardAddReqModel reqModel = new UserRewardAddReqModel();
            reqModel.setTemplateId(templateId);
            reqModel.setDefinitionId(definitionId);
            reqModel.setSid(sid);
            reqModel.setTerm(term);
            reqModel.setType(type);
            reqModel.setApprover(approver);
            List<UserRewardAddStudentReqModel> studentInfos = new ArrayList<>();
            for (UserRewardImportDTO userRewardImportDTO : correctList) {
                UserRewardAddStudentReqModel studentReqModel = new UserRewardAddStudentReqModel();
                BeanUtils.copyProperties(userRewardImportDTO, studentReqModel);
                studentInfos.add(studentReqModel);
            }
            reqModel.setStudentInfos(studentInfos);
            addUserRewards(schoolId, userId, reqModel);
        }
    }

    private int processBatchExcelLine(List<ImportRecordSaveDTO> importErrorDTOS, List<UserRewardImportModel> list, Long schoolId, List<UserRewardImportDTO> correctList, Integer type, SchoolLanguageEnum languageEnum) {
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
            int correctCount = list.size();//正确处理的条数
            Map<String, StudentEntity> studentNumberMap = new HashMap<>();
            Set<String> studentCodes = list.stream().map(UserRewardImportModel::getStudentCode).collect(Collectors.toSet());
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(studentCodes)) {
                QueryWrapper<StudentEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(StudentEntity::getSchoolId, schoolId)
                        .in(StudentEntity::getStudentNo, studentCodes);
                List<StudentEntity> studentEntities = studentService.list(queryWrapper);
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(studentEntities)) {
                    studentNumberMap = studentEntities.stream().collect(Collectors.toMap(StudentEntity::getStudentNo, student -> student, (key1, key2) -> key1));
                }
            }
            //遍历要插入的每一行
            for (UserRewardImportModel bo : list) {
                List<String> errorList = new ArrayList<>();
                if (!check(bo, errorList, studentNumberMap, type, languageEnum)) {
                    //不合法
                    correctCount--;
                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(errorList)) {
                        ImportRecordSaveDTO errorDTO = new ImportRecordSaveDTO();
                        errorDTO.setIncorrectLineno(String.valueOf(bo.getExcelLineNo()));
                        errorDTO.setIncorrectReason(StringUtils.join(errorList, "；"));
                        importErrorDTOS.add(errorDTO);
                    }
                    continue;
                }
                correctList.add(importConvert(bo, studentNumberMap, languageEnum));
            }
            return correctCount;
        }
        return 0;
    }

    private UserRewardImportDTO importConvert(UserRewardImportModel bo, Map<String, StudentEntity> studentNumberMap, SchoolLanguageEnum languageEnum) {
        UserRewardImportDTO result = new UserRewardImportDTO();
        StudentEntity studentEntity = studentNumberMap.get(bo.getStudentCode());
        if (studentEntity != null) {
            result.setStudentId(studentEntity.getId());
        }
        result.setStudentName(bo.getStudentName());
        result.setStudentCode(bo.getStudentCode());
        Date meetingDate;
        if (StringUtils.isNumeric(bo.getMeetingDate())) {
            //execl日期格式解析为全数字，如：43444
            meetingDate = DateUtil.getJavaDate(Double.parseDouble(bo.getMeetingDate()));
        } else if (bo.getMeetingDate().contains("/")) {
            //字符串格式日期，如2024/12/18
            meetingDate = DateUtils.formatStringToDate(bo.getMeetingDate(), "yyyy/MM/dd");
        } else {
            //字符串格式日期，如2024-12-18
            meetingDate = DateUtils.formatStringToDate(bo.getMeetingDate(), "yyyy-MM-dd");
        }
        Instant instant = meetingDate.toInstant();
        ZoneId zoneId = ZoneId.systemDefault(); // 使用系统默认时区
        result.setMeetingDate(instant.atZone(zoneId).toLocalDate());
        result.setRewardReason(bo.getRewardReason());
        result.setType(UserRewardTypeEnum.getCode(bo.getRewardType(), languageEnum));
        result.setFrequency(Integer.parseInt(bo.getFrequency()));
        result.setRemark(bo.getRemark());
        return result;
    }

    private boolean check(UserRewardImportModel bo, List<String> errorList, Map<String, StudentEntity> studentNumberMap, Integer type, SchoolLanguageEnum languageEnum) {
        //學生中文姓名、學生編號检查
        if (!StringUtils.isNotBlank(bo.getStudentCode()) || !StringUtils.isNotBlank(bo.getStudentName())) {
            if (!StringUtils.isNotBlank(bo.getStudentCode())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_NO_REQUIRED));
            }
            if (!StringUtils.isNotBlank(bo.getStudentName())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.CHINESE_NAME_REQUIRED));
            }
        } else {
            StudentEntity student = studentNumberMap.get(bo.getStudentCode());
            if (student != null) {
                if (!student.getChineseName().equals(bo.getStudentName())) {
                    errorList.add(String.format(languageUtil.getMessage(LanguageConstants.STUDENT_NAME_NOT_MATCH_STUDENT_NO), bo.getStudentName()));
                }
            } else {
                errorList.add(String.format(languageUtil.getMessage(LanguageConstants.STUDENT_NAME_NOT_FOUND), bo.getStudentName()));
            }
        }
        //會議通過日期检查
        if (StringUtils.isNotBlank(bo.getMeetingDate())) {
            try {
                if (StringUtils.isNumeric(bo.getMeetingDate())) {
                    //execl日期格式解析为全数字，如：43444
                    DateUtil.getJavaDate(Double.parseDouble(bo.getMeetingDate()));
                } else if (bo.getMeetingDate().contains("/")) {
                    //字符串格式日期，如2024/12/18
                    DateUtils.formatStringToDate(bo.getMeetingDate(), "yyyy/MM/dd");
                } else {
                    //字符串格式日期，如2024-12-18
                    DateUtils.formatStringToDate(bo.getMeetingDate(), "yyyy-MM-dd");
                }
            } catch (Exception e) {
                errorList.add(languageUtil.getMessage(LanguageConstants.DATE_FORMAT_ERROR_YMD));
            }
        } else {
            errorList.add(languageUtil.getMessage(LanguageConstants.DATE_REQUIRED));
        }
        //原因检查
        if (!StringUtils.isNotBlank(bo.getRewardReason())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.REWARD_REASON_REQUIRED));
        }
        //類型检查
        if (!StringUtils.isNotBlank(bo.getRewardType())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.TYPE_REQUIRED));
        } else {
            UserRewardTypeEnum typeEnum = UserRewardTypeEnum.getByValue(bo.getRewardType(), languageEnum);
            List<String> errors = new ArrayList<>();
            if (type == 1) {
                //奖励
                errors.add(UserRewardTypeEnum.getValue(UserRewardTypeEnum.GREAT_REWARD.getCode(), languageEnum));
                errors.add(UserRewardTypeEnum.getValue(UserRewardTypeEnum.SMALL_REWARD.getCode(), languageEnum));
                errors.add(UserRewardTypeEnum.getValue(UserRewardTypeEnum.MERIT.getCode(), languageEnum));
                // 检查是否为有效的奖励类型
                if (typeEnum != UserRewardTypeEnum.GREAT_REWARD && typeEnum != UserRewardTypeEnum.SMALL_REWARD && typeEnum != UserRewardTypeEnum.MERIT) {
                    errorList.add(String.format(languageUtil.getMessage(LanguageConstants.REWARD_TYPE_FORMAT_ERROR), String.join(",", errors)));
                }
            } else {
                //惩罚
                errors.add(UserRewardTypeEnum.getValue(UserRewardTypeEnum.GREAT_OFFENSE.getCode(), languageEnum));
                errors.add(UserRewardTypeEnum.getValue(UserRewardTypeEnum.SMALL_OFFENSE.getCode(), languageEnum));
                errors.add(UserRewardTypeEnum.getValue(UserRewardTypeEnum.DEMERIT.getCode(), languageEnum));
                // 检查是否为有效的惩罚类型
                if (typeEnum != UserRewardTypeEnum.GREAT_OFFENSE && typeEnum != UserRewardTypeEnum.SMALL_OFFENSE && typeEnum != UserRewardTypeEnum.DEMERIT) {
                    errorList.add(String.format(languageUtil.getMessage(LanguageConstants.REWARD_TYPE_FORMAT_ERROR), String.join(",", errors)));
                }
            }
        }
        //次數检查
        if (!StringUtils.isNotBlank(bo.getFrequency())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.REWARD_FREQUENCY_REQUIRED));
        } else {
            // 检查次数是否为数字
            if (!StringUtils.isNumeric(bo.getFrequency())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.FORMAT_ERROR_ONLY_ALLOW_NUMERIC_INPUT));
            }
        }
        return !org.apache.commons.collections4.CollectionUtils.isNotEmpty(errorList);
    }

    private List<UserRewardImportModel> readExcelData(MultipartFile file, SchoolLanguageEnum schoolLanguageEnum, Integer type) {
        List<UserRewardImportModel> result = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            switch (schoolLanguageEnum) {
                case ZH_MO:
                    if (type == 1) {
                        StudentRewardImportZhTwListener importZhTwListener = new StudentRewardImportZhTwListener();
                        EasyExcel.read(inputStream, StudentRewardImportZhTwModel.class, importZhTwListener).sheet().doReadSync();
                        List<StudentRewardImportZhTwModel> importZhTwModels = importZhTwListener.getDataList();
                        result = importZhTwModels.stream().map(item -> {
                            UserRewardImportModel model = new UserRewardImportModel();
                            BeanUtils.copyProperties(item, model);
                            return model;
                        }).collect(Collectors.toList());
                    } else {
                        StudentPunishmentImportZhTwListener importZhTwListener = new StudentPunishmentImportZhTwListener();
                        EasyExcel.read(inputStream, StudentPunishmentImportZhTwModel.class, importZhTwListener).sheet().doReadSync();
                        List<StudentPunishmentImportZhTwModel> importZhTwModels = importZhTwListener.getDataList();
                        result = importZhTwModels.stream().map(item -> {
                            UserRewardImportModel model = new UserRewardImportModel();
                            BeanUtils.copyProperties(item, model);
                            return model;
                        }).collect(Collectors.toList());
                    }
                    break;
                case EN_US:
                    if (type == 1) {
                        StudentRewardImportEnUsListener importEnUsListener = new StudentRewardImportEnUsListener();
                        EasyExcel.read(inputStream, StudentRewardImportEnUsModel.class, importEnUsListener).sheet().doReadSync();
                        List<StudentRewardImportEnUsModel> importEnUsModels = importEnUsListener.getDataList();
                        result = importEnUsModels.stream().map(item -> {
                            UserRewardImportModel model = new UserRewardImportModel();
                            BeanUtils.copyProperties(item, model);
                            return model;
                        }).collect(Collectors.toList());
                    } else {
                        StudentPunishmentImportEnUsListener importEnUsListener = new StudentPunishmentImportEnUsListener();
                        EasyExcel.read(inputStream, StudentPunishmentImportEnUsModel.class, importEnUsListener).sheet().doReadSync();
                        List<StudentPunishmentImportEnUsModel> importEnUsModels = importEnUsListener.getDataList();
                        result = importEnUsModels.stream().map(item -> {
                            UserRewardImportModel model = new UserRewardImportModel();
                            BeanUtils.copyProperties(item, model);
                            return model;
                        }).collect(Collectors.toList());
                    }
                    break;
                case PT_PT:
                    if (type == 1) {
                        StudentRewardImportPtPtListener importPtPtListener = new StudentRewardImportPtPtListener();
                        EasyExcel.read(inputStream, StudentRewardImportPtPtModel.class, importPtPtListener).sheet().doReadSync();
                        List<StudentRewardImportPtPtModel> importPtPtModels = importPtPtListener.getDataList();
                        result = importPtPtModels.stream().map(item -> {
                            UserRewardImportModel model = new UserRewardImportModel();
                            BeanUtils.copyProperties(item, model);
                            return model;
                        }).collect(Collectors.toList());
                    } else {
                        StudentPunishmentImportPtPtListener importPtPtListener = new StudentPunishmentImportPtPtListener();
                        EasyExcel.read(inputStream, StudentPunishmentImportPtPtModel.class, importPtPtListener).sheet().doReadSync();
                        List<StudentPunishmentImportPtPtModel> importPtPtModels = importPtPtListener.getDataList();
                        result = importPtPtModels.stream().map(item -> {
                            UserRewardImportModel model = new UserRewardImportModel();
                            BeanUtils.copyProperties(item, model);
                            return model;
                        }).collect(Collectors.toList());
                    }
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            log.error("Excel文件读取失败", e);
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.FILE_READ_ERROR));
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @Override
    public PageInfo<UserRewardPendingPageResModel> pending(Long schoolId, Long userId, UserRewardPendingReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<UserRewardPendingPageResModel> list = this.getBaseMapper().pending(schoolId, userId, reqModel);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
            PageInfo<UserRewardPendingPageResModel> pageInfo = new PageInfo<>(list);
            List<Long> ids = pageInfo.getList().stream().map(UserRewardPendingPageResModel::getId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(ids)) {
                List<UserReward> userRewards = this.listByIds(ids);
                Map<Long, UserReward> userRewardMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(userRewards)) {
                    userRewardMap = userRewards.stream().collect(Collectors.toMap(UserReward::getId, userReward -> userReward));
                }
                for (UserRewardPendingPageResModel resModel : pageInfo.getList()) {
                    UserReward userReward = userRewardMap.get(resModel.getId());
                    if (userReward != null) {
                        if (userReward.getType() == 1) {
                            //奖励
                            if (userReward.getMaxReward() > 0) {
                                //大功
                                resModel.setType(UserRewardTypeEnum.GREAT_REWARD.getCode());
                                resModel.setFrequency(userReward.getMaxReward());
                            } else if (userReward.getMidReward() > 0) {
                                //小功
                                resModel.setType(UserRewardTypeEnum.SMALL_REWARD.getCode());
                                resModel.setFrequency(userReward.getMidReward());
                            } else {
                                //优点
                                resModel.setType(UserRewardTypeEnum.MERIT.getCode());
                                resModel.setFrequency(userReward.getMinReward());
                            }
                        } else {
                            //惩罚
                            if (userReward.getMaxReward() > 0) {
                                //大过
                                resModel.setType(UserRewardTypeEnum.GREAT_OFFENSE.getCode());
                                resModel.setFrequency(userReward.getMaxReward());
                            } else if (userReward.getMidReward() > 0) {
                                //小过
                                resModel.setType(UserRewardTypeEnum.SMALL_OFFENSE.getCode());
                                resModel.setFrequency(userReward.getMidReward());
                            } else {
                                //缺点
                                resModel.setType(UserRewardTypeEnum.DEMERIT.getCode());
                                resModel.setFrequency(userReward.getMinReward());
                            }
                        }
                    }
                }
            }
            return pageInfo;
        }
        return null;
    }

    @Override
    public PageInfo<UserRewardPendingPageResModel> approved(Long schoolId, Long userId, UserRewardPendingReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<UserRewardPendingPageResModel> list = this.getBaseMapper().approved(schoolId, userId, reqModel);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
            PageInfo<UserRewardPendingPageResModel> pageInfo = new PageInfo<>(list);
            //获取奖惩信息
            List<Long> ids = pageInfo.getList().stream().map(UserRewardPendingPageResModel::getId).collect(Collectors.toList());
            Map<Long, UserReward> userRewardMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(ids)) {
                List<UserReward> userRewards = this.listByIds(ids);
                if (!CollectionUtils.isEmpty(userRewards)) {
                    userRewardMap = userRewards.stream().collect(Collectors.toMap(UserReward::getId, userReward -> userReward));
                }
            }
            //获取拒绝审批历史
            List<Long> instanceIds = pageInfo.getList().stream().map(UserRewardPendingPageResModel::getInstanceId).collect(Collectors.toList());
            Map<Long, ActApprovalHistoryEntity> historyMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(instanceIds)) {
                QueryWrapper<ActApprovalHistoryEntity> wrapper = new QueryWrapper<>();
                wrapper.lambda().in(ActApprovalHistoryEntity::getInstanceId, instanceIds)
                        .eq(ActApprovalHistoryEntity::getApprovalResult, 2);
                List<ActApprovalHistoryEntity> historyList = actApprovalHistoryService.list(wrapper);
                if (!CollectionUtils.isEmpty(historyList)) {
                    historyMap = historyList.stream().collect(Collectors.toMap(ActApprovalHistoryEntity::getInstanceId, history -> history));
                }
            }
            for (UserRewardPendingPageResModel resModel : pageInfo.getList()) {
                UserReward userReward = userRewardMap.get(resModel.getId());
                if (userReward != null) {
                    if (userReward.getType() == 1) {
                        //奖励
                        if (userReward.getMaxReward() > 0) {
                            //大功
                            resModel.setType(UserRewardTypeEnum.GREAT_REWARD.getCode());
                            resModel.setFrequency(userReward.getMaxReward());
                        } else if (userReward.getMidReward() > 0) {
                            //小功
                            resModel.setType(UserRewardTypeEnum.SMALL_REWARD.getCode());
                            resModel.setFrequency(userReward.getMidReward());
                        } else {
                            //优点
                            resModel.setType(UserRewardTypeEnum.MERIT.getCode());
                            resModel.setFrequency(userReward.getMinReward());
                        }
                    } else {
                        //惩罚
                        if (userReward.getMaxReward() > 0) {
                            //大过
                            resModel.setType(UserRewardTypeEnum.GREAT_OFFENSE.getCode());
                            resModel.setFrequency(userReward.getMaxReward());
                        } else if (userReward.getMidReward() > 0) {
                            //小过
                            resModel.setType(UserRewardTypeEnum.SMALL_OFFENSE.getCode());
                            resModel.setFrequency(userReward.getMidReward());
                        } else {
                            //缺点
                            resModel.setType(UserRewardTypeEnum.DEMERIT.getCode());
                            resModel.setFrequency(userReward.getMinReward());
                        }
                    }
                }
                ActApprovalHistoryEntity actApprovalHistoryEntity = historyMap.get(resModel.getInstanceId());
                if (actApprovalHistoryEntity != null) {
                    resModel.setComment(actApprovalHistoryEntity.getComment());
                }
            }
            return pageInfo;
        }
        return null;
    }

    @Override
    public PageInfo<UserRewardAllListPageResModel> allList(Long schoolId, Long userId, UserRewardAllListReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(userId, schoolId);
        List<Long> classIds = new ArrayList<>();
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(userId, schoolId);
            if (CollectionUtils.isEmpty(classIds)) {
                return null;
            }
        }
        if (!CollectionUtils.isEmpty(classIds) && reqModel.getClassId() != null && reqModel.getClassId() > 0) {
            //普通用户班级权限不为空，请求选择了某个班级
            if (!classIds.contains(reqModel.getClassId())) {
                return null;
            }
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<UserRewardAllListPageResModel> list = this.getBaseMapper().allList(schoolId, userId, classIds, reqModel);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
            PageInfo<UserRewardAllListPageResModel> pageInfo = new PageInfo<>(list);
            //获取奖惩信息
            List<Long> ids = pageInfo.getList().stream().map(UserRewardAllListPageResModel::getId).collect(Collectors.toList());
            Map<Long, UserReward> userRewardMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(ids)) {
                List<UserReward> userRewards = this.listByIds(ids);
                if (!CollectionUtils.isEmpty(userRewards)) {
                    userRewardMap = userRewards.stream().collect(Collectors.toMap(UserReward::getId, userReward -> userReward));
                }
            }
            //获取拒绝审批历史
            List<Long> instanceIds = pageInfo.getList().stream().map(UserRewardAllListPageResModel::getInstanceId).collect(Collectors.toList());
            Map<Long, ActApprovalHistoryEntity> historyMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(instanceIds)) {
                QueryWrapper<ActApprovalHistoryEntity> wrapper = new QueryWrapper<>();
                wrapper.lambda().in(ActApprovalHistoryEntity::getInstanceId, instanceIds)
                        .eq(ActApprovalHistoryEntity::getApprovalResult, 2);
                List<ActApprovalHistoryEntity> historyList = actApprovalHistoryService.list(wrapper);
                if (!CollectionUtils.isEmpty(historyList)) {
                    historyMap = historyList.stream().collect(Collectors.toMap(ActApprovalHistoryEntity::getInstanceId, history -> history));
                }
            }
            for (UserRewardPendingPageResModel resModel : pageInfo.getList()) {
                UserReward userReward = userRewardMap.get(resModel.getId());
                if (userReward != null) {
                    if (userReward.getType() == 1) {
                        //奖励
                        if (userReward.getMaxReward() > 0) {
                            //大功
                            resModel.setType(UserRewardTypeEnum.GREAT_REWARD.getCode());
                            resModel.setFrequency(userReward.getMaxReward());
                        } else if (userReward.getMidReward() > 0) {
                            //小功
                            resModel.setType(UserRewardTypeEnum.SMALL_REWARD.getCode());
                            resModel.setFrequency(userReward.getMidReward());
                        } else {
                            //优点
                            resModel.setType(UserRewardTypeEnum.MERIT.getCode());
                            resModel.setFrequency(userReward.getMinReward());
                        }
                    } else {
                        //惩罚
                        if (userReward.getMaxReward() > 0) {
                            //大过
                            resModel.setType(UserRewardTypeEnum.GREAT_OFFENSE.getCode());
                            resModel.setFrequency(userReward.getMaxReward());
                        } else if (userReward.getMidReward() > 0) {
                            //小过
                            resModel.setType(UserRewardTypeEnum.SMALL_OFFENSE.getCode());
                            resModel.setFrequency(userReward.getMidReward());
                        } else {
                            //缺点
                            resModel.setType(UserRewardTypeEnum.DEMERIT.getCode());
                            resModel.setFrequency(userReward.getMinReward());
                        }
                    }
                }
                ActApprovalHistoryEntity actApprovalHistoryEntity = historyMap.get(resModel.getInstanceId());
                if (actApprovalHistoryEntity != null) {
                    resModel.setComment(actApprovalHistoryEntity.getComment());
                }
            }
            return pageInfo;
        }
        return null;
    }

    @Override
    public UserRewardInfoResModel info(Long schoolId, Long id) {
        UserRewardInfoResModel resModel = new UserRewardInfoResModel();
        UserReward userReward = this.getById(id);
        if (userReward != null) {
            resModel.setMeetingDate(userReward.getMeetingDate());
            resModel.setRewardReason(userReward.getRewardReason());
            resModel.setRegisterType(userReward.getRegisterType());
            // 校外活动关联信息
            if (userReward.getRegisterType() == 1 && userReward.getExternalCompetitionRecordId() != null) {
                //外部竞赛记录
                ExternalCompetitionRecordEntity externalCompetitionRecord = externalCompetitionRecordService.getById(userReward.getExternalCompetitionRecordId());
                if (externalCompetitionRecord != null) {
                    resModel.setAwardsRemark(externalCompetitionRecord.getAwardsRemark());
                    resModel.setApproveRemark(externalCompetitionRecord.getApproveRemark());
                }
            }
            if (userReward.getType() == 1) {
                //奖励
                if (userReward.getMaxReward() > 0) {
                    //大功
                    resModel.setType(UserRewardTypeEnum.GREAT_REWARD.getCode());
                    resModel.setFrequency(userReward.getMaxReward());
                } else if (userReward.getMidReward() > 0) {
                    //小功
                    resModel.setType(UserRewardTypeEnum.SMALL_REWARD.getCode());
                    resModel.setFrequency(userReward.getMidReward());
                } else {
                    //优点
                    resModel.setType(UserRewardTypeEnum.MERIT.getCode());
                    resModel.setFrequency(userReward.getMinReward());
                }
            } else {
                //惩罚
                if (userReward.getMaxReward() > 0) {
                    //大过
                    resModel.setType(UserRewardTypeEnum.GREAT_OFFENSE.getCode());
                    resModel.setFrequency(userReward.getMaxReward());
                } else if (userReward.getMidReward() > 0) {
                    //小过
                    resModel.setType(UserRewardTypeEnum.SMALL_OFFENSE.getCode());
                    resModel.setFrequency(userReward.getMidReward());
                } else {
                    //缺点
                    resModel.setType(UserRewardTypeEnum.DEMERIT.getCode());
                    resModel.setFrequency(userReward.getMinReward());
                }
            }
            resModel.setRemark(userReward.getRemark());
            //获取学生信息
            StudentEntity student = studentService.getById(userReward.getStudentId());
            if (student != null) {
                resModel.setStudentName(student.getChineseName());
                //获取班级信息
                SysClass sysClass = sysClassService.getById(student.getClassId());
                if (sysClass != null) {
                    resModel.setClassName(sysClass.getClassName());
                    //获取级组信息
                    GradeGroup gradeGroup = gradeGroupService.getById(sysClass.getGradeGroup());
                    if (gradeGroup != null) {
                        resModel.setGroupName(gradeGroup.getGradeGroupName());
                    }
                }
            }
            //查询流程信息
            QueryWrapper<ActApprovalInstanceEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(ActApprovalInstanceEntity::getBusinessId, id)
                    .eq(ActApprovalInstanceEntity::getProcessType, ActProcessTemplateTypeEnum.STUDENT_REWARD_PUNISHMENT.getCode());
            ActApprovalInstanceEntity approvalInstance = actApprovalInstanceService.getOne(wrapper);
            if (approvalInstance != null) {
                resModel.setStatus(approvalInstance.getStatus());
                resModel.setInitiatedStartTime(approvalInstance.getStartTime());
                resModel.setStartUserName(approvalInstance.getStartUserName());
                //获取审批节点信息
                QueryWrapper<ActInstanceNodeEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ActInstanceNodeEntity::getInstanceId, approvalInstance.getId());
                List<ActInstanceNodeEntity> nodeList = actInstanceNodeService.list(queryWrapper);
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(nodeList)) {
                    List<Long> userIds = nodeList.stream()
                            .flatMap(node -> JSON.parseArray(node.getApproverIds(), Long.class).stream())
                            .collect(Collectors.toList());
                    Map<Long, UserSchoolRelEntity> userSchoolRelMap = new HashMap<>();
                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(userIds)) {
                        QueryWrapper<UserSchoolRelEntity> queryWrapperUserSchoolRel = new QueryWrapper<>();
                        queryWrapperUserSchoolRel.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId)
                                .in(UserSchoolRelEntity::getUserId, userIds);
                        List<UserSchoolRelEntity> list = userSchoolRelService.list(queryWrapperUserSchoolRel);
                        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
                            userSchoolRelMap = list.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserId, userSchoolRelEntity -> userSchoolRelEntity));
                        }
                    }
                    List<ActApprovalInstanceInfoNodeResModel> nodes = new ArrayList<>();
                    //获取审批历史信息
                    QueryWrapper<ActApprovalHistoryEntity> queryWrapperHistory = new QueryWrapper<>();
                    queryWrapperHistory.lambda().eq(ActApprovalHistoryEntity::getInstanceId, approvalInstance.getId());
                    List<ActApprovalHistoryEntity> historyList = actApprovalHistoryService.list(queryWrapperHistory);
                    Map<String, List<ActApprovalHistoryEntity>> historyMap = new HashMap<>();
                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(historyList)) {
                        historyMap = historyList.stream().collect(Collectors.groupingBy(ActApprovalHistoryEntity::getNodeCode));
                    }
                    //获取抄送信息
                    QueryWrapper<ActApprovalCcEntity> queryWrapperCc = new QueryWrapper<>();
                    queryWrapperCc.lambda().eq(ActApprovalCcEntity::getInstanceId, approvalInstance.getId());
                    List<ActApprovalCcEntity> ccList = actApprovalCcService.list(queryWrapperCc);
                    Map<String, List<ActApprovalCcEntity>> ccMap = new HashMap<>();
                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(ccList)) {
                        ccMap = ccList.stream().collect(Collectors.groupingBy(ActApprovalCcEntity::getNodeCode));
                    }
                    for (ActInstanceNodeEntity actInstanceNodeEntity : nodeList) {
                        ActApprovalInstanceInfoNodeResModel node = new ActApprovalInstanceInfoNodeResModel();
                        node.setNodeType(actInstanceNodeEntity.getNodeType());
                        node.setNodeCode(actInstanceNodeEntity.getNodeCode());
                        node.setNodeName(actInstanceNodeEntity.getNodeName());
                        if (StringUtils.isNotBlank(actInstanceNodeEntity.getNodeFrom())) {
                            List<String> nodeFroms = JSON.parseArray(actInstanceNodeEntity.getNodeFrom(), String.class);
                            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(nodeFroms)) {
                                node.setNodeFrom(String.join(",", nodeFroms));
                            }
                        }
                        List<ActApprovalInstanceInfoNodeApproverResModel> approver = new ArrayList<>();
                        ActProcessNodeTypeEnum nodeType = ActProcessNodeTypeEnum.getByCode(actInstanceNodeEntity.getNodeType());
                        switch (nodeType) {
                            case APPROVER: // 审批人节点
                                List<Long> approverIds = JSON.parseArray(actInstanceNodeEntity.getApproverIds(), Long.class);
                                List<ActApprovalHistoryEntity> historyEntityList = historyMap.get(actInstanceNodeEntity.getNodeCode());
                                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(approverIds)) {
                                    Map<Long, ActApprovalHistoryEntity> historyEntityMap = new HashMap<>();
                                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(historyEntityList)) {
                                        historyEntityMap = historyEntityList.stream().collect(Collectors.toMap(ActApprovalHistoryEntity::getOperateUserId, actApprovalHistoryEntity -> actApprovalHistoryEntity));
                                    }
                                    //有审批人
                                    for (Long approverId : approverIds) {
                                        ActApprovalInstanceInfoNodeApproverResModel approverResModel = new ActApprovalInstanceInfoNodeApproverResModel();
                                        UserSchoolRelEntity userSchoolRel = userSchoolRelMap.get(approverId);
                                        if (userSchoolRel != null) {
                                            approverResModel.setUserName(userSchoolRel.getUsername());
                                        }
                                        ActApprovalHistoryEntity actApprovalHistoryEntity = historyEntityMap.get(approverId);
                                        if (actApprovalHistoryEntity != null) {
                                            approverResModel.setApprovalResult(actApprovalHistoryEntity.getApprovalResult());
                                            approverResModel.setApprovalTime(actApprovalHistoryEntity.getOperateTime());
                                            approverResModel.setComment(actApprovalHistoryEntity.getComment());
                                        }
                                        approver.add(approverResModel);
                                    }
                                } else {
                                    //无审批人，自动审批
                                    for (ActApprovalHistoryEntity actApprovalHistoryEntity : historyEntityList) {
                                        ActApprovalInstanceInfoNodeApproverResModel approverResModel = new ActApprovalInstanceInfoNodeApproverResModel();
                                        approverResModel.setUserName(actApprovalHistoryEntity.getOperateUserName());
                                        approverResModel.setApprovalResult(actApprovalHistoryEntity.getApprovalResult());
                                        approverResModel.setApprovalTime(actApprovalHistoryEntity.getOperateTime());
                                        approverResModel.setComment(actApprovalHistoryEntity.getComment());
                                        approver.add(approverResModel);
                                    }
                                }
                                break;
                            case COPY: // 抄送人节点
                                List<Long> copyIds = JSON.parseArray(actInstanceNodeEntity.getApproverIds(), Long.class);
                                List<ActApprovalCcEntity> ccEntityList = ccMap.get(actInstanceNodeEntity.getNodeCode());
                                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(copyIds)) {
                                    Map<Long, ActApprovalCcEntity> ccEntityMap = new HashMap<>();
                                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(ccEntityList)) {
                                        ccEntityMap = ccEntityList.stream().collect(Collectors.toMap(ActApprovalCcEntity::getCcUserId, actApprovalCcEntity -> actApprovalCcEntity));
                                    }
                                    //有审批人
                                    for (Long copyId : copyIds) {
                                        ActApprovalInstanceInfoNodeApproverResModel approverResModel = new ActApprovalInstanceInfoNodeApproverResModel();
                                        UserSchoolRelEntity userSchoolRel = userSchoolRelMap.get(copyId);
                                        if (userSchoolRel != null) {
                                            approverResModel.setUserName(userSchoolRel.getUsername());
                                        }
                                        ActApprovalCcEntity actApprovalCcEntity = ccEntityMap.get(copyId);
                                        if (actApprovalCcEntity != null) {
                                            approverResModel.setApprovalTime(actApprovalCcEntity.getCcTime());
                                        }
                                        approver.add(approverResModel);
                                    }
                                }
                                break;
                        }
                        node.setApprover(approver);
                        nodes.add(node);
                    }
                    resModel.setNodes(nodes);
                }
            }
        }
        return resModel;
    }

    @Override
    public List<StudentPerformanceTotalResModel> getTotal(StudentPerformanceTotalReqModel reqModel) {
        List<StudentPerformanceTotalResModel> resModels = new ArrayList<>();
        List<UserReward> list = this.list(new QueryWrapper<UserReward>().lambda()
                .eq(UserReward::getSchoolId, reqModel.getSchoolId())
                .eq(UserReward::getStudentId, reqModel.getStudentId())
                .eq(UserReward::getSid, reqModel.getSchoolYear())
                .eq(UserReward::getTerm, reqModel.getSemesterId()));
        if (CollectionUtils.isEmpty(list)) {
            Arrays.asList(8,9,10,11,12,13).forEach(typeEnum -> {
                StudentPerformanceTotalResModel resModel = new StudentPerformanceTotalResModel();
                resModel.setType(typeEnum);
                resModel.setNum(0);
                resModels.add(resModel);
            });
            return resModels;
        }
        Map<Integer, List<UserReward>> collect = list.stream().collect(Collectors.groupingBy(UserReward::getType));
        collect.forEach((key, value) -> {
            if (key == 1){
                resModels.add(StudentPerformanceTotalResModel.builder()
                        .type(8)
                        .num(value.stream().mapToInt(UserReward::getMinReward).sum())
                        .build());
                resModels.add(StudentPerformanceTotalResModel.builder()
                        .type(9)
                        .num(value.stream().mapToInt(UserReward::getMaxReward).sum())
                        .build());
                resModels.add(StudentPerformanceTotalResModel.builder()
                        .type(10)
                        .num(value.stream().mapToInt(UserReward::getMidReward).sum())
                        .build());
            } else if (key == 2){
                resModels.add(StudentPerformanceTotalResModel.builder()
                        .type(11)
                        .num(value.stream().mapToInt(UserReward::getMinReward).sum())
                        .build());
                resModels.add(StudentPerformanceTotalResModel.builder()
                        .type(12)
                        .num(value.stream().mapToInt(UserReward::getMaxReward).sum())
                        .build());
                resModels.add(StudentPerformanceTotalResModel.builder()
                        .type(13)
                        .num(value.stream().mapToInt(UserReward::getMidReward).sum())
                        .build());
            }
        });
        // 未奖励的类型
        Arrays.asList(8,9,10,11,12,13).forEach(typeEnum -> {
            if (resModels.stream().noneMatch(item -> item.getType() == typeEnum)) {
                resModels.add(StudentPerformanceTotalResModel.builder()
                        .type(typeEnum)
                        .num(0)
                        .build());
            }
        });
        return resModels;
    }

    @Override
    public Long exportPdf(Long schoolId, Long classId,Date startTime, Date endTime) {

        SysClass sysClass = sysClassService.getSysClassById(classId);
        if (sysClass == null)
        {
            log.error("班级不存在");
            return null;
        }
        //获取这个班级的全部学生
        List<StudentEntity> studentEntities = studentService.getStudentListByClassId(classId);
        if (CollectionUtils.isEmpty(studentEntities))
        {
            log.warn("班级没有学生");
            return null;
        }
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        String preFix = "pdf_export_" + UUID.randomUUID() + File.separator;
        File file = tempFileUtils.creteFolder(preFix);
        String fileName = "学生成绩单_"+ sysClass.getId() + "_" +sysClass.getClassName() + ".pdf";
        String exportFileName = tempFileUtils.getExportFileName(preFix, fileName);
        try {
            UserSchoolRelEntity relEntity = getUserSchoolRelEntity(schoolId, sysClass.getHeadTeacher());

            List<Long> studentIds = studentEntities.stream().map(StudentEntity::getId).collect(Collectors.toList());

            List<ConventionalPerformanceEntity> conventionalPerformances = getConventionalPerformance(studentIds,schoolId,startTime,endTime);

            List<UserRewardDTO> userRewards = calculateUserRewards(studentIds, startTime, endTime, schoolId);
            if (userRewards == null)
            {
                userRewards = new ArrayList<>();
            }
            Map<Long, List<UserRewardDTO>> userRewardMap = new HashMap<>();
            //to map
            if (!userRewards.isEmpty()){
                userRewardMap = userRewards.stream().collect(Collectors.groupingBy(UserRewardDTO::getStudentId));
            }

            List<StudentUsuallyTaskEntity> taskEntities = getStudentUsuallyTask(classId,startTime,endTime);
            //tomap
            Map<Long, StudentUsuallyTaskEntity> taskMap = taskEntities.stream().collect(Collectors.toMap(StudentUsuallyTaskEntity::getId, item -> item));
            List<Long> taskIds = taskEntities.stream().map(StudentUsuallyTaskEntity::getId).distinct().collect(Collectors.toList());
            List<Long> typeIds = taskEntities.stream().map(StudentUsuallyTaskEntity::getTypeId).distinct().collect(Collectors.toList());
            List<Long> subjectIds = taskEntities.stream().map(StudentUsuallyTaskEntity::getSubjectId).distinct().collect(Collectors.toList());
            List<Long> updateIds = taskEntities.stream().map(StudentUsuallyTaskEntity::getUpdateId).distinct().collect(Collectors.toList());
            Map<Long, List<StudentUsuallyScoreEntity>> scoreMap = getStudentUsuallyScoreMap(taskIds, studentIds);
            Map<Long, StudentUsuallyTypeEntity> typeMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(taskIds))
            {
                List<StudentUsuallyTypeEntity> studentUsuallyTypeEntities = studentUsuallyTypeService.listByIds(typeIds);
                typeMap = studentUsuallyTypeEntities.stream().collect(Collectors.toMap(StudentUsuallyTypeEntity::getId, item -> item));
            }
            //获取科目
            Map<Long, SubjectRelResModel> subjectMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(subjectIds)) {
                List<SubjectRelResModel> relResModels = subjectRelService.listByIds(subjectIds);
                if (relResModels == null) {
                    relResModels = new ArrayList<>();
                }
                subjectMap = relResModels.stream().collect(Collectors.toMap(SubjectRelResModel::getId, item -> item));
            }
            Map<Long, UserSchoolRelEntity> updateMap = getUpdateMap(updateIds,schoolId);
            String currentLanguage = languageUtil.getCurrentLanguage();
            SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);

            Map<Long, List<UserReward>> rewardMap = getRewardMap(schoolId,studentIds,startTime,endTime);
            String message = languageUtil.getMessage(LanguageConstants.DEMERIT_TIMES);
            //查询全部奖惩
            List<SemesterEntity> list = getSemesterEntity(schoolId, sysClass.getSid(),sysClass.getDepartment());
            List<Long> sids = list.stream().map(SemesterEntity::getId).collect(Collectors.toList());

            Map<Long, List<UserReward>> allRewardMap = getAllRewardMap(schoolId,studentIds,sids);

            String startTimeString = DateUtils.formatDateToString(DateUtils.toLocalDateTime(startTime), PATTERN);
            String endTimeString = DateUtils.formatDateToString(DateUtils.toLocalDateTime(endTime), PATTERN);
            String reportTime =  startTimeString + "-" + endTimeString;

            Map<Long, List<StudentLeaveEntity>> leaveMap = getLeaveMap(schoolId, studentIds, startTime, endTime,sysClass);

            Map<Long, List<StudentBusinessEntity>> officialMap = getOfficialMap(schoolId, studentIds, startTime, endTime,sysClass);

            Map<Long, List<StudentAttendanceEntity>> attendanceMap = getAttendanceMap(studentIds, startTime, endTime);

            Map<Long, List<StudentLeaveEntity>> studentLeaveMap = getStudentLeaveMap(schoolId, studentIds, startTime, endTime);

            List<StudentRewardExportDTO> items = new ArrayList<>();
            studentEntities.sort(Comparator.comparing(
                    StudentEntity::getSeatNo,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ));
            for (StudentEntity studentEntity : studentEntities)
            {
                List<StudentUsuallyScoreEntity> scoreEntities = scoreMap.get(studentEntity.getId());
                List<UserReward> userRewardList = rewardMap.get(studentEntity.getId());
                List<UserRewardDTO> userReward = userRewardMap.get(studentEntity.getId());
                List<UserReward> allUserReward = allRewardMap.get(studentEntity.getId());
                List<StudentLeaveEntity> leaveEntityList = leaveMap.get(studentEntity.getId());
                List<StudentBusinessEntity> studentBusinessEntities = officialMap.get(studentEntity.getId());
                List<StudentAttendanceEntity> studentAttendanceEntities = attendanceMap.get(studentEntity.getId());
                List<StudentLeaveEntity> studentLeaveEntities = studentLeaveMap.get(studentEntity.getId());
                StudentRewardExportDTO studentRewardExportDTO = getStudentRewardExportDTO(studentEntity, relEntity, sysClass, reportTime, endTimeString, userReward,
                        scoreEntities, taskMap, typeMap, subjectMap, updateMap, languageEnum, userRewardList, message, allUserReward, list,leaveEntityList,
                        conventionalPerformances,studentBusinessEntities,studentAttendanceEntities, studentLeaveEntities);
                items.add(studentRewardExportDTO);
            }
            //to pdf
            Map<String, Object> toHtmlData = convertToHtmlDataList(items);
            switch (languageEnum) {
                case EN_US:
                    pdfService.generatePdfInDir("student_management_en.html",toHtmlData,exportFileName);
                    break;
                case PT_PT:
                    pdfService.generatePdfInDir("student_management_pt.html",toHtmlData,exportFileName);
                    break;
                default:
                    pdfService.generatePdfInDir("student_management.html",toHtmlData,exportFileName);
            }
        }catch (Exception e)
        {
            log.error("导出失败", e);
        }finally {
            try {
                if(zos != null)
                {
                    zos.close();
                }
                if(fos != null)
                {
                    fos.close();
                }
            }catch (Exception e)
            {
                log.error("关闭流异常", e);
            }
        }
        byte[] bytes = tempFileUtils.fileToBetyArray(exportFileName);
        String url = fileUploadService.upload(bytes, FileTypeEnum.PDF, fileName, schoolId);
        ExportRecord exportRecord = new ExportRecord();
        exportRecord.setSchoolId(schoolId);
        exportRecord.setType(1);
        exportRecord.setUrl(url);
        exportRecord.setRelId(classId);
        exportRecord.setStartTime(startTime);
        exportRecord.setEndTime(endTime);
        exportRecordService.save(exportRecord);
        //删除文件
        tempFileUtils.deleteFolder(file);
        return exportRecord.getId();
    }

    private Map<Long, List<StudentLeaveEntity>> getStudentLeaveMap(Long schoolId, List<Long> studentIds, Date startTime, Date endTime) {
        // 学生课堂迟到
        List<StudentLeaveEntity> studentLeaveList = studentLeaveDao.selectList(Wrappers.<StudentLeaveEntity>lambdaQuery()
                .eq(StudentLeaveEntity::getLeaveType, "3")
                .in(StudentLeaveEntity::getStudentId, studentIds)
                .between(StudentLeaveEntity::getLeaveDate, startTime, endTime));
        if (ObjectUtils.isEmpty(studentLeaveList)) {
            studentLeaveList = new ArrayList<>();
        }
        Map<Long, List<StudentLeaveEntity>> studentLeaveMap = studentLeaveList.stream().collect(Collectors.groupingBy(StudentLeaveEntity::getStudentId));
        return studentLeaveMap;
    }

    private Map<Long, List<StudentAttendanceEntity>> getAttendanceMap(List<Long> studentIds, Date startTime, Date endTime) {
        // 入校迟到
        List<StudentAttendanceEntity> attendanceList = studentAttendanceService.list(Wrappers.<StudentAttendanceEntity>lambdaQuery()
                //筛选1,2,3中的1 FIND_IN_SET
                .apply("FIND_IN_SET(1,status)")
                .in(StudentAttendanceEntity::getStudentId, studentIds)
                .between(StudentAttendanceEntity::getAttendanceDate, startTime, endTime));
        if (ObjectUtils.isEmpty(attendanceList)) {
            attendanceList = new ArrayList<>();
        } else {
            Map<Long, List<StudentAttendanceEntity>> listMap = attendanceList.stream()
                    .collect(Collectors.groupingBy(StudentAttendanceEntity::getStudentId));
            attendanceList = studentAttendanceService.getFilterRecords(listMap);
        }
        //tomap
        Map<Long, List<StudentAttendanceEntity>> attendanceMap = attendanceList.stream().collect(Collectors.groupingBy(StudentAttendanceEntity::getStudentId));

        return attendanceMap;
    }

    private Map<Long, List<StudentBusinessEntity>> getOfficialMap(Long schoolId, List<Long> studentIds, Date startTime, Date endTime, SysClass sysClass) {
        //公务
        LambdaQueryWrapper<StudentBusinessEntity> officialQueryWrapper = new LambdaQueryWrapper<>();
        officialQueryWrapper.eq(StudentBusinessEntity::getSchoolId, schoolId);
        officialQueryWrapper.in(StudentBusinessEntity::getStudentId, studentIds);
        officialQueryWrapper.eq(StudentBusinessEntity::getClassId, sysClass.getId());
        officialQueryWrapper.ge(StudentBusinessEntity::getEndTime, startTime);
        officialQueryWrapper.le(StudentBusinessEntity::getStartTime, endTime);
        List<StudentBusinessEntity> officialEntities = studentBusinessService.list(officialQueryWrapper);
        if (officialEntities == null)
        {
            officialEntities = new ArrayList<>();
        }
        Map<Long, List<StudentBusinessEntity>> officialMap = officialEntities.stream().collect(Collectors.groupingBy(StudentBusinessEntity::getStudentId));
        return officialMap;
    }

    private Map<Long, List<StudentLeaveEntity>> getLeaveMap(Long schoolId, List<Long> studentIds, Date startTime, Date endTime, SysClass sysClass) {
        //请假
        LambdaQueryWrapper<StudentLeaveEntity> leaveQueryWrapper = new LambdaQueryWrapper<>();
        leaveQueryWrapper.eq(StudentLeaveEntity::getSchoolId, schoolId);
        leaveQueryWrapper.in(StudentLeaveEntity::getStudentId, studentIds);
        leaveQueryWrapper.eq(StudentLeaveEntity::getClassId, sysClass.getId());
        leaveQueryWrapper.eq(StudentLeaveEntity::getSchoolYear, sysClass.getSid());
        leaveQueryWrapper.between(StudentLeaveEntity::getLeaveDate, startTime, endTime);
        List<StudentLeaveEntity> leaveEntities = studentLeaveService.list(leaveQueryWrapper);
        if (leaveEntities == null)
        {
            leaveEntities = new ArrayList<>();
        }
        Map<Long, List<StudentLeaveEntity>> leaveMap = leaveEntities.stream().filter(item -> item.getLeaveType() ==
                        StudentLeaveTypeEnum.LEAVE.getCode() || item.getLeaveType() == StudentLeaveTypeEnum.ABSENT.getCode())
                .collect(Collectors.groupingBy(StudentLeaveEntity::getStudentId));

        return leaveMap;
    }

    private Map<Long, List<UserReward>> getAllRewardMap(Long schoolId, List<Long> studentIds, List<Long> sids) {
        Map<Long, List<UserReward>> allRewardMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(sids)){
            LambdaQueryWrapper<UserReward> allRewardQueryWrapper = new LambdaQueryWrapper<>();
            allRewardQueryWrapper.eq(UserReward::getSchoolId, schoolId);
            allRewardQueryWrapper.in(UserReward::getStudentId, studentIds);
            allRewardQueryWrapper.eq(UserReward::getType, 2);
            allRewardQueryWrapper.eq(UserReward::getIsAuto, 1);
            allRewardQueryWrapper.in(UserReward::getTerm, sids);
            List<UserReward> allRewardEntities = this.list(allRewardQueryWrapper);
            if (allRewardEntities == null)
            {
                allRewardEntities = new ArrayList<>();
            }
            if (!CollectionUtils.isEmpty(studentIds)){
                List<UserReward> userRewardList = this.baseMapper.getUserRewardByActApproval(studentIds, sids, schoolId,null,null);
                if (!CollectionUtils.isEmpty(userRewardList))
                {
                    allRewardEntities.addAll(userRewardList);
                }
            }
            //tomap
            allRewardMap = allRewardEntities.stream().collect(Collectors.groupingBy(UserReward::getStudentId));
        }
        return allRewardMap;
    }

    private List<SemesterEntity> getSemesterEntity(Long schoolId, String sid, Integer department) {
        LambdaQueryWrapper<SemesterEntity> semesterQueryWrapper = new LambdaQueryWrapper<>();
        semesterQueryWrapper.eq(SemesterEntity::getSchoolYear, sid);
        semesterQueryWrapper.eq(SemesterEntity::getSchoolId, schoolId);
        semesterQueryWrapper.eq(SemesterEntity::getDepartment, department);
        List<SemesterEntity> list = semesterService.list(semesterQueryWrapper);
        if (list == null)
        {
            list = new ArrayList<>();
        }
        return list;
    }

    private Map<Long, List<UserReward>> getRewardMap(Long schoolId, List<Long> studentIds, Date startTime, Date endTime) {
        if (CollectionUtils.isEmpty(studentIds))
        {
            return new HashMap<>();
        }
        //查询惩罚数据
        List<UserReward> rewardEntities = this.baseMapper.getUserRewardByActApproval(studentIds, null, schoolId, startTime, endTime);
        if (rewardEntities == null)
        {
            rewardEntities = new ArrayList<>();
        }
        // to map
        Map<Long, List<UserReward>> rewardMap = rewardEntities.stream().collect(Collectors.groupingBy(UserReward::getStudentId));
        return rewardMap;
    }

    private Map<Long, UserSchoolRelEntity> getUpdateMap(List<Long> updateIds,Long schoolId) {
        //获取老师
        Map<Long, UserSchoolRelEntity> updateMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(updateIds)) {
            LambdaQueryWrapper<UserSchoolRelEntity> schoolRelEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            schoolRelEntityLambdaQueryWrapper.in(UserSchoolRelEntity::getUserId, updateIds);
            schoolRelEntityLambdaQueryWrapper.eq(UserSchoolRelEntity::getSchoolId, schoolId);
            List<UserSchoolRelEntity> userSchoolRelEntities = userSchoolRelService.list(schoolRelEntityLambdaQueryWrapper);
            if(userSchoolRelEntities == null)
            {
                userSchoolRelEntities = new ArrayList<>();
            }
            updateMap = userSchoolRelEntities.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserId, Function.identity(),(item,item1) -> item));
        }
        return updateMap;
    }

    private Map<Long, List<StudentUsuallyScoreEntity>> getStudentUsuallyScoreMap(List<Long> taskIds, List<Long> studentIds) {
        Map<Long, List<StudentUsuallyScoreEntity>> scoreMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(taskIds))
        {
            LambdaQueryWrapper<StudentUsuallyScoreEntity> scoreQueryWrapper = new LambdaQueryWrapper<>();
            scoreQueryWrapper.in(StudentUsuallyScoreEntity::getStudentId, studentIds);
            scoreQueryWrapper.in(StudentUsuallyScoreEntity::getTaskId, taskIds);
            scoreQueryWrapper.lt(StudentUsuallyScoreEntity::getScore, 6000);
            List<StudentUsuallyScoreEntity> scoreEntities = studentUsuallyScoreService.list(scoreQueryWrapper);
            if (scoreEntities == null)
            {
                scoreEntities = new ArrayList<>();
            }
            //tomap
            scoreMap = scoreEntities.stream().collect(Collectors.groupingBy(StudentUsuallyScoreEntity::getStudentId));
        }
        return scoreMap;
    }

    private List<StudentUsuallyTaskEntity> getStudentUsuallyTask(Long classId, Date startTime, Date endTime) {
        //查询平时成绩
        LambdaQueryWrapper<StudentUsuallyTaskEntity> taskQueryWrapper = new LambdaQueryWrapper<>();
        taskQueryWrapper.eq(StudentUsuallyTaskEntity::getClassId, classId);
        taskQueryWrapper.ge(StudentUsuallyTaskEntity::getTestDate, startTime);
        taskQueryWrapper.le(StudentUsuallyTaskEntity::getTestDate, endTime);
        List<StudentUsuallyTaskEntity> taskEntities = studentUsuallyTaskService.list(taskQueryWrapper);
        if (taskEntities == null)
        {
            taskEntities = new ArrayList<>();
        }
        return taskEntities;
    }

    private List<ConventionalPerformanceEntity> getConventionalPerformance(List<Long> studentIds, Long schoolId, Date startTime, Date endTime) {
        // 查询conventional_performance数据
        List<ConventionalPerformanceEntity> conventionalPerformances = conventionalPerformanceService.list(
                Wrappers.<ConventionalPerformanceEntity>lambdaQuery()
                        .in(ConventionalPerformanceEntity::getStudentId, studentIds)
                        .eq(ConventionalPerformanceEntity::getSchoolId, schoolId)
                        .ge(ConventionalPerformanceEntity::getDate, startTime)
                        .le(ConventionalPerformanceEntity::getDate, endTime)
        );
        if (conventionalPerformances == null) {
            conventionalPerformances = new ArrayList<>();
        }
        return conventionalPerformances;
    }

    private UserSchoolRelEntity getUserSchoolRelEntity(Long schoolId, Long headTeacher) {
        LambdaQueryWrapper<UserSchoolRelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserSchoolRelEntity::getSchoolId, schoolId);
        queryWrapper.eq(UserSchoolRelEntity::getId, headTeacher);
        UserSchoolRelEntity relEntity = userSchoolRelService.getOne(queryWrapper);
        return relEntity;
    }


    private StudentRewardExportDTO getStudentRewardExportDTO(StudentEntity student,UserSchoolRelEntity relEntity,
                                                             SysClass sysClass,String reportTime,String updateTime,
                                                             List<UserRewardDTO> userRewards,List<StudentUsuallyScoreEntity> scoreEntities,
                                                             Map<Long, StudentUsuallyTaskEntity> taskMap,Map<Long, StudentUsuallyTypeEntity> typeMap,
                                                             Map<Long, SubjectRelResModel> subjectMap,Map<Long, UserSchoolRelEntity> updateMap,
                                                             SchoolLanguageEnum languageEnum,List<UserReward> userRewardList,String message,
                                                             List<UserReward> allRewards,List<SemesterEntity> semesterResModels,List<StudentLeaveEntity> leaveEntities,
                                                             List<ConventionalPerformanceEntity> conventionalPerformances,List<StudentBusinessEntity> studentBusinessEntities,
                                                             List<StudentAttendanceEntity> studentAttendanceEntities,List<StudentLeaveEntity> studentLeaveEntities) {
        StudentRewardExportDTO studentRewardExportDTO = new StudentRewardExportDTO();
        studentRewardExportDTO.setTeacherName(relEntity == null ? "" : relEntity.getUsername());
        studentRewardExportDTO.setTeacherNumber(relEntity == null ? "" : relEntity.getUserNumber());
        studentRewardExportDTO.setClassNumber(sysClass.getClassNumber());
        studentRewardExportDTO.setReportTime(reportTime);
        studentRewardExportDTO.setUpdateTime(updateTime);
        String seatNo = student.getSeatNo() == null ? "" : student.getSeatNo().toString();
        studentRewardExportDTO.setStudentNumber(parseSchoolYear(sysClass.getSid()) + sysClass.getClassNumber() + seatNo);
        studentRewardExportDTO.setStudentName(student.getChineseName());
        //拼接不合格成绩
        List<StudentRewardExportDTO.Record> records = new ArrayList<>();
        if (!CollectionUtils.isEmpty(scoreEntities)) {
            for (StudentUsuallyScoreEntity scoreEntity : scoreEntities) {
                StudentRewardExportDTO.Record record = new StudentRewardExportDTO.Record();
                StudentUsuallyTaskEntity studentUsuallyTaskEntity = taskMap.get(scoreEntity.getTaskId());
                if (studentUsuallyTaskEntity == null) {
                    continue;
                }
                //获取科目
                SubjectRelResModel subjectRelResModel = subjectMap.get(studentUsuallyTaskEntity.getSubjectId());
                record.setActivityName(subjectRelResModel.getSubject() == null ? "" : subjectRelResModel.getSubject().getSubjectName());
                //类型
                StudentUsuallyTypeEntity studentUsuallyTypeEntity = typeMap.get(studentUsuallyTaskEntity.getTypeId());
                record.setCareLevel(studentUsuallyTypeEntity == null ? "" : studentUsuallyTypeEntity.getTypeName());
                record.setDate(DateUtils.formatDateToString(studentUsuallyTaskEntity.getTestDate().atStartOfDay(), DEFAULT_PATTERN));
                //分数/100保留2位小数
                record.setScore(scoreEntity.getScore() == null ? "" : BigDecimal.valueOf(scoreEntity.getScore())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP).toString());
                UserSchoolRelEntity userSchoolRelEntity = updateMap.get(scoreEntity.getUpdateId());
                record.setTeacherName(userSchoolRelEntity == null ? "" : userSchoolRelEntity.getUsername());
                records.add(record);
            }
        }
        studentRewardExportDTO.setRecords(records);

        //拼接形成性明细和计算小结
        List<StudentRewardExportDTO.Summary> summaries = new ArrayList<>();
        List<StudentRewardExportDTO.Formative> formatives = new ArrayList<>();

        
        // 使用传入的conventional_performance数据，按学生ID过滤
        List<ConventionalPerformanceEntity> studentConventionalPerformances = conventionalPerformances.stream()
            .filter(performance -> performance.getStudentId().equals(student.getId()))
            .collect(Collectors.toList());
        
        // 形成性部分：直接使用conventional_performance表的原始记录，不拆解frequency
        if (!CollectionUtils.isEmpty(studentConventionalPerformances)) {
            for (ConventionalPerformanceEntity performance : studentConventionalPerformances) {
                String name = "";
                // 根据类型获取名称
                switch (performance.getType()) {
                    case 1:
                        name = "上課違規";
                        break;
                    case 2:
                        name = "欠作業";
                        break;
                    case 3:
                        name = "儀表不符";
                        break;
                    case 5:
                        name = "欠課本";
                        break;
                    case 7:
                        name = "欠回條";
                        break;
                }
                
                // 多语言处理
                switch (languageEnum) {
                    case EN_US:
                        switch (performance.getType()) {
                            case 1:
                                name = "Classroom Violation";
                                break;
                            case 2:
                                name = "Missing Homework";
                                break;
                            case 3:
                                name = "Improper Appearance";
                                break;
                            case 5:
                                name = "Missing Textbook";
                                break;
                            case 7:
                                name = "Missing Callback";
                                break;
                        }
                        break;
                    case PT_PT:
                        switch (performance.getType()) {
                            case 1:
                                name = "Violação de Sala de Aula";
                                break;
                            case 2:
                                name = "Trabalho de Casa em Falta";
                                break;
                            case 3:
                                name = "Aparência Imprópria";
                                break;
                            case 5:
                                name = "Livro Didático em Falta";
                                break;
                            case 7:
                                name = "Retorno em Falta";
                                break;
                        }
                        break;
                }
                
                String byCode = DepartmentLanguageEnum.getByCode(sysClass.getDepartment(), languageEnum);
                
                StudentRewardExportDTO.Formative formative = new StudentRewardExportDTO.Formative();
                formative.setType(name);
                formative.setDate(DateUtils.formatDateToString(performance.getDate().atStartOfDay(), DEFAULT_PATTERN));
                formative.setProject(byCode + "_" + name);
                formative.setSum(performance.getFrequency() + ""); // 直接使用frequency字段，不拆解
                
                // conventional_performance表的数据不需要备注
                formative.setRemarks(performance.getRemark());
                formatives.add(formative);
            }
        }
        //迟到 入校
        if (!CollectionUtils.isEmpty(studentAttendanceEntities))
        {
            for (StudentAttendanceEntity studentAttendanceEntity : studentAttendanceEntities) {
                StudentRewardExportDTO.Formative formative = new StudentRewardExportDTO.Formative();
                formative.setDate(DateUtils.formatDateToString(studentAttendanceEntity.getAttendanceDate().atStartOfDay(), DEFAULT_PATTERN));
                String schoolName = "入校";
                String name = UserRewardEnum.getName(3, languageEnum);
                String byCode = DepartmentLanguageEnum.getByCode(sysClass.getDepartment(), languageEnum);
                switch (languageEnum)
                {
                    case ZH_MO:
                        break;
                    case EN_US:
                        schoolName = "school entry";
                        break;
                    case PT_PT:
                        schoolName = "Entrada da escola";
                        break;
                }
                formative.setType(name + "(" + schoolName + ")");
                formative.setProject(byCode + "_" + formative.getType());
                formative.setSum("1");
                formative.setRemarks("");
                formatives.add(formative);
            }
        }
        //迟到课堂
        if (!CollectionUtils.isEmpty(studentLeaveEntities))
        {
            for (StudentLeaveEntity studentLeaveEntity : studentLeaveEntities) {
                StudentRewardExportDTO.Formative formative = new StudentRewardExportDTO.Formative();
                formative.setDate(DateUtils.formatDateToString(studentLeaveEntity.getLeaveDate().atStartOfDay(), DEFAULT_PATTERN));
                String name = UserRewardEnum.getName(3, languageEnum);
                String byCode = DepartmentLanguageEnum.getByCode(sysClass.getDepartment(), languageEnum);
                formative.setType(name);
                formative.setProject(byCode + "_" + formative.getType());
                formative.setSum("1");
                switch (languageEnum) {
                    case ZH_MO:
                        formative.setRemarks(studentLeaveEntity.getPeriods() + "節"); // 设置节数
                        break;
                    case EN_US:
                        formative.setRemarks(studentLeaveEntity.getPeriods() + "periods");
                        break;
                    case PT_PT:
                        formative.setRemarks(studentLeaveEntity.getPeriods() + "períodos");
                        break;
                }
                formatives.add(formative);
            }
        }
        
        // 计算小结部分：使用原有的拆解逻辑，按frequency拆分成多条记录
        if (!CollectionUtils.isEmpty(userRewards)) {
            for (UserRewardDTO userReward : userRewards) {
                String name = UserRewardEnum.getName(userReward.getAutoType(), languageEnum);
                String byCode = DepartmentLanguageEnum.getByCode(sysClass.getDepartment(), languageEnum);
                
                //迟到特殊处理 - 分别处理课堂迟到和入校迟到的明细
                if(userReward.getAutoType().equals(3))
                {
                    String schoolName = "入校";
                    switch (languageEnum)
                    {
                        case ZH_MO:
                            break;
                        case EN_US:
                            schoolName = "school entry";
                            break;
                        case PT_PT:
                            schoolName = "Entrada da escola";
                            break;
                    }
                    
                    // 处理课堂迟到明细
                    List<UserRewardDetailsDTO> details1 = userReward.getDetails1();
                    if (details1 != null && !details1.isEmpty()){
                        for (UserRewardDetailsDTO detail1 : details1) {
                            StudentRewardExportDTO.Summary summary = new StudentRewardExportDTO.Summary();
                            summary.setDate(DateUtils.formatDateToString(detail1.getDate().atStartOfDay(), DEFAULT_PATTERN));
                            summary.setProject(byCode + "_" + name);
                            summary.setRemarks(detail1.getNumber() + " x " + summary.getProject());
                            // 根据detail的penaltyType字段判断惩罚类型
                            String penaltyTypeName = getPenaltyTypeNameFromDetail(detail1, languageEnum);
                            summary.setRecords(penaltyTypeName + " " + String.format(message,detail1.getReportNumber()));
                            summaries.add(summary);
                        }
                    }
                    
                    // 处理入校迟到明细
                    List<UserRewardDetailsDTO> details = userReward.getDetails();
                    if (details != null && !details.isEmpty()){
                        for (UserRewardDetailsDTO detail : details) {
                            StudentRewardExportDTO.Summary summary = new StudentRewardExportDTO.Summary();
                            summary.setDate(DateUtils.formatDateToString(detail.getDate().atStartOfDay(), DEFAULT_PATTERN));
                            summary.setProject(byCode + "_" + name + "(" + schoolName + ")");
                            summary.setRemarks(detail.getNumber() + " x " + summary.getProject());
                            // 根据detail的penaltyType字段判断惩罚类型
                            String penaltyTypeName = getPenaltyTypeNameFromDetail(detail, languageEnum);
                            summary.setRecords(penaltyTypeName + " " + String.format(message,detail.getReportNumber()));
                            summaries.add(summary);
                        }
                    }
                }else if(userReward.getAutoType().equals(5))
                {
                    List<UserRewardDetailsDTO> details = userReward.getDetails1();
                    if (details != null && !details.isEmpty()){
                        for (UserRewardDetailsDTO detail : details) {
                            StudentRewardExportDTO.Summary summary = new StudentRewardExportDTO.Summary();
                            summary.setDate(DateUtils.formatDateToString(detail.getDate().atStartOfDay(), DEFAULT_PATTERN));
                            summary.setProject(byCode + "_" + name);
                            summary.setRemarks(detail.getNumber() + " x " + summary.getProject());
                            // 根据detail的penaltyType字段判断惩罚类型
                            String penaltyTypeName = getPenaltyTypeNameFromDetail(detail, languageEnum);
                            summary.setRecords(penaltyTypeName + " " + String.format(message,detail.getReportNumber()));
                            summaries.add(summary);
                        }
                    }
                } else {
                    // 处理其他类型的明细
                    List<UserRewardDetailsDTO> details = userReward.getDetails();
                    if (details != null && !details.isEmpty()){
                        for (UserRewardDetailsDTO detail : details) {
                            StudentRewardExportDTO.Summary summary = new StudentRewardExportDTO.Summary();
                            summary.setDate(DateUtils.formatDateToString(detail.getDate().atStartOfDay(), DEFAULT_PATTERN));
                            summary.setProject(byCode + "_" + name);
                            summary.setRemarks(detail.getNumber() + " x " + summary.getProject());
                            // 根据detail的penaltyType字段判断惩罚类型
                            String penaltyTypeName = getPenaltyTypeNameFromDetail(detail, languageEnum);
                            summary.setRecords(penaltyTypeName + " " + String.format(message,detail.getReportNumber()));
                            summaries.add(summary);
                        }
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(leaveEntities))
        {
            for (StudentLeaveEntity leaveEntity : leaveEntities)
            {
                StudentRewardExportDTO.Formative formative = new StudentRewardExportDTO.Formative();

                String name = StudentLeaveTypeEnum.getValueByLanguage(leaveEntity.getLeaveType(), languageEnum);
                formative.setType(name);
                formative.setDate(DateUtils.formatDateToString(leaveEntity.getLeaveDate().atStartOfDay(), DEFAULT_PATTERN));
                String byCode = DepartmentLanguageEnum.getByCode(sysClass.getDepartment(), languageEnum);
                formative.setProject(byCode + "_" + name);
                formative.setSum("1");
                switch (languageEnum)
                {
                    case ZH_MO:
                        formative.setRemarks(leaveEntity.getPeriods() +"節");
                        break;
                    case EN_US:
                        formative.setRemarks(leaveEntity.getPeriods() +"periods");
                        break;
                    case PT_PT:
                        formative.setRemarks(leaveEntity.getPeriods() +"períodos");
                        break;
                }
                formatives.add(formative);
                
            }
        }

        studentRewardExportDTO.setFormative(formatives);


        if (!CollectionUtils.isEmpty(userRewardList))
        {
            for (UserReward userReward : userRewardList)
            {
                StudentRewardExportDTO.Summary summary = new StudentRewardExportDTO.Summary();
                summary.setDate(DateUtils.formatDateToString(userReward.getMeetingDate().atStartOfDay(), DEFAULT_PATTERN));
                summary.setProject(userReward.getRewardReason());
                summary.setRemarks(userReward.getRemark());
                if (userReward.getMaxReward() > 0)
                {
                    String penaltyTypeName = getTypeNameFromDetail("1", languageEnum);
                    summary.setRecords(penaltyTypeName + " " + String.format(message,userReward.getMaxReward()));
                }else if (userReward.getMidReward() > 0)
                {
                    String penaltyTypeName = getTypeNameFromDetail("2", languageEnum);
                    summary.setRecords(penaltyTypeName + " " + String.format(message,userReward.getMidReward()));
                }else {
                    String penaltyTypeName = getTypeNameFromDetail("3", languageEnum);
                    summary.setRecords(penaltyTypeName + " " + String.format(message,userReward.getMinReward()));
                }
                summaries.add(summary);
            }
        }
        studentRewardExportDTO.setSummary(summaries);

        Map<Long, List<UserReward>> rewardMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(allRewards))
        {
            //tomap
            rewardMap = allRewards.stream().collect(Collectors.groupingBy(UserReward::getTerm));
        }

        List<StudentRewardExportDTO.YearRecord> yearRecord = new ArrayList<>();
        int i = 0;
        if (!CollectionUtils.isEmpty(semesterResModels)) {
            for (SemesterEntity semesterResModel : semesterResModels) {
                //now
                if (semesterResModel.getStartTime().isAfter(LocalDateTime.now()))
                {
                    continue;
                }
                StudentRewardExportDTO.YearRecord record = new StudentRewardExportDTO.YearRecord();
                record.setName(semesterResModel.getName());
                record.setNumber(studentRewardExportDTO.getStudentNumber());
                record.setIndex(String.valueOf(++i));
                List<UserReward> rewardList = rewardMap.get(semesterResModel.getId());
                if (!CollectionUtils.isEmpty(rewardList)) {
                    //求和
                    int maxSum = rewardList.stream().mapToInt(UserReward::getMaxReward).sum();
                    record.setBig(maxSum + "");
                    int minSum = rewardList.stream().mapToInt(UserReward::getMidReward).sum();
                    record.setMini(minSum + "");
                    int defectSum = rewardList.stream().mapToInt(UserReward::getMinReward).sum();
                    record.setDefect(defectSum + "");
                }
                yearRecord.add(record);
            }
        }
        studentRewardExportDTO.setYearRecord(yearRecord);

        //公务
        List<StudentRewardExportDTO.Office> offices = new ArrayList<>();
        if (!CollectionUtils.isEmpty(studentBusinessEntities))
        {
            for (StudentBusinessEntity studentBusinessEntity : studentBusinessEntities)
            {
                StudentRewardExportDTO.Office office = new StudentRewardExportDTO.Office();
                office.setStartTime(DateUtils.formatDateToString(studentBusinessEntity.getStartTime(), OFFICE_PATTERN));
                office.setEndTime(DateUtils.formatDateToString(studentBusinessEntity.getEndTime(), OFFICE_PATTERN));
                office.setTask(studentBusinessEntity.getReason());
                offices.add(office);
            }
        }
        studentRewardExportDTO.setOfficial(offices);
        return studentRewardExportDTO;
    }

    private String parseSchoolYear(String schoolYear)
    {
        if (schoolYear == null)
        {
            return  "";
        }
        //2024-2025->24
        return schoolYear.substring(2,4);
    }

    /**
     * 将StudentRewardExportDTO转换为符合student_management.html模板格式的数据结构
     * @return 符合HTML模板格式的数据Map
     */
    public Map<String, Object> convertToHtmlDataList(List<StudentRewardExportDTO> studentRewardExportDTOS) {

        if (studentRewardExportDTOS == null || studentRewardExportDTOS.isEmpty())
        {
            return new HashMap<>();
        }
        Map<String, Object> resultMap = new HashMap<>(studentRewardExportDTOS.size());
        StudentRewardExportDTO studentRewardExportDTO = studentRewardExportDTOS.get(0);

        // 基本信息
        resultMap.put("teacherNumber", studentRewardExportDTO.getTeacherNumber());
        resultMap.put("teacherName", studentRewardExportDTO.getTeacherName());
        resultMap.put("classNumber", studentRewardExportDTO.getClassNumber());
        resultMap.put("reportTime", studentRewardExportDTO.getReportTime());
        resultMap.put("updateTime", studentRewardExportDTO.getUpdateTime());

        resultMap.put("items",studentRewardExportDTOS);
        return resultMap;
    }
}