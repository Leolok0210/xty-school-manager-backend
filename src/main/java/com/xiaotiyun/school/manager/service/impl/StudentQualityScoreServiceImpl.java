package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtils;
import com.xiaotiyun.school.manager.dao.StudentMapper;
import com.xiaotiyun.school.manager.dao.StudentQualityScoreDao;
import com.xiaotiyun.school.manager.listener.StudentQualityScoreImportListener;
import com.xiaotiyun.school.manager.model.dto.ImportRecordSaveDTO;
import com.xiaotiyun.school.manager.model.entity.ImportTaskEntity;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.entity.StudentQualityScore;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.model.req.StudentPageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentQualityScoreQueryReqModel;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentQualityScoreServiceImpl extends ServiceImpl<StudentQualityScoreDao, StudentQualityScore> implements StudentQualityScoreService {

    @Autowired
    private StudentQualityScoreDao studentQualityScoreDao;


    @Autowired
    private StudentService studentService;

    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private ImportRecordService importRecordService;
    @Resource
    private SystemSettingService systemSettingService;
    @Resource(name = "importExecutor")
    private ThreadPoolTaskExecutor importExecutor;

    @Autowired
    private QualityEvaluationService qualityEvaluationService;

    @Resource
    private LanguageUtil languageUtil;


    @Resource
    private StudentMapper studentMapper;

    @Resource
    private SysClassService sysClassService;

    @Override
    public void createStudentQualityScores(List<StudentQualityScore> studentQualityScores) {
        studentQualityScores.forEach(studentQualityScore -> {
            studentQualityScore.setCreateTime(LocalDateTime.now());
            studentQualityScore.setUpdateTime(LocalDateTime.now());
            studentQualityScore.setDeleted(0L);
        });
        saveBatch(studentQualityScores);
    }

    @Override
    public void updateStudentQualityScore(StudentQualityScore studentQualityScore) {
        studentQualityScore.setUpdateTime(LocalDateTime.now());
        updateById(studentQualityScore);
    }

    @Override
    public void deleteStudentQualityScore(Long id) {
        //逻辑删除
        removeById(id);
    }

    @Override
    public StudentQualityScore getStudentQualityScoreById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public PageInfo<StudentQualityScoreListResModel> getStudentQualityScoreList(StudentQualityScoreQueryReqModel reqModel) {
        StudentPageReqModel pageReqModel = new StudentPageReqModel();
        pageReqModel.setPageNum(reqModel.getPageNum());
        pageReqModel.setPageSize(reqModel.getPageSize());
        pageReqModel.setSchoolId(reqModel.getSchoolId());
        pageReqModel.setClassId(reqModel.getClassId());
        pageReqModel.setUserId(reqModel.getUserId());
        PageInfo<StudentPageResModel> page = studentService.page(pageReqModel);
        PageInfo<StudentQualityScoreListResModel> info = new PageInfo<>();
        info.setPages(page.getPages());
        info.setTotal(page.getTotal());
        if(page.getList().isEmpty())
        {
            info.setList(new ArrayList<>());
            return info;
        }
        List<Long> ids = page.getList().stream().map(StudentPageResModel::getId).collect(Collectors.toList());
        //转map
        Map<Long, StudentPageResModel> studentMap = page.getList().stream().collect(Collectors.toMap(StudentPageResModel::getId, item -> item));
        LambdaQueryWrapper<StudentQualityScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(reqModel.getSid()), StudentQualityScore::getSid, reqModel.getSid())
                .eq(reqModel.getTerm() != null && reqModel.getTerm() > 0, StudentQualityScore::getTerm, reqModel.getTerm())
                .in(StudentQualityScore::getStudentId, ids)
                .eq(reqModel.getClassId() != null && reqModel.getClassId() > 0, StudentQualityScore::getClassId, reqModel.getClassId())
                .eq( StudentQualityScore::getDeleted,0);
        List<StudentQualityScore> studentQualityScores = this.baseMapper.selectList(wrapper);
        List<StudentQualityScoreDetailResModel> studentQualityScoreDetailResModels = studentQualityScores.stream()
                .map(item -> {
                    StudentQualityScoreDetailResModel resModel = new StudentQualityScoreDetailResModel();
                    BeanUtils.copyProperties(item, resModel);
                    return resModel;
                }).collect(Collectors.toList());
        //查询标准
        List<QualityEvaluationGradeStandardResModel> standardEntities = qualityEvaluationService.listGradeStandards(reqModel.getSchoolId());
        //查询标准
        SysClass sysClass = sysClassService.getSysClassById(reqModel.getClassId());
        List<QualityIndicatorListResModel> indicatorListResModels = qualityEvaluationService.listIndicator(reqModel.getSchoolId(),sysClass.getDepartment());
        //根据名称转map
        Map<Long, QualityIndicatorListResModel> indicatorMap = indicatorListResModels.stream()
                .collect(Collectors.toMap(QualityIndicatorListResModel::getId, indicator -> indicator));
        //转map
        Map<Long, List<StudentQualityScoreDetailResModel>> studentPageResModelMap = studentQualityScoreDetailResModels.stream().collect(Collectors.groupingBy(StudentQualityScoreDetailResModel::getStudentId));
        List<StudentQualityScoreListResModel> studentQualityScoreListResModels = ids.stream().map(item -> {
            StudentQualityScoreListResModel resModel = new StudentQualityScoreListResModel();
            List<StudentQualityScoreDetailResModel> detailResModels = studentPageResModelMap.get(item);
            StudentPageResModel studentPageResModel = studentMap.get(item);
            resModel.setStudentId(item);
            resModel.setStudentName(studentPageResModel.getStudentName());
            resModel.setSeatNo(studentPageResModel.getSeatNo());
            resModel.setEnglishName(studentPageResModel.getEnglishName());
            List<StudentQualityScoreDetailResModel> resModels = new ArrayList<>();
            if(detailResModels == null)
            {
                detailResModels = new ArrayList<>();
            }
            Map<Long, List<StudentQualityScoreDetailResModel>> typeMap = detailResModels.stream().collect(Collectors.groupingBy(StudentQualityScoreDetailResModel::getQualityProjectId));
            //indicatorMap
            indicatorMap.forEach((key,data)->{
                String displayType = data.getDisplayType();
                StudentQualityScoreDetailResModel itemType = new StudentQualityScoreDetailResModel();
                itemType.setQualityProjectId(key);
                //求和
                List<StudentQualityScoreDetailResModel> value = typeMap.get(key);
                if (value != null && !value.isEmpty()) {
                    itemType.setQualityProjectScore(value.stream().mapToLong(StudentQualityScoreDetailResModel::getQualityProjectScore).sum() / value.size());
                    itemType.setQualityProjectLevel(getGrade(itemType.getQualityProjectScore(), standardEntities));
                    itemType.setDisplay("SCORE".equals(displayType));
                }
                itemType.setQualityProjectName(data.getContent());
                itemType.setStudentId(item);
                resModels.add(itemType);
            });
            if(!CollectionUtils.isEmpty(resModels))
            {
                //求和
                long score = 0;
                for (StudentQualityScoreDetailResModel detailResModel : resModels)
                {
                    QualityIndicatorListResModel listResModel = indicatorMap.get(detailResModel.getQualityProjectId());
                    if(listResModel != null)
                    {
                        score += (detailResModel.getQualityProjectScore() == null ? 0:detailResModel.getQualityProjectScore())*listResModel.getWeight();
                    }
                }
                StudentQualityScoreDetailResModel itemType = new StudentQualityScoreDetailResModel();
                itemType.setQualityProjectId(0L);
                if(score > 0) {
                    itemType.setQualityProjectScore(score / 100);
                    itemType.setQualityProjectLevel(getGrade(itemType.getQualityProjectScore(), standardEntities));
                    itemType.setDisplay(false);
                }
                itemType.setQualityProjectName("操行");
                itemType.setStudentId(item);
                resModels.add(itemType);
            }else {
                StudentQualityScoreDetailResModel itemType = new StudentQualityScoreDetailResModel();
                itemType.setQualityProjectId(0L);
                itemType.setQualityProjectName("操行");
                itemType.setStudentId(item);
                resModels.add(itemType);
            }
            resModels.sort(Comparator.comparing(StudentQualityScoreDetailResModel::getQualityProjectId));
            resModel.setResModels(resModels);
            return resModel;
        }).collect(Collectors.toList());
        info.setList(studentQualityScoreListResModels);
        return info;
    }

    @Override
    public List<StudentQualityScoreListResModel> getStudentQualityScoreExportList(StudentQualityScoreQueryReqModel reqModel) {
        StudentPageReqModel pageReqModel = new StudentPageReqModel();
        pageReqModel.setPageNum(reqModel.getPageNum());
        pageReqModel.setPageSize(reqModel.getPageSize());
        pageReqModel.setSchoolId(reqModel.getSchoolId());
        pageReqModel.setClassId(reqModel.getClassId());
        List<StudentPageResModel> studentPageResModels = studentMapper.page(pageReqModel);
        if(CollectionUtils.isEmpty(studentPageResModels))
        {
            return new ArrayList<>();
        }
        List<Long> ids = studentPageResModels.stream().map(StudentPageResModel::getId).collect(Collectors.toList());
        //转map
        Map<Long, StudentPageResModel> studentMap = studentPageResModels.stream().collect(Collectors.toMap(StudentPageResModel::getId, item -> item));
        LambdaQueryWrapper<StudentQualityScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(reqModel.getSid()), StudentQualityScore::getSid, reqModel.getSid())
                .eq(reqModel.getTerm() != null && reqModel.getTerm() > 0, StudentQualityScore::getTerm, reqModel.getTerm())
                .in(StudentQualityScore::getStudentId, ids)
                .eq(reqModel.getClassId() != null && reqModel.getClassId() > 0, StudentQualityScore::getClassId, reqModel.getClassId())
                .eq( StudentQualityScore::getDeleted,0);
        List<StudentQualityScore> studentQualityScores = this.baseMapper.selectList(wrapper);
        List<StudentQualityScoreDetailResModel> studentQualityScoreDetailResModels = studentQualityScores.stream()
                .map(item -> {
                    StudentQualityScoreDetailResModel resModel = new StudentQualityScoreDetailResModel();
                    BeanUtils.copyProperties(item, resModel);
                    return resModel;
                }).collect(Collectors.toList());
        //查询标准
        List<QualityEvaluationGradeStandardResModel> standardEntities = qualityEvaluationService.listGradeStandards(reqModel.getSchoolId());
        //查询标准
        SysClass sysClass = sysClassService.getSysClassById(reqModel.getClassId());
        List<QualityIndicatorListResModel> indicatorListResModels = qualityEvaluationService.listIndicator(reqModel.getSchoolId(),sysClass.getDepartment());
        //根据名称转map
        Map<Long, QualityIndicatorListResModel> indicatorMap = indicatorListResModels.stream()
                .collect(Collectors.toMap(QualityIndicatorListResModel::getId, indicator -> indicator));
        //转map
        Map<Long, List<StudentQualityScoreDetailResModel>> studentPageResModelMap = studentQualityScoreDetailResModels.stream().collect(Collectors.groupingBy(StudentQualityScoreDetailResModel::getStudentId));
        List<StudentQualityScoreListResModel> studentQualityScoreListResModels = ids.stream().map(item -> {
            StudentQualityScoreListResModel resModel = new StudentQualityScoreListResModel();
            List<StudentQualityScoreDetailResModel> detailResModels = studentPageResModelMap.get(item);
            StudentPageResModel studentPageResModel = studentMap.get(item);
            resModel.setStudentId(item);
            resModel.setStudentName(studentPageResModel.getStudentName());
            resModel.setSeatNo(studentPageResModel.getSeatNo());
            resModel.setEnglishName(studentPageResModel.getEnglishName());
            List<StudentQualityScoreDetailResModel> resModels = new ArrayList<>();
            if(detailResModels == null)
            {
                detailResModels = new ArrayList<>();
            }
            Map<Long, List<StudentQualityScoreDetailResModel>> typeMap = detailResModels.stream().collect(Collectors.groupingBy(StudentQualityScoreDetailResModel::getQualityProjectId));
            //indicatorMap
            indicatorMap.forEach((key,data)->{
                String displayType = data.getDisplayType();
                StudentQualityScoreDetailResModel itemType = new StudentQualityScoreDetailResModel();
                itemType.setQualityProjectId(key);
                //求和
                List<StudentQualityScoreDetailResModel> value = typeMap.get(key);
                if (value != null && !value.isEmpty()) {
                    itemType.setQualityProjectScore(value.stream().mapToLong(StudentQualityScoreDetailResModel::getQualityProjectScore).sum() / value.size());
                    itemType.setQualityProjectLevel(getGrade(itemType.getQualityProjectScore(), standardEntities));
                    itemType.setDisplay("SCORE".equals(displayType));
                }
                itemType.setQualityProjectName(data.getContent());
                itemType.setStudentId(item);
                resModels.add(itemType);
            });
            if(!CollectionUtils.isEmpty(resModels))
            {
                //求和
                long score = 0;
                for (StudentQualityScoreDetailResModel detailResModel : resModels)
                {
                    QualityIndicatorListResModel listResModel = indicatorMap.get(detailResModel.getQualityProjectId());
                    if(listResModel != null && detailResModel.getQualityProjectScore() != null)
                    {
                        score += detailResModel.getQualityProjectScore()*listResModel.getWeight();
                    }
                }
                StudentQualityScoreDetailResModel itemType = new StudentQualityScoreDetailResModel();
                itemType.setQualityProjectId(0L);
                itemType.setQualityProjectName("操行");
                if(score > 0) {
                    itemType.setQualityProjectScore(score / 100);
                    itemType.setQualityProjectLevel(getGrade(itemType.getQualityProjectScore(), standardEntities));
                    itemType.setDisplay(false);
                }
                resModels.add(itemType);
            }else {
                StudentQualityScoreDetailResModel itemType = new StudentQualityScoreDetailResModel();
                itemType.setQualityProjectId(0L);
                itemType.setStudentId(item);
                itemType.setQualityProjectName("操行");
                resModels.add(itemType);
            }
            resModel.setResModels(resModels);
            return resModel;
        }).collect(Collectors.toList());
        return studentQualityScoreListResModels;
    }

    @Override
    public Long importStudentQualityScore(MultipartFile file, Long schoolId, Long classId, Long term,String sid) {
        if (schoolId == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.NO_SCHOOL_FILE_CONTENT_EMPTY));
        }
        //获取学校语言设置信息
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
        Map<Integer, String> head = null;
        Map<Integer, Map<Integer, String>> dataList = null;
        try (InputStream inputStream = file.getInputStream()) {
            StudentQualityScoreImportListener importListener = new StudentQualityScoreImportListener();
            EasyExcel.read(inputStream, importListener).sheet().doRead();
            head = importListener.getHead();
            dataList = importListener.getDataList();
            log.info("Excel文件读取成功 head:{},data:{}",head,dataList);
        } catch (IOException e) {
            log.error("Excel文件读取失败", e);
            throw new BusinessException(languageUtil.getMessage(LanguageConstants.FILE_FORMAT_ERROR));
        }
        //检验表头前三个
        String seat = head.get(0);
        if(!LanguageUtils.getSeatNumber(languageEnum).equals(seat))
        {
            throw new BusinessException(languageUtil.getMessage(LanguageConstants.FILE_FORMAT_ERROR));
        }
        if(!LanguageUtils.getChineseName(languageEnum).equals(head.get(1)) || !LanguageUtils.getStudentNumber(languageEnum).equals(head.get(2)))
        {
            throw new BusinessException(languageUtil.getMessage(LanguageConstants.FILE_FORMAT_ERROR));
        }
        if(dataList == null){
            dataList = new HashMap<>();
        }
        SysClass sysClass = sysClassService.getSysClassById(classId);
        Integer department = sysClass.getDepartment();
        //查询标准
        List<QualityIndicatorListResModel> indicatorListResModels = qualityEvaluationService.listIndicator(schoolId,department);
        //根据名称转map
        Map<String, QualityIndicatorListResModel> indicatorMap = indicatorListResModels.stream()
                .collect(Collectors.toMap(QualityIndicatorListResModel::getContent, Function.identity(),(x1,x2)->x1));
        //素质项目
        for (int i = 3; i < head.size(); i++)
        {
            String qualityProject = head.get(i);
            if(!indicatorMap.containsKey(qualityProject))
            {
                throw new BusinessException(languageUtil.getMessage(LanguageConstants.FILE_FORMAT_ERROR));
            }
        }
        // 创建导入任务
        ImportTaskEntity task = new ImportTaskEntity();
        task.setSchoolId(schoolId);
        task.setFileName(file.getOriginalFilename());
        task.setType(ImportTaskTypeEnum.STUDENT_QUALITY_SCORE_INFO.getCode());
        task.setTotalCount(0);
        task.setSuccessCount(0);
        task.setFailCount(0);
        importTaskService.save(task);
        Map<Integer, Map<Integer, String>> finalDataList = dataList;
        Map<Integer, String> finalHead = head;
        CompletableFuture.runAsync(() -> {
            languageUtil.setLanguage(languageEnum.getCode());
            log.info("当前使用的语言是:{}", LanguageUtil.getCurrentLanguage());
            handleStudentQualityScoreImport(languageEnum, task, finalDataList, finalHead, schoolId,classId,indicatorMap,sid,term);
            LanguageUtil.clearLanguage();
        }, importExecutor).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("导入学生素质评分任务执行结束taskId=【{}】异常={}",task.getId(),ex);
            } else {
                log.info("导入学生素质评分完成，任务ID={}",task.getId());
            }
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            importTaskService.updateById(task);
        });
        return task.getId();
    }

    @Override
    public boolean hasScore(Long periodId) {
        if (periodId == null) {
            return false;
        }
        LambdaQueryWrapper<StudentQualityScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentQualityScore::getTerm,periodId);
        return  this.count(queryWrapper) > 0;
    }

    private String getGrade(Long score,List<QualityEvaluationGradeStandardResModel> standardEntities )
    {
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

    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.ANNUAL_QUALITY)
    private List<StudentQualityScore> handleStudentQualityScoreImport(SchoolLanguageEnum languageEnum, ImportTaskEntity task, Map<Integer, Map<Integer, String>> dataList,
                                                 Map<Integer, String>head,Long schoolId,Long classId,Map<String, QualityIndicatorListResModel> indicatorMap,
                                                    String sid,Long term) {
        task.setTotalCount(dataList.size());
        task.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
        importTaskService.updateById(task);
        int successCount = 0;
        List<StudentQualityScore> studentQualityScores = new ArrayList<>();
        try {
            //查询这个班级下面的全部学生
            List<StudentEntity> studentEntities = studentService.getStudentListByClassId(classId);
            Map<String, List<StudentEntity>> studentMap = studentEntities.stream().collect(Collectors.groupingBy(StudentEntity::getStudentNo));
            //根据中文姓名转map
            Map<String,  List<StudentEntity>> studentMapByName = studentEntities.stream().collect(Collectors.groupingBy(StudentEntity::getChineseName));
            List<ImportRecordSaveDTO> failureReason = new ArrayList<>();

            for (Map.Entry<Integer, Map<Integer, String>> entry : dataList.entrySet())
            {
                Map<Integer, String> data = entry.getValue();
                // 数据校验
                if (!validateStudentQualityScore(entry.getKey(),languageEnum, data,head, failureReason, task.getId(),studentMap,studentMapByName)) {
                    continue;
                }
                //素质项目
                //获取当前学生
                String studentName = data.get(1);
                String studentNumber = data.get(2);
                List<StudentEntity> currentStudent = studentMap.get(studentNumber);
                List<StudentEntity> entities = currentStudent.stream().filter(studentEntity -> studentEntity.getChineseName().equals(studentName))
                        .collect(Collectors.toList());
                StudentEntity studentEntity = entities.get(0);
                for (int i = 3; i < head.size(); i++)
                {
                    String qualityProject = head.get(i);
                    QualityIndicatorListResModel projectModel = indicatorMap.get(qualityProject);
                    StudentQualityScore studentQualityScore = new StudentQualityScore();
                    //获取项目id
                    studentQualityScore.setQualityProjectId(projectModel.getId());
                    studentQualityScore.setStudentId(studentEntity.getId());
                    studentQualityScore.setSid(sid);
                    studentQualityScore.setTerm(term);
                    studentQualityScore.setClassId(classId);
                    String score = data.get(i);
                    if(score == null || score.isEmpty())
                    {
                        continue;
                    }
                    studentQualityScore.setQualityProjectScore((long) (Double.parseDouble(score) * 100));
                    studentQualityScore.setSchoolId(schoolId);
                    studentQualityScore.setDeleted(0L);
                    studentQualityScore.setCreateTime(LocalDateTime.now());
                    studentQualityScore.setUpdateTime(LocalDateTime.now());
                    studentQualityScores.add(studentQualityScore);
                }
                successCount++;
            }
            if(!CollectionUtils.isEmpty(studentQualityScores)) {
                saveBatch(studentQualityScores);
            }
            // 保存错误信息
            if (!CollectionUtils.isEmpty(failureReason)) {
                importRecordService.save(failureReason);
            }
        }finally {
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            task.setFailCount(task.getTotalCount() - successCount);
            task.setSuccessCount(successCount);
            importTaskService.updateById(task);
        }
        return studentQualityScores;
    }

    private boolean validateStudentQualityScore(Integer index,SchoolLanguageEnum languageEnum, Map<Integer, String> data,
                                                Map<Integer, String> head,List<ImportRecordSaveDTO> failureReasons, Long taskId,
                                                Map<String,  List<StudentEntity>> studentMap, Map<String,  List<StudentEntity>> studentMapByName) {
        boolean isValid = true;

        StringBuilder failureReason = new StringBuilder();
        // 座位号
        String seat = data.get(0);

        // name
        String studentName = data.get(1);
        if (studentName == null || studentName.isEmpty()) {
            failureReason.append(languageUtil.getMessage(LanguageConstants.CHINESE_NAME_REQUIRED));
            isValid = false;
        }

        //学生编号
        String studentNumber = data.get(2); // 自动清除首尾空格
        if (studentNumber == null || studentNumber.isEmpty()) {
            failureReason.append(languageUtil.getMessage(LanguageConstants.STUDENT_NO_REQUIRED));
            isValid = false;
        }

        if (!studentMapByName.containsKey(studentName)) {
            failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.STUDENT_NAME_NOT_FOUND), studentName));
            isValid = false;
        }else {
            List<StudentEntity> studentEntities = studentMapByName.get(studentName);
            List<StudentEntity> entities = studentEntities.stream().filter(studentEntity -> studentEntity.getStudentNo().equals(studentNumber)).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(entities))
            {
                failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.STUDENT_NAME_NOT_MATCH_STUDENT_NO), studentName));
                isValid = false;
            }
        }

        if (!studentMap.containsKey(studentNumber)) {
            failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.STUDENT_NO_NOT_FOUND_IN_SCHOOL), studentNumber));
            isValid = false;
        }else {
            List<StudentEntity> studentEntities = studentMap.get(studentNumber);
            List<StudentEntity> entities = studentEntities.stream().filter(studentEntity -> studentEntity.getChineseName().equals(studentName)).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(entities))
            {
                failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.STUDENT_NO_NOT_MATCH_STUDENT_NAME), studentNumber));
                isValid = false;
            }
        }
        //素质项目
        for (int i = 3; i < head.size(); i++)
        {
            String qualityProject = head.get(i);
            String qualityProjectScore = data.get(i);
            if (qualityProjectScore == null || qualityProject == null || qualityProject.isEmpty())
            {
                continue;
            }
            //校验得分是否是数字格式
            if (!qualityProjectScore.matches("^[0-9]+(.[0-9]+)?$")) {
                failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.QUALITY_PROJECT_FORMAT_ERROR), qualityProject));
                isValid = false;
            }
        }

        if (!isValid) {
            ImportRecordSaveDTO failureReasonDTO = new ImportRecordSaveDTO();
            failureReasonDTO.setTaskId(taskId);
            failureReasonDTO.setIncorrectLineno(String.valueOf(index));
            failureReasonDTO.setIncorrectReason(failureReason.toString());
            failureReasons.add(failureReasonDTO);
        }
        return isValid;
    }

}