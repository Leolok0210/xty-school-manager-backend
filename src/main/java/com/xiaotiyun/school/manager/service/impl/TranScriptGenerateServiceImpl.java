package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.enums.DepartmentEnum;
import com.xiaotiyun.school.manager.basic.enums.DepartmentScoreRuleEnum;
import com.xiaotiyun.school.manager.basic.enums.SystemSettingKeyEnum;
import com.xiaotiyun.school.manager.model.dto.*;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.SemesterQueryReqModel;
import com.xiaotiyun.school.manager.model.req.SubjectRelGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.req.TranScriptGenerateReqModel;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranScriptGenerateServiceImpl implements TranScriptGenerateService {


    private final StudentService studentService;
    private final SchoolService schoolService;
    private final SystemSettingService systemSettingService;
    private final SysClassService sysClassService;
    //极组信息
    private final GradeGroupService gradeGroupService;
    //学段
    private final SemesterService semesterService;
    private final SysSemesterRuleService sysSemesterRuleService;

    private final StudentUsuallyScoreService studentUsuallyScoreService;
    private final StudentExamScoreService studentExamScoreService;
    //ClassPerformanceService
    private final ClassPerformanceService classPerformanceService;
    //ExternalCompetitionRecordService
    private final ExternalCompetitionRecordService externalCompetitionRecordService;
    //DepartmentScoreRuleService
    private final DepartmentScoreRuleService departmentScoreRuleService;
    //StudentUsuallyRuleService
    private final StudentUsuallyRuleService studentUsuallyRuleService;
    //SubjectLevelRuleServiceImpl.java
    private final SubjectLevelRuleService subjectLevelRuleService;
    //qualityEvaluationService
    private final QualityEvaluationService qualityEvaluationService;
    private final VolunteerService volunteerService;
    //CompetitionRecordService
    private final CompetitionRecordService competitionRecordService;

    private final SubjectRelService subjectRelService;

    private static final int WIGHT = 10000;
    @Override
    public List<TranScriptGenerateResModel> generate(TranScriptGenerateReqModel reqModel) {
        //查询班级
        SysClass sysClass = sysClassService.getSysClassById(reqModel.getClassId());
        if (sysClass == null) {
            return Collections.emptyList();
        }
        Long schoolId = sysClass.getSchoolId();
        List<TranScriptGenerateResModel> result = getClassStudentSchoolInfo(reqModel.getClassId(), schoolId, sysClass);
        if (!CollectionUtils.isEmpty(result)) {
            //获取学段数据列表
            List<TranScriptSchoolYearDTO> schoolYearDTOS = getSemesterDataList(schoolId,sysClass.getSid(),sysClass.getGradeGroup());
            //获取科目成绩
            assembleData(reqModel.getClassId(), result, schoolYearDTOS,schoolId);
            getSubjectScores(reqModel.getClassId(), schoolId,sysClass.getGradeGroup(),-1L,result,schoolYearDTOS);
            calculateSchoolYearData(result,schoolId,sysClass.getGradeGroup());
            // 班级是-primary班级 才获取课外活动信息
            if (sysClass.getDepartment().equals(DepartmentEnum.MIDDLE.getCode())) {
                // 获取课外活动信息
                externalCompetitionRecordService.getByTranScriptGenerateResModel(result);
            }
        }
        return result;
    }

    @Override
    public List<KindergartenTranscriptResModel> generateKindergarten(TranScriptGenerateReqModel reqModel) {
        //查询班级
        SysClass sysClass = sysClassService.getSysClassById(reqModel.getClassId());
        if (sysClass == null) {
            return Collections.emptyList();
        }
        Long schoolId = sysClass.getSchoolId();
        List<KindergartenTranscriptResModel> result = getKindergartenClassStudentSchoolInfo(reqModel.getClassId(), schoolId, sysClass);
        if (!CollectionUtils.isEmpty(result)) {
            //获取学段数据列表
            List<TranScriptSchoolYearDTO> schoolYearDTOS = getSemesterDataList(schoolId,sysClass.getSid(),sysClass.getGradeGroup());
            //获取科目成绩
            assembleKindergartenData(reqModel.getClassId(), result, schoolYearDTOS,schoolId);
            getKindergartenSubjectScores(reqModel.getClassId(), schoolId,sysClass.getGradeGroup(),-1L,result,schoolYearDTOS);
            calculateKindergartenSchoolYearData(result,schoolId,sysClass.getGradeGroup());
        }
        return result;
    }

    private List<TranScriptGenerateResModel> getClassStudentSchoolInfo(Long classId, Long schoolId, SysClass sysClass) {
        List<TranScriptGenerateResModel> result = new ArrayList<>();

        // 获取学校信息
        SchoolEntity school = schoolService.getById(schoolId);
        if (school == null) {
            return Collections.emptyList();
        }
        //学校logo
        String schoolLogo = null;
        SystemSettingResModel schoolSettings = systemSettingService.getSchoolSettings(schoolId);
        if (schoolSettings != null && schoolSettings.getSettings() != null) {
            schoolLogo = schoolSettings.getSettings().get("logo");
        }
        //查询极组信息
        Long gradeGroupId = sysClass.getGradeGroup();
        GradeGroup gradeGroup = gradeGroupService.getById(gradeGroupId);

        // 获取学生信息
        List<StudentEntity> studentEntities = studentService.getStudentListByClassId(classId);
        if (studentEntities != null && !studentEntities.isEmpty()) {
            for (StudentEntity studentEntity : studentEntities) {
                TranScriptGenerateResModel studentSchoolInfo = new TranScriptGenerateResModel();
                studentSchoolInfo.setSchoolLogo(schoolLogo);
                studentSchoolInfo.setSchoolName(school.getName());
                studentSchoolInfo.setStudentPhoto(studentEntity.getImgUrl() == null ? "" : studentEntity.getImgUrl());
                studentSchoolInfo.setClassName(gradeGroup.getGradeGroupName() + sysClass.getClassName());
                studentSchoolInfo.setClassNo(sysClass.getClassNumber());
                studentSchoolInfo.setStudentName(studentEntity.getChineseName());
                studentSchoolInfo.setEnglishName(studentEntity.getEnglishName());
                studentSchoolInfo.setEducationNo(studentEntity.getStudentNo());
                studentSchoolInfo.setStudentEducationNo(studentEntity.getEducationNo());
                studentSchoolInfo.setStudentNo(studentEntity.getSeatNo() == null ? "" : String.valueOf(studentEntity.getSeatNo()));
                studentSchoolInfo.setSchoolYear(sysClass.getSid());
                studentSchoolInfo.setIssueDate(new Date());
                studentSchoolInfo.setClassId(classId);
                studentSchoolInfo.setSchoolId(schoolId);
                studentSchoolInfo.setStudentId(studentEntity.getId());
                studentSchoolInfo.setArtScience(0);
                if(sysClass.getArtsScience() != null && sysClass.getArtsScience() > 0)
                {
                    studentSchoolInfo.setArtScience(sysClass.getArtsScience());
                }else if(studentEntity.getArtsScience() != null && studentEntity.getArtsScience() > 0)
                {
                    studentSchoolInfo.setArtScience(studentEntity.getArtsScience());
                }
                // 其他学生信息...
                studentSchoolInfo.setExternalCompetitionRecords(new ArrayList<>());
                result.add(studentSchoolInfo);
            }
        }
        return result;
    }

    private List<KindergartenTranscriptResModel> getKindergartenClassStudentSchoolInfo(Long classId, Long schoolId, SysClass sysClass) {
        List<KindergartenTranscriptResModel> result = new ArrayList<>();

        // 获取学校信息
        SchoolEntity school = schoolService.getById(schoolId);
        if (school == null) {
            return Collections.emptyList();
        }
        //学校logo
        String schoolLogo = null;
        SystemSettingResModel schoolSettings = systemSettingService.getSchoolSettings(schoolId);
        if (schoolSettings != null && schoolSettings.getSettings() != null) {
            schoolLogo = schoolSettings.getSettings().get("logo");
        }
        //查询极组信息
        Long gradeGroupId = sysClass.getGradeGroup();
        GradeGroup gradeGroup = gradeGroupService.getById(gradeGroupId);

        // 获取学生信息
        List<StudentEntity> studentEntities = studentService.getStudentListByClassId(classId);
        if (studentEntities != null && !studentEntities.isEmpty()) {
            for (StudentEntity studentEntity : studentEntities) {
                KindergartenTranscriptResModel studentSchoolInfo = new KindergartenTranscriptResModel();
                studentSchoolInfo.setSchoolLogo(schoolLogo);
                studentSchoolInfo.setSchoolName(school.getName());
                studentSchoolInfo.setStudentPhoto(studentEntity.getImgUrl() == null ? "" : studentEntity.getImgUrl());
                studentSchoolInfo.setClassName(gradeGroup.getGradeGroupName() + sysClass.getClassName());
                studentSchoolInfo.setStudentName(studentEntity.getChineseName());
                studentSchoolInfo.setEducationNo(studentEntity.getStudentNo());
                studentSchoolInfo.setStudentNo(studentEntity.getSeatNo() == null ? "" : String.valueOf(studentEntity.getSeatNo()));
                studentSchoolInfo.setSchoolYear(sysClass.getSid());
                studentSchoolInfo.setIssueDate(new Date());
                studentSchoolInfo.setClassId(classId);
                studentSchoolInfo.setSchoolId(schoolId);
                studentSchoolInfo.setStudentId(studentEntity.getId());
                studentSchoolInfo.setArtScience(0);
                if(sysClass.getArtsScience() != null && sysClass.getArtsScience() > 0)
                {
                    studentSchoolInfo.setArtScience(sysClass.getArtsScience());
                }else if(studentEntity.getArtsScience() != null && studentEntity.getArtsScience() > 0)
                {
                    studentSchoolInfo.setArtScience(studentEntity.getArtsScience());
                }
                // 其他学生信息...
                result.add(studentSchoolInfo);
            }
        }
        return result;
    }
    //学段数据列表
    private List<TranScriptSchoolYearDTO> getSemesterDataList(Long schoolId, String schoolYear,Long groupId) {
        SemesterQueryReqModel semesterQueryReqModel = new SemesterQueryReqModel();
        semesterQueryReqModel.setSchoolYear(schoolYear);
        List<SemesterResModel> list = semesterService.list(semesterQueryReqModel, schoolId);
        List<SysSemesterRuleAddDetailResModel> rule = sysSemesterRuleService.getRuleBySchoolYearAndDepartment(schoolYear, groupId,schoolId);
        //tomap
        Map<Long, SysSemesterRuleAddDetailResModel> ruleMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(rule))
        {
            ruleMap = rule.stream()
                    .collect(Collectors.toMap(SysSemesterRuleAddDetailResModel::getSemesterId, item -> item));
        }
        if(!CollectionUtils.isEmpty(list)){
            List<TranScriptSchoolYearDTO> tranScriptSchoolYearDTOS = new ArrayList<>();
            for (SemesterResModel semesterResModel : list)
            {
                TranScriptSchoolYearDTO tranScriptSchoolYearDTO = new TranScriptSchoolYearDTO();
                tranScriptSchoolYearDTO.setPeriodId(semesterResModel.getId());
                tranScriptSchoolYearDTO.setPeriodName(semesterResModel.getName());
                tranScriptSchoolYearDTO.setStartTime(semesterResModel.getStartTime());
                SysSemesterRuleAddDetailResModel ruleModel = ruleMap.get(semesterResModel.getId());
                if(ruleModel == null)
                {
                    continue;
                }
                tranScriptSchoolYearDTO.setProportion(ruleModel.getWeight());
                tranScriptSchoolYearDTOS.add(tranScriptSchoolYearDTO);
            }
            return tranScriptSchoolYearDTOS;
        }
        return Collections.emptyList();
    }


    //科目成绩拼接
    private void getSubjectScores(Long classId, Long schoolId,Long groupId,Long priodId,List<TranScriptGenerateResModel> result,
                                  List<TranScriptSchoolYearDTO> schoolYearDTOS) {
        //获取成绩规则
        GradeGroup byId = gradeGroupService.getById(groupId);
        int artScience = byId.getProfessionalSubject() == null ? 0 : byId.getProfessionalSubject();
        //科目成绩权重
        DepartmentScoreRuleResModel departmentScoreRuleResModel = departmentScoreRuleService.getRuleByDepartment(schoolId, groupId);
        List<StudentUsuallyRuleResModel> usuallyRuleServiceRule = studentUsuallyRuleService.getRule(schoolId);
        List<SubjectLevelRuleResModel> subjectLevelRuleResModels = subjectLevelRuleService.getRuleByDepartment(schoolId, groupId);
        // 平时成绩类型科目关联开关
        boolean isUsualTypeRelSub = false;
        List<SystemSettingEntity> list = systemSettingService.list(Wrappers.<SystemSettingEntity>lambdaQuery()
                .eq(SystemSettingEntity::getSettingKey, SystemSettingKeyEnum.USUAL_TYPE_REL_SUB.getKey())
                .eq(SystemSettingEntity::getSchoolId, schoolId));
        if (!CollectionUtils.isEmpty(list)) {
            isUsualTypeRelSub = list.get(0).getSettingValue().equals("1");
        }
        List<StudentUsuallyRuleResModel> groupUsualRule = usuallyRuleServiceRule.stream()
                .filter(item -> item.getGradeGroupId().equals(groupId)).collect(Collectors.toList());
        // 根据开关封装map
        Map<Long, StudentUsuallyRuleResModel> usuallyRuleMap = new HashMap<>();// 不关联用这个 类型id-规则
        Map<Long, Map<Long, StudentUsuallyRuleResModel>> usuallyRuleMapBySub = new HashMap<>();// 关联用这个  科目id-类型id-规则zh
        if (!CollectionUtils.isEmpty(groupUsualRule)){
            if (isUsualTypeRelSub){
                try {
                    usuallyRuleMapBySub = groupUsualRule.stream().filter(item -> item.getSubjectId() != null)
                            .collect(Collectors.groupingBy(StudentUsuallyRuleResModel::getSubjectId, Collectors.toMap(StudentUsuallyRuleResModel::getTypeId, item -> item)));
                } catch (Exception e) {
                    log.error("学校Id{},平时成绩科目占比配置错误4！", schoolId, e);
                }
            } else {
                try {
                    usuallyRuleMap = groupUsualRule.stream().filter(item -> item.getSubjectId() == null || item.getSubjectId() == 0)
                            .collect(Collectors.toMap(StudentUsuallyRuleResModel::getTypeId, Function.identity()));
                } catch (Exception e) {
                    log.error("学校Id{},平时成绩占比配置错误4！", schoolId, e);
                }
            }
        }
        Map<String, DepartmentScoreRuleDetailResModel> scoreTypeMap;
        Map<Long, DepartmentScoreRuleDetailResModel> subjectWightMap = new HashMap<>();
        Map<Long, DepartmentScoreRuleDetailResModel> artSubjectWightMap = new HashMap<>();
        Map<Long, DepartmentScoreRuleDetailResModel> scienceSubjectWightMap = new HashMap<>();
        Map<Long, DepartmentScoreRuleDetailResModel> commerceSubjectWightMap = new HashMap<>();
        List<DepartmentScoreRuleDepartmentResModel> details = null;
        if (departmentScoreRuleResModel != null)
        {
            details = departmentScoreRuleResModel.getDetails();
        }
        if(!CollectionUtils.isEmpty(details))
        {
            List<DepartmentScoreRuleDetailResModel> ruleDetailResModels = details.get(0).getDetails();
            ruleDetailResModels = ruleDetailResModels.stream().filter(item -> item.getScoreType() != null).collect(Collectors.toList());
            //to map 0-平时成绩,1-考试成绩,2-科目成绩
            scoreTypeMap = ruleDetailResModels.stream()
                    .filter(item -> (item.getScoreType().equals("0") || item.getScoreType().equals("1")))
                    .collect(Collectors.toMap(DepartmentScoreRuleDetailResModel::getScoreType, item -> item));
            if(artScience == 0 || artScience == 1){
                subjectWightMap = ruleDetailResModels.stream()
                        .filter(item -> item.getScoreType().equals("2"))
                        .collect(Collectors.toMap(DepartmentScoreRuleDetailResModel::getSubjectId, item -> item));
            }else {
                artSubjectWightMap = ruleDetailResModels.stream()
                        .filter(item -> item.getScoreType().equals(DepartmentScoreRuleEnum.COMMON_SCHOOL_SCORE.getValue()))
                        .collect(Collectors.toMap(DepartmentScoreRuleDetailResModel::getSubjectId, item -> item));
                scienceSubjectWightMap = ruleDetailResModels.stream()
                        .filter(item -> item.getScoreType().equals(DepartmentScoreRuleEnum.COMMON_PROVINCE_SCORE.getValue()))
                        .collect(Collectors.toMap(DepartmentScoreRuleDetailResModel::getSubjectId, item -> item));
                if (artScience == 3) {
                    commerceSubjectWightMap = ruleDetailResModels.stream()
                            .filter(item -> item.getScoreType().equals(DepartmentScoreRuleEnum.COMMON_COMMERCE_SCORE.getValue()))
                            .collect(Collectors.toMap(DepartmentScoreRuleDetailResModel::getSubjectId, item -> item));
                }
            }
        }else {
            scoreTypeMap = new HashMap<>();
        }


        //tomap subjectLevelRuleResModels
        Map<Long, SubjectLevelRuleResModel> subjectLevelRuleDetailResModelMap = subjectLevelRuleResModels.stream()
                .collect(Collectors.toMap(SubjectLevelRuleResModel::getSubjectId, item -> item));


        //获取科目
        SubjectRelGroupQueryReqModel reqModel = new SubjectRelGroupQueryReqModel();
        reqModel.setGroupId(groupId);
        reqModel.setSchoolId(schoolId);
        List<SubjectRelResModel> relResModels = subjectRelService.listByGroup(reqModel);
        if (relResModels == null  || relResModels.isEmpty()) {
            return;
        }
        //获取平时成绩
        List<StudentPeriodScoreResModel> usuallyScores = studentUsuallyScoreService.getUsuallyScores(classId, priodId);
        //转map
        Map<Long, List<StudentPeriodScoreResModel>> usuallyScoreMap = usuallyScores.stream()
                .collect(Collectors.groupingBy(StudentPeriodScoreResModel::getStudentId));
        //获取考试成绩
        List<StudentPeriodScoreResModel> studentPeriodScores = studentExamScoreService.getStudentPeriodScores(classId, priodId);
        //转map
        Map<Long, List<StudentPeriodScoreResModel>> examScoreMap = studentPeriodScores.stream()
                .collect(Collectors.groupingBy(StudentPeriodScoreResModel::getStudentId));

        Map<Long, List<ScoreRankDTO>> priodScoresMap = new HashMap<>();
        for (TranScriptGenerateResModel tranScriptGenerateResModel : result) {
            Map<Long, DepartmentScoreRuleDetailResModel> wightMap;
            List<SubjectRelResModel> subjectRelList;
            if(tranScriptGenerateResModel.getArtScience() == 1){
                wightMap = artSubjectWightMap;
                subjectRelList = relResModels.stream()
                        .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(tranScriptGenerateResModel.getArtScience()))
                        .collect(Collectors.toList());
            }else if(tranScriptGenerateResModel.getArtScience() == 2)
            {
                wightMap = scienceSubjectWightMap;
                subjectRelList = relResModels.stream()
                        .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(tranScriptGenerateResModel.getArtScience()))
                        .collect(Collectors.toList());
            }else if(tranScriptGenerateResModel.getArtScience() == 3)
            {
                wightMap = commerceSubjectWightMap;
                subjectRelList = relResModels.stream()
                        .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(tranScriptGenerateResModel.getArtScience()))
                        .collect(Collectors.toList());
            } else {
                wightMap = subjectWightMap;
                subjectRelList = relResModels.stream()
                        .filter(item -> item.getArtsScience() == 0)
                        .collect(Collectors.toList());
            }
            Long studentId = tranScriptGenerateResModel.getStudentId();
            List<StudentPeriodScoreResModel> usuallyScoreList = usuallyScoreMap.get(studentId);
            List<StudentPeriodScoreResModel> examScoreList = examScoreMap.get(studentId);
            //1. 每个科目的每个学段的成绩= 该科目该学段下平时成绩总分 *权重 + 考试成绩总分*权重
            // 2. 平时成绩总分 = 学段下 每个类型成绩总分 *权重 求和
            // 3. 每个类型成绩总分 = 该类型下每次成绩求和/ 该类型测验次数
            // 说明：
            // 1. 权重配置参考：系统设置=》成绩计算规则中配置=》科目测验/考试权重配置 和 平时成绩权重配置
            // 2. 平时测验类型包括：作业、大测、小测、堂课、其他
            // 3. 展示规则：
            // 1. 参考“系统设置=》科目评级设定”中“成绩展示规则”，选择“分数”，展示分值，选择“评级”，则分数转换成“评级”展示；未设置评级按照“分数”展示
            // 2. 分数展示位数：四舍五入，取整
            //平时成绩
            Map<Long, Map<Long,Integer>> periodDataMap = new HashMap<>();
            if (usuallyScoreList != null)
            {
                for (StudentPeriodScoreResModel studentPeriodScoreResModel : usuallyScoreList) {
                    if(!periodDataMap.containsKey(studentPeriodScoreResModel.getPeriodId())){
                        periodDataMap.put(studentPeriodScoreResModel.getPeriodId(), new HashMap<>());
                    }
                    //这个学段下的所以科目成绩 科目id，成绩
                    Map<Long, Integer> subjecScoreMap = periodDataMap.get(studentPeriodScoreResModel.getPeriodId());
                    List<StudentSubjectScoreResModel> subjectScores = studentPeriodScoreResModel.getSubjectScores();
                    //转maplist
                    Map<Long, List<StudentSubjectScoreResModel>> subjectScoreMap = subjectScores.stream()
                            .collect(Collectors.groupingBy(StudentSubjectScoreResModel::getSubjectId));
                    for (Map.Entry<Long, List<StudentSubjectScoreResModel>> entry : subjectScoreMap.entrySet()) {
                        Long subjectId = entry.getKey();
                        List<StudentSubjectScoreResModel> subjectScoreList = entry.getValue();
                        // 计算每个科目的成绩
                        // to map 类型成绩总分
                        Map<Long, Integer> typeSumMap = new HashMap<>();
                        Map<Long, List<StudentSubjectScoreResModel>> typeMap = subjectScoreList.stream()
                                .collect(Collectors.groupingBy(StudentSubjectScoreResModel::getTypeId));
                        typeMap.forEach((type, typeList) -> {
                            // 每个类型成绩总分 = 该类型下每次成绩求和/ 该类型测验次数
                            int sumScore = typeList.stream()
                                    .mapToInt(StudentSubjectScoreResModel::getScore)
                                    .sum() / typeList.size();
                            Integer score = typeSumMap.get(type);
                            Integer typeSum = score == null ? sumScore : score + sumScore;
                            typeSumMap.put(type, typeSum);
                        });
                        Integer subjectSumScore = subjecScoreMap.get(subjectId);
                        // 获取类型规则
                        Map<Long, StudentUsuallyRuleResModel> typeUsualRuleMap = new HashMap<>();
                        // 根据是否关联科目开关
                        if (isUsualTypeRelSub){
                            typeUsualRuleMap = usuallyRuleMapBySub.get(subjectId);
                        } else {
                            typeUsualRuleMap = usuallyRuleMap;
                        }
                        // 这个科目的平时成绩总分
                        for (Map.Entry<Long, Integer> sumEntity : typeSumMap.entrySet()) {
                            StudentUsuallyRuleResModel wight = typeUsualRuleMap.get(sumEntity.getKey());
                            if (wight != null && wight.getWeight() != null) {
                                Integer value = (sumEntity.getValue() * wight.getWeight()) / WIGHT;
                                subjectSumScore = subjectSumScore == null ? value : subjectSumScore + value;
                            }
                        }
                        subjecScoreMap.put(subjectId, subjectSumScore);
                    }
                }
            }
            //考试成绩
            Map<Long, Map<Long,Integer>> periodExamDataMap = new HashMap<>();
            if (examScoreList != null)
            {
                for (StudentPeriodScoreResModel studentPeriodScoreResModel : examScoreList) {
                    if(!periodExamDataMap.containsKey(studentPeriodScoreResModel.getPeriodId())){
                        periodExamDataMap.put(studentPeriodScoreResModel.getPeriodId(), new HashMap<>());
                    }
                    Map<Long, Integer> subjecScoreMap = periodExamDataMap.get(studentPeriodScoreResModel.getPeriodId());
                    List<StudentSubjectScoreResModel> subjectScores = studentPeriodScoreResModel.getSubjectScores();
                    Map<Long, List<StudentSubjectScoreResModel>> subjectScoreMap = subjectScores.stream()
                            .collect(Collectors.groupingBy(StudentSubjectScoreResModel::getSubjectId));
                    subjectScoreMap.forEach((subjectId, subjectScoreList) -> {
                        Integer subjectSumScore = subjecScoreMap.get(subjectId);
                        int examScore = subjectScoreList.stream().mapToInt(StudentSubjectScoreResModel::getScore).sum() / subjectScoreList.size();
                        subjectSumScore = subjectSumScore == null ? examScore : subjectSumScore + examScore;
                        subjecScoreMap.put(subjectId, subjectSumScore);
                    });
                }
            }
            //计算每个学段的科目成绩
            Map<Long, List<TranScriptSubjectScoreResModel>> periodSubjectScoreMap = new HashMap<>();
            schoolYearDTOS.forEach(tranScriptSchoolYearDTO -> {
                Map<Long, Integer> usuallyScore = periodDataMap.get(tranScriptSchoolYearDTO.getPeriodId());
                Map<Long, Integer> examScore = periodExamDataMap.get(tranScriptSchoolYearDTO.getPeriodId());
                List<TranScriptSubjectScoreResModel> subjectScores = new ArrayList<>();
                subjectRelList.sort(Comparator.comparing(SubjectRelResModel::getNumber));
                subjectRelList.forEach(subject -> {
                    TranScriptSubjectScoreResModel tranScriptSubjectScoreResModel = new TranScriptSubjectScoreResModel();
                    tranScriptSubjectScoreResModel.setSubjectName(subject.getSubject() == null ? "" : subject.getSubject().getSubjectName());
                    tranScriptSubjectScoreResModel.setSubjectType(subject.getSubjectType());
                    if(subject.getSubjectType() != null && subject.getSubjectType() == 1)
                    {
                        tranScriptSubjectScoreResModel.setSubjectName("@" + tranScriptSubjectScoreResModel.getSubjectName());
                    }
                    tranScriptSubjectScoreResModel.setSubjectId(subject.getId());
                    tranScriptSubjectScoreResModel.setCountedInAverage(subject.getCountedInAverage());
                    if(usuallyScore != null && usuallyScore.containsKey(subject.getId()))
                    {
                        //0-平时成绩,1-考试成绩,2-科目成绩
                        DepartmentScoreRuleDetailResModel subjectWight = scoreTypeMap.get("0");
                        if(subjectWight != null && subjectWight.getWeight() != null) {
                            Integer score = usuallyScore.get(subject.getId());
                            if(score != null){
                                Integer usually = (score * subjectWight.getWeight()) / WIGHT;
                                tranScriptSubjectScoreResModel.setScore(tranScriptSubjectScoreResModel.getScore() == null ? usually :
                                        tranScriptSubjectScoreResModel.getScore() + usually);
                            }
                        }
                    }
                    if(examScore != null && examScore.containsKey(subject.getId())){
                        //0-平时成绩,1-考试成绩,2-科目成绩
                        DepartmentScoreRuleDetailResModel subjectWight = scoreTypeMap.get("1");
                        if(subjectWight != null && subjectWight.getWeight() != null) {
                            Integer exam = (examScore.get(subject.getId()) * subjectWight.getWeight()) / WIGHT;
                            tranScriptSubjectScoreResModel.setScore(tranScriptSubjectScoreResModel.getScore() == null ? exam :
                                    tranScriptSubjectScoreResModel.getScore() + exam);
                        }
                    }
                    SubjectLevelRuleResModel subjectLevelRuleResModel = subjectLevelRuleDetailResModelMap.get(subject.getId());
                    if(subjectLevelRuleResModel != null && subjectLevelRuleResModel.getShowRule() != null)
                    {
                        tranScriptSubjectScoreResModel.setShowRule(subjectLevelRuleResModel.getShowRule());
                        if(subjectLevelRuleResModel.getShowRule() == 1) {
                            tranScriptSubjectScoreResModel.setScoreLevel(getGrade(tranScriptSubjectScoreResModel.getScore(), subjectLevelRuleResModel.getDetailList()));
                        }
                        subjectScores.add(tranScriptSubjectScoreResModel);
                    }
                });
                periodSubjectScoreMap.put(tranScriptSchoolYearDTO.getPeriodId(), subjectScores);
            });
            //插入每个学段的成绩
            List<TranScriptPeriodDataResModel> periodDataList = tranScriptGenerateResModel.getPeriodDataList();
            periodDataList.forEach(periodData -> {
                List<TranScriptSubjectScoreResModel> subjectScores = periodSubjectScoreMap.get(periodData.getPeriodId());
                periodData.setSubjectScores(subjectScores);
                //计算平均分
                double averageScore = 0;
                if(departmentScoreRuleResModel.getAvgType() == 1) {
                    averageScore = subjectScores.stream().filter(item -> item.getCountedInAverage() == 1).mapToInt(item ->{
                        DepartmentScoreRuleDetailResModel ruleDetailResModel = wightMap.get(item.getSubjectId());
                        if(ruleDetailResModel == null || ruleDetailResModel.getWeight() == null || item.getScore() == null) {
                            return 0;
                        }
                        return (item.getScore() * ruleDetailResModel.getWeight()) / WIGHT;
                    }).sum();
                }else {
                    averageScore = subjectScores.stream().filter(item -> item.getCountedInAverage() == 1).mapToInt(item -> {
                        if(item.getScore() == null) {
                            return 0;
                        }
                        return item.getScore();
                    }).average().orElse(0);
                }
                periodData.setAverageScore(averageScore);
                periodData.setTotalStudents(result.size());
                //排名分数
                List<ScoreRankDTO> dataResModels = priodScoresMap.get(periodData.getPeriodId());
                if (CollectionUtils.isEmpty(dataResModels)) {
                    dataResModels = new ArrayList<>();
                }
                ScoreRankDTO scoreRankDTO = new ScoreRankDTO();
                scoreRankDTO.setAvgScore(periodData.getAverageScore());
                scoreRankDTO.setStudentId(studentId);
                dataResModels.add(scoreRankDTO);
                priodScoresMap.put(periodData.getPeriodId(), dataResModels);
            });
        }

        //
        Map<Long, Map<Long, Integer>> rankScore = new HashMap<>();
        priodScoresMap.forEach((periodId, dataResModels) -> {
            dataResModels.sort(Comparator.comparing(ScoreRankDTO::getAvgScore).reversed());
            Map<Long, Integer> periodRankScore = new HashMap<>();
            int rank = 1;
            double privousScore = 0;
            for (ScoreRankDTO scoreRankDTO : dataResModels) {
                if (privousScore == scoreRankDTO.getAvgScore()) {
                    periodRankScore.put(scoreRankDTO.getStudentId(), rank - 1);
                } else {
                    periodRankScore.put(scoreRankDTO.getStudentId(), rank++);
                }
                privousScore = scoreRankDTO.getAvgScore();
            }
            rankScore.put(periodId, periodRankScore);
        });
        //计算排名
        for (TranScriptGenerateResModel tranScriptGenerateResModel : result) {
            List<TranScriptPeriodDataResModel> periodDataList = tranScriptGenerateResModel.getPeriodDataList();
            for (TranScriptPeriodDataResModel periodData : periodDataList) {
                Map<Long, Integer> scoreRankDTOS = rankScore.get(periodData.getPeriodId());
                if (CollectionUtils.isEmpty(scoreRankDTOS)) {
                    continue;
                }
                //计算这个学生排名
                Integer rank = scoreRankDTOS.get(tranScriptGenerateResModel.getStudentId());
                if (rank != null) {
                    periodData.setRank(rank);
                }
            }
        }
    }



    //科目成绩拼接
    private void getKindergartenSubjectScores(Long classId, Long schoolId,Long groupId,Long priodId,List<KindergartenTranscriptResModel> result,
                                  List<TranScriptSchoolYearDTO> schoolYearDTOS) {
        //获取成绩规则
        GradeGroup byId = gradeGroupService.getById(groupId);
        int artScience = byId.getProfessionalSubject() == null ? 0 : byId.getProfessionalSubject();
        //科目成绩权重
        DepartmentScoreRuleResModel departmentScoreRuleResModel = departmentScoreRuleService.getRuleByDepartment(schoolId, groupId);
        List<StudentUsuallyRuleResModel> usuallyRuleServiceRule = studentUsuallyRuleService.getRule(schoolId);
        List<SubjectLevelRuleResModel> subjectLevelRuleResModels = subjectLevelRuleService.getRuleByDepartment(schoolId, groupId);
        // 平时成绩类型科目关联开关
        boolean isUsualTypeRelSub = false;
        List<SystemSettingEntity> list = systemSettingService.list(Wrappers.<SystemSettingEntity>lambdaQuery()
                .eq(SystemSettingEntity::getSettingKey, SystemSettingKeyEnum.USUAL_TYPE_REL_SUB.getKey())
                .eq(SystemSettingEntity::getSchoolId, schoolId));
        if (!CollectionUtils.isEmpty(list)) {
            isUsualTypeRelSub = list.get(0).getSettingValue().equals("1");
        }
        List<StudentUsuallyRuleResModel> groupUsualRule = usuallyRuleServiceRule.stream()
                .filter(item -> item.getGradeGroupId().equals(groupId)).collect(Collectors.toList());
        // 根据开关封装map
        Map<Long, StudentUsuallyRuleResModel> usuallyRuleMap = new HashMap<>();// 不关联用这个 类型id-规则
        Map<Long, Map<Long, StudentUsuallyRuleResModel>> usuallyRuleMapBySub = new HashMap<>();// 关联用这个  科目id-类型id-规则zh
        if (!CollectionUtils.isEmpty(groupUsualRule)){
            if (isUsualTypeRelSub){
                try {
                    usuallyRuleMapBySub = groupUsualRule.stream().filter(item -> item.getSubjectId() != null)
                            .collect(Collectors.groupingBy(StudentUsuallyRuleResModel::getSubjectId, Collectors.toMap(StudentUsuallyRuleResModel::getTypeId, item -> item)));
                } catch (Exception e) {
                    log.error("学校Id{},平时成绩科目占比配置错误1！", schoolId, e);
                }
            } else {
                try {
                    usuallyRuleMap = groupUsualRule.stream().filter(item -> item.getSubjectId() == null || item.getSubjectId() == 0)
                            .collect(Collectors.toMap(StudentUsuallyRuleResModel::getTypeId, Function.identity()));
                } catch (Exception e) {
                    log.error("学校Id{},平时成绩占比配置错误1！", schoolId, e);
                }
            }
        }
        Map<String, DepartmentScoreRuleDetailResModel> scoreTypeMap;
        Map<Long, DepartmentScoreRuleDetailResModel> subjectWightMap = new HashMap<>();
        Map<Long, DepartmentScoreRuleDetailResModel> artSubjectWightMap = new HashMap<>();
        Map<Long, DepartmentScoreRuleDetailResModel> scienceSubjectWightMap = new HashMap<>();
        Map<Long, DepartmentScoreRuleDetailResModel> commerceSubjectWightMap = new HashMap<>();
        List<DepartmentScoreRuleDepartmentResModel> details = null;
        if (departmentScoreRuleResModel != null)
        {
            details = departmentScoreRuleResModel.getDetails();
        }
        if(!CollectionUtils.isEmpty(details))
        {
            List<DepartmentScoreRuleDetailResModel> ruleDetailResModels = details.get(0).getDetails();
            ruleDetailResModels = ruleDetailResModels.stream().filter(item -> item.getScoreType() != null).collect(Collectors.toList());
            //to map
            scoreTypeMap = ruleDetailResModels.stream()
                    .filter(item -> item.getScoreType().equals("0") || item.getScoreType().equals("1"))
                    .collect(Collectors.toMap(DepartmentScoreRuleDetailResModel::getScoreType, item -> item));
            if(artScience == 0 || artScience == 1){
                subjectWightMap = ruleDetailResModels.stream()
                        .filter(item -> item.getScoreType().equals("2"))
                        .collect(Collectors.toMap(DepartmentScoreRuleDetailResModel::getSubjectId, item -> item));
            }else {
                artSubjectWightMap = ruleDetailResModels.stream()
                        .filter(item -> item.getScoreType().equals(DepartmentScoreRuleEnum.COMMON_SCHOOL_SCORE.getValue()))
                        .collect(Collectors.toMap(DepartmentScoreRuleDetailResModel::getSubjectId, item -> item));
                scienceSubjectWightMap = ruleDetailResModels.stream()
                        .filter(item -> item.getScoreType().equals(DepartmentScoreRuleEnum.COMMON_PROVINCE_SCORE.getValue()))
                        .collect(Collectors.toMap(DepartmentScoreRuleDetailResModel::getSubjectId, item -> item));
                if (artScience == 3)
                {
                    commerceSubjectWightMap = ruleDetailResModels.stream()
                            .filter(item -> item.getScoreType().equals(DepartmentScoreRuleEnum.COMMON_COMMERCE_SCORE.getValue()))
                            .collect(Collectors.toMap(DepartmentScoreRuleDetailResModel::getSubjectId, item -> item));
                }
            }
        }else {
            scoreTypeMap = new HashMap<>();
        }


        //tomap subjectLevelRuleResModels
        Map<Long, SubjectLevelRuleResModel> subjectLevelRuleDetailResModelMap = subjectLevelRuleResModels.stream()
                .collect(Collectors.toMap(SubjectLevelRuleResModel::getSubjectId, item -> item));


        //获取科目
        SubjectRelGroupQueryReqModel reqModel = new SubjectRelGroupQueryReqModel();
        reqModel.setGroupId(groupId);
        reqModel.setSchoolId(schoolId);
        List<SubjectRelResModel> relResModels = subjectRelService.listByGroup(reqModel);
        if (relResModels == null  || relResModels.isEmpty()) {
            return;
        }
        //获取平时成绩
        List<StudentPeriodScoreResModel> usuallyScores = studentUsuallyScoreService.getUsuallyScores(classId, priodId);
        //转map
        Map<Long, List<StudentPeriodScoreResModel>> usuallyScoreMap = usuallyScores.stream()
                .collect(Collectors.groupingBy(StudentPeriodScoreResModel::getStudentId));
        //获取考试成绩
        List<StudentPeriodScoreResModel> studentPeriodScores = studentExamScoreService.getStudentPeriodScores(classId, priodId);
        //转map
        Map<Long, List<StudentPeriodScoreResModel>> examScoreMap = studentPeriodScores.stream()
                .collect(Collectors.groupingBy(StudentPeriodScoreResModel::getStudentId));

//        Map<Long, List<ScoreRankDTO>> priodScoresMap = new HashMap<>();
        for (KindergartenTranscriptResModel tranScriptGenerateResModel : result) {
            Map<Long, DepartmentScoreRuleDetailResModel> wightMap;
            List<SubjectRelResModel> subjectRelList;
            if(tranScriptGenerateResModel.getArtScience() == 1){
                wightMap = artSubjectWightMap;
                subjectRelList = relResModels.stream()
                        .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(tranScriptGenerateResModel.getArtScience()))
                        .collect(Collectors.toList());
            }else if(tranScriptGenerateResModel.getArtScience() == 2)
            {
                wightMap = scienceSubjectWightMap;
                subjectRelList = relResModels.stream()
                        .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(tranScriptGenerateResModel.getArtScience()))
                        .collect(Collectors.toList());
            }else if (tranScriptGenerateResModel.getArtScience() == 3)
            {
                wightMap = commerceSubjectWightMap;
                subjectRelList = relResModels.stream()
                        .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(tranScriptGenerateResModel.getArtScience()))
                        .collect(Collectors.toList());
            } else {
                wightMap = subjectWightMap;
                subjectRelList = relResModels.stream()
                        .filter(item -> item.getArtsScience() == 0)
                        .collect(Collectors.toList());
            }

            Long studentId = tranScriptGenerateResModel.getStudentId();
            List<StudentPeriodScoreResModel> usuallyScoreList = usuallyScoreMap.get(studentId);
            List<StudentPeriodScoreResModel> examScoreList = examScoreMap.get(studentId);
            //1. 每个科目的每个学段的成绩= 该科目该学段下平时成绩总分 *权重 + 考试成绩总分*权重
            // 2. 平时成绩总分 = 学段下 每个类型成绩总分 *权重 求和
            // 3. 每个类型成绩总分 = 该类型下每次成绩求和/ 该类型测验次数
            // 说明：
            // 1. 权重配置参考：系统设置=》成绩计算规则中配置=》科目测验/考试权重配置 和 平时成绩权重配置
            // 2. 平时测验类型包括：作业、大测、小测、堂课、其他
            // 3. 展示规则：
            // 1. 参考“系统设置=》科目评级设定”中“成绩展示规则”，选择“分数”，展示分值，选择“评级”，则分数转换成“评级”展示；未设置评级按照“分数”展示
            // 2. 分数展示位数：四舍五入，取整
            //平时成绩
            Map<Long, Map<Long,Integer>> periodDataMap = new HashMap<>();
            if (usuallyScoreList != null)
            {
                for (StudentPeriodScoreResModel studentPeriodScoreResModel : usuallyScoreList) {
                    if(!periodDataMap.containsKey(studentPeriodScoreResModel.getPeriodId())){
                        periodDataMap.put(studentPeriodScoreResModel.getPeriodId(), new HashMap<>());
                    }
                    //这个学段下的所以科目成绩 科目id，成绩
                    Map<Long, Integer> subjecScoreMap = periodDataMap.get(studentPeriodScoreResModel.getPeriodId());
                    List<StudentSubjectScoreResModel> subjectScores = studentPeriodScoreResModel.getSubjectScores();
                    //转maplist
                    Map<Long, List<StudentSubjectScoreResModel>> subjectScoreMap = subjectScores.stream()
                            .collect(Collectors.groupingBy(StudentSubjectScoreResModel::getSubjectId));
                    for (Map.Entry<Long, List<StudentSubjectScoreResModel>> entry : subjectScoreMap.entrySet()) {
                        Long subjectId = entry.getKey();
                        List<StudentSubjectScoreResModel> subjectScoreList = entry.getValue();
                        // 计算每个科目的成绩
                        // to map 类型成绩总分
                        Map<Long, Integer> typeSumMap = new HashMap<>();
                        Map<Long, List<StudentSubjectScoreResModel>> typeMap = subjectScoreList.stream()
                                .collect(Collectors.groupingBy(StudentSubjectScoreResModel::getTypeId));
                        typeMap.forEach((type, typeList) -> {
                            // 每个类型成绩总分 = 该类型下每次成绩求和/ 该类型测验次数
                            int sumScore = typeList.stream()
                                    .mapToInt(StudentSubjectScoreResModel::getScore)
                                    .sum() / typeList.size();
                            Integer score = typeSumMap.get(type);
                            Integer typeSum = score == null ? sumScore : score + sumScore;
                            typeSumMap.put(type, typeSum);
                        });
                        Integer subjectSumScore = subjecScoreMap.get(subjectId);
                        // 获取类型规则
                        Map<Long, StudentUsuallyRuleResModel> typeUsualRuleMap = new HashMap<>();
                        // 根据是否关联科目开关
                        if (isUsualTypeRelSub){
                            typeUsualRuleMap = usuallyRuleMapBySub.get(subjectId);
                        } else {
                            typeUsualRuleMap = usuallyRuleMap;
                        }
                        // 这个科目的平时成绩总分
                        for (Map.Entry<Long, Integer> sumEntity : typeSumMap.entrySet()) {
                            StudentUsuallyRuleResModel wight = typeUsualRuleMap.get(sumEntity.getKey());
                            if (wight != null && wight.getWeight() != null) {
                                Integer value = (sumEntity.getValue() * wight.getWeight()) / WIGHT;
                                subjectSumScore = subjectSumScore == null ? value : subjectSumScore + value;
                            }
                        }
                        subjecScoreMap.put(subjectId, subjectSumScore);
                    }
                }
            }
            //考试成绩
            Map<Long, Map<Long,Integer>> periodExamDataMap = new HashMap<>();
            if (examScoreList != null)
            {
                for (StudentPeriodScoreResModel studentPeriodScoreResModel : examScoreList) {
                    if(!periodExamDataMap.containsKey(studentPeriodScoreResModel.getPeriodId())){
                        periodExamDataMap.put(studentPeriodScoreResModel.getPeriodId(), new HashMap<>());
                    }
                    Map<Long, Integer> subjecScoreMap = periodExamDataMap.get(studentPeriodScoreResModel.getPeriodId());
                    List<StudentSubjectScoreResModel> subjectScores = studentPeriodScoreResModel.getSubjectScores();
                    Map<Long, List<StudentSubjectScoreResModel>> subjectScoreMap = subjectScores.stream()
                            .collect(Collectors.groupingBy(StudentSubjectScoreResModel::getSubjectId));
                    subjectScoreMap.forEach((subjectId, subjectScoreList) -> {
                        Integer subjectSumScore = subjecScoreMap.get(subjectId);
                        int examScore = subjectScoreList.stream().mapToInt(StudentSubjectScoreResModel::getScore).sum() / subjectScoreList.size();
                        subjectSumScore = subjectSumScore == null ? examScore : subjectSumScore + examScore;
                        subjecScoreMap.put(subjectId, subjectSumScore);
                    });
                }
            }
            //计算每个学段的科目成绩
            Map<Long, List<TranScriptSubjectScoreResModel>> periodSubjectScoreMap = new HashMap<>();
            schoolYearDTOS.forEach(tranScriptSchoolYearDTO -> {
                Map<Long, Integer> usuallyScore = periodDataMap.get(tranScriptSchoolYearDTO.getPeriodId());
                Map<Long, Integer> examScore = periodExamDataMap.get(tranScriptSchoolYearDTO.getPeriodId());
                List<TranScriptSubjectScoreResModel> subjectScores = new ArrayList<>();
                subjectRelList.sort(Comparator.comparing(SubjectRelResModel::getNumber));
                subjectRelList.forEach(subject -> {
                    TranScriptSubjectScoreResModel tranScriptSubjectScoreResModel = new TranScriptSubjectScoreResModel();
                    tranScriptSubjectScoreResModel.setSubjectName(subject.getSubject() == null ? null : subject.getSubject().getSubjectName());
                    tranScriptSubjectScoreResModel.setSubjectType(subject.getSubjectType());
                    if(subject.getSubjectType() != null && subject.getSubjectType() == 1)
                    {
                        tranScriptSubjectScoreResModel.setSubjectName("@" + tranScriptSubjectScoreResModel.getSubjectName());
                    }
                    tranScriptSubjectScoreResModel.setSubjectId(subject.getId());
                    tranScriptSubjectScoreResModel.setCountedInAverage(subject.getCountedInAverage());
                    if(usuallyScore != null && usuallyScore.containsKey(subject.getId()))
                    {
                        DepartmentScoreRuleDetailResModel subjectWight = scoreTypeMap.get("0");
                        if(subjectWight != null && subjectWight.getWeight() != null) {
                            Integer usually = (usuallyScore.get(subject.getId()) * subjectWight.getWeight()) / WIGHT;
                            tranScriptSubjectScoreResModel.setScore(tranScriptSubjectScoreResModel.getScore() == null ? usually :
                                    tranScriptSubjectScoreResModel.getScore() + usually);
                        }
                    }
                    if(examScore != null && examScore.containsKey(subject.getId())){
                        DepartmentScoreRuleDetailResModel subjectWight = scoreTypeMap.get("1");
                        if(subjectWight != null && subjectWight.getWeight() != null) {
                            Integer exam = (examScore.get(subject.getId()) * subjectWight.getWeight()) / WIGHT;
                            tranScriptSubjectScoreResModel.setScore(tranScriptSubjectScoreResModel.getScore() == null ? exam :
                                    tranScriptSubjectScoreResModel.getScore() + exam);
                        }
                    }
                    SubjectLevelRuleResModel subjectLevelRuleResModel = subjectLevelRuleDetailResModelMap.get(subject.getId());
                    if(subjectLevelRuleResModel != null && subjectLevelRuleResModel.getShowRule() != null)
                    {
                        tranScriptSubjectScoreResModel.setShowRule(subjectLevelRuleResModel.getShowRule());
                        if(subjectLevelRuleResModel.getShowRule() == 1) {
                            tranScriptSubjectScoreResModel.setScoreLevel(getGrade(tranScriptSubjectScoreResModel.getScore(), subjectLevelRuleResModel.getDetailList()));
                        }
                        subjectScores.add(tranScriptSubjectScoreResModel);
                    }
                });
                periodSubjectScoreMap.put(tranScriptSchoolYearDTO.getPeriodId(), subjectScores);
            });
            //插入每个学段的成绩
            List<KindergartenTranScriptPeriodDataResModel> periodDataList = tranScriptGenerateResModel.getPeriodDataList();
            periodDataList.forEach(periodData -> {
                List<TranScriptSubjectScoreResModel> subjectScores = periodSubjectScoreMap.get(periodData.getPeriodId());
                periodData.setSubjectScores(subjectScores);
                //计算平均分
                double averageScore = 0;
                if(departmentScoreRuleResModel.getAvgType() == 1) {
                    averageScore = subjectScores.stream().filter(item -> item.getCountedInAverage() == 1).mapToInt(item ->{
                        DepartmentScoreRuleDetailResModel ruleDetailResModel = wightMap.get(item.getSubjectId());
                        if(ruleDetailResModel == null || ruleDetailResModel.getWeight() == null || item.getScore() == null) {
                            return 0;
                        }
                        return (item.getScore() * ruleDetailResModel.getWeight()) / WIGHT;
                    }).sum();
                }else {
                    averageScore = subjectScores.stream().filter(item -> item.getCountedInAverage() == 1).mapToInt(item -> {
                        if(item.getScore() == null) {
                            return 0;
                        }
                        return item.getScore();
                    }).average().orElse(0);
                }
                periodData.setAverageScore(averageScore);
            });
        }
    }


    private String getGrade(Integer score,List<SubjectLevelRuleDetailResModel> standardEntities )
    {
        if(CollectionUtils.isEmpty(standardEntities) || score == null)
        {
            return null;
        }
        for(SubjectLevelRuleDetailResModel standardEntity : standardEntities)
        {
            //0就默认是>=0
            if(score == 0 && standardEntity.getRuleMin() == 0)
            {
                return standardEntity.getRuleLevel();
            }
            if(score > standardEntity.getRuleMin() * 100 && score <= standardEntity.getRuleMax() * 100)
            {
                return standardEntity.getRuleLevel();
            }
        }
        return null;
    }


    //获取操行
    private void assembleKindergartenData(Long classId, List<KindergartenTranscriptResModel> result,
                                          List<TranScriptSchoolYearDTO> schoolYearDTOS,Long schoolId)
    {
        for (TranScriptSchoolYearDTO schoolYearDTO : schoolYearDTOS)
        {
            //获取该学段下的所有素质分数
            List<StudentQualityScoreModel> studentQualityScoreList = classPerformanceService.getStudentQualityScoreList(schoolYearDTO.getPeriodId(), classId,schoolId);
            List<ExternalCompetitionRecordDTO> competitionRecords = externalCompetitionRecordService.getCompetitionRecords(schoolYearDTO.getPeriodId(), classId);
            //tomap
            Map<Long, List<ExternalCompetitionRecordDTO>> competitionRecordMap;
            if(CollectionUtils.isEmpty(competitionRecords))
            {
                competitionRecordMap = new HashMap<>();
            }else {
                competitionRecordMap = competitionRecords.stream()
                        .collect(Collectors.groupingBy(ExternalCompetitionRecordDTO::getStudentId));
            }
            //转map
            Map<Long, StudentQualityScoreModel> studentQualityScoreMap = studentQualityScoreList.stream()
                    .collect(Collectors.toMap(StudentQualityScoreModel::getStudentId, Function.identity(), (oldValue, newValue) -> oldValue));
            for (KindergartenTranscriptResModel tranScriptGenerateResModel : result)
            {
                List<KindergartenTranScriptPeriodDataResModel> periodDataList = tranScriptGenerateResModel.getPeriodDataList();
                if(periodDataList == null)
                {
                    periodDataList = new ArrayList<>();
                }
                KindergartenTranScriptPeriodDataResModel dataResModel = new KindergartenTranScriptPeriodDataResModel();
                dataResModel.setPeriodId(schoolYearDTO.getPeriodId());
                dataResModel.setPeriodName(schoolYearDTO.getPeriodName());
                dataResModel.setProportion(schoolYearDTO.getProportion());
                dataResModel.setStartTime(schoolYearDTO.getStartTime());
                StudentQualityScoreModel studentQualityScoreModel = studentQualityScoreMap.get(tranScriptGenerateResModel.getStudentId());
                if (studentQualityScoreModel != null)
                {
                    //操行
                    List<StudentQualityScoreDetailResModel> resModels = studentQualityScoreModel.getResModels();
                    if (!CollectionUtils.isEmpty(resModels))
                    {
                        List<KindergartenStudentQualityScoreModel> scoreModels = new ArrayList<>();
                        for (StudentQualityScoreDetailResModel resModel : resModels)
                        {
                            KindergartenStudentQualityScoreModel scoreModel = new KindergartenStudentQualityScoreModel();
                            scoreModel.setQualityProjectId(resModel.getQualityProjectId());
                            scoreModel.setQualityProjectName(resModel.getQualityProjectName());
                            scoreModel.setQualityProjectLevel(resModel.getQualityProjectLevel());
                            scoreModel.setQualityProjectScore(resModel.getQualityProjectScore());
                            scoreModel.setDisplay(false);
                            scoreModels.add(scoreModel);
                        }
                        dataResModel.setResModels(scoreModels);
                    }
                    dataResModel.setComments(studentQualityScoreModel.getComments());
                    //其他
                    dataResModel.setLeavePeriods(studentQualityScoreModel.getLeaveRecords());
                    dataResModel.setAbsencePeriods(studentQualityScoreModel.getAbsenceCount());
                    dataResModel.setLateTimes(studentQualityScoreModel.getLateCount());
                    //奖项数
                    List<ExternalCompetitionRecordDTO> externalCompetitionRecordDTOS = competitionRecordMap.get(tranScriptGenerateResModel.getStudentId());
                    if (externalCompetitionRecordDTOS != null && !externalCompetitionRecordDTOS.isEmpty())
                    {
                        dataResModel.setAwards(externalCompetitionRecordDTOS.stream().map(ExternalCompetitionRecordDTO::getPrize).collect(Collectors.toList()));
                    }
                }
                periodDataList.add(dataResModel);
                tranScriptGenerateResModel.setPeriodDataList(periodDataList);
            }
        }
    }


    //获取操行
    private void assembleData(Long classId, List<TranScriptGenerateResModel> result, List<TranScriptSchoolYearDTO> schoolYearDTOS,Long schoolId)
    {
        for (TranScriptSchoolYearDTO schoolYearDTO : schoolYearDTOS)
        {
            //获取该学段下的所有素质分数
            List<StudentQualityScoreModel> studentQualityScoreList = classPerformanceService.getStudentQualityScoreList(schoolYearDTO.getPeriodId(), classId,schoolId);
            Map<Long, Integer> rewardMap = externalCompetitionRecordService.countCompetitionRecords(schoolYearDTO.getPeriodId(), classId);
            Map<Long, Integer> volunteerCount = volunteerService.getVolunteerCount(classId, schoolYearDTO.getPeriodId());
            List<CompetitionStudentCountDTO> countStudent = competitionRecordService.getCountStudent(classId, schoolYearDTO.getPeriodId());
            //tomap
            Map<Long, CompetitionStudentCountDTO> competitionStudentCountDTOMap;
            if(CollectionUtils.isEmpty(countStudent))
            {
                competitionStudentCountDTOMap = new HashMap<>();
            }else {
                competitionStudentCountDTOMap = countStudent.stream().collect(Collectors.toMap(CompetitionStudentCountDTO::getStudentId, Function.identity(),
                        (oldValue, newValue) -> oldValue));
            }

            //转map
            Map<Long, StudentQualityScoreModel> studentQualityScoreMap = studentQualityScoreList.stream()
                    .collect(Collectors.toMap(StudentQualityScoreModel::getStudentId, Function.identity(), (oldValue, newValue) -> oldValue));
            for (TranScriptGenerateResModel tranScriptGenerateResModel : result)
            {
                List<TranScriptPeriodDataResModel> periodDataList = tranScriptGenerateResModel.getPeriodDataList();
                if(periodDataList == null)
                {
                    periodDataList = new ArrayList<>();
                }
                TranScriptPeriodDataResModel dataResModel = new TranScriptPeriodDataResModel();
                dataResModel.setPeriodId(schoolYearDTO.getPeriodId());
                dataResModel.setPeriodName(schoolYearDTO.getPeriodName());
                dataResModel.setProportion(schoolYearDTO.getProportion());
                dataResModel.setStartTime(schoolYearDTO.getStartTime());
                StudentQualityScoreModel studentQualityScoreModel = studentQualityScoreMap.get(tranScriptGenerateResModel.getStudentId());
                if (studentQualityScoreModel != null)
                {
                    //操行
                    List<StudentQualityScoreDetailResModel> resModels = studentQualityScoreModel.getResModels();
                    if (!CollectionUtils.isEmpty(resModels))
                    {
                        resModels.stream().filter(item -> item.getQualityProjectId() == 0).findFirst().ifPresent(item ->{
                            dataResModel.setConduct(item.getQualityProjectLevel() == null ? "" : item.getQualityProjectLevel());
                        });
                    }
                    //其他
                    dataResModel.setMerits(studentQualityScoreModel.getStrengths());
                    dataResModel.setDemerits(studentQualityScoreModel.getWeaknesses());
                    dataResModel.setOutstandingServices(studentQualityScoreModel.getMajorMerit());
                    dataResModel.setGoodServices(studentQualityScoreModel.getMinorMerit());
                    dataResModel.setMinorFaults(studentQualityScoreModel.getMinorDemerit());
                    dataResModel.setMajorFaults(studentQualityScoreModel.getMajorDemerit());
                    dataResModel.setLeavePeriods(studentQualityScoreModel.getLeaveRecords());
                    dataResModel.setAbsencePeriods(studentQualityScoreModel.getAbsenceCount());
                    dataResModel.setLateTimes(studentQualityScoreModel.getLateCount());
                    //奖项数
                    Integer times = rewardMap.get(tranScriptGenerateResModel.getStudentId());
                    if (times != null)
                    {
                        dataResModel.setAwards(times);
                    }
                    Integer time = volunteerCount.get(tranScriptGenerateResModel.getStudentId());
                    if (time != null)
                    {
                        dataResModel.setVolunteerHours(time);
                    }
                    //比赛的大功小过等等
                    CompetitionStudentCountDTO competitionStudentCountDTO = competitionStudentCountDTOMap.get(tranScriptGenerateResModel.getStudentId());
                    if (competitionStudentCountDTO != null)
                    {
                        dataResModel.setMerits(dataResModel.getMerits() == null ? competitionStudentCountDTO.getMeritAdvantage() :
                                dataResModel.getMerits() + competitionStudentCountDTO.getMeritAdvantage());
                        dataResModel.setDemerits(dataResModel.getDemerits() == null ? competitionStudentCountDTO.getDemeritShortcoming() :
                                dataResModel.getDemerits() + competitionStudentCountDTO.getDemeritShortcoming());
                        dataResModel.setOutstandingServices(dataResModel.getOutstandingServices() == null ? competitionStudentCountDTO.getMeritBig() :
                                dataResModel.getOutstandingServices() + competitionStudentCountDTO.getMeritBig());
                        dataResModel.setGoodServices(dataResModel.getGoodServices() == null ? competitionStudentCountDTO.getMeritSmall() :
                                dataResModel.getGoodServices() + competitionStudentCountDTO.getMeritSmall());
                        dataResModel.setMinorFaults(dataResModel.getMinorFaults() == null ? competitionStudentCountDTO.getDemeritSmall() :
                                dataResModel.getMinorFaults() + competitionStudentCountDTO.getDemeritSmall());
                        dataResModel.setMajorFaults(dataResModel.getMajorFaults() == null ? competitionStudentCountDTO.getDemeritBig() :
                                dataResModel.getMajorFaults() + competitionStudentCountDTO.getDemeritBig());
                    }
                }
                periodDataList.add(dataResModel);
                tranScriptGenerateResModel.setPeriodDataList(periodDataList);
            }
        }
    }


    //计算这个学年的数据
    private void calculateSchoolYearData(List<TranScriptGenerateResModel> result,Long schoolId,Long groupId)
    {
        List<SubjectLevelRuleResModel> subjectLevelRuleResModels = subjectLevelRuleService.getRuleByDepartment(schoolId, groupId);
        //tomap subjectLevelRuleResModels
        Map<Long, SubjectLevelRuleResModel> subjectLevelRuleDetailResModelMap = subjectLevelRuleResModels.stream()
                .collect(Collectors.toMap(SubjectLevelRuleResModel::getSubjectId, item -> item));
        List<ScoreRankDTO> rankScore = new ArrayList<>();
        for (TranScriptGenerateResModel tranScriptGenerateResModel : result)
        {

            List<TranScriptPeriodDataResModel> periodDataList = tranScriptGenerateResModel.getPeriodDataList();
            if(periodDataList == null)
            {
                return;
            }
            TranScriptYearSummaryResModel yearSummary = new TranScriptYearSummaryResModel();
            yearSummary.setTotalStudents(result.size());
//            List<TranScriptSubjectScoreResModel> subjectScores = new ArrayList<>();
            Map<Long, TranScriptSubjectScoreResModel> subjectScoreMap = new HashMap<>();
            double avgScore = 0;
            String conduct = "";
            periodDataList.sort(Comparator.comparing(TranScriptPeriodDataResModel::getStartTime));
            for (TranScriptPeriodDataResModel periodData : periodDataList)
            {
                List<TranScriptSubjectScoreResModel> scoreResModels = periodData.getSubjectScores();
                if(scoreResModels != null){
                    for (TranScriptSubjectScoreResModel scoreResModel : scoreResModels)
                    {
                        TranScriptSubjectScoreResModel scoreResModel1 = subjectScoreMap.get(scoreResModel.getSubjectId());
                        if(scoreResModel1 == null){
                            TranScriptSubjectScoreResModel resModel = new TranScriptSubjectScoreResModel();
                            resModel.setSubjectId(scoreResModel.getSubjectId());
                            resModel.setSubjectName(scoreResModel.getSubjectName());
                            if(scoreResModel.getScore() != null && periodData.getProportion() != null) {
                                resModel.setScore((scoreResModel.getScore() * periodData.getProportion()) /WIGHT);
                            }
                            subjectScoreMap.put(scoreResModel.getSubjectId(), resModel);
                        }else{
                            if(scoreResModel.getScore() != null && periodData.getProportion() != null) {
                                scoreResModel1.setScore(scoreResModel1.getScore() == null ? ((scoreResModel.getScore() * periodData.getProportion()) /WIGHT) :
                                        scoreResModel1.getScore() + ((scoreResModel.getScore() * periodData.getProportion()) /WIGHT));
                            }
                        }
                    }
                }
                if(periodData.getAverageScore() != null && periodData.getProportion() != null){
                    avgScore += periodData.getAverageScore() * periodData.getProportion() /WIGHT;
                }
                conduct = periodData.getConduct();
                //totalLeavePeriods
                if(periodData.getLeavePeriods() != null) {
                    yearSummary.setTotalLeavePeriods(yearSummary.getTotalLeavePeriods() == null ?
                            periodData.getLeavePeriods() : yearSummary.getTotalLeavePeriods() + periodData.getLeavePeriods());
                }
                if(periodData.getAbsencePeriods() != null) {
                    yearSummary.setTotalAbsencePeriods(yearSummary.getTotalAbsencePeriods() == null ?
                            periodData.getAbsencePeriods() : yearSummary.getTotalAbsencePeriods() + periodData.getAbsencePeriods());
                }
                if(periodData.getLateTimes() != null) {
                    yearSummary.setTotalLateTimes(yearSummary.getTotalLateTimes() == null ?
                            periodData.getLateTimes() : yearSummary.getTotalLateTimes() + periodData.getLateTimes());
                }
                if(periodData.getAwards() != null) {
                    yearSummary.setTotalAwards(yearSummary.getTotalAwards() == null ?
                            periodData.getAwards() : yearSummary.getTotalAwards() + periodData.getAwards());
                }
                if(periodData.getMerits() != null) {
                    yearSummary.setTotalMerits(yearSummary.getTotalMerits() == null ?
                            periodData.getMerits() : yearSummary.getTotalMerits() + periodData.getMerits());
                }
                if(periodData.getDemerits() != null) {
                    yearSummary.setTotalDemerits(yearSummary.getTotalDemerits() == null ?
                            periodData.getDemerits() : yearSummary.getTotalDemerits() + periodData.getDemerits());
                }
                if(periodData.getGoodServices() != null) {
                    yearSummary.setTotalGoodServices(yearSummary.getTotalGoodServices() == null ?
                            periodData.getGoodServices() : yearSummary.getTotalGoodServices() + periodData.getGoodServices());
                }
                if(periodData.getOutstandingServices() != null) {
                    yearSummary.setTotalOutstandingServices(yearSummary.getTotalOutstandingServices() == null ?
                            periodData.getOutstandingServices() : yearSummary.getTotalOutstandingServices() + periodData.getOutstandingServices());
                }
                if(periodData.getMinorFaults() != null) {
                yearSummary.setTotalMinorFaults(yearSummary.getTotalMinorFaults() == null ?
                        periodData.getMinorFaults() : yearSummary.getTotalMinorFaults() + periodData.getMinorFaults());
                }
                if(periodData.getMajorFaults() != null) {
                    yearSummary.setTotalMajorFaults(yearSummary.getTotalMajorFaults() == null ?
                            periodData.getMajorFaults() : yearSummary.getTotalMajorFaults() + periodData.getMajorFaults());
                }
            }
            yearSummary.setSubjectScores(getSubjectsBySort(subjectScoreMap.values()));
            for (TranScriptSubjectScoreResModel scoreResModel : yearSummary.getSubjectScores())
            {
                SubjectLevelRuleResModel subjectLevelRuleResModel = subjectLevelRuleDetailResModelMap.get(scoreResModel.getSubjectId());
                if(subjectLevelRuleResModel != null)
                {
                    scoreResModel.setShowRule(subjectLevelRuleResModel.getShowRule());
                    scoreResModel.setScoreLevel(getGrade(scoreResModel.getScore(), subjectLevelRuleResModel.getDetailList()));
                }
            }
            yearSummary.setAverageScore(avgScore);
            if(avgScore >= 6000)
            {
                //	学年成绩平均分 大于等于60分，显示：准予升级 小于60分，显示：留级
                tranScriptGenerateResModel.setRemarks("准予升级");
            }else {
                tranScriptGenerateResModel.setRemarks("留级");
            }
            yearSummary.setConduct(conduct);
            tranScriptGenerateResModel.setYearSummary(yearSummary);
            ScoreRankDTO scoreRankDTO = new ScoreRankDTO();
            scoreRankDTO.setStudentId(tranScriptGenerateResModel.getStudentId());
            scoreRankDTO.setAvgScore(avgScore);
            rankScore.add(scoreRankDTO);
        }

        rankScore.sort(Comparator.comparing(ScoreRankDTO::getAvgScore).reversed());
        Map<Long, Integer> periodRankScore = new HashMap<>();
        int rank = 1;
        double privousScore = 0;
        for (ScoreRankDTO scoreRankDTO : rankScore) {
            if (privousScore == scoreRankDTO.getAvgScore()) {
                periodRankScore.put(scoreRankDTO.getStudentId(), (rank - 1) > 0 ? (rank - 1) : rank);
            } else {
                periodRankScore.put(scoreRankDTO.getStudentId(), rank++);
            }
            privousScore = scoreRankDTO.getAvgScore();
        }
        //计算排名
        for (TranScriptGenerateResModel tranScriptGenerateResModel : result) {
            TranScriptYearSummaryResModel periodDataList = tranScriptGenerateResModel.getYearSummary();
            if (periodDataList == null) {
                continue;
            }
            //计算这个学生排名
            Integer ranks = periodRankScore.get(tranScriptGenerateResModel.getStudentId());
            if (ranks != null) {
                periodDataList.setRank(ranks);
            }
        }
    }

    private List<TranScriptSubjectScoreResModel> getSubjectsBySort(Collection<TranScriptSubjectScoreResModel> values) {
        List<Long> subjectIds = values.stream().map(TranScriptSubjectScoreResModel::getSubjectId).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(subjectIds)) {
            return new ArrayList<>(values);
        }
        Map<Long, TranScriptSubjectScoreResModel> subjectScoreMap = values.stream().collect(Collectors.toMap(TranScriptSubjectScoreResModel::getSubjectId, item -> item));
        List<SubjectRelEntity> list = subjectRelService.list(Wrappers.<SubjectRelEntity>lambdaQuery()
                .in(SubjectRelEntity::getId, subjectIds));
        if (ObjectUtils.isNotEmpty(list)) {
            List<TranScriptSubjectScoreResModel> resultList = new ArrayList<>();
            list.sort(Comparator.comparing(SubjectRelEntity::getNumber));
            List<Long> sortId = list.stream().map(BaseEntity::getId).collect(Collectors.toList());
            for (Long id : sortId){
                resultList.add(subjectScoreMap.get(id));
            }
            return resultList;
        }
        return new ArrayList<>(values);
    }


    //计算这个学年的数据
    private void calculateKindergartenSchoolYearData(List<KindergartenTranscriptResModel> result,Long schoolId,Long groupId)
    {
        List<SubjectLevelRuleResModel> subjectLevelRuleResModels = subjectLevelRuleService.getRuleByDepartment(schoolId, groupId);
        List<QualityEvaluationGradeStandardResModel> standardEntities = qualityEvaluationService.listGradeStandards(schoolId);
        //tomap subjectLevelRuleResModels
        Map<Long, SubjectLevelRuleResModel> subjectLevelRuleDetailResModelMap = subjectLevelRuleResModels.stream()
                .collect(Collectors.toMap(SubjectLevelRuleResModel::getSubjectId, item -> item));
        for (KindergartenTranscriptResModel tranScriptGenerateResModel : result)
        {

            List<KindergartenTranScriptPeriodDataResModel> periodDataList = tranScriptGenerateResModel.getPeriodDataList();
            if(periodDataList == null)
            {
                return;
            }
            KindergartenTranScriptYearSummaryResModel yearSummary = new KindergartenTranScriptYearSummaryResModel();
//            List<TranScriptSubjectScoreResModel> subjectScores = new ArrayList<>();
            Map<Long, TranScriptSubjectScoreResModel> subjectScoreMap = new HashMap<>();
            Map<Long, KindergartenStudentQualityScoreModel> qualityScoreMap = new HashMap<>();
            double avgScore = 0;
            periodDataList.sort(Comparator.comparing(KindergartenTranScriptPeriodDataResModel::getStartTime));
            for (KindergartenTranScriptPeriodDataResModel periodData : periodDataList)
            {
                List<TranScriptSubjectScoreResModel> scoreResModels = periodData.getSubjectScores();
                if(scoreResModels != null){
                    for (TranScriptSubjectScoreResModel scoreResModel : scoreResModels)
                    {
                        TranScriptSubjectScoreResModel scoreResModel1 = subjectScoreMap.get(scoreResModel.getSubjectId());
                        if(scoreResModel1 == null){
                            TranScriptSubjectScoreResModel resModel = new TranScriptSubjectScoreResModel();
                            resModel.setSubjectId(scoreResModel.getSubjectId());
                            resModel.setSubjectName(scoreResModel.getSubjectName());
                            if(scoreResModel.getScore() != null && periodData.getProportion() != null) {
                                resModel.setScore((scoreResModel.getScore() * periodData.getProportion()) / WIGHT);
                            }
                            subjectScoreMap.put(scoreResModel.getSubjectId(), resModel);
                        }else{
                            if(scoreResModel.getScore() != null && periodData.getProportion() != null) {
                                scoreResModel1.setScore(scoreResModel1.getScore() == null ? ((scoreResModel.getScore() * periodData.getProportion()) /WIGHT) :
                                        scoreResModel1.getScore() + ((scoreResModel.getScore() * periodData.getProportion()) /WIGHT));
                            }
                        }

                    }
                }
                if(periodData.getAverageScore() != null && periodData.getProportion() != null) {
                    avgScore += periodData.getAverageScore() * periodData.getProportion() / WIGHT;
                }
                //totalLeavePeriods
                if(periodData.getLeavePeriods() != null) {
                    yearSummary.setTotalLeavePeriods(yearSummary.getTotalLeavePeriods() == null ?
                            periodData.getLeavePeriods() : yearSummary.getTotalLeavePeriods() + periodData.getLeavePeriods());
                }
                if(periodData.getAbsencePeriods() != null) {
                    yearSummary.setTotalAbsencePeriods(yearSummary.getTotalAbsencePeriods() == null ?
                            periodData.getAbsencePeriods() : yearSummary.getTotalAbsencePeriods() + periodData.getAbsencePeriods());
                }
                if(periodData.getLateTimes() != null) {
                    yearSummary.setTotalLateTimes(yearSummary.getTotalLateTimes() == null ?
                            periodData.getLateTimes() : yearSummary.getTotalLateTimes() + periodData.getLateTimes());
                }

                List<KindergartenStudentQualityScoreModel> resModels = periodData.getResModels();
                if(resModels != null){
                    for (KindergartenStudentQualityScoreModel resModel : resModels)
                    {
                        KindergartenStudentQualityScoreModel qualityScore = qualityScoreMap.get(resModel.getQualityProjectId());
                        if(qualityScore == null){
                            KindergartenStudentQualityScoreModel qualityScoreModel = new KindergartenStudentQualityScoreModel();
                            qualityScoreModel.setQualityProjectId(resModel.getQualityProjectId());
                            qualityScoreModel.setQualityProjectName(resModel.getQualityProjectName());
                            if(resModel.getQualityProjectScore() != null && periodData.getProportion() != null) {
                                qualityScoreModel.setQualityProjectScore((resModel.getQualityProjectScore() * periodData.getProportion()) / WIGHT);
                            }
                            qualityScoreMap.put(resModel.getQualityProjectId(), qualityScoreModel);
                        }else {
                            if(resModel.getQualityProjectScore() != null && periodData.getProportion() != null && qualityScore.getQualityProjectScore() != null) {
                                qualityScore.setQualityProjectScore(qualityScore.getQualityProjectScore() + ((resModel.getQualityProjectScore() * periodData.getProportion()) / WIGHT));
                            }
                        }
                    }
                }
            }
            yearSummary.setSubjectScores(getSubjectsBySort(subjectScoreMap.values()));
            yearSummary.setResModels(new ArrayList<>(qualityScoreMap.values()));
            for (TranScriptSubjectScoreResModel scoreResModel : yearSummary.getSubjectScores())
            {
                SubjectLevelRuleResModel subjectLevelRuleResModel = subjectLevelRuleDetailResModelMap.get(scoreResModel.getSubjectId());
                if(subjectLevelRuleResModel != null)
                {
                    scoreResModel.setShowRule(subjectLevelRuleResModel.getShowRule());
                    scoreResModel.setScoreLevel(getGrade(scoreResModel.getScore(), subjectLevelRuleResModel.getDetailList()));
                }
            }
            for (KindergartenStudentQualityScoreModel qualityScore : yearSummary.getResModels())
            {
                if(standardEntities != null)
                {
                    qualityScore.setDisplay(false);
                    qualityScore.setQualityProjectLevel(getGrade(qualityScore.getQualityProjectScore(), standardEntities));
                }
            }
            yearSummary.setAverageScore(avgScore);
            if(avgScore >= 6000)
            {
                //	学年成绩平均分 大于等于60分，显示：准予升级 小于60分，显示：留级
                tranScriptGenerateResModel.setRemarks("准予升级");
            }else {
                tranScriptGenerateResModel.setRemarks("留级");
            }
            tranScriptGenerateResModel.setYearSummary(yearSummary);

        }
    }


    private String getGrade(Long score,List<QualityEvaluationGradeStandardResModel> standardEntities )
    {
        if(CollectionUtils.isEmpty(standardEntities) || score == null)
        {
            return null;
        }
        for(QualityEvaluationGradeStandardResModel standardEntity : standardEntities)
        {
            //0就默认是>=0
            if(score == 0 && standardEntity.getScoreMin() == 0)
            {
                return standardEntity.getGrade();
            }
            if(score > standardEntity.getScoreMin() * 100 && score <= standardEntity.getScoreMax() * 100)
            {
                return standardEntity.getGrade();
            }
        }
        return null;
    }




}