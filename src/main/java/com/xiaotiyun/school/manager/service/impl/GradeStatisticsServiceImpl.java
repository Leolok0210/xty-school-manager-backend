package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.handler.WriteHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.DepartmentScoreRuleEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.enums.SystemSettingKeyEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.CustomMergeUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.dto.StudentSubjectScoreDTO;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GradeStatisticsServiceImpl implements GradeStatisticsService {

    @Autowired
    private ExportFileHandler exportFileHandler;
    @Resource
    private StudentUsuallyRuleService studentUsuallyRuleService;
    @Resource
    private DepartmentScoreRuleService departmentScoreRuleService;
    @Resource
    private SemesterService semesterService;
    @Resource
    private SysClassService sysClassService;
    @Resource
    private GradeGroupService gradeGroupService;
    @Resource
    private StudentService studentService;
    @Resource
    private StudentUsuallyTaskService studentUsuallyTaskService;
    @Resource
    private StudentUsuallyScoreService studentUsuallyScoreService;
    @Resource
    private StudentExamTaskService studentExamTaskService;
    @Resource
    private StudentExamScoreService studentExamScoreService;
    @Resource
    private SystemSettingService systemSettingService;

    @Resource
    private SubjectRelService subjectRelService;

    @Resource
    private UserAuthHelper userAuthHelper;

    @Override
    public List<GradeClassAvgResModel> getGradeClassAvg(GradeClassAvgReqModel reqModel) {
        // 检查学段平均分规则

        // 获取学段权重
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if(CollectionUtils.isEmpty(classIds))
            {
                return new ArrayList<>();
            }
        }
        // 获取班级
        List<SysClass> classList = sysClassService.list(Wrappers.<SysClass>lambdaQuery()
                .eq(SysClass::getDepartment, reqModel.getDepartment())
                .eq(SysClass::getSid, reqModel.getSchoolYear())
                .in(!CollectionUtils.isEmpty(classIds), SysClass::getId, classIds)
                .eq(SysClass::getSchoolId, reqModel.getSchoolId()));
        if (ObjectUtils.isEmpty(classList)) {
            log.error("获取各班平均分失败，没有查询到班级");
            return null;
        }
        List<Long> classId = classList.stream().map(SysClass::getId).collect(Collectors.toList());
        List<Long> groupId = classList.stream().map(SysClass::getGradeGroup).collect(Collectors.toList());
        // 获取级组信息
        List<GradeGroup> gradeGroups = gradeGroupService.listByIds(groupId);
        if (ObjectUtils.isEmpty(gradeGroups)) {
            log.error("获取各班平均分失败，没有查询到级组");
            return null;
        }
        Map<Long, GradeGroup> gradeGroupMap = gradeGroups.stream().collect(Collectors.toMap(GradeGroup::getId, Function.identity()));
        // 获取班级学生数量
        List<StudentEntity> studentEntities = studentService.list(Wrappers.<StudentEntity>lambdaQuery().in(StudentEntity::getClassId, classId));
        if (ObjectUtils.isEmpty(studentEntities)) {
            log.error("获取各班平均分失败，没有查询到学生");
            return null;
        }
        Map<Long, List<StudentEntity>> classSizeMap = studentEntities.stream().collect(Collectors.groupingBy(StudentEntity::getClassId));

        // 计算各班平均分
        List<GradeClassAvgResModel> resList = new ArrayList<>();
        for (SysClass sysClass : classList) {
            // 获取科目
            SubjectRelGroupQueryReqModel reqSubjectRel = new SubjectRelGroupQueryReqModel();
            reqSubjectRel.setSchoolId(reqModel.getSchoolId());
            reqSubjectRel.setGroupId(sysClass.getGradeGroup());
            reqSubjectRel.setCountedInAverage(1);
            List<SubjectRelResModel> relResModels = subjectRelService.listByGroup(reqSubjectRel);
            if (ObjectUtils.isEmpty(relResModels)) {
                //产品要求：如果没有科目或者是科目相关的配置则不返回改班级相关的信息
                continue;
            }
            // 不加权不需要判断规则 0-加权平均 1-直接平均
            Map<Long, DepartmentScoreRuleEntity> subjectIdRuleMap = new HashMap<>();
            Map<Long, DepartmentScoreRuleEntity> artSubjectWightMap = new HashMap<>();
            Map<Long, DepartmentScoreRuleEntity> scienceSubjectWightMap = new HashMap<>();
            //商业分科
            Map<Long, DepartmentScoreRuleEntity> commerceSubjectWightMap = new HashMap<>();
            GradeGroup gradeGroup = gradeGroupMap.get(sysClass.getGradeGroup());
            int artsScienceType = gradeGroup.getProfessionalSubject();
            List<DepartmentScoreRuleEntity> scoreRuleEntities = departmentScoreRuleService.list(Wrappers.<DepartmentScoreRuleEntity>lambdaQuery()
                    .eq(DepartmentScoreRuleEntity::getGroupId, sysClass.getGradeGroup())
                    .eq(DepartmentScoreRuleEntity::getSchoolId, reqModel.getSchoolId()));
            if (ObjectUtils.isEmpty(scoreRuleEntities)) {
                //产品要求：如果没有科目或者是科目相关的配置则不返回改班级相关的信息
                continue;
            }
            Optional<DepartmentScoreRuleEntity> first = scoreRuleEntities.stream()
                    .filter(item -> item.getScoreType().equals(DepartmentScoreRuleEnum.SCORE_AVG_RULE.getValue())).findFirst();
            Integer semesterAvgRule = 0;
            if (first.isPresent()) {
                semesterAvgRule = first.get().getScoreRule();
            }
            if (semesterAvgRule == 1) {
                if(artsScienceType == 0 || artsScienceType == 1){
                    subjectIdRuleMap = scoreRuleEntities.stream()
                            .filter(item -> item.getScoreType().equals("2"))
                            .collect(Collectors.toMap(DepartmentScoreRuleEntity::getSubjectId, item -> item));
                }else {
                    artSubjectWightMap = scoreRuleEntities.stream()
                            .filter(item -> item.getScoreType().equals(DepartmentScoreRuleEnum.COMMON_SCHOOL_SCORE.getValue()))
                            .collect(Collectors.toMap(DepartmentScoreRuleEntity::getSubjectId, item -> item));
                    scienceSubjectWightMap = scoreRuleEntities.stream()
                            .filter(item -> item.getScoreType().equals(DepartmentScoreRuleEnum.COMMON_PROVINCE_SCORE.getValue()))
                            .collect(Collectors.toMap(DepartmentScoreRuleEntity::getSubjectId, item -> item));
                    if (artsScienceType == 3)
                    {
                        commerceSubjectWightMap = scoreRuleEntities.stream()
                                .filter(item -> item.getScoreType().equals(DepartmentScoreRuleEnum.COMMON_COMMERCE_SCORE.getValue()))
                                .collect(Collectors.toMap(DepartmentScoreRuleEntity::getSubjectId, item -> item));
                    }
                }
                // 获取科目评级 不做校验，如果科目没有配置的，直接跳过
//                for (SubjectRelResModel subjectDetailResModel : relResModels) {
//                    if (!scoreRuleSubjectIds.contains(subjectDetailResModel.getId()))
//                        throw new BusinessException(LanguageConstants.SUBJECT_NOT_FOUND);
//                }
            }
            // 获取各班学生
            List<StudentSubjectScoreDTO> studentSubjectScore = getStudentSubjectScore(GetStudentSubjectScoreReqModel.builder()
                    .department(reqModel.getDepartment())
                    .schoolYear(reqModel.getSchoolYear())
                    .schoolId(reqModel.getSchoolId())
                    .classId(Collections.singletonList(sysClass.getId()))
                    .groupId(sysClass.getGradeGroup())
                    .semesterId(Collections.singletonList(reqModel.getSemesterId()))
                    .build());
            if (ObjectUtils.isEmpty(studentSubjectScore)) {
                log.error("获取各班平均分失败，没有获取到学生学段平均分");
                continue;
            }
            Map<Long, List<StudentSubjectScoreDTO>> classIdListMap = studentSubjectScore.stream().filter(a -> a.getScore() > 0L).collect(Collectors.groupingBy(StudentSubjectScoreDTO::getClassId));
            GradeClassAvgResModel resModel = new GradeClassAvgResModel();
            resModel.setClassId(sysClass.getId());
            resModel.setClassName(sysClass.getClassName());
            resModel.setClassGroupId(sysClass.getGradeGroup());
            resModel.setClassGroupName(gradeGroupMap.get(sysClass.getGradeGroup()).getGradeGroupName());
            resModel.setClassSize(classSizeMap.get(sysClass.getId()) == null? 0 :classSizeMap.get(sysClass.getId()).size());
            List<StudentSubjectScoreDTO> studentList = classIdListMap.get(sysClass.getId());
            //在这里做学生的区分，用来获取不同的科目权重规则。
            if(artsScienceType == 0)
            {
                //这个级组不做文理科区分
                resModel.setAverageScore(getGradeClassAvgResModel(studentList, relResModels, subjectIdRuleMap, semesterAvgRule).toString());
                resList.add(resModel);
            }else {
                //需要区分文理科
                if(gradeGroup.getArtsScienceType() == 1)
                {
                    //班级区分
                    if(sysClass.getArtsScience() == 1)
                    {
                        //文科 Or 理工科
                        List<SubjectRelResModel> subjectRelList = relResModels.stream()
                                .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(sysClass.getArtsScience()))
                                .collect(Collectors.toList());
                        resModel.setAverageScore(getGradeClassAvgResModel(studentList, subjectRelList, artSubjectWightMap, semesterAvgRule).toString());
                        resList.add(resModel);
                    }else if (sysClass.getArtsScience() == 2){
                        List<SubjectRelResModel> subjectRelList = relResModels.stream()
                                .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(sysClass.getArtsScience()))
                                .collect(Collectors.toList());
                        resModel.setAverageScore(getGradeClassAvgResModel(studentList, subjectRelList, scienceSubjectWightMap, semesterAvgRule).toString());
                        resList.add(resModel);
                    }else if (sysClass.getArtsScience() == 3){
                        List<SubjectRelResModel> subjectRelList = relResModels.stream()
                                .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(sysClass.getArtsScience()))
                                .collect(Collectors.toList());
                        resModel.setAverageScore(getGradeClassAvgResModel(studentList, subjectRelList, commerceSubjectWightMap, semesterAvgRule).toString());
                    }
                }else if(gradeGroup.getArtsScienceType() == 2)
                {
                    List<SubjectRelResModel> artSubjectRelList = relResModels.stream()
                            .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(1))
                            .collect(Collectors.toList());
                    List<SubjectRelResModel> scienceSubjectRelList = relResModels.stream()
                            .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(2))
                            .collect(Collectors.toList());
                    //区分文理科
                    List<StudentSubjectScoreDTO> artStudentList = studentSubjectScore.stream().filter(a -> a.getArtsScience() == 1).collect(Collectors.toList());
                    List<StudentSubjectScoreDTO> scienceStudentList = studentSubjectScore.stream().filter(a -> a.getArtsScience() == 2).collect(Collectors.toList());
                    BigDecimal artScore = getGradeClassAvgResModel(artStudentList, artSubjectRelList, artSubjectWightMap, semesterAvgRule);
                    BigDecimal scienceScore = getGradeClassAvgResModel(scienceStudentList, scienceSubjectRelList, scienceSubjectWightMap, semesterAvgRule);
                    if (artsScienceType == 3)
                    {
                        List<SubjectRelResModel> commerceSubjectRelList = relResModels.stream()
                                .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(3))
                                .collect(Collectors.toList());
                        List<StudentSubjectScoreDTO> commerceStudentList = studentSubjectScore.stream().filter(a -> a.getArtsScience() == 3).collect(Collectors.toList());
                        BigDecimal commerceScore = getGradeClassAvgResModel(commerceStudentList, commerceSubjectRelList, commerceSubjectWightMap, semesterAvgRule);
                        resModel.setAverageScore(artScore.add(scienceScore).add(commerceScore).divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP).toString());
                    }else {
                        resModel.setAverageScore(artScore.add(scienceScore).divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP).toString());
                    }
                    resList.add(resModel);
                }
            }
        }
        return resList;
    }

    private BigDecimal getGradeClassAvgResModel(List<StudentSubjectScoreDTO> studentList,List<SubjectRelResModel> relResModels ,
                                          Map<Long, DepartmentScoreRuleEntity> subjectIdRuleMap, Integer semesterAvgRule)
    {
        BigDecimal avgScore = BigDecimal.ZERO;
        if (CollectionUtils.isEmpty(relResModels))
        {
            return avgScore;
        }
        if (ObjectUtils.isNotEmpty(studentList)){
            for (SubjectRelResModel subject : relResModels) {
                Double subAvgScore = studentList.stream()
                        .filter(student -> student.getSubjectId().equals(subject.getId()))
                        .collect(Collectors.averagingDouble(StudentSubjectScoreDTO::getScore));
                if (semesterAvgRule == 1){
                    DepartmentScoreRuleEntity scoreRuleEntity = subjectIdRuleMap.get(subject.getId());
                    if(scoreRuleEntity != null && scoreRuleEntity.getWeight() != null) {
                        avgScore = avgScore.add(BigDecimal.valueOf(subAvgScore)
                                .multiply(BigDecimal.valueOf(scoreRuleEntity.getWeight()))
                                .divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP));
                    }
                } else {
                    avgScore = avgScore.add(BigDecimal.valueOf(subAvgScore));
                }
            }
            if (semesterAvgRule == 0){
                avgScore = avgScore.divide(BigDecimal.valueOf(relResModels.size()), 2, RoundingMode.HALF_UP);
            }
        }
        return avgScore.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    public ResponseEntity<byte[]> exportGradeClassAvg(GradeClassAvgReqModel reqModel) throws UnsupportedEncodingException {
        // 获取数据
        List<GradeClassAvgResModel> resModels = getGradeClassAvg(reqModel);
        if (ObjectUtils.isEmpty(resModels)) {
            log.error("导出各班平均分失败，没有获取到数据");
            return null;
        }
        // 设置导出相应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        // 根据语言，导出不同的语言文档
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
            List<GradeClassAvgExportEnModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        GradeClassAvgExportEnModel exportModel = new GradeClassAvgExportEnModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, GradeClassAvgExportEnModel.class)
                    .sheet("The average score of each class")
                    .doWrite(exportEnModels);
            headers.setContentDispositionFormData("attachment", "The average score of each class_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } else if (currentLanguage.equals(SchoolLanguageEnum.ZH_MO.getCode())) {
            List<GradeClassAvgExportModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        GradeClassAvgExportModel exportModel = new GradeClassAvgExportModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, GradeClassAvgExportModel.class)
                    .sheet("各班平均分")
                    .doWrite(exportEnModels);
            String encodedFileName = URLEncoder.encode("各班平均分_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx", "UTF-8");
            headers.setContentDispositionFormData("attachment", encodedFileName);
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
            List<GradeClassAvgExportPtModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        GradeClassAvgExportPtModel exportModel = new GradeClassAvgExportPtModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, GradeClassAvgExportPtModel.class)
                    .sheet("Pontuação média por classe")
                    .doWrite(exportEnModels);
            headers.setContentDispositionFormData("attachment", "Pontuação média por classe_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        }
        return null;
    }

    @Override
    public List<GradeFlunkResModel> getGradeFlunk(GradeFlunkReqModel reqModel) {
        // 获取科目
        SubjectRelGroupQueryReqModel reqSubjectRel = new SubjectRelGroupQueryReqModel();
        reqSubjectRel.setSchoolId(reqModel.getSchoolId());
        reqSubjectRel.setGroupId(reqModel.getGroupId());
        reqSubjectRel.setCountedInAverage(1);
        List<SubjectRelResModel> relResModels = subjectRelService.listByGroup(reqSubjectRel);
        if (ObjectUtils.isEmpty(relResModels)) {
            log.error("获取不合格成绩失败，没有科目");
            return null;
        }
        // 获得学段
        List<SemesterEntity> semesterEntities = semesterService.list(Wrappers.<SemesterEntity>lambdaQuery()
                .eq(SemesterEntity::getSchoolYear, reqModel.getSchoolYear())
                .eq(SemesterEntity::getDepartment, reqModel.getDepartment())
                .eq(SemesterEntity::getSchoolId, reqModel.getSchoolId()));
        if (ObjectUtils.isEmpty(semesterEntities)) {
            log.error("获取不合格成绩失败，没有查询到学期");
            return null;
        }
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if(CollectionUtils.isEmpty(classIds))
            {
                return new ArrayList<>();
            }
        }
        // 获取班级和级组
        List<SysClass> classList = sysClassService.list(Wrappers.<SysClass>lambdaQuery()
                .eq(SysClass::getDepartment, reqModel.getDepartment())
                .eq(SysClass::getGradeGroup, reqModel.getGroupId())
                .eq(SysClass::getSid, reqModel.getSchoolYear())
                .in(CollectionUtils.isNotEmpty(classIds), SysClass::getId, classIds)
                .eq(SysClass::getSchoolId, reqModel.getSchoolId()));
        if (ObjectUtils.isEmpty(classList)) {
            log.error("获取不合格成绩失败，没有查询到班级");
            return null;
        }
        List<Long> groupId = classList.stream().map(SysClass::getGradeGroup).collect(Collectors.toList());
        List<GradeGroup> gradeGroups = gradeGroupService.listByIds(groupId);
        if (ObjectUtils.isEmpty(gradeGroups)) {
            log.error("获取不合格成绩失败，没有查询到级组");
            return null;
        }
        Map<Long, GradeGroup> gradeGroupMap = gradeGroups.stream().collect(Collectors.toMap(GradeGroup::getId, Function.identity()));
        // 获取学生科目数据
        List<StudentSubjectScoreDTO> studentSubjectScore = getStudentSubjectScore(GetStudentSubjectScoreReqModel.builder()
                .department(reqModel.getDepartment())
                .schoolYear(reqModel.getSchoolYear())
                .classId(classList.stream().map(SysClass::getId).collect(Collectors.toList()))
                .schoolId(reqModel.getSchoolId())
                .groupId(reqModel.getGroupId())
                .build());
        if (ObjectUtils.isEmpty(studentSubjectScore)) {
            log.error("获取不合格成绩失败，没有查询到学生科目数据");
            return null;
        }
        Map<Long, List<StudentSubjectScoreDTO>> semesterIdListMap = studentSubjectScore.stream().collect(Collectors.groupingBy(StudentSubjectScoreDTO::getSemesterId));
        // 拼装返回数据
        List<GradeFlunkResModel> resList = new ArrayList<>();
        // 遍历科目
        for (SubjectRelResModel subject : relResModels) {
            GradeFlunkResModel resModel = new GradeFlunkResModel();
            resModel.setSubjectId(subject.getId());
            resModel.setSubjectName(subject.getSubject().getSubjectName());
            List<GradeFlunkDetailResModel> detailResList = new ArrayList<>();
            // 遍历班级
            for (SysClass sysClass : classList) {
                GradeFlunkDetailResModel detailResModel = new GradeFlunkDetailResModel();
                detailResModel.setClassId(sysClass.getId());
                detailResModel.setClassName(sysClass.getClassName());
                detailResModel.setGroupId(sysClass.getGradeGroup());
                detailResModel.setGroupName(gradeGroupMap.get(sysClass.getGradeGroup()).getGradeGroupName());
                List<GradeFlunkSemesterResModel> semesterResList = new ArrayList<>();
                // 遍历学段
                for (SemesterEntity semesterEntity : semesterEntities) {
                    GradeFlunkSemesterResModel semesterResModel = new GradeFlunkSemesterResModel();
                    semesterResModel.setSemesterId(semesterEntity.getId());
                    semesterResModel.setSemesterName(semesterEntity.getName());
                    List<StudentSubjectScoreDTO> studentList = semesterIdListMap.get(semesterEntity.getId());
                    if (ObjectUtils.isNotEmpty(studentList)) {
                        // 计算不合格人数，StudentSubjectScoreDTO结果分数*100
                        semesterResModel.setFlunkCount((int) studentList.stream()
                               .filter(student -> student.getSubjectId().equals(subject.getId()) &&
                                       student.getClassId().equals(sysClass.getId()) &&
                                       student.getScore() < 6000)
                               .count());
                    } else {
                        semesterResModel.setFlunkCount(0);
                    }
                    semesterResList.add(semesterResModel);
                }
                detailResModel.setDetails(semesterResList);
                detailResList.add(detailResModel);
            }
            resModel.setDetails(detailResList);
            resList.add(resModel);
        }
        return resList;
    }

    @Override
    public ResponseEntity<byte[]> exportFlunkExport(GradeFlunkReqModel reqModel) throws UnsupportedEncodingException {
        // 获取学段
        List<SemesterEntity> semesterEntities = semesterService.list(Wrappers.<SemesterEntity>lambdaQuery()
                .eq(SemesterEntity::getSchoolYear, reqModel.getSchoolYear())
                .eq(SemesterEntity::getDepartment, reqModel.getDepartment())
                .eq(SemesterEntity::getSchoolId, reqModel.getSchoolId())
                .orderByAsc(SemesterEntity::getStartTime));
        if (ObjectUtils.isEmpty(semesterEntities)) {
            log.error("导出不合格成绩失败，没有查询到学期");
            return null;
        }
        // 获取数据
        List<GradeFlunkResModel> resModels = getGradeFlunk(reqModel);
        if (ObjectUtils.isEmpty(resModels)) {
            log.error("导出不合格成绩失败，没有获取到数据");
            return null;
        }
        // 设置动态列头
        List<List<String>> head = new ArrayList<>();
        // 设置导出相应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        // 根据语言，导出不同的语言文档
        Map<Long,Integer> semesterMap = new HashMap<>();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String sheetName = "";
        String fileName = "";
        if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
            head.add(Collections.singletonList("Subject"));
            head.add(Collections.singletonList("Class"));
            int headNum = 2;
            for (SemesterEntity semesterEntity : semesterEntities) {
                head.add(Collections.singletonList(semesterEntity.getName() + " Number of unqualified"));
                semesterMap.put(semesterEntity.getId(),headNum);
                headNum += 1;
            }
            sheetName = "Unqualified grade";
            fileName = "Unqualified grade_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        } else if (currentLanguage.equals(SchoolLanguageEnum.ZH_MO.getCode())) {
            head.add(Collections.singletonList("科目"));
            head.add(Collections.singletonList("班級"));
            int headNum = 2;
            for (SemesterEntity semesterEntity : semesterEntities) {
                head.add(Collections.singletonList(semesterEntity.getName() + "不合格人数"));
                semesterMap.put(semesterEntity.getId(),headNum);
                headNum += 1;
            }
            sheetName = "不合格成績";
            fileName = URLEncoder.encode("不合格成績_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx", "UTF-8");
        } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
            head.add(Collections.singletonList("disciplinas"));
            head.add(Collections.singletonList("classes"));
            int headNum = 2;
            for (SemesterEntity semesterEntity : semesterEntities) {
                head.add(Collections.singletonList(semesterEntity.getName() + " Número de pessoas não qualificadas"));
                semesterMap.put(semesterEntity.getId(),headNum);
                headNum += 1;
            }
            sheetName = "Notas insuficientes";
            fileName = "Notas insuficientes_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        }
        // 拼装数据
        // 多个合并区域
        List<CellRangeAddress> mergeRegions = new ArrayList<>();
        List<Map<Integer, String>> data = new ArrayList<>();
        int rowNum = 0;
        for (GradeFlunkResModel resModel : resModels) {
            for (int i = 0; i < resModel.getDetails().size(); i++) {
                Map<Integer, String> row = new HashMap<>();
                row.put(0, resModel.getSubjectName());
                GradeFlunkDetailResModel detailResModel = resModel.getDetails().get(i);
                row.put(1, detailResModel.getGroupName() + detailResModel.getClassName());
                for (GradeFlunkSemesterResModel detail : detailResModel.getDetails()) {
                    row.put(semesterMap.get(detail.getSemesterId()), String.valueOf(detail.getFlunkCount()));
                }
                data.add(row);
                rowNum += 1;
            }
            // 添加单元格合并规则
            mergeRegions.add(new CellRangeAddress(rowNum - resModel.getDetails().size() + 1, rowNum,0, 0));
        }
        // 合并行策略
        WriteHandler mergeStrategy = new CustomMergeUtils(mergeRegions);
        // 生成excel
        EasyExcel.write(outputStream)
                .head(head)
                .registerWriteHandler(mergeStrategy)
                .sheet(sheetName)
                .doWrite(data.stream().map(row -> {
                    List<String> rowList = new ArrayList<>();
                    for (int i = 0; i < head.size(); i++) {
                        rowList.add(row.get(i));
                    }
                    return rowList;
                }).collect(Collectors.toList()));
        headers.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }

    @Override
    public GradeYearResModel getGradeYear(GradeYearReqModel reqModel) {
        // 获取科目
        // 获取科目
        SysClass sysClass = sysClassService.getSysClassById(reqModel.getClassId());
        if(sysClass == null)
        {
            return null;
        }
        SubjectRelGroupQueryReqModel reqSubjectRel = new SubjectRelGroupQueryReqModel();
        reqSubjectRel.setSchoolId(reqModel.getSchoolId());
        reqSubjectRel.setGroupId(sysClass.getGradeGroup());
        reqSubjectRel.setCountedInAverage(1);
        List<SubjectRelResModel> relResModels = subjectRelService.listByGroup(reqSubjectRel);
        if (ObjectUtils.isEmpty(relResModels)) {
            log.error("获取学年成绩总结失败，没有科目");
            return null;
        }
        // 获取学生成绩数据
        List<StudentSubjectScoreDTO> studentSubjectScore = getStudentSubjectScore(GetStudentSubjectScoreReqModel.builder()
                .department(reqModel.getDepartment())
                .schoolYear(reqModel.getSchoolYear())
                .schoolId(reqModel.getSchoolId())
                .groupId(sysClass.getGradeGroup())
                .classId(Collections.singletonList(reqModel.getClassId())).build());
        if (ObjectUtils.isEmpty(studentSubjectScore)) {
            log.error("获取学年成绩总结失败，没有获取到学生学段平均分");
            return null;
        }
        Map<Long, List<StudentSubjectScoreDTO>> semesterIdListMap = studentSubjectScore.stream().collect(Collectors.groupingBy(StudentSubjectScoreDTO::getSemesterId));
        // 拼装返回值
        GradeYearResModel resModel = new GradeYearResModel();
        List<GradeYearTotalResModel> totals = null;
        List<GradeYearSemesterResModel> semesters = null;
        // 判断学段类型，0-学年总结，>0则是某一学段
        if (reqModel.getSemesterId().equals(0L)){
            // 获得学段
            List<SemesterEntity> semesterEntities = semesterService.list(Wrappers.<SemesterEntity>lambdaQuery()
                    .eq(SemesterEntity::getSchoolYear, reqModel.getSchoolYear())
                    .eq(SemesterEntity::getDepartment, reqModel.getDepartment())
                    .eq(SemesterEntity::getSchoolId, reqModel.getSchoolId()));
            if (ObjectUtils.isEmpty(semesterEntities)) {
                log.error("获取学年成绩总结失败，没有查询到学期");
                return null;
            }
            totals = new ArrayList<>();
            // 遍历科目
            for (SubjectRelResModel subject : relResModels) {
                GradeYearTotalResModel totalResModel = new GradeYearTotalResModel();
                totalResModel.setSubjectId(subject.getId());
                totalResModel.setSubjectName(subject.getSubject().getSubjectName());
                List<GradeYearTotalDetailResModel> semesterResList = new ArrayList<>();
                for (SemesterEntity semesterEntity : semesterEntities) {
                    GradeYearTotalDetailResModel semesterResModel = new GradeYearTotalDetailResModel();
                    semesterResModel.setSemesterId(semesterEntity.getId());
                    semesterResModel.setSemesterName(semesterEntity.getName());
                    if (semesterIdListMap.containsKey(semesterEntity.getId())) {
                        List<StudentSubjectScoreDTO> studentSubjectScoreDTOS = semesterIdListMap.get(semesterEntity.getId());
                        if (reqModel.getGradeType() == 0){
                            semesterResModel.setAvgScore(String.valueOf(BigDecimal.valueOf(studentSubjectScoreDTOS.stream()
                                            .filter(student -> student.getSubjectId().equals(subject.getId()))
                                            .collect(Collectors.averagingDouble(StudentSubjectScoreDTO::getUsuallyScore)))
                                    .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)));
                        } else if (reqModel.getGradeType() == 1){
                            semesterResModel.setAvgScore(String.valueOf(BigDecimal.valueOf(studentSubjectScoreDTOS.stream()
                                            .filter(student -> student.getSubjectId().equals(subject.getId()))
                                            .collect(Collectors.averagingDouble(StudentSubjectScoreDTO::getExamScore)))
                                    .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)));
                        }
                    } else {
                        semesterResModel.setAvgScore("0");
                    }
                    semesterResList.add(semesterResModel);
                }
                totalResModel.setDetails(semesterResList);
                totals.add(totalResModel);
            }
        }
        if (reqModel.getSemesterId().compareTo(0L) > 0){
            if (semesterIdListMap.containsKey(reqModel.getSemesterId())) {
                semesters = new ArrayList<>();
                List<StudentSubjectScoreDTO> studentSubjectScoreDTOS = semesterIdListMap.get(reqModel.getSemesterId());
                for (SubjectRelResModel subject : relResModels) {
                    GradeYearSemesterResModel semesterResModel = new GradeYearSemesterResModel();
                    semesterResModel.setSubjectId(subject.getId());
                    semesterResModel.setSubjectName(subject.getSubject().getSubjectName());
                    if (reqModel.getGradeType() == 0) {
                        semesterResModel.setQualifiedCount((int) studentSubjectScoreDTOS.stream()
                                .filter(student -> student.getSubjectId().equals(subject.getId()) &&
                                        student.getUsuallyScore() >= 6000)
                                .count());
                        semesterResModel.setFlunkCount((int) studentSubjectScoreDTOS.stream()
                                .filter(student -> student.getSubjectId().equals(subject.getId()) &&
                                        student.getUsuallyScore() < 6000)
                                .count());
                        semesterResModel.setSixtyToEightyCount((int) studentSubjectScoreDTOS.stream()
                                .filter(student -> student.getSubjectId().equals(subject.getId()) &&
                                        student.getUsuallyScore() >= 6000 &&
                                        student.getUsuallyScore() < 8000)
                                .count());
                        semesterResModel.setEightyToNinetyCount((int) studentSubjectScoreDTOS.stream()
                                .filter(student -> student.getSubjectId().equals(subject.getId()) &&
                                        student.getUsuallyScore() >= 8000 &&
                                        student.getUsuallyScore() < 9000)
                                .count());
                        semesterResModel.setNinetyToHundredCount((int) studentSubjectScoreDTOS.stream()
                                .filter(student -> student.getSubjectId().equals(subject.getId()) &&
                                        student.getUsuallyScore() >= 9000 &&
                                        student.getUsuallyScore() <= 10000)
                                .count());
                    }
                    if (reqModel.getGradeType() == 1) {
                        semesterResModel.setQualifiedCount((int) studentSubjectScoreDTOS.stream()
                                .filter(student -> student.getSubjectId().equals(subject.getId()) &&
                                        student.getExamScore() >= 6000)
                                .count());
                        semesterResModel.setFlunkCount((int) studentSubjectScoreDTOS.stream()
                                .filter(student -> student.getSubjectId().equals(subject.getId()) &&
                                        student.getExamScore() < 6000)
                                .count());
                        semesterResModel.setSixtyToEightyCount((int) studentSubjectScoreDTOS.stream()
                                .filter(student -> student.getSubjectId().equals(subject.getId()) &&
                                        student.getExamScore() >= 6000 &&
                                        student.getExamScore() < 8000)
                                .count());
                        semesterResModel.setEightyToNinetyCount((int) studentSubjectScoreDTOS.stream()
                                .filter(student -> student.getSubjectId().equals(subject.getId()) &&
                                        student.getExamScore() >= 8000 &&
                                        student.getExamScore() < 9000)
                                .count());
                        semesterResModel.setNinetyToHundredCount((int) studentSubjectScoreDTOS.stream()
                                .filter(student -> student.getSubjectId().equals(subject.getId()) &&
                                        student.getExamScore() >= 9000 &&
                                        student.getExamScore() <= 10000)
                                .count());
                    }
                    // 计算比例，百分比最后一项会使用100-其他比例，防止加起来不是100
                    BigDecimal qualifiedCount = new BigDecimal(semesterResModel.getQualifiedCount());
                    BigDecimal flunkCount = new BigDecimal(semesterResModel.getFlunkCount());
                    BigDecimal hundred = new BigDecimal(100);
                    if (flunkCount.equals(BigDecimal.ZERO) && qualifiedCount.equals(BigDecimal.ZERO)){
                        semesterResModel.setQualifiedRate("0");
                        semesterResModel.setFlunkRate("0");
                    } else {
                        semesterResModel.setQualifiedRate(qualifiedCount.divide(qualifiedCount.add(flunkCount),2, RoundingMode.HALF_UP)
                                .multiply(hundred).toString());
                        semesterResModel.setFlunkRate(hundred.subtract(new BigDecimal(semesterResModel.getQualifiedRate())).toString());
                    }
                    if (qualifiedCount.equals(BigDecimal.ZERO)){
                        semesterResModel.setSixtyToEightyRate("0");
                        semesterResModel.setEightyToNinetyRate("0");
                        semesterResModel.setNinetyToHundredRate("0");
                        semesters.add(semesterResModel);
                        continue;
                    }
                    semesterResModel.setSixtyToEightyRate(new BigDecimal(semesterResModel.getSixtyToEightyCount()).divide(qualifiedCount.add(flunkCount),2, RoundingMode.HALF_UP).multiply(hundred).toString());
                    semesterResModel.setEightyToNinetyRate(new BigDecimal(semesterResModel.getEightyToNinetyCount()).divide(qualifiedCount.add(flunkCount),2, RoundingMode.HALF_UP).multiply(hundred).toString());
                    semesterResModel.setNinetyToHundredRate(hundred.subtract(new BigDecimal(semesterResModel.getSixtyToEightyRate()).add(new BigDecimal(semesterResModel.getEightyToNinetyRate())).add(new BigDecimal(semesterResModel.getFlunkRate()))).toString());
                    semesters.add(semesterResModel);
                }
            } else {
                // 没有数据默认全为0
                semesters = relResModels.stream().map(a -> {
                    GradeYearSemesterResModel semesterResModel = new GradeYearSemesterResModel();
                    semesterResModel.setSubjectId(a.getId());
                    semesterResModel.setSubjectName(a.getSubject().getSubjectName());
                    semesterResModel.setQualifiedCount(0);
                    semesterResModel.setFlunkCount(0);
                    semesterResModel.setSixtyToEightyCount(0);
                    semesterResModel.setEightyToNinetyCount(0);
                    semesterResModel.setNinetyToHundredCount(0);
                    semesterResModel.setQualifiedRate("0");
                    semesterResModel.setFlunkRate("0");
                    semesterResModel.setSixtyToEightyRate("0");
                    semesterResModel.setEightyToNinetyRate("0");
                    semesterResModel.setNinetyToHundredRate("0");
                    return semesterResModel;
                }).collect(Collectors.toList());
            }
        }
        resModel.setTotals(totals);
        resModel.setSemesters(semesters);
        return resModel;
    }

    @Override
    public ResponseEntity<byte[]> exportGradeYear(GradeYearReqModel reqModel) throws UnsupportedEncodingException {
        // 获取数据
        GradeYearResModel dataResModel = getGradeYear(reqModel);
        // 设置导出相应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        // 根据语言，导出不同的语言文档
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (reqModel.getSemesterId().equals(0L)){
            // 获取学段
            List<SemesterEntity> semesterEntities = semesterService.list(Wrappers.<SemesterEntity>lambdaQuery()
                    .eq(SemesterEntity::getSchoolYear, reqModel.getSchoolYear())
                    .eq(SemesterEntity::getDepartment, reqModel.getDepartment())
                    .eq(SemesterEntity::getSchoolId, reqModel.getSchoolId())
                    .orderByAsc(SemesterEntity::getStartTime));
            if (ObjectUtils.isEmpty(semesterEntities)) {
                log.error("导出学年成绩失败，没有查询到学期");
                return null;
            }
            // 导出学年总结
            List<GradeYearTotalResModel> resModels = dataResModel.getTotals();
            // 设置动态列头
            List<List<String>> head = new ArrayList<>();
            // 根据语言，导出不同的语言文档
            Map<Long,Integer> semesterMap = new HashMap<>();
            String sheetName = "";
            String fileName = "";
            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                head.add(Collections.singletonList("Subject"));
                int headNum = 1;
                for (SemesterEntity semesterEntity : semesterEntities) {
                    head.add(Collections.singletonList(semesterEntity.getName() + " Number of unqualified"));
                    semesterMap.put(semesterEntity.getId(),headNum);
                    headNum += 1;
                }
                sheetName = "Summary and statistics of academic results";
                fileName = "Summary and statistics of academic results_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
            } else if (currentLanguage.equals(SchoolLanguageEnum.ZH_MO.getCode())) {
                head.add(Collections.singletonList("科目"));
                int headNum = 1;
                for (SemesterEntity semesterEntity : semesterEntities) {
                    head.add(Collections.singletonList(semesterEntity.getName() + "平均分"));
                    semesterMap.put(semesterEntity.getId(),headNum);
                    headNum += 1;
                }
                sheetName = "學年成績總結統計";
                fileName = URLEncoder.encode("學年成績總結統計_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx", "UTF-8");
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                head.add(Collections.singletonList("disciplinas"));
                int headNum = 1;
                for (SemesterEntity semesterEntity : semesterEntities) {
                    head.add(Collections.singletonList(semesterEntity.getName() + " Número de pessoas não qualificadas"));
                    semesterMap.put(semesterEntity.getId(),headNum);
                    headNum += 1;
                }
                sheetName = "Sumário de resultados do ano letivo";
                fileName = "Sumário de resultados do ano letivo_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
            }
            // 拼装数据
            List<Map<Integer, String>> data = new ArrayList<>();
            for (GradeYearTotalResModel resModel : resModels) {
                Map<Integer, String> row = new HashMap<>();
                row.put(0, resModel.getSubjectName());
                for (GradeYearTotalDetailResModel detail : resModel.getDetails()) {
                    row.put(semesterMap.get(detail.getSemesterId()), detail.getAvgScore());
                }
                data.add(row);
            }
            // 生成excel
            EasyExcel.write(outputStream)
                    .head(head)
                    .sheet(sheetName)
                    .doWrite(data.stream().map(row -> {
                        List<String> rowList = new ArrayList<>();
                        for (int i = 0; i < head.size(); i++) {
                            rowList.add(row.get(i));
                        }
                        return rowList;
                    }).collect(Collectors.toList()));
            headers.setContentDispositionFormData("attachment", fileName);
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        }
        if (reqModel.getSemesterId().compareTo(0L) > 0){
            // 导出某一学段
            List<GradeYearSemesterResModel> resModels = dataResModel.getSemesters();
            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                List<GradeYearExportEnModel> exportEnModels = resModels.stream()
                        .map(resModel -> {
                            GradeYearExportEnModel exportModel = new GradeYearExportEnModel();
                            BeanUtils.copyProperties(resModel,exportModel);
                            return exportModel;
                        })
                        .collect(Collectors.toList());
                EasyExcel.write(outputStream, GradeYearExportEnModel.class)
                        .sheet("Academic year score statistics")
                        .doWrite(exportEnModels);
                headers.setContentDispositionFormData("attachment", "Academic year score statistics_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
                return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
            } else if (currentLanguage.equals(SchoolLanguageEnum.ZH_MO.getCode())) {
                List<GradeYearExportModel> exportEnModels = resModels.stream()
                        .map(resModel -> {
                            GradeYearExportModel exportModel = new GradeYearExportModel();
                            BeanUtils.copyProperties(resModel,exportModel);
                            return exportModel;
                        })
                        .collect(Collectors.toList());
                EasyExcel.write(outputStream, GradeYearExportModel.class)
                        .sheet("學年成績統計")
                        .doWrite(exportEnModels);
                String encodedFileName = URLEncoder.encode("學年成績統計_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx", "UTF-8");
                headers.setContentDispositionFormData("attachment", encodedFileName);
                return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                List<GradeYearExportPtModel> exportEnModels = resModels.stream()
                        .map(resModel -> {
                            GradeYearExportPtModel exportModel = new GradeYearExportPtModel();
                            BeanUtils.copyProperties(resModel,exportModel);
                            return exportModel;
                        })
                        .collect(Collectors.toList());
                EasyExcel.write(outputStream, GradeYearExportPtModel.class)
                        .sheet("Estatísticas de resultados letivos")
                        .doWrite(exportEnModels);
                headers.setContentDispositionFormData("attachment", "Estatísticas de resultados letivos_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
                return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
            }
        }
        return null;
    }

    @Override
    public List<StudentSubjectScoreDTO> getStudentSubjectScore(GetStudentSubjectScoreReqModel reqModel) {
        // 检查平时成绩规则
        Map<Long, StudentUsuallyRuleEntity> typeIdRuleMap = new HashMap<>();// 不关联用这个 类型id-规则
        Map<Long, Map<Long, StudentUsuallyRuleEntity>> usuallyRuleMapBySub = new HashMap<>();// 关联用这个  科目id-类型id-规则zh

        List<StudentUsuallyRuleEntity> list = studentUsuallyRuleService.list(Wrappers.<StudentUsuallyRuleEntity>lambdaQuery()
                .eq(StudentUsuallyRuleEntity::getGradeGroupId, reqModel.getGroupId())
                .eq(StudentUsuallyRuleEntity::getSchoolId, reqModel.getSchoolId()));
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
        if (!ObjectUtils.isEmpty(list)) {
            if (isUsualTypeRelSub){
                try {
                    usuallyRuleMapBySub = list.stream().filter(item -> item.getSubjectId() != null)
                            .collect(Collectors.groupingBy(StudentUsuallyRuleEntity::getSubjectId, Collectors.toMap(StudentUsuallyRuleEntity::getTypeId, item -> item)));
                } catch (Exception e) {
                    log.error("学校Id{},平时成绩科目占比配置错误5！", reqModel.getSchoolId(), e);
                }
            } else {
                try {
                    typeIdRuleMap = list.stream().collect(Collectors.toMap(StudentUsuallyRuleEntity::getTypeId, Function.identity()));
                } catch (Exception e) {
                    log.error("学校Id{},平时成绩类型占比配置错误5！", reqModel.getSchoolId(), e);
                }
            }
        }
        // 检查学部成绩权重规则
        // 获取科目
        // 获取科目
        SubjectRelGroupQueryReqModel reqSubjectRel = new SubjectRelGroupQueryReqModel();
        reqSubjectRel.setSchoolId(reqModel.getSchoolId());
        reqSubjectRel.setGroupId(reqModel.getGroupId());
        reqSubjectRel.setCountedInAverage(1);
        List<SubjectRelResModel> relResModels = subjectRelService.listByGroup(reqSubjectRel);
        if (ObjectUtils.isEmpty(relResModels)) {
            return null;
        }
        // 过滤需要计算的科目
        if (ObjectUtils.isNotEmpty(reqModel.getSubjectId())) {
            relResModels = relResModels.stream().filter(subject -> reqModel.getSubjectId().contains(subject.getId())).collect(Collectors.toList());
        }
        // 获取科目和平时成绩权重规则
        List<DepartmentScoreRuleEntity> scoreRuleEntities = departmentScoreRuleService.list(Wrappers.<DepartmentScoreRuleEntity>lambdaQuery()
                .eq(DepartmentScoreRuleEntity::getGroupId, reqModel.getGroupId())
                .eq(DepartmentScoreRuleEntity::getSchoolId, reqModel.getSchoolId()));
        DepartmentScoreRuleEntity semesterScoreRule0 = null;
        DepartmentScoreRuleEntity semesterScoreRule1 = null;
        if (!ObjectUtils.isEmpty(scoreRuleEntities)) {
            semesterScoreRule0 = scoreRuleEntities.stream().filter(rule -> rule.getScoreType().equals("0")).findFirst().orElse(null);
            semesterScoreRule1 = scoreRuleEntities.stream().filter(rule -> rule.getScoreType().equals("1")).findFirst().orElse(null);
        }
        // 获取需要计算的学生
        Map<Long, SysClass> classIdMap = null;
        LambdaQueryWrapper<StudentEntity> where = Wrappers.lambdaQuery();
        if (ObjectUtils.isNotEmpty(reqModel.getStudentId())) {
            where.in(BaseEntity::getId, reqModel.getStudentId());
        } else if (ObjectUtils.isNotEmpty(reqModel.getClassId())) {
            where.in(StudentEntity::getClassId, reqModel.getClassId());
            List<SysClass> sysClasses = sysClassService.listByIds(reqModel.getClassId());
            classIdMap = sysClasses.stream().collect(Collectors.toMap(SysClass::getId, Function.identity()));
        } else if (ObjectUtils.isNotEmpty(reqModel.getDepartment())) {
            List<SysClass> classList = sysClassService.list(Wrappers.<SysClass>lambdaQuery()
                    .eq(SysClass::getDepartment, reqModel.getDepartment())
                    .eq(SysClass::getSchoolId, reqModel.getSchoolId()));
            if (ObjectUtils.isEmpty(classList)) {
                log.error("获取学生科目平均分失败，没有查询到班级");
                return null;
            }
            classIdMap = classList.stream().collect(Collectors.toMap(SysClass::getId, Function.identity()));
            where.in(StudentEntity::getClassId, classList.stream().map(SysClass::getId).collect(Collectors.toList()));
        } else if (ObjectUtils.isNotEmpty(reqModel.getSemesterId())){
            List<SysClass> classList = sysClassService.list(Wrappers.<SysClass>lambdaQuery()
                    .eq(SysClass::getSid, reqModel.getSchoolYear())
                    .eq(SysClass::getSchoolId, reqModel.getSchoolId()));
            if (ObjectUtils.isEmpty(classList)) {
                log.error("获取学生科目平均分失败，没有查询到班级");
                return null;
            }
            classIdMap = classList.stream().collect(Collectors.toMap(SysClass::getId, Function.identity()));
            where.in(StudentEntity::getClassId, classList.stream().map(SysClass::getId).collect(Collectors.toList()));
        }
        List<StudentEntity> studentList = studentService.list(where);
        if (ObjectUtils.isEmpty(studentList)) {
            log.error("获取学生科目平均分失败，没有查询到班级下的学生");
            return null;
        }
        if (ObjectUtils.isEmpty(classIdMap)) {
            List<SysClass> sysClasses = sysClassService.listByIds(studentList.stream().map(StudentEntity::getClassId).collect(Collectors.toList()));
            if (ObjectUtils.isEmpty(sysClasses)) {
                return null;
            }
            classIdMap = sysClasses.stream().collect(Collectors.toMap(SysClass::getId, Function.identity()));
        }
        // 获取平时成绩任务
        List<StudentUsuallyTaskEntity> usuallyTaskList = studentUsuallyTaskService.list(Wrappers.<StudentUsuallyTaskEntity>lambdaQuery()
                .eq(StudentUsuallyTaskEntity::getSchoolId, reqModel.getSchoolId())
                .in(ObjectUtils.isNotEmpty(reqModel.getSemesterId()),StudentUsuallyTaskEntity::getPeriodId, reqModel.getSemesterId())
                .in(StudentUsuallyTaskEntity::getClassId, classIdMap.keySet()));
        List<Long> usuallyTaskIds = null;
        Map<Long, List<StudentUsuallyTaskEntity>> subjectIdUsuallyTaskMap = new HashMap<>();
        if (ObjectUtils.isNotEmpty(usuallyTaskList)) {
            usuallyTaskIds = usuallyTaskList.stream().map(BaseEntity::getId).collect(Collectors.toList());
            subjectIdUsuallyTaskMap = usuallyTaskList.stream().collect(Collectors.groupingBy(StudentUsuallyTaskEntity::getSubjectId));
        }
        // 获取平时成绩
        Map<Long,List<StudentUsuallyScoreEntity>> studentIdUsuallyScoreMap = new HashMap<>();
        if (usuallyTaskIds != null) {
            // 获取平时成绩
            List<StudentUsuallyScoreEntity> usuallyScoreList = studentUsuallyScoreService.list(Wrappers.<StudentUsuallyScoreEntity>lambdaQuery()
                    .in(StudentUsuallyScoreEntity::getTaskId, usuallyTaskIds));
            if (ObjectUtils.isNotEmpty(usuallyScoreList))
                studentIdUsuallyScoreMap = usuallyScoreList.stream().collect(Collectors.groupingBy(StudentUsuallyScoreEntity::getStudentId));
        }
        // 获取考试成绩任务
        List<StudentExamTaskEntity> examTaskList = studentExamTaskService.list(Wrappers.<StudentExamTaskEntity>lambdaQuery()
                .eq(StudentExamTaskEntity::getSchoolId, reqModel.getSchoolId())
                .in(ObjectUtils.isNotEmpty(reqModel.getSemesterId()), StudentExamTaskEntity::getPeriodId, reqModel.getSemesterId())
                .in(StudentExamTaskEntity::getClassId, classIdMap.keySet()));
        List<Long> examTaskIds = null;
        Map<Long, List<StudentExamTaskEntity>> subjectIdExamTaskMap = new HashMap<>();
        if (ObjectUtils.isNotEmpty(examTaskList)) {
            examTaskIds = examTaskList.stream().map(BaseEntity::getId).collect(Collectors.toList());
            subjectIdExamTaskMap = examTaskList.stream().collect(Collectors.groupingBy(StudentExamTaskEntity::getSubjectId));
        }
        // 获取考试成绩
        Map<Long,List<StudentExamScoreEntity>> studentIdExamScoreMap = new HashMap<>();
        if (examTaskIds != null) {
            // 获取考试成绩
            List<StudentExamScoreEntity> examScoreList = studentExamScoreService.list(Wrappers.<StudentExamScoreEntity>lambdaQuery()
                    .in(StudentExamScoreEntity::getTaskId, examTaskIds));
            if (ObjectUtils.isNotEmpty(examScoreList))
                studentIdExamScoreMap = examScoreList.stream().collect(Collectors.groupingBy(StudentExamScoreEntity::getStudentId));
        }
        // 计算学生科目成绩
        List<StudentSubjectScoreDTO> result = new ArrayList<>();
        List<SemesterEntity> semesterList;
        if (ObjectUtils.isNotEmpty(reqModel.getSemesterId())) {
            semesterList = semesterService.list(Wrappers.<SemesterEntity>lambdaQuery().in(SemesterEntity::getId, reqModel.getSemesterId()));
        } else {
            semesterList = semesterService.list(Wrappers.<SemesterEntity>lambdaQuery()
                    .eq(SemesterEntity::getSchoolId, reqModel.getSchoolId())
                    .eq(SemesterEntity::getSchoolYear, reqModel.getSchoolYear())
                    .eq(SemesterEntity::getDepartment, reqModel.getDepartment()));
        }
        // 根据学段遍历
        for (SemesterEntity semesterEntity : semesterList) {
            // 遍历每个学生
            for (StudentEntity studentEntity : studentList) {
                // 遍历科目，拼装返回数据
                for (SubjectRelResModel subject : relResModels) {
                    StudentSubjectScoreDTO studentSubjectScoreDto = new StudentSubjectScoreDTO();
                    studentSubjectScoreDto.setSemesterId(semesterEntity.getId());
                    studentSubjectScoreDto.setSemesterName(semesterEntity.getName());
                    studentSubjectScoreDto.setStudentId(studentEntity.getId());
                    studentSubjectScoreDto.setStudentName(studentEntity.getChineseName());
                    studentSubjectScoreDto.setClassId(studentEntity.getClassId());
                    studentSubjectScoreDto.setClassName(classIdMap.get(studentEntity.getClassId()).getClassName());
                    studentSubjectScoreDto.setSubjectId(subject.getId());
                    studentSubjectScoreDto.setSubjectName(subject.getSubject().getSubjectName());
                    studentSubjectScoreDto.setArtsScience(studentEntity.getArtsScience());
                    // 计算平时成绩和考试成绩
                    BigDecimal usuallyScore = BigDecimal.ZERO;
                    BigDecimal examScore = BigDecimal.ZERO;
                    if (studentIdUsuallyScoreMap.containsKey(studentEntity.getId())) {
                        // 获得学生的所有科目下的所有类型平时成绩
                        List<StudentUsuallyScoreEntity> usuallyScoreEntities = studentIdUsuallyScoreMap.get(studentEntity.getId());
                        // 根据科目遍历平时成绩任务
                        if (subjectIdUsuallyTaskMap.containsKey(subject.getId())) {
                            Map<Long, List<StudentUsuallyTaskEntity>> typeTaskIds = subjectIdUsuallyTaskMap.get(subject.getId()).stream()
                                    .filter(a -> a.getPeriodId().equals(semesterEntity.getId()))
                                    .collect(Collectors.groupingBy(StudentUsuallyTaskEntity::getTypeId));
                            // 获取该科目的平时成绩规则
                            Map<Long, StudentUsuallyRuleEntity> typeIdRuleMapBySub = new HashMap<>();
                            if (isUsualTypeRelSub){
                                typeIdRuleMapBySub = usuallyRuleMapBySub.get(subject.getId());
                            }else {
                                typeIdRuleMapBySub = typeIdRuleMap;
                            }
                            // 根据平时成绩任务类型，遍历平时成绩
                            for (Map.Entry<Long, List<StudentUsuallyTaskEntity>> typeEntry : typeTaskIds.entrySet()) {
                                List<Long> taskIds = typeEntry.getValue().stream().map(BaseEntity::getId).collect(Collectors.toList());
                                Double typeAvg = usuallyScoreEntities.stream().filter(a -> taskIds.contains(a.getTaskId())).collect(Collectors.averagingDouble(StudentUsuallyScoreEntity::getScore));
                                StudentUsuallyRuleEntity studentUsuallyRuleEntity = typeIdRuleMapBySub.get(typeEntry.getKey());
                                if (studentUsuallyRuleEntity != null && studentUsuallyRuleEntity.getWeight() > 0){
                                    BigDecimal typeAvgBD = BigDecimal.valueOf(typeAvg);
                                    usuallyScore = usuallyScore.add(typeAvgBD.multiply(new BigDecimal(studentUsuallyRuleEntity.getWeight())).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP));
                                }
                            }
                        }
                    }
                    if (studentIdExamScoreMap.containsKey(studentEntity.getId())) {
                        // 获得学生科目下的所有考试成绩
                        List<StudentExamScoreEntity> examScoreEntities = studentIdExamScoreMap.get(studentEntity.getId());
                        // 遍历考试成绩任务
                        if (subjectIdExamTaskMap.containsKey(subject.getId())) {
                            List<StudentExamTaskEntity> examTaskEntity = subjectIdExamTaskMap.get(subject.getId());
                            List<Long> taskIds = examTaskEntity.stream()
                                    .filter(a -> a.getPeriodId().equals(semesterEntity.getId()))
                                    .map(BaseEntity::getId).collect(Collectors.toList());
                            Double examAvg = examScoreEntities.stream().filter(a -> taskIds.contains(a.getTaskId())).collect(Collectors.averagingDouble(StudentExamScoreEntity::getScore));
                            examScore = BigDecimal.valueOf(examAvg);
                        }
                    }
                    //2.6.0 bug修改，现在返回的考试成绩和平时成绩都是返回原始成绩，不乘上权重
                    int usuallyWight = 0;
                    if(semesterScoreRule0 != null && semesterScoreRule0.getWeight() > 0)
                    {
                        usuallyWight = semesterScoreRule0.getWeight();
                    }
                    int examWight = 0;
                    if(semesterScoreRule1 != null && semesterScoreRule1.getWeight() > 0)
                    {
                        examWight = semesterScoreRule1.getWeight();
                    }
                    BigDecimal usuallyWightScore = usuallyScore.multiply(new BigDecimal(usuallyWight)).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP);
                    BigDecimal examWightScore = examScore.multiply(new BigDecimal(examWight)).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP);
                    studentSubjectScoreDto.setScore(usuallyWightScore.add(examWightScore).doubleValue());
                    studentSubjectScoreDto.setUsuallyScore(usuallyScore.doubleValue());
                    studentSubjectScoreDto.setExamScore(examScore.doubleValue());
                    if (studentSubjectScoreDto.getScore() > 0 ||
                            studentSubjectScoreDto.getUsuallyScore() > 0 ||
                            studentSubjectScoreDto.getExamScore() > 0 ) {
                        result.add(studentSubjectScoreDto);
                    }
                }
            }
        }
        return result;
    }


    @Override
    public ExcellentAndGoodGradesResModel getExcellentAndGood(ExcellentAndGoodGradesReqModel reqModel,long schoolId) {
        // 获取所有科目
        // 获取科目
        SubjectRelGroupQueryReqModel reqSubjectRel = new SubjectRelGroupQueryReqModel();
        reqSubjectRel.setSchoolId(schoolId);
        reqSubjectRel.setGroupId(reqModel.getGroupId());
        List<SubjectRelResModel> relResModels = subjectRelService.listByGroup(reqSubjectRel);
        if (ObjectUtils.isEmpty(relResModels)) {
            return null;
        }
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), schoolId);
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), schoolId);
            if(CollectionUtils.isEmpty(classIds))
            {
                return null;
            }
        }

        // 获取班级
        List<SysClassListResModel> classList = sysClassService.getSysClassListBySchoolIdAndSidAndGradeGroupId(schoolId,reqModel.getGroupId(),reqModel.getSchoolYear());
        if (ObjectUtils.isEmpty(classList)) {
            log.error("没有查询到班级，reqModel={},schoolId={}",reqModel,schoolId);
            return null;
        }
        if(!CollectionUtils.isEmpty(classIds))
        {
            //to map
            final List<Long> ids = classIds;
            classList = classList.stream().filter(a -> ids.contains(a.getClassId())).collect(Collectors.toList());
        }
        // 获取学生平均分成绩
        GetStudentSubjectScoreReqModel getStudentSubjectScoreReqModel = GetStudentSubjectScoreReqModel.builder()
                .department(reqModel.getDepartment())
                .schoolYear(reqModel.getSchoolYear())
                .schoolId(schoolId)
                .groupId(reqModel.getGroupId())
                .semesterId(Collections.singletonList(reqModel.getSemesterId()))
                .build();
        List<StudentSubjectScoreDTO> studentSubjectScore = getStudentSubjectScore(getStudentSubjectScoreReqModel);
        if (ObjectUtils.isEmpty(studentSubjectScore)) {
            log.error("没有获取到学生学段平均分，查询条件={},schoolId={}",getStudentSubjectScoreReqModel,schoolId);
            return null;
        }

        // 汇总表
        GradesStatisticsExcelResModel allResModel = new GradesStatisticsExcelResModel();
        // 班级分组表
        GradesStatisticsExcelResModel groupByClassResModel = new GradesStatisticsExcelResModel();

        // 1. 汇总表(allResModel)处理
        // 1.1 设置表头 - 动态获取科目
        List<List<String>> allTitle = new ArrayList<>();

        // 构建多语言模板
        String projectText = "项目";
        String excellentText = "80-100分";
        String totalText = "参与考试人数";
        String percentageText = "占比 %";
        String classText = "班级";

        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            // 如果未获取到语言设置，使用默认中文
            currentLanguage = SchoolLanguageEnum.ZH_MO.getCode();
        }
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        switch (languageEnum) {
            case EN_US:
                projectText = "Project";
                excellentText = "80-100 points";
                totalText = "Number of exam participants";
                percentageText = "Percentage %";
                classText = "Class";
                break;
            case PT_PT:
                projectText = "Projeto";
                excellentText = "80-100 pontos";
                totalText = "Número de participantes do exame";
                percentageText = "Percentagem %";
                classText = "Classe";
                break;
            case ZH_MO:
            default:
                // 默认使用中文，已在上面初始化
                break;
        }

        allTitle.add(Arrays.asList(projectText));
        // 从subjects中动态添加科目名称
        relResModels.forEach(subject -> allTitle.add(Arrays.asList(subject.getSubject().getSubjectName())));
        allResModel.setTitle(allTitle);

        // 1.2 准备数据
        Map<Long, List<StudentSubjectScoreDTO>> subjectGroupMap = studentSubjectScore.stream()
                .collect(Collectors.groupingBy(StudentSubjectScoreDTO::getSubjectId));
        
        // 1.3 构建内容行
        List<List<Object>> allContent = new ArrayList<>();
        
        // 80-100分行
        List<Object> excellentRow = new ArrayList<>();
        excellentRow.add(excellentText);
        
        // 参与考试人数行
        List<Object> totalRow = new ArrayList<>();
        totalRow.add(totalText);
        
        // 占比行
        List<Object> percentageRow = new ArrayList<>();
        percentageRow.add(percentageText);
        
        // 按科目顺序填充数据
        relResModels.forEach(subject -> {
            Long subjectId = subject.getId();
            List<StudentSubjectScoreDTO> scores = subjectGroupMap.get(subjectId);
            if (scores != null) {
                long totalCount = scores.size();
                long excellentCount = scores.stream()
                        .filter(score -> score.getScore() >= 8000 && score.getScore() <= 10000)
                        .count();
                double percentage = totalCount > 0 ? 
                    new BigDecimal(excellentCount * 100.0 / totalCount)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue() : 0;
                
                excellentRow.add(excellentCount);
                totalRow.add(totalCount);
                percentageRow.add(percentage + "%");
            } else {
                excellentRow.add(0);
                totalRow.add(0);
                percentageRow.add("0%");
            }
        });
        
        allContent.add(excellentRow);
        allContent.add(totalRow);
        allContent.add(percentageRow);
        allResModel.setContent(allContent);

        // 2. 班级分组表(groupByClassResModel)处理
        // 2.1 设置表头 - 动态获取科目
        List<List<String>> groupByClassTitle = new ArrayList<>();
        groupByClassTitle.add(Arrays.asList(classText));
        // 从subjects中动态添加科目名称
        relResModels.forEach(subject -> groupByClassTitle.add(Arrays.asList(subject.getSubject().getSubjectName())));
        groupByClassResModel.setTitle(groupByClassTitle);

        // 2.2 按班级分组统计
        Map<Long, Map<Long, List<StudentSubjectScoreDTO>>> classSubjectGroupMap =
            studentSubjectScore.stream()
                .collect(Collectors.groupingBy(StudentSubjectScoreDTO::getClassId,
                        Collectors.groupingBy(StudentSubjectScoreDTO::getSubjectId)));

        // 2.3 构建内容行 - 使用classList确保所有班级都显示
        List<List<Object>> groupByClassContent = new ArrayList<>();
        classList.forEach(sysClass -> {
            List<Object> classRow = new ArrayList<>();
            classRow.add(sysClass.getGroupName()+sysClass.getClassName());
            
            // 按科目顺序填充数据
            relResModels.forEach(subject -> {
                Map<Long, List<StudentSubjectScoreDTO>> subjectScores =
                    classSubjectGroupMap.get(sysClass.getClassId());
                if (subjectScores != null && subjectScores.get(subject.getId()) != null) {
                    List<StudentSubjectScoreDTO> scores = subjectScores.get(subject.getId());
                    long excellentCount = scores.stream()
                            .filter(score -> score.getScore() >= 8000 && score.getScore() <= 10000)
                            .count();
                    classRow.add(excellentCount);
                } else {
                    classRow.add(0);
                }
            });
            
            groupByClassContent.add(classRow);
        });
        groupByClassResModel.setContent(groupByClassContent);

        ExcellentAndGoodGradesResModel excellentAndGoodGradesResModel = new ExcellentAndGoodGradesResModel();
        excellentAndGoodGradesResModel.setAll(allResModel);
        excellentAndGoodGradesResModel.setGroupByClass(groupByClassResModel);
        return excellentAndGoodGradesResModel;
    }

    @Override
    public byte[] exportExcellentAndGood(ExcellentAndGoodGradesReqModel reqModel, long schoolId) {
        // 获取表单数据
        ExcellentAndGoodGradesResModel excellentAndGoodGradesResModel = getExcellentAndGood(reqModel, schoolId);

        List<ExcelSheetDataDTO> sheetDataList = new ArrayList<>();
        // 添加表1数据
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        String sheet1 = "成绩分布";
        String sheet2 = "成绩明细";
        if (SchoolLanguageEnum.EN_US.getCode().equals(currentLanguage)) {
            sheet1 = "Excellent and Good Grades";
            sheet2 = "Score Detail";
        } else if (SchoolLanguageEnum.PT_PT.getCode().equals(currentLanguage)) {
            sheet1 = "Distribuição de Notas";
            sheet2 = "Detalhe da Nota";
        }

        sheetDataList.add(createSheet(excellentAndGoodGradesResModel.getAll(), sheet1 ));
        // 添加表2数据
        sheetDataList.add(createSheet(excellentAndGoodGradesResModel.getGroupByClass(),sheet2));

        return exportFileHandler.exportMultiSheetExcel(sheetDataList);

    }

    private ExcelSheetDataDTO createSheet(GradesStatisticsExcelResModel resModel,String sheetName) {
        return ExcelSheetDataDTO.builder()
                .sheetName(sheetName)
                .headers(resModel.getTitle())
                .data(resModel.getContent())
                .build();
    }

    @Override
    public GradesStatisticsExcelResModel getTopScore(TopScoreReqModel reqModel, long schoolId) {
        // 获取所有科目
        // 获取科目
        SubjectRelGroupQueryReqModel reqSubjectRel = new SubjectRelGroupQueryReqModel();
        reqSubjectRel.setSchoolId(schoolId);
        reqSubjectRel.setGroupId(reqModel.getGroupId());
        List<SubjectRelResModel> relResModels = subjectRelService.listByGroup(reqSubjectRel);
        if (ObjectUtils.isEmpty(relResModels)) {
            return null;
        }

        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), schoolId);
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), schoolId);
            if(CollectionUtils.isEmpty(classIds))
            {
                return null;
            }
        }

        // 获取班级
        List<SysClassListResModel> classList = sysClassService.getSysClassListBySchoolIdAndSidAndGradeGroupId(schoolId,reqModel.getGroupId(),reqModel.getSchoolYear());
        if (ObjectUtils.isEmpty(classList)) {
            log.error("没有查询到班级，reqModel={},schoolId={}",reqModel,schoolId);
            return null;
        }
        if(!CollectionUtils.isEmpty(classIds))
        {
            final List<Long> ids = classIds;
            classList = classList.stream().filter(a -> ids.contains(a.getClassId())).collect(Collectors.toList());
            if (ObjectUtils.isEmpty(classList)) {
                log.error("没有查询到班级，reqModel={},schoolId={}",reqModel,schoolId);
                return null;
            }
        }

        // 获取学生成绩数据
        GetStudentSubjectScoreReqModel getStudentSubjectScoreReqModel = GetStudentSubjectScoreReqModel.builder()
                .department(reqModel.getDepartment())
                .schoolYear(reqModel.getSchoolYear())
                .schoolId(schoolId)
                .groupId(reqModel.getGroupId())
                .semesterId(Collections.singletonList(reqModel.getSemesterId()))
                .build();
        List<StudentSubjectScoreDTO> studentSubjectScore = getStudentSubjectScore(getStudentSubjectScoreReqModel);
        if (ObjectUtils.isEmpty(studentSubjectScore)) {
            log.error("没有获取到学生学段平均分，查询条件={},schoolId={}", getStudentSubjectScoreReqModel, schoolId);
            return null;
        }

        // 构建结果对象
        GradesStatisticsExcelResModel result = new GradesStatisticsExcelResModel();
        
        // 构建表头
        List<List<String>> headers = new ArrayList<>();

        // 获取当前语言设置
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            currentLanguage = SchoolLanguageEnum.ZH_MO.getCode();
        }
        
        // 设置表头文本
        String classText = "班级";
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        switch (languageEnum) {
            case EN_US:
                classText = "Class";
                break;
            case PT_PT:
                classText = "Classe";
                break;
            default:
                break;
        }
        headers.add(Arrays.asList(classText));

        relResModels.forEach(subject -> headers.add(Arrays.asList(subject.getSubject().getSubjectName())));
        result.setTitle(headers);

        // 按班级和科目分组的成绩数据
        Map<Long, Map<Long, List<StudentSubjectScoreDTO>>> classSubjectScores = studentSubjectScore.stream()
                .collect(Collectors.groupingBy(StudentSubjectScoreDTO::getClassId,
                        Collectors.groupingBy(StudentSubjectScoreDTO::getSubjectId)));

        // 构建内容数据
        List<List<Object>> content = new ArrayList<>();
        
        // 遍历每个班级
        classList.forEach(sysClass -> {
            List<Object> rowData = new ArrayList<>();
            rowData.add(sysClass.getGroupName()+sysClass.getClassName());
            
            // 遍历每个科目
            relResModels.forEach(subject -> {
                Map<Long, List<StudentSubjectScoreDTO>> subjectScores = classSubjectScores.get(sysClass.getClassId());
                if (subjectScores != null && subjectScores.get(subject.getId()) != null) {
                    List<StudentSubjectScoreDTO> scores = subjectScores.get(subject.getId());
                    // 找出最高分的学生
                    StudentSubjectScoreDTO topScore = scores.stream()
                            .max(Comparator.comparingDouble(StudentSubjectScoreDTO::getScore))
                            .orElse(null);
                    
                    if (topScore != null) {
                        // 格式化成绩，保留两位小数
                        BigDecimal score = BigDecimal.valueOf(topScore.getScore())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        rowData.add(score + "\n" + topScore.getStudentName());
                    } else {
                        rowData.add("");
                    }
                } else {
                    rowData.add("");
                }
            });
            content.add(rowData);
        });
        
        result.setContent(content);
        return result;
    }

    @Override
    public byte[] exportTopScore(TopScoreReqModel reqModel, long schoolId) {
        // 获取统计数据
        GradesStatisticsExcelResModel statisticsData = getTopScore(reqModel, schoolId);
        if (statisticsData == null) {
            return null;
        }

        List<ExcelSheetDataDTO> sheetDataList = new ArrayList<>();
        
        // 获取当前语言
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        String sheetName = "最高成绩统计";
        if (SchoolLanguageEnum.EN_US.getCode().equals(currentLanguage)) {
            sheetName = "Top Score Statistics";
        } else if (SchoolLanguageEnum.PT_PT.getCode().equals(currentLanguage)) {
            sheetName = "Estatísticas de Pontuação Máxima";
        }

        // 添加数据到sheet
        sheetDataList.add(createSheet(statisticsData,sheetName));
        // 使用exportFileHandler导出Excel
        return exportFileHandler.exportMultiSheetExcel(sheetDataList);
    }
}