package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaotiyun.school.manager.basic.enums.DepartmentScoreRuleEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.enums.SystemSettingKeyEnum;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.model.dto.*;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.ExcelSheetDataDTO;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentScoreCheckServiceImpl implements StudentScoreCheckService {

    private final StudentService studentService;
    private final SysClassService sysClassService;
    // 学段
    private final SemesterService semesterService;
    private final SysSemesterRuleService sysSemesterRuleService;
    private final SystemSettingService systemSettingService;

    private final StudentUsuallyScoreService studentUsuallyScoreService;
    private final StudentExamScoreService studentExamScoreService;
    // ClassPerformanceService
    private final ClassPerformanceService classPerformanceService;
    // DepartmentScoreRuleService
    private final DepartmentScoreRuleService departmentScoreRuleService;
    // StudentUsuallyRuleService
    private final StudentUsuallyRuleService studentUsuallyRuleService;
    // SubjectLevelRuleServiceImpl.java
    private final SubjectLevelRuleService subjectLevelRuleService;
    @Autowired
    private ExportFileHandler exportFileHandler;
    private static final int WIGHT = 10000;

    private final SubjectRelService subjectRelService;

    private final GradeGroupService gradeGroupService;;

    @Override
    public List<ClassTopStudentsResModel> getClassTopStudents(ClassTopStudentsReqModel reqModel) {

        List<StudentScoreCheckDTO> scoreCheckDTOs = generate(reqModel.getClassId(), reqModel.getSection());
        if (CollectionUtils.isEmpty(scoreCheckDTOs)) {
            return Collections.emptyList();
        }
        List<ClassTopStudentsResModel> resModels = new ArrayList<>(scoreCheckDTOs.size());
        for (StudentScoreCheckDTO scoreCheckDTO : scoreCheckDTOs) {
            ClassTopStudentsResModel resModel = new ClassTopStudentsResModel();
            resModel.setStudentName(scoreCheckDTO.getStudentName());
            resModel.setClassNumber(scoreCheckDTO.getStudentNo());
            resModel.setPhotoUrl(scoreCheckDTO.getStudentPhoto());
            resModels.add(resModel);
            List<StudentScoreCheckPeriodDataDTO> periodDataList = scoreCheckDTO.getPeriodDataList();
            if (CollectionUtils.isEmpty(periodDataList)) {
                // 如果没有学段数据，设置默认排名为0，表示未排名
                resModel.setRanking(0);
                continue;
            }
            StudentScoreCheckPeriodDataDTO periodDataDTO = periodDataList.get(0);
            // 确保ranking不为null，如果为null则设置为0
            resModel.setRanking(periodDataDTO.getRank() != null ? periodDataDTO.getRank() : 0);
            resModel.setAverageScore(periodDataDTO.getAverageScore());
            resModel.setConduct(periodDataDTO.getConduct());
            resModel.setConductScore(periodDataDTO.getConductScore());
            resModel.setConductDisplay(periodDataDTO.getShowScore());
        }
        resModels.sort(Comparator.comparing(ClassTopStudentsResModel::getRanking,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return resModels;
    }

    @Override
    public List<StudentSubjectScoresResModel> getStudentSubjectScores(StudentSubjectScoresReqModel reqModel) {
        Long classId = reqModel.getClassId();

        // 生成学生成绩数据
        List<StudentScoreCheckDTO> scoreCheckDTOs = generate(classId, reqModel.getSection());
        if (CollectionUtils.isEmpty(scoreCheckDTOs)) {
            return Collections.emptyList();
        }

        // 转换为响应模型
        List<StudentSubjectScoresResModel> resModels = new ArrayList<>(scoreCheckDTOs.size());
        for (StudentScoreCheckDTO scoreCheckDTO : scoreCheckDTOs) {
            StudentSubjectScoresResModel resModel = new StudentSubjectScoresResModel();
            resModel.setStudentName(scoreCheckDTO.getStudentName());
            resModel.setClassNumber(scoreCheckDTO.getStudentNo());
            resModel.setPhotoUrl(scoreCheckDTO.getStudentPhoto());
            // 获取学年总评数据
            List<StudentScoreCheckPeriodDataDTO> periodDataList = scoreCheckDTO.getPeriodDataList();
            if (CollectionUtils.isEmpty(periodDataList)) {
                continue;
            }
            StudentScoreCheckPeriodDataDTO periodDataDTO = periodDataList.get(0);
            // 转换科目成绩
            List<StudentSubjectScoresResModel.SubjectScore> examScores = new ArrayList<>();
            List<StudentSubjectScoresResModel.SubjectScore> usualScores = new ArrayList<>();
            int unqualifiedSubjectCount = 0;
            int unqualifiedUsualCount = 0;
            List<StudentScoreCheckSubjectScoreDTO> subjectScores = periodDataDTO.getSubjectScores();
            if (CollectionUtils.isEmpty(subjectScores)) {
                continue;
            }
            // 过滤掉null元素并排序
            subjectScores = subjectScores.stream()
                    .filter(Objects::nonNull)
                    .filter(score -> score.getSubjectId() != null)
                    .sorted(Comparator.comparing(StudentScoreCheckSubjectScoreDTO::getSubjectId))
                    .collect(Collectors.toList());

            for (StudentScoreCheckSubjectScoreDTO subjectScoreDTO : subjectScores) {

                StudentSubjectScoresResModel.SubjectScore subjectScore = new StudentSubjectScoresResModel.SubjectScore();
                subjectScore.setSubjectId(subjectScoreDTO.getSubjectId());
                subjectScore.setSubjectName(subjectScoreDTO.getSubjectName());
                // 设置考试成绩
                if (subjectScoreDTO.getExamScore() != null) {
                    // 创建科目成绩对象
                    subjectScore.setDisplayRule(subjectScoreDTO.getShowRule());
                    subjectScore.setScore(subjectScoreDTO.getExamScore());
//                    subjectScore.setGrade(subjectScoreDTO.getScoreLevel());
                    // 判断是否不合格（假设60分为及格线）
                    if (subjectScoreDTO.getExamScore() < 6000) {
                        unqualifiedSubjectCount++;
                    }
                }
                examScores.add(subjectScore);
                StudentSubjectScoresResModel.SubjectScore usualScore = new StudentSubjectScoresResModel.SubjectScore();
                usualScore.setSubjectId(subjectScoreDTO.getSubjectId());
                usualScore.setSubjectName(subjectScoreDTO.getSubjectName());
                // 设置平时成绩
                if (subjectScoreDTO.getUsuallyScore() != null) {
                    usualScore.setScore(subjectScoreDTO.getUsuallyScore());
//                    usualScore.setGrade(subjectScoreDTO.getScoreLevel());
                    usualScore.setDisplayRule(subjectScoreDTO.getShowRule());
                    // 判断是否不合格（假设60分为及格线）
                    if (subjectScoreDTO.getUsuallyScore() < 6000) {
                        unqualifiedUsualCount++;
                    }
                }
                usualScores.add(usualScore);
            }

            resModel.setExamScores(examScores);
            resModel.setUsualScores(usualScores);
            resModel.setUnqualifiedSubjectCount(unqualifiedSubjectCount);
            resModel.setUnqualifiedUsualCount(unqualifiedUsualCount);

            resModels.add(resModel);
        }

        return resModels;
    }

    @Override
    public List<YearGradeCheckResModel> getYearGradeCheck(YearGradeCheckReqModel reqModel) {

        // 获取班级ID
        Long classId = reqModel.getClassId();

        // 生成学生成绩数据
        List<StudentScoreCheckDTO> scoreCheckDTOs = generate(classId, null);
        if (CollectionUtils.isEmpty(scoreCheckDTOs)) {
            return Collections.emptyList();
        }

        // 转换为响应模型
        List<YearGradeCheckResModel> resModels = new ArrayList<>(scoreCheckDTOs.size());
        for (StudentScoreCheckDTO scoreCheckDTO : scoreCheckDTOs) {
            YearGradeCheckResModel resModel = new YearGradeCheckResModel();
            resModel.setStudentName(scoreCheckDTO.getStudentName());
            resModel.setClassNumber(scoreCheckDTO.getStudentNo());
            resModel.setPhotoUrl(scoreCheckDTO.getStudentPhoto());

            // 获取学年总评数据
            StudentScoreCheckYearSummaryDTO yearSummary = scoreCheckDTO.getYearSummary();
            if (yearSummary != null) {
                // 设置平均分
                if (yearSummary.getAverageScore() != null) {
                    resModel.setAverageScore(yearSummary.getAverageScore());
                }

                // 设置名次
                resModel.setRanking(yearSummary.getRank());

                // 设置操行
                resModel.setConduct(yearSummary.getConduct());
                resModel.setConductScore(yearSummary.getConductScore());
                resModel.setShowConductScore(yearSummary.getShowScore());

                // 转换科目成绩
                if (!CollectionUtils.isEmpty(yearSummary.getSubjectScores())) {
                    List<YearGradeCheckResModel.SubjectYearScore> subjectScores = new ArrayList<>();
                    int unqualifiedSubjectCount = 0;

                    List<StudentScoreCheckSubjectScoreDTO> subjectScores1 = yearSummary.getSubjectScores();
                    if (CollectionUtils.isEmpty(subjectScores1)) {
                        continue;
                    }
                    // 过滤掉null元素并排序
                    subjectScores1 = subjectScores1.stream()
                            .filter(Objects::nonNull)
                            .filter(score -> score.getSubjectId() != null)
                            .sorted(Comparator.comparing(StudentScoreCheckSubjectScoreDTO::getSubjectId))
                            .collect(Collectors.toList());
                    for (StudentScoreCheckSubjectScoreDTO subjectScoreDTO : subjectScores1) {
                        YearGradeCheckResModel.SubjectYearScore subjectScore = new YearGradeCheckResModel.SubjectYearScore();
                        subjectScore.setSubjectId(subjectScoreDTO.getSubjectId());
                        subjectScore.setSubjectName(subjectScoreDTO.getSubjectName());
                        subjectScore.setDisplayRule(subjectScoreDTO.getShowRule());

                        // 设置科目成绩
                        if (subjectScoreDTO.getScore() != null) {
                            subjectScore.setScore(subjectScoreDTO.getScore());
                            subjectScore.setGrade(subjectScoreDTO.getScoreLevel());

                            // 判断是否不合格（假设60分为及格线）
                            if (subjectScoreDTO.getScore() < 6000) {
                                unqualifiedSubjectCount++;
                            }
                        }

                        subjectScores.add(subjectScore);
                    }

                    resModel.setSubjectScores(subjectScores);
                    resModel.setUnqualifiedSubjectCount(unqualifiedSubjectCount);
                }
            }

            resModels.add(resModel);
        }

        resModels.sort(Comparator.comparing(YearGradeCheckResModel::getRanking,
                Comparator.nullsLast(Comparator.naturalOrder())));

        return resModels;
    }

    @Override
    public byte[] getExportYearGradeCheck(YearGradeCheckReqModel reqModel, String fileName) {
        List<YearGradeCheckResModel> yearGradeCheck = getYearGradeCheck(reqModel);
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            // 如果未获取到语言设置，使用默认中文
            currentLanguage = SchoolLanguageEnum.ZH_MO.getCode();
        }

        // 构建多语言表头
        Map<String, String> headerMap = new HashMap<>();
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        switch (languageEnum) {
            case EN_US:
                headerMap.put("classNumber", "Class Number");
                headerMap.put("studentName", "Student Name");
                headerMap.put("averageScore", "Average Score");
                headerMap.put("ranking", "Ranking");
                headerMap.put("conduct", "Conduct");
                headerMap.put("unqualifiedSubjectCount", "Unqualified Subjects");
                break;
            case PT_PT:
                headerMap.put("classNumber", "Número da Classe");
                headerMap.put("studentName", "Nome do Estudante");
                headerMap.put("averageScore", "Pontuação Média");
                headerMap.put("ranking", "Classificação");
                headerMap.put("conduct", "Conduta");
                headerMap.put("unqualifiedSubjectCount", "Disciplinas Não Qualificadas");
                break;
            case ZH_MO:
            default:
                // 默认使用中文
                headerMap.put("classNumber", "班内号");
                headerMap.put("studentName", "学生姓名");
                headerMap.put("averageScore", "平均分");
                headerMap.put("ranking", "名次");
                headerMap.put("conduct", "操行");
                headerMap.put("unqualifiedSubjectCount", "不合格科目");
                break;
        }

        // 构建表头
        List<List<String>> allTitle = new ArrayList<>();
        allTitle.add(Collections.singletonList(headerMap.get("classNumber")));
        allTitle.add(Collections.singletonList(headerMap.get("studentName")));
        allTitle.add(Collections.singletonList(headerMap.get("averageScore")));
        allTitle.add(Collections.singletonList(headerMap.get("ranking")));
        allTitle.add(Collections.singletonList(headerMap.get("conduct")));

        // 第二行：科目成绩表头
        if (!CollectionUtils.isEmpty(yearGradeCheck)
                && !CollectionUtils.isEmpty(yearGradeCheck.get(0).getSubjectScores())) {
            // 添加科目名称
            for (YearGradeCheckResModel.SubjectYearScore subjectScore : yearGradeCheck.get(0).getSubjectScores()) {
                allTitle.add(Collections.singletonList(subjectScore.getSubjectName()));
            }

            // 添加不合格科目数
            allTitle.add(Collections.singletonList(headerMap.get("unqualifiedSubjectCount")));
        }

        // 构建内容
        List<List<Object>> content = new ArrayList<>();
        for (YearGradeCheckResModel model : yearGradeCheck) {
            List<Object> row = new ArrayList<>();
            row.add(String.valueOf(model.getClassNumber()));
            row.add(model.getStudentName());
            if (model.getAverageScore() != null) {
                BigDecimal score = BigDecimal.valueOf(model.getAverageScore())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                row.add(score.toString());
            } else {
                row.add(" ");
            }
            row.add(String.valueOf(model.getRanking()));
            if (model.getShowConductScore() != null && model.getShowConductScore() == 1) {
                BigDecimal score = new BigDecimal(model.getConductScore() == null ? 0 : model.getConductScore())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                row.add(score.toString());
            } else {
                row.add(model.getConduct());
            }

            // 添加科目成绩
            if (!CollectionUtils.isEmpty(model.getSubjectScores())) {
                for (YearGradeCheckResModel.SubjectYearScore subjectScore : model.getSubjectScores()) {
                    // 根据展示规则决定显示分数还是等级
                    if (subjectScore.getDisplayRule() != null && subjectScore.getDisplayRule() == 1) {
                        row.add(subjectScore.getGrade());
                    } else {
                        // 显示分数时，除以100并保留两位小数
                        if (subjectScore.getScore() != null) {
                            BigDecimal score = new BigDecimal(subjectScore.getScore()).divide(new BigDecimal("100"), 2,
                                    RoundingMode.HALF_UP);
                            row.add(score.toString());
                        } else {
                            row.add(" ");
                        }
                    }
                }
            }

            // 添加不合格科目数
            row.add(String.valueOf(model.getUnqualifiedSubjectCount()));

            content.add(row);
        }
        List<ExcelSheetDataDTO> sheetDataList = new ArrayList<>();
        ExcelSheetDataDTO build = ExcelSheetDataDTO.builder()
                .headers(allTitle)
                .data(content)
                .build();
        sheetDataList.add(build);

        // 这里需要调用导出Excel的方法，将allTitle和content传入
        return exportFileHandler.exportMultiSheetExcel(sheetDataList);
    }

    @Override
    public byte[] exportClassTopStudents(ClassTopStudentsReqModel reqModel) {
        // 获取各班名列前茅名单
        List<ClassTopStudentsResModel> topStudents = getClassTopStudents(reqModel);
        if (CollectionUtils.isEmpty(topStudents)) {
            return null;
        }

        // 构建多语言表头
        List<List<String>> allTitle = new ArrayList<>();
        Map<String, String> headerMap = new HashMap<>();
        String currentLanguage = LanguageUtil.getCurrentLanguage();

        // 根据当前语言设置表头
        if (SchoolLanguageEnum.EN_US.getCode().equals(currentLanguage)) {
            headerMap.put("classNumber", "Class Number");
            headerMap.put("studentName", "Student Name");
            headerMap.put("averageScore", "Average Score");
            headerMap.put("ranking", "Ranking");
            headerMap.put("conduct", "Conduct");
        } else if (SchoolLanguageEnum.PT_PT.getCode().equals(currentLanguage)) {
            headerMap.put("classNumber", "Número da Classe");
            headerMap.put("studentName", "Nome do Estudante");
            headerMap.put("averageScore", "Pontuação Média");
            headerMap.put("ranking", "Classificação");
            headerMap.put("conduct", "Conduta");
        } else {
            headerMap.put("classNumber", "班号");
            headerMap.put("studentName", "学生姓名");
            headerMap.put("averageScore", "平均分");
            headerMap.put("ranking", "排名");
            headerMap.put("conduct", "操行");
        }

        // 添加表头
        allTitle.add(Collections.singletonList(headerMap.get("classNumber")));
        allTitle.add(Collections.singletonList(headerMap.get("studentName")));
        allTitle.add(Collections.singletonList(headerMap.get("averageScore")));
        allTitle.add(Collections.singletonList(headerMap.get("ranking")));
        allTitle.add(Collections.singletonList(headerMap.get("conduct")));

        // 构建内容
        List<List<Object>> content = new ArrayList<>();
        for (ClassTopStudentsResModel student : topStudents) {
            List<Object> row = new ArrayList<>();
            row.add(student.getClassNumber());
            row.add(student.getStudentName());
            // 处理平均分：除以100并四舍五入保留2位小数
            if (student.getAverageScore() != null) {
                BigDecimal score = BigDecimal.valueOf(student.getAverageScore())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                row.add(score.toString());
            } else {
                row.add("");
            }
            row.add(student.getRanking());
            if (student.getConductDisplay() != null && student.getConductDisplay() == 1) {
                // 处理操行分数：除以100并四舍五入保留2位小数
                if (student.getConductScore() != null) {
                    BigDecimal conductScore = BigDecimal.valueOf(student.getConductScore())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    row.add(conductScore.toString());
                } else {
                    row.add("");
                }
            } else {
                row.add(student.getConduct());
            }
            content.add(row);
        }

        // 构建Excel数据
        ExcelSheetDataDTO sheetData = ExcelSheetDataDTO.builder()
                .headers(allTitle)
                .data(content)
                .build();

        // 导出Excel
        return exportFileHandler.exportMultiSheetExcel(Collections.singletonList(sheetData));
    }

    @Override
    public byte[] exportStudentSubjectScores(StudentSubjectScoresReqModel reqModel) {
        // 获取学生各科成绩
        List<StudentSubjectScoresResModel> subjectScores = getStudentSubjectScores(reqModel);
        if (CollectionUtils.isEmpty(subjectScores)) {
            return null;
        }

        // 构建多语言表头
        List<List<String>> allTitle = new ArrayList<>();
        Map<String, String> headerMap = new HashMap<>();
        String currentLanguage = LanguageUtil.getCurrentLanguage();

        // 根据当前语言设置表头
        if (SchoolLanguageEnum.EN_US.getCode().equals(currentLanguage)) {
            headerMap.put("classNumber", "Class Number");
            headerMap.put("studentName", "Student Name");
            headerMap.put("unqualifiedExamCount", "Unqualified Exam Subjects");
            headerMap.put("unqualifiedUsualCount", "Unqualified Usual Subjects");
        } else if (SchoolLanguageEnum.PT_PT.getCode().equals(currentLanguage)) {
            headerMap.put("classNumber", "Número da Classe");
            headerMap.put("studentName", "Nome do Estudante");
            headerMap.put("unqualifiedExamCount", "Disciplinas de Exame Não Qualificadas");
            headerMap.put("unqualifiedUsualCount", "Disciplinas Usuais Não Qualificadas");
        } else {
            headerMap.put("classNumber", "班内号");
            headerMap.put("studentName", "学生姓名");
            headerMap.put("unqualifiedExamCount", "考试成绩不合格科目");
            headerMap.put("unqualifiedUsualCount", "平时成绩不合格科目");
        }

        // 添加表头
        allTitle.add(Collections.singletonList(headerMap.get("classNumber")));
        allTitle.add(Collections.singletonList(headerMap.get("studentName")));

        // 添加科目考试成绩表头
        if (!CollectionUtils.isEmpty(subjectScores) && !CollectionUtils.isEmpty(subjectScores.get(0).getExamScores())) {
            for (StudentSubjectScoresResModel.SubjectScore subjectScore : subjectScores.get(0).getExamScores()) {
                String subjectName = subjectScore.getSubjectName();
                if (SchoolLanguageEnum.EN_US.getCode().equals(currentLanguage)) {
                    allTitle.add(Collections.singletonList(subjectName + " (Exam)"));
                } else if (SchoolLanguageEnum.PT_PT.getCode().equals(currentLanguage)) {
                    allTitle.add(Collections.singletonList(subjectName + " (Exame)"));
                } else {
                    allTitle.add(Collections.singletonList(subjectName + " (考试)"));
                }
            }
        }

        // 添加科目平时成绩表头
        if (!CollectionUtils.isEmpty(subjectScores)
                && !CollectionUtils.isEmpty(subjectScores.get(0).getUsualScores())) {
            for (StudentSubjectScoresResModel.SubjectScore subjectScore : subjectScores.get(0).getUsualScores()) {
                String subjectName = subjectScore.getSubjectName();
                if (SchoolLanguageEnum.EN_US.getCode().equals(currentLanguage)) {
                    allTitle.add(Collections.singletonList(subjectName + " (Usual)"));
                } else if (SchoolLanguageEnum.PT_PT.getCode().equals(currentLanguage)) {
                    allTitle.add(Collections.singletonList(subjectName + " (Usual)"));
                } else {
                    allTitle.add(Collections.singletonList(subjectName + " (平时)"));
                }
            }
        }

        allTitle.add(Collections.singletonList(headerMap.get("unqualifiedExamCount")));
        allTitle.add(Collections.singletonList(headerMap.get("unqualifiedUsualCount")));

        // 构建内容
        List<List<Object>> content = new ArrayList<>();
        for (StudentSubjectScoresResModel student : subjectScores) {
            List<Object> row = new ArrayList<>();
            row.add(student.getClassNumber());
            row.add(student.getStudentName());

            // 添加考试成绩
            if (!CollectionUtils.isEmpty(student.getExamScores())) {
                for (StudentSubjectScoresResModel.SubjectScore score : student.getExamScores()) {
                    if (score.getScore() != null) {
                        // 处理考试成绩：除以100并四舍五入保留2位小数
                        BigDecimal scoreValue = BigDecimal.valueOf(score.getScore())
                                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                        row.add(scoreValue.toString());
                    } else {
                        row.add(" ");
                    }
                }
            }

            // 添加平时成绩
            if (!CollectionUtils.isEmpty(student.getUsualScores())) {
                for (StudentSubjectScoresResModel.SubjectScore score : student.getUsualScores()) {
                    if (score.getScore() != null) {
                        // 处理平时成绩：除以100并四舍五入保留2位小数
                        BigDecimal scoreValue = BigDecimal.valueOf(score.getScore())
                                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                        row.add(scoreValue.toString());
                    } else {
                        row.add(" ");
                    }
                }
            }

            // 添加不合格科目数量
            row.add(student.getUnqualifiedSubjectCount());
            row.add(student.getUnqualifiedUsualCount());

            content.add(row);
        }

        // 构建Excel数据
        ExcelSheetDataDTO sheetData = ExcelSheetDataDTO.builder()
                .headers(allTitle)
                .data(content)
                .build();

        // 导出Excel
        return exportFileHandler.exportMultiSheetExcel(Collections.singletonList(sheetData));
    }

    private List<StudentScoreCheckDTO> generate(Long classId, Long periodId) {
        // 查询班级
        SysClass sysClass = sysClassService.getSysClassById(classId);
        if (sysClass == null) {
            return Collections.emptyList();
        }
        Long schoolId = sysClass.getSchoolId();
        List<StudentScoreCheckDTO> result = getClassStudentSchoolInfo(classId, schoolId, sysClass);
        if (!CollectionUtils.isEmpty(result)) {
            // 获取学段数据列表
            List<TranScriptSchoolYearDTO> schoolYearDTOS = getSemesterDataList(schoolId, sysClass.getSid(),
                    sysClass.getGradeGroup(), periodId);
            // 获取科目成绩
            assembleData(classId, result, schoolYearDTOS, schoolId);
            getSubjectScores(classId, schoolId, sysClass.getGradeGroup(), periodId, result, schoolYearDTOS);
            if (periodId == null) {
                calculateSchoolYearData(result, schoolId, sysClass.getGradeGroup());
            }
        }
        return result;
    }

    private List<StudentScoreCheckDTO> getClassStudentSchoolInfo(Long classId, Long schoolId, SysClass sysClass) {
        List<StudentScoreCheckDTO> result = new ArrayList<>();

        // 获取学生信息
        List<StudentEntity> studentEntities = studentService.getStudentListByClassId(classId);
        if (studentEntities != null && !studentEntities.isEmpty()) {
            for (StudentEntity studentEntity : studentEntities) {
                StudentScoreCheckDTO studentSchoolInfo = new StudentScoreCheckDTO();
                studentSchoolInfo.setStudentPhoto(studentEntity.getImgUrl() == null ? "" : studentEntity.getImgUrl());
                studentSchoolInfo.setStudentName(studentEntity.getChineseName());
                studentSchoolInfo.setEducationNo(studentEntity.getStudentNo());
                studentSchoolInfo.setStudentNo(
                        studentEntity.getSeatNo() == null ? "" : String.valueOf(studentEntity.getSeatNo()));
                studentSchoolInfo.setSchoolYear(sysClass.getSid());
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

    // 学段数据列表
    private List<TranScriptSchoolYearDTO> getSemesterDataList(Long schoolId, String schoolYear, Long groupId,
            Long periodId) {
        SemesterQueryReqModel semesterQueryReqModel = new SemesterQueryReqModel();
        semesterQueryReqModel.setSchoolYear(schoolYear);
        List<SemesterResModel> list = semesterService.list(semesterQueryReqModel, schoolId);
        if (periodId != null && !CollectionUtils.isEmpty(list)) {
            list = list.stream()
                    .filter(Objects::nonNull)
                    .filter(item -> item.getId() != null && item.getId().equals(periodId))
                    .collect(Collectors.toList());
        }
        List<SysSemesterRuleAddDetailResModel> rule = sysSemesterRuleService
                .getRuleBySchoolYearAndDepartment(schoolYear, groupId, schoolId);
        // tomap
        Map<Long, SysSemesterRuleAddDetailResModel> ruleMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(rule)) {
            ruleMap = rule.stream()
                    .collect(Collectors.toMap(SysSemesterRuleAddDetailResModel::getSemesterId, item -> item));
        }
        if (!CollectionUtils.isEmpty(list)) {
            List<TranScriptSchoolYearDTO> tranScriptSchoolYearDTOS = new ArrayList<>();
            for (SemesterResModel semesterResModel : list) {
                TranScriptSchoolYearDTO tranScriptSchoolYearDTO = new TranScriptSchoolYearDTO();
                tranScriptSchoolYearDTO.setPeriodId(semesterResModel.getId());
                tranScriptSchoolYearDTO.setPeriodName(semesterResModel.getName());
                tranScriptSchoolYearDTO.setStartTime(semesterResModel.getStartTime());
                SysSemesterRuleAddDetailResModel ruleModel = ruleMap.get(semesterResModel.getId());
                if (ruleModel != null) {
                    tranScriptSchoolYearDTO.setProportion(ruleModel.getWeight());
                }
                if (periodId == null && ruleModel == null) {
                    continue;
                }
                tranScriptSchoolYearDTOS.add(tranScriptSchoolYearDTO);
            }
            return tranScriptSchoolYearDTOS;
        }
        return Collections.emptyList();
    }

    // 科目成绩拼接
    private void getSubjectScores(Long classId, Long schoolId, Long groupId, Long priodId,
            List<StudentScoreCheckDTO> result,
            List<TranScriptSchoolYearDTO> schoolYearDTOS) {

        GradeGroup byId = gradeGroupService.getById(groupId);
        int artScience = byId.getProfessionalSubject() == null ? 0 : byId.getProfessionalSubject();
        // 获取成绩规则
        // 科目成绩权重
        DepartmentScoreRuleResModel departmentScoreRuleResModel = departmentScoreRuleService
                .getRuleByDepartment(schoolId, groupId);
        List<StudentUsuallyRuleResModel> usuallyRuleServiceRule = studentUsuallyRuleService.getRule(schoolId);
        List<SubjectLevelRuleResModel> subjectLevelRuleResModels = subjectLevelRuleService.getRuleByDepartment(schoolId,
                groupId);
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
                    log.error("学校Id{},平时成绩科目占比配置错误3！", schoolId, e);
                }
            } else {
                try {
                    usuallyRuleMap = groupUsualRule.stream().filter(item -> item.getSubjectId() == null || item.getSubjectId() == 0)
                            .collect(Collectors.toMap(StudentUsuallyRuleResModel::getTypeId, Function.identity()));
                } catch (Exception e) {
                    log.error("学校Id{},平时成绩占比配置错误3！", schoolId, e);
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
        if (!CollectionUtils.isEmpty(details) && details.get(0) != null) {
            List<DepartmentScoreRuleDetailResModel> ruleDetailResModels = details.get(0).getDetails();
            ruleDetailResModels = ruleDetailResModels.stream()
                    .filter(Objects::nonNull)
                    .filter(item -> item.getScoreType() != null)
                    .collect(Collectors.toList());
            // to map 0-平时成绩,1-考试成绩,2-科目成绩
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
                if (artScience == 3) {
                    commerceSubjectWightMap = ruleDetailResModels.stream()
                            .filter(item -> item.getScoreType().equals(DepartmentScoreRuleEnum.COMMON_COMMERCE_SCORE.getValue()))
                            .collect(Collectors.toMap(DepartmentScoreRuleDetailResModel::getSubjectId, item -> item));
                }
            }
        } else {
            scoreTypeMap = new HashMap<>();
        }

        // tomap subjectLevelRuleResModels
        Map<Long, SubjectLevelRuleResModel> subjectLevelRuleDetailResModelMap = subjectLevelRuleResModels.stream()
                .collect(Collectors.toMap(SubjectLevelRuleResModel::getSubjectId, item -> item));

        // 获取科目
        SubjectRelGroupQueryReqModel reqModel = new SubjectRelGroupQueryReqModel();
        reqModel.setGroupId(groupId);
        reqModel.setSchoolId(schoolId);
        List<SubjectRelResModel> relResModels = subjectRelService.listByGroup(reqModel);
        if (relResModels == null || relResModels.isEmpty()) {
            return;
        }
        // 获取平时成绩
        List<StudentPeriodScoreResModel> usuallyScores = studentUsuallyScoreService.getUsuallyScores(classId, priodId);
        // 转map
        Map<Long, List<StudentPeriodScoreResModel>> usuallyScoreMap = usuallyScores.stream()
                .collect(Collectors.groupingBy(StudentPeriodScoreResModel::getStudentId));
        // 获取考试成绩
        List<StudentPeriodScoreResModel> studentPeriodScores = studentExamScoreService.getStudentPeriodScores(classId,
                priodId);
        // 转map
        Map<Long, List<StudentPeriodScoreResModel>> examScoreMap = studentPeriodScores.stream()
                .collect(Collectors.groupingBy(StudentPeriodScoreResModel::getStudentId));

        Map<Long, List<ScoreRankDTO>> priodScoresMap = new HashMap<>();
        for (StudentScoreCheckDTO studentScoreCheckDTO : result) {
            Map<Long, DepartmentScoreRuleDetailResModel> wightMap;
            List<SubjectRelResModel> subjectRelList;
            if(studentScoreCheckDTO.getArtScience() == 1){
                wightMap = artSubjectWightMap;
                 subjectRelList = relResModels.stream()
                        .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(studentScoreCheckDTO.getArtScience()))
                        .collect(Collectors.toList());
            }else if(studentScoreCheckDTO.getArtScience() == 2)
            {
                wightMap = scienceSubjectWightMap;
                subjectRelList = relResModels.stream()
                        .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(studentScoreCheckDTO.getArtScience()))
                        .collect(Collectors.toList());
            }else if(studentScoreCheckDTO.getArtScience() == 3)
            {
                wightMap = commerceSubjectWightMap;
                subjectRelList = relResModels.stream()
                        .filter(item -> item.getArtsScience() == 0 || item.getArtsScience().equals(studentScoreCheckDTO.getArtScience()))
                        .collect(Collectors.toList());
            }else {
                wightMap = subjectWightMap;
                subjectRelList = relResModels;
            }

            Long studentId = studentScoreCheckDTO.getStudentId();
            List<StudentPeriodScoreResModel> usuallyScoreList = usuallyScoreMap.get(studentId);
            List<StudentPeriodScoreResModel> examScoreList = examScoreMap.get(studentId);
            // 1. 每个科目的每个学段的成绩= 该科目该学段下平时成绩总分 *权重 + 考试成绩总分*权重
            // 2. 平时成绩总分 = 学段下 每个类型成绩总分 *权重 求和
            // 3. 每个类型成绩总分 = 该类型下每次成绩求和/ 该类型测验次数
            // 说明：
            // 1. 权重配置参考：系统设置=》成绩计算规则中配置=》科目测验/考试权重配置 和 平时成绩权重配置
            // 2. 平时测验类型包括：作业、大测、小测、堂课、其他
            // 3. 展示规则：
            // 1. 参考"系统设置=》科目评级设定"中"成绩展示规则"，选择"分数"，展示分值，选择"评级"，则分数转换成"评级"展示；未设置评级按照"分数"展示
            // 2. 分数展示位数：四舍五入，取整
            // 平时成绩
            Map<Long, Map<Long, Integer>> periodDataMap = new HashMap<>();
            if (usuallyScoreList != null) {
                for (StudentPeriodScoreResModel studentPeriodScoreResModel : usuallyScoreList) {
                    if (!periodDataMap.containsKey(studentPeriodScoreResModel.getPeriodId())) {
                        periodDataMap.put(studentPeriodScoreResModel.getPeriodId(), new HashMap<>());
                    }
                    // 这个学段下的所以科目成绩 科目id，成绩
                    Map<Long, Integer> subjecScoreMap = periodDataMap.get(studentPeriodScoreResModel.getPeriodId());
                    List<StudentSubjectScoreResModel> subjectScores = studentPeriodScoreResModel.getSubjectScores();
                    // 转maplist
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
            // 考试成绩
            Map<Long, Map<Long, Integer>> periodExamDataMap = new HashMap<>();
            if (examScoreList != null) {
                for (StudentPeriodScoreResModel studentPeriodScoreResModel : examScoreList) {
                    if (!periodExamDataMap.containsKey(studentPeriodScoreResModel.getPeriodId())) {
                        periodExamDataMap.put(studentPeriodScoreResModel.getPeriodId(), new HashMap<>());
                    }
                    Map<Long, Integer> subjecScoreMap = periodExamDataMap.get(studentPeriodScoreResModel.getPeriodId());
                    List<StudentSubjectScoreResModel> subjectScores = studentPeriodScoreResModel.getSubjectScores();
                    Map<Long, List<StudentSubjectScoreResModel>> subjectScoreMap = subjectScores.stream()
                            .collect(Collectors.groupingBy(StudentSubjectScoreResModel::getSubjectId));
                    subjectScoreMap.forEach((subjectId, subjectScoreList) -> {
                        Integer subjectSumScore = subjecScoreMap.get(subjectId);
                        int examScore = subjectScoreList.stream().mapToInt(StudentSubjectScoreResModel::getScore).sum()
                                / subjectScoreList.size();
                        subjectSumScore = subjectSumScore == null ? examScore : subjectSumScore + examScore;
                        subjecScoreMap.put(subjectId, subjectSumScore);
                    });
                }
            }
            // 计算每个学段的科目成绩
            Map<Long, List<StudentScoreCheckSubjectScoreDTO>> periodSubjectScoreMap = new HashMap<>();
            schoolYearDTOS.forEach(tranScriptSchoolYearDTO -> {
                Map<Long, Integer> usuallyScore = periodDataMap.get(tranScriptSchoolYearDTO.getPeriodId());
                Map<Long, Integer> examScore = periodExamDataMap.get(tranScriptSchoolYearDTO.getPeriodId());
                List<StudentScoreCheckSubjectScoreDTO> subjectScores = new ArrayList<>();
                subjectRelList.forEach(subject -> {
                    StudentScoreCheckSubjectScoreDTO subjectScoreDTO = new StudentScoreCheckSubjectScoreDTO();
                    subjectScoreDTO.setSubjectName(subject.getSubject() == null ? null : subject.getSubject().getSubjectName());
                    subjectScoreDTO.setSubjectId(subject.getId());
                    subjectScoreDTO.setCountedInAverage(subject.getCountedInAverage());
                    subjectScoreDTO.setArtsScience(subject.getArtsScience());
                    if (usuallyScore != null && usuallyScore.containsKey(subject.getId())) {
                        // 0-平时成绩,1-考试成绩,2-科目成绩
                        DepartmentScoreRuleDetailResModel subjectWight = scoreTypeMap.get("0");
                        if (subjectWight != null && subjectWight.getWeight() != null) {
                            Integer usually = (usuallyScore.get(subject.getId()) * subjectWight.getWeight()) / WIGHT;
                            subjectScoreDTO.setScore(subjectScoreDTO.getScore() == null ? usually
                                    : subjectScoreDTO.getScore() + usually);
                            subjectScoreDTO.setUsuallyScore(usuallyScore.get(subject.getId()));
                        }
                    }
                    if (examScore != null && examScore.containsKey(subject.getId())) {
                        // 0-平时成绩,1-考试成绩,2-科目成绩
                        DepartmentScoreRuleDetailResModel subjectWight = scoreTypeMap.get("1");
                        if (subjectWight != null && subjectWight.getWeight() != null) {
                            Integer exam = (examScore.get(subject.getId()) * subjectWight.getWeight()) / WIGHT;
                            subjectScoreDTO.setScore(
                                    subjectScoreDTO.getScore() == null ? exam : subjectScoreDTO.getScore() + exam);
                            subjectScoreDTO.setExamScore(examScore.get(subject.getId()));
                        }
                    }
                    SubjectLevelRuleResModel subjectLevelRuleResModel = subjectLevelRuleDetailResModelMap
                            .get(subject.getId());
                    if (subjectLevelRuleResModel != null && subjectLevelRuleResModel.getShowRule() != null) {
                        subjectScoreDTO.setShowRule(subjectLevelRuleResModel.getShowRule());
                        if (subjectLevelRuleResModel.getShowRule() == 1) {
                            subjectScoreDTO.setScoreLevel(
                                    getGrade(subjectScoreDTO.getScore(), subjectLevelRuleResModel.getDetailList()));
                        }
                        subjectScores.add(subjectScoreDTO);
                    }
                });
                periodSubjectScoreMap.put(tranScriptSchoolYearDTO.getPeriodId(), subjectScores);
            });
            // 插入每个学段的成绩
            if(studentScoreCheckDTO!=null && !CollectionUtils.isEmpty(studentScoreCheckDTO.getPeriodDataList())){
                List<StudentScoreCheckPeriodDataDTO> periodDataList = studentScoreCheckDTO.getPeriodDataList();
                periodDataList.forEach(periodData -> {
                    List<StudentScoreCheckSubjectScoreDTO> subjectScores = periodSubjectScoreMap
                            .get(periodData.getPeriodId());
                    periodData.setSubjectScores(subjectScores);
                    // 计算平均分
                    double averageScore = 0;
                    if (departmentScoreRuleResModel != null && departmentScoreRuleResModel.getAvgType()!= null &&  departmentScoreRuleResModel.getAvgType() == 1) {
                        averageScore = subjectScores.stream().filter(item -> item.getCountedInAverage() == 1)
                                .mapToInt(item -> {
                                    DepartmentScoreRuleDetailResModel ruleDetailResModel = wightMap
                                            .get(item.getSubjectId());
                                    if (ruleDetailResModel == null || ruleDetailResModel.getWeight() == null
                                            || item.getScore() == null) {
                                        return 0;
                                    }
                                    return (item.getScore() * ruleDetailResModel.getWeight()) / WIGHT;
                                }).sum();
                    } else {
                        averageScore = subjectScores.stream().filter(item -> item.getCountedInAverage() == 1)
                                .mapToInt(item -> {
                                    if (item.getScore() == null) {
                                        return 0;
                                    }
                                    return item.getScore();
                                }).average().orElse(0);
                    }
                    periodData.setAverageScore(averageScore);
                    periodData.setTotalStudents(result.size());
                    // 排名分数
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
        }

        //
        Map<Long, Map<Long, Integer>> rankScore = new HashMap<>();
        priodScoresMap.forEach((periodId, dataResModels) -> {
            if (CollectionUtils.isEmpty(dataResModels)) {
                return;
            }
            dataResModels.sort(Comparator.comparing(ScoreRankDTO::getAvgScore,
                    Comparator.nullsLast(Comparator.reverseOrder())));
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
        // 计算排名
        for (StudentScoreCheckDTO studentScoreCheckDTO : result) {
            List<StudentScoreCheckPeriodDataDTO> periodDataList = studentScoreCheckDTO.getPeriodDataList();
            if(!CollectionUtils.isEmpty(periodDataList)){
                for (StudentScoreCheckPeriodDataDTO periodData : periodDataList) {
                    Map<Long, Integer> scoreRankDTOS = rankScore.get(periodData.getPeriodId());
                    if (CollectionUtils.isEmpty(scoreRankDTOS)) {
                        continue;
                    }
                    // 计算这个学生排名
                    Integer rank = scoreRankDTOS.get(studentScoreCheckDTO.getStudentId());
                    if (rank != null) {
                        periodData.setRank(rank);
                    }
                }
            }
        }
    }

    private String getGrade(Integer score, List<SubjectLevelRuleDetailResModel> standardEntities) {
        if (CollectionUtils.isEmpty(standardEntities) || score == null) {
            return null;
        }
        for (SubjectLevelRuleDetailResModel standardEntity : standardEntities) {
            // 0就默认是>=0
            if (score == 0 && standardEntity.getRuleMin() == 0) {
                return standardEntity.getRuleLevel();
            }
            if (score > standardEntity.getRuleMin() * 100 && score <= standardEntity.getRuleMax() * 100) {
                return standardEntity.getRuleLevel();
            }
        }
        return null;
    }

    // 获取操行
    private void assembleData(Long classId, List<StudentScoreCheckDTO> result,
            List<TranScriptSchoolYearDTO> schoolYearDTOS, Long schoolId) {
        for (TranScriptSchoolYearDTO schoolYearDTO : schoolYearDTOS) {
            // 获取该学段下的所有素质分数
            List<StudentQualityScoreModel> studentQualityScoreList = classPerformanceService
                    .getStudentQualityScoreList(schoolYearDTO.getPeriodId(), classId, schoolId);

            // 转map
            Map<Long, StudentQualityScoreModel> studentQualityScoreMap = studentQualityScoreList.stream()
                    .collect(Collectors.toMap(StudentQualityScoreModel::getStudentId, Function.identity(),
                            (oldValue, newValue) -> oldValue));
            for (StudentScoreCheckDTO studentScoreCheckDTO : result) {
                List<StudentScoreCheckPeriodDataDTO> periodDataList = studentScoreCheckDTO.getPeriodDataList();
                if (periodDataList == null) {
                    periodDataList = new ArrayList<>();
                }
                StudentScoreCheckPeriodDataDTO dataResModel = new StudentScoreCheckPeriodDataDTO();
                dataResModel.setPeriodId(schoolYearDTO.getPeriodId());
                dataResModel.setPeriodName(schoolYearDTO.getPeriodName());
                dataResModel.setProportion(schoolYearDTO.getProportion());
                dataResModel.setStartTime(schoolYearDTO.getStartTime());
                StudentQualityScoreModel studentQualityScoreModel = studentQualityScoreMap
                        .get(studentScoreCheckDTO.getStudentId());
                if (studentQualityScoreModel != null) {
                    // 操行
                    List<StudentQualityScoreDetailResModel> resModels = studentQualityScoreModel.getResModels();
                    if (!CollectionUtils.isEmpty(resModels)) {
                        resModels.stream().filter(item -> item.getQualityProjectId() == 0).findFirst()
                                .ifPresent(item -> {
                                    dataResModel.setConduct(
                                            item.getQualityProjectLevel() == null ? "" : item.getQualityProjectLevel());
                                    dataResModel.setConductScore(item.getQualityProjectScore());
                                    dataResModel.setShowScore(item.isDisplay() ? 1 : 0);
                                });
                    }
                }
                periodDataList.add(dataResModel);
                studentScoreCheckDTO.setPeriodDataList(periodDataList);
            }
        }
    }

    // 计算这个学年的数据
    private void calculateSchoolYearData(List<StudentScoreCheckDTO> result, Long schoolId, Long groupId) {
        List<SubjectLevelRuleResModel> subjectLevelRuleResModels = subjectLevelRuleService.getRuleByDepartment(schoolId,
                groupId);
        // tomap subjectLevelRuleResModels
        Map<Long, SubjectLevelRuleResModel> subjectLevelRuleDetailResModelMap = subjectLevelRuleResModels.stream()
                .collect(Collectors.toMap(SubjectLevelRuleResModel::getSubjectId, item -> item));
        List<ScoreRankDTO> rankScore = new ArrayList<>();
        for (StudentScoreCheckDTO studentScoreCheckDTO : result) {

            List<StudentScoreCheckPeriodDataDTO> periodDataList = studentScoreCheckDTO.getPeriodDataList();
            if (periodDataList == null) {
                return;
            }
            StudentScoreCheckYearSummaryDTO yearSummary = new StudentScoreCheckYearSummaryDTO();
            yearSummary.setTotalStudents(result.size());
            // List<TranScriptSubjectScoreResModel> subjectScores = new ArrayList<>();
            Map<Long, StudentScoreCheckSubjectScoreDTO> subjectScoreMap = new HashMap<>();
            double avgScore = 0;
            String conduct = "";
            periodDataList.sort(Comparator.comparing(StudentScoreCheckPeriodDataDTO::getStartTime));
            for (StudentScoreCheckPeriodDataDTO periodData : periodDataList) {
                List<StudentScoreCheckSubjectScoreDTO> scoreResModels = periodData.getSubjectScores();
                if (scoreResModels != null) {
                    for (StudentScoreCheckSubjectScoreDTO scoreResModel : scoreResModels) {
                        StudentScoreCheckSubjectScoreDTO scoreResModel1 = subjectScoreMap
                                .get(scoreResModel.getSubjectId());
                        if (scoreResModel1 == null) {
                            StudentScoreCheckSubjectScoreDTO resModel = new StudentScoreCheckSubjectScoreDTO();
                            resModel.setSubjectId(scoreResModel.getSubjectId());
                            resModel.setSubjectName(scoreResModel.getSubjectName());
                            if (scoreResModel.getScore() != null && periodData.getProportion() != null) {
                                resModel.setScore((scoreResModel.getScore() * periodData.getProportion()) / WIGHT);
                            }
                            subjectScoreMap.put(scoreResModel.getSubjectId(), resModel);
                        } else {
                            if (scoreResModel.getScore() != null && periodData.getProportion() != null) {
                                scoreResModel1.setScore(scoreResModel1.getScore() == null
                                        ? ((scoreResModel.getScore() * periodData.getProportion()) / WIGHT)
                                        : scoreResModel1.getScore()
                                                + ((scoreResModel.getScore() * periodData.getProportion()) / WIGHT));
                            }
                        }
                    }
                }
                if (periodData.getAverageScore() != null && periodData.getProportion() != null) {
                    avgScore += periodData.getAverageScore() * periodData.getProportion() / WIGHT;
                }
                conduct = periodData.getConduct();
                yearSummary.setConductScore(periodData.getConductScore());
                yearSummary.setShowScore(periodData.getShowScore());
            }
            yearSummary.setSubjectScores(new ArrayList<>(subjectScoreMap.values()));
            for (StudentScoreCheckSubjectScoreDTO scoreResModel : yearSummary.getSubjectScores()) {
                SubjectLevelRuleResModel subjectLevelRuleResModel = subjectLevelRuleDetailResModelMap
                        .get(scoreResModel.getSubjectId());
                if (subjectLevelRuleResModel != null) {
                    scoreResModel.setShowRule(subjectLevelRuleResModel.getShowRule());
                    scoreResModel.setScoreLevel(
                            getGrade(scoreResModel.getScore(), subjectLevelRuleResModel.getDetailList()));
                }
            }
            yearSummary.setAverageScore(avgScore);
            yearSummary.setConduct(conduct);
            studentScoreCheckDTO.setYearSummary(yearSummary);
            ScoreRankDTO scoreRankDTO = new ScoreRankDTO();
            scoreRankDTO.setStudentId(studentScoreCheckDTO.getStudentId());
            scoreRankDTO.setAvgScore(avgScore);
            rankScore.add(scoreRankDTO);
        }

        if (!CollectionUtils.isEmpty(rankScore)) {
            rankScore.sort(Comparator.comparing(ScoreRankDTO::getAvgScore,
                    Comparator.nullsLast(Comparator.reverseOrder())));
        }
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
        // 计算排名
        for (StudentScoreCheckDTO studentScoreCheckDTO : result) {
            StudentScoreCheckYearSummaryDTO periodDataList = studentScoreCheckDTO.getYearSummary();
            if (periodDataList == null) {
                continue;
            }
            // 计算这个学生排名
            Integer ranks = periodRankScore.get(studentScoreCheckDTO.getStudentId());
            if (ranks != null) {
                periodDataList.setRank(ranks);
            }
        }
    }

}