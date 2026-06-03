package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.ConventionalPerformanceDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.listener.ConventionalPerformanceImportEnUsListener;
import com.xiaotiyun.school.manager.listener.ConventionalPerformanceImportPtPtListener;
import com.xiaotiyun.school.manager.listener.ConventionalPerformanceImportZhTwListener;
import com.xiaotiyun.school.manager.model.dto.ConventionalPerformanceImportDTO;
import com.xiaotiyun.school.manager.model.dto.ImportRecordSaveDTO;
import com.xiaotiyun.school.manager.model.entity.ConventionalPerformanceEntity;
import com.xiaotiyun.school.manager.model.entity.ImportRecordEntity;
import com.xiaotiyun.school.manager.model.entity.ImportTaskEntity;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.ConventionalPerformancePageResModel;
import com.xiaotiyun.school.manager.model.res.StudentPerformanceTotalResModel;
import com.xiaotiyun.school.manager.service.ConventionalPerformanceService;
import com.xiaotiyun.school.manager.service.ImportRecordService;
import com.xiaotiyun.school.manager.service.ImportTaskService;
import com.xiaotiyun.school.manager.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConventionalPerformanceServiceImpl extends ServiceImpl<ConventionalPerformanceDao, ConventionalPerformanceEntity> implements ConventionalPerformanceService {
    private final ExportFileHandler exportFileHandler;
    private final LanguageUtil languageUtil;
    private final ImportTaskService importTaskService;
    private final ImportRecordService importRecordService;
    private final StudentService studentService;
    @Resource(name = "importExecutor")
    private ThreadPoolTaskExecutor importPool;

    @Override
    public PageInfo<ConventionalPerformancePageResModel> page(Long schoolId, ConventionalPerformancePageReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<ConventionalPerformancePageResModel> list = this.getBaseMapper().page(schoolId, reqModel);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public void save(Long schoolId, Long userId, ConventionalPerformanceSaveReqModel reqModel) {
        if (CollectionUtils.isNotEmpty(reqModel.getStudentInfos())) {
            List<ConventionalPerformanceEntity> insertList = new ArrayList<>();
            for (ConventionalPerformanceAddStudentReqModel studentInfo : reqModel.getStudentInfos()) {
                if (CollectionUtils.isNotEmpty(studentInfo.getTypes())) {
                    for (ConventionalPerformanceAddStudentTypeReqModel typeInfo : studentInfo.getTypes()) {
                        ConventionalPerformanceEntity entity = BeanConvertUtil.convert(typeInfo, ConventionalPerformanceEntity.class);
                        entity.setSchoolId(schoolId);
                        entity.setClassId(reqModel.getClassId());
                        entity.setTerm(reqModel.getTerm());
                        entity.setSid(reqModel.getSid());
                        entity.setCreateId(userId);
                        entity.setDate(studentInfo.getDate());
                        entity.setStudentId(studentInfo.getStudentId());
                        entity.setRemark(typeInfo.getRemark());
                        insertList.add(entity);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(insertList)) {
                this.saveBatch(insertList);
            }
        }
    }

    @Override
    @Transactional
    public void update(Long id, ConventionalPerformanceUpdateReqModel reqModel) {
        ConventionalPerformanceEntity entity = this.getById(id);
        if (entity != null) {
            BeanUtils.copyProperties(reqModel, entity);
            this.updateById(entity);
        }
    }

    @Override
    public void delete(Long id) {
        ConventionalPerformanceEntity entity = this.getById(id);
        if (entity != null) {
            this.removeById(id);
        }
    }

    @Override
    public Long importRecord(Long schoolId, Long userId, String sid, Long term, Long classId, MultipartFile file) {
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
        List<ConventionalPerformanceImportModel> list = readExcelData(file, languageEnum);
        if (CollectionUtils.isNotEmpty(list)) {
            // 创建导入任务
            ImportTaskEntity task = new ImportTaskEntity();
            task.setSchoolId(schoolId);
            task.setFileName(file.getOriginalFilename());
            task.setType(ImportTaskTypeEnum.CONVENTIONAL_PERFORMANCE.getCode());
            task.setTotalCount(0);
            task.setSuccessCount(0);
            task.setFailCount(0);
            importTaskService.save(task);
            CompletableFuture.runAsync(() -> {
                languageUtil.setLanguage(languageEnum.getCode());
                log.info("当前使用的语言是:{}", LanguageUtil.getCurrentLanguage());
                handleImportData(task, list, schoolId, userId, sid, term, classId, languageEnum);
                LanguageUtil.clearLanguage();
            }, importPool).whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("导入常规表现任务执行结束taskId=【{}】异常={}",task.getId(),ex);
                } else {
                    log.info("导入常规表现完成，任务ID={}",task.getId());
                }
                task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
                importTaskService.updateById(task);
            });
            return task.getId();
        }
        return null;
    }

    private void handleImportData(List<ConventionalPerformanceImportDTO> correctList, Long schoolId, Long userId, String sid, Long term, Long classId) {
        if (CollectionUtils.isNotEmpty(correctList)) {
            List<ConventionalPerformanceEntity> insertList = new ArrayList<>();
            for (ConventionalPerformanceImportDTO importDTO : correctList) {
                // 检查每个字段是否大于0，如果大于0则创建对应的记录
                if (importDTO.getMissingHomework() != null && importDTO.getMissingHomework() > 0) {
                    ConventionalPerformanceEntity entity = new ConventionalPerformanceEntity();
                    entity.setSchoolId(schoolId);
                    entity.setClassId(classId);
                    entity.setTerm(term);
                    entity.setStudentId(importDTO.getStudentId());
                    entity.setSid(sid);
                    entity.setDate(importDTO.getDate());
                    entity.setType(ConventionalPerformanceTypeEnum.MISSING_HOMEWORK.getCode()); // 欠作业类型
                    entity.setFrequency(importDTO.getMissingHomework());
                    entity.setRemark(importDTO.getMissingHomeworkRemark());
                    entity.setCreateId(userId);
                    insertList.add(entity);
                }
                if (importDTO.getMissingTextbook() != null && importDTO.getMissingTextbook() > 0) {
                    ConventionalPerformanceEntity entity = new ConventionalPerformanceEntity();
                    entity.setSchoolId(schoolId);
                    entity.setClassId(classId);
                    entity.setTerm(term);
                    entity.setStudentId(importDTO.getStudentId());
                    entity.setSid(sid);
                    entity.setDate(importDTO.getDate());
                    entity.setType(ConventionalPerformanceTypeEnum.MISSING_TEXTBOOK.getCode()); // 欠课本类型
                    entity.setFrequency(importDTO.getMissingTextbook());
                    entity.setRemark(importDTO.getMissingTextbookRemark());
                    entity.setCreateId(userId);
                    insertList.add(entity);
                }
                if (importDTO.getClassViolation() != null && importDTO.getClassViolation() > 0) {
                    ConventionalPerformanceEntity entity = new ConventionalPerformanceEntity();
                    entity.setSchoolId(schoolId);
                    entity.setClassId(classId);
                    entity.setTerm(term);
                    entity.setStudentId(importDTO.getStudentId());
                    entity.setSid(sid);
                    entity.setDate(importDTO.getDate());
                    entity.setType(ConventionalPerformanceTypeEnum.CLASS_VIOLATION.getCode()); // 上课违规类型
                    entity.setFrequency(importDTO.getClassViolation());
                    entity.setRemark(importDTO.getClassViolationRemark());
                    entity.setCreateId(userId);
                    insertList.add(entity);
                }
                if (importDTO.getUniformNonCompliance() != null && importDTO.getUniformNonCompliance() > 0) {
                    ConventionalPerformanceEntity entity = new ConventionalPerformanceEntity();
                    entity.setSchoolId(schoolId);
                    entity.setClassId(classId);
                    entity.setTerm(term);
                    entity.setStudentId(importDTO.getStudentId());
                    entity.setSid(sid);
                    entity.setDate(importDTO.getDate());
                    entity.setType(ConventionalPerformanceTypeEnum.UNIFORM_NON_COMPLIANCE.getCode()); // 仪表不符类型
                    entity.setFrequency(importDTO.getUniformNonCompliance());
                    entity.setRemark(importDTO.getUniformNonComplianceRemark());
                    entity.setCreateId(userId);
                    insertList.add(entity);
                }
                if (importDTO.getMissingReturnSticker() != null && importDTO.getMissingReturnSticker() > 0) {
                    ConventionalPerformanceEntity entity = new ConventionalPerformanceEntity();
                    entity.setSchoolId(schoolId);
                    entity.setClassId(classId);
                    entity.setTerm(term);
                    entity.setStudentId(importDTO.getStudentId());
                    entity.setSid(sid);
                    entity.setDate(importDTO.getDate());
                    entity.setType(ConventionalPerformanceTypeEnum.MISSING_RETURN_STICKER.getCode()); // 欠回条类型
                    entity.setFrequency(importDTO.getMissingReturnSticker());
                    entity.setRemark(importDTO.getMissingReturnStickerRemark());
                    entity.setCreateId(userId);
                    insertList.add(entity);
                }
            }
            if (CollectionUtils.isNotEmpty(insertList)) {
                this.saveBatch(insertList);
            }
        }
    }

    private boolean check(ConventionalPerformanceImportModel bo, List<String> errorList, Map<String, StudentEntity> studentNumberMap, SchoolLanguageEnum languageEnum) {
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
        //事件日期检查
        if (StringUtils.isNotBlank(bo.getDate())) {
            try {
                if (StringUtils.isNumeric(bo.getDate())) {
                    //execl日期格式解析为全数字，如：43444
                    DateUtil.getJavaDate(Double.parseDouble(bo.getDate()));
                } else if (bo.getDate().contains("/")) {
                    //字符串格式日期，如2024/12/18
                    DateUtils.formatStringToDate(bo.getDate(), "yyyy/MM/dd");
                } else {
                    //字符串格式日期，如2024-12-18
                    DateUtils.formatStringToDate(bo.getDate(), "yyyy-MM-dd");
                }
            } catch (Exception e) {
                errorList.add(languageUtil.getMessage(LanguageConstants.DATE_FORMAT_ERROR_YMD));
            }
        } else {
            errorList.add(languageUtil.getMessage(LanguageConstants.DATE_REQUIRED));
        }
        //欠作业检查
        if (!StringUtils.isNotBlank(bo.getMissingHomework())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.CONVENTIONAL_PERFORMANCE_MISSING_HOMEWORK_REQUIRED));
        } else {
            // 检查次数是否为数字
            if (!StringUtils.isNumeric(bo.getMissingHomework())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.FORMAT_ERROR_ONLY_ALLOW_NUMERIC_INPUT));
            }
            // 檢查次數等於0時，備註不可填寫
            if (Integer.parseInt(bo.getMissingHomework()) == 0 &&
                    StringUtils.isNotBlank(bo.getMissingHomeworkRemark())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.CONVENTIONAL_PERFORMANCE_MISSING_HOMEWORK_REMARK_CANNOT_BE_USED));
            }
        }
        //欠课本检查
        if (!StringUtils.isNotBlank(bo.getMissingTextbook())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.CONVENTIONAL_PERFORMANCE_MISSING_TEXTBOOK_REQUIRED));
        } else {
            // 检查次数是否为数字
            if (!StringUtils.isNumeric(bo.getMissingTextbook())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.FORMAT_ERROR_ONLY_ALLOW_NUMERIC_INPUT));
            }
            // 檢查次數等於0時，備註不可填寫
            if (Integer.parseInt(bo.getMissingTextbook()) == 0 &&
                    StringUtils.isNotBlank(bo.getMissingTextbookRemark())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.CONVENTIONAL_PERFORMANCE_MISSING_TEXTBOOK_REMARK_CANNOT_BE_USED));
            }
        }
        //上课违规检查
        if (!StringUtils.isNotBlank(bo.getClassViolation())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.CONVENTIONAL_PERFORMANCE_CLASS_VIOLATION_REQUIRED));
        } else {
            // 检查次数是否为数字
            if (!StringUtils.isNumeric(bo.getClassViolation())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.FORMAT_ERROR_ONLY_ALLOW_NUMERIC_INPUT));
            }
            // 檢查次數等於0時，備註不可填寫
            if (Integer.parseInt(bo.getClassViolation()) == 0 &&
                    StringUtils.isNotBlank(bo.getClassViolationRemark())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.CONVENTIONAL_PERFORMANCE_CLASS_VIOLATION_REMARK_CANNOT_BE_USED));
            }
        }
        //仪表不符检查
        if (!StringUtils.isNotBlank(bo.getUniformNonCompliance())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.CONVENTIONAL_PERFORMANCE_UNIFORM_NON_COMPLIANCE_REQUIRED));
        } else {
            // 检查次数是否为数字
            if (!StringUtils.isNumeric(bo.getUniformNonCompliance())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.FORMAT_ERROR_ONLY_ALLOW_NUMERIC_INPUT));
            }
            // 檢查次數等於0時，備註不可填寫
            if (Integer.parseInt(bo.getUniformNonCompliance()) == 0 &&
                    StringUtils.isNotBlank(bo.getUniformNonComplianceRemark())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.CONVENTIONAL_PERFORMANCE_UNIFORM_NON_COMPLIANCE_REMARK_CANNOT_BE_USED));
            }
        }
        //欠回条检查
        if (!StringUtils.isNotBlank(bo.getMissingReturnSticker())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.CONVENTIONAL_PERFORMANCE_MISSING_RETURN_STICKER_REQUIRED));
        } else {
            // 检查次数是否为数字
            if (!StringUtils.isNumeric(bo.getMissingReturnSticker())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.FORMAT_ERROR_ONLY_ALLOW_NUMERIC_INPUT));
            }
            // 檢查次數等於0時，備註不可填寫
            if (Integer.parseInt(bo.getMissingReturnSticker()) == 0 &&
                    StringUtils.isNotBlank(bo.getMissingReturnStickerRemark())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.CONVENTIONAL_PERFORMANCE_MISSING_RETURN_STICKER_REMARK_CANNOT_BE_USED));
            }
        }
        return !CollectionUtils.isNotEmpty(errorList);
    }

    private int processBatchExcelLine(List<ImportRecordSaveDTO> importErrorDTOS, List<ConventionalPerformanceImportModel> list, Long schoolId, Long classId, List<ConventionalPerformanceImportDTO> correctList, SchoolLanguageEnum languageEnum) {
        if (CollectionUtils.isNotEmpty(list)) {
            int correctCount = list.size();//正确处理的条数
            Map<String, StudentEntity> studentNumberMap = new HashMap<>();
            Set<String> studentCodes = list.stream().map(ConventionalPerformanceImportModel::getStudentCode).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(studentCodes)) {
                QueryWrapper<StudentEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(StudentEntity::getSchoolId, schoolId)
                        .eq(StudentEntity::getClassId, classId)
                        .in(StudentEntity::getStudentNo, studentCodes);
                List<StudentEntity> studentEntities = studentService.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(studentEntities)) {
                    studentNumberMap = studentEntities.stream().collect(Collectors.toMap(StudentEntity::getStudentNo, student -> student, (key1, key2) -> key1));
                }
            }
            //遍历要插入的每一行
            for (ConventionalPerformanceImportModel bo : list) {
                List<String> errorList = new ArrayList<>();
                if (!check(bo, errorList, studentNumberMap, languageEnum)) {
                    //不合法
                    correctCount--;
                    if (CollectionUtils.isNotEmpty(errorList)) {
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

    private ConventionalPerformanceImportDTO importConvert(ConventionalPerformanceImportModel bo, Map<String, StudentEntity> studentNumberMap, SchoolLanguageEnum languageEnum) {
        ConventionalPerformanceImportDTO result = new ConventionalPerformanceImportDTO();
        StudentEntity studentEntity = studentNumberMap.get(bo.getStudentCode());
        if (studentEntity != null) {
            result.setStudentId(studentEntity.getId());
        }
        result.setStudentName(bo.getStudentName());
        result.setStudentCode(bo.getStudentCode());
        Date date;
        if (StringUtils.isNumeric(bo.getDate())) {
            //execl日期格式解析为全数字，如：43444
            date = DateUtil.getJavaDate(Double.parseDouble(bo.getDate()));
        } else if (bo.getDate().contains("/")) {
            //字符串格式日期，如2024/12/18
            date = DateUtils.formatStringToDate(bo.getDate(), "yyyy/MM/dd");
        } else {
            //字符串格式日期，如2024-12-18
            date = DateUtils.formatStringToDate(bo.getDate(), "yyyy-MM-dd");
        }
        result.setDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        result.setMissingHomework(Integer.parseInt(bo.getMissingHomework()));
        result.setMissingTextbook(Integer.parseInt(bo.getMissingTextbook()));
        result.setClassViolation(Integer.parseInt(bo.getClassViolation()));
        result.setUniformNonCompliance(Integer.parseInt(bo.getUniformNonCompliance()));
        result.setMissingReturnSticker(Integer.parseInt(bo.getMissingReturnSticker()));
        result.setMissingHomeworkRemark(bo.getMissingHomeworkRemark());
        result.setMissingTextbookRemark(bo.getMissingTextbookRemark());
        result.setClassViolationRemark(bo.getClassViolationRemark());
        result.setUniformNonComplianceRemark(bo.getUniformNonComplianceRemark());
        result.setMissingReturnStickerRemark(bo.getMissingReturnStickerRemark());
        return result;
    }

    private void handleImportData(ImportTaskEntity task, List<ConventionalPerformanceImportModel> list, Long schoolId, Long userId, String sid, Long term, Long classId, SchoolLanguageEnum languageEnum) {
        task.setTotalCount(list.size());
        task.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
        importTaskService.updateById(task);
        log.info("开始处理数据导入...");
        Iterator<ConventionalPerformanceImportModel> iterator = list.iterator();
        //每500个处理一次
        List<ConventionalPerformanceImportModel> batchExcelLine = new ArrayList<>(500);
        int correctCount = 0;
        List<ImportRecordSaveDTO> importRecordSaveDTOS = new ArrayList<>();
        List<ConventionalPerformanceImportDTO> correctList = new ArrayList<>();
        while (iterator.hasNext()) {
            ConventionalPerformanceImportModel importModel = iterator.next();
            batchExcelLine.add(importModel);
            if (batchExcelLine.size() >= 500) {
                //处理数据 插入数据库
                correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, classId, correctList, languageEnum);
                batchExcelLine.clear();
            }
        }
        if (!batchExcelLine.isEmpty()) {
            correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, classId, correctList, languageEnum);
            batchExcelLine.clear();
        }
        if (CollectionUtils.isNotEmpty(correctList)) {
            //将正确的记录处理后导入数据库
            handleImportData(correctList, schoolId, userId, sid, term, classId);
        }
        //当前处理进度写入数据库
        task.setSuccessCount(correctCount);
        task.setFailCount(list.size() - correctCount);
        task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
        importTaskService.updateById(task);
        //错误信息写入数据库
        if (CollectionUtils.isNotEmpty(importRecordSaveDTOS)) {
            List<ImportRecordEntity> entityList = importRecordSaveDTOS.stream().map(dto -> {
                ImportRecordEntity importRecordEntity = new ImportRecordEntity();
                BeanUtils.copyProperties(dto, importRecordEntity);
                importRecordEntity.setTaskId(task.getId());
                return importRecordEntity;
            }).collect(Collectors.toList());
            importRecordService.saveBatch(entityList);
        }
    }

    private List<ConventionalPerformanceImportModel> readExcelData(MultipartFile file, SchoolLanguageEnum schoolLanguageEnum) {
        List<ConventionalPerformanceImportModel> result = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            switch (schoolLanguageEnum) {
                case ZH_MO:
                    ConventionalPerformanceImportZhTwListener importZhTwListener = new ConventionalPerformanceImportZhTwListener();
                    EasyExcel.read(inputStream, ConventionalPerformanceImportZhTwModel.class, importZhTwListener).sheet().doReadSync();
                    List<ConventionalPerformanceImportZhTwModel> importZhTwModels = importZhTwListener.getDataList();
                    result = importZhTwModels.stream().map(item -> {
                        ConventionalPerformanceImportModel model = new ConventionalPerformanceImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case EN_US:
                    ConventionalPerformanceImportEnUsListener importEnUsListener = new ConventionalPerformanceImportEnUsListener();
                    EasyExcel.read(inputStream, ConventionalPerformanceImportEnUsModel.class, importEnUsListener).sheet().doReadSync();
                    List<ConventionalPerformanceImportEnUsModel> importEnUsModels = importEnUsListener.getDataList();
                    result = importEnUsModels.stream().map(item -> {
                        ConventionalPerformanceImportModel model = new ConventionalPerformanceImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case PT_PT:
                    ConventionalPerformanceImportPtPtListener importPtPtListener = new ConventionalPerformanceImportPtPtListener();
                    EasyExcel.read(inputStream, ConventionalPerformanceImportPtPtModel.class, importPtPtListener).sheet().doReadSync();
                    List<ConventionalPerformanceImportPtPtModel> importPtPtModels = importPtPtListener.getDataList();
                    result = importPtPtModels.stream().map(item -> {
                        ConventionalPerformanceImportModel model = new ConventionalPerformanceImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
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
    public String export(Long schoolId, ConventionalPerformancePageReqModel reqModel) {
        List<ConventionalPerformancePageResModel> list = this.getBaseMapper().page(schoolId, reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            String fileName = "常规违规登记导出.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();
            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                List<ConventionalPerformanceEnExportModel> exportEnModels = list.stream()
                        .map(resModel -> {
                            ConventionalPerformanceEnExportModel exportModel = new ConventionalPerformanceEnExportModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setClassName(StringUtils.isNotBlank(resModel.getGradeGroupName()) && StringUtils.isNotBlank(resModel.getClassName()) ?
                                    resModel.getGradeGroupName() + resModel.getClassName() : "");
                            exportModel.setDate(resModel.getDate().toString());
                            exportModel.setCreateTime(resModel.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            exportModel.setType(ConventionalPerformanceTypeEnum.getValue(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, ConventionalPerformanceEnExportModel.class, FileTypeEnum.EXPORT, schoolId);
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                List<ConventionalPerformancePtExportModel> exportPtModels = list.stream()
                        .map(resModel -> {
                            ConventionalPerformancePtExportModel exportModel = new ConventionalPerformancePtExportModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setClassName(StringUtils.isNotBlank(resModel.getGradeGroupName()) && StringUtils.isNotBlank(resModel.getClassName()) ?
                                    resModel.getGradeGroupName() + resModel.getClassName() : "");
                            exportModel.setDate(resModel.getDate().toString());
                            exportModel.setCreateTime(resModel.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            exportModel.setType(ConventionalPerformanceTypeEnum.getValue(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, StudentAttendanceExportPtModel.class, FileTypeEnum.EXPORT, schoolId);
            } else {
                List<ConventionalPerformanceExportModel> exportModels = list.stream()
                        .map(resModel -> {
                            ConventionalPerformanceExportModel exportModel = new ConventionalPerformanceExportModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setClassName(StringUtils.isNotBlank(resModel.getGradeGroupName()) && StringUtils.isNotBlank(resModel.getClassName()) ?
                                    resModel.getGradeGroupName() + resModel.getClassName() : "");
                            exportModel.setDate(resModel.getDate().toString());
                            exportModel.setCreateTime(resModel.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            exportModel.setType(ConventionalPerformanceTypeEnum.getValue(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportModels, fileName, ConventionalPerformanceExportModel.class, FileTypeEnum.EXPORT, schoolId);
            }
        }
        return null;
    }

    @Override
    public List<StudentPerformanceTotalResModel> getTotal(StudentPerformanceTotalReqModel reqModel) {
        List<ConventionalPerformanceEntity> list = this.list(new LambdaQueryWrapper<ConventionalPerformanceEntity>()
                .eq(ConventionalPerformanceEntity::getSchoolId, reqModel.getSchoolId())
                .eq(ConventionalPerformanceEntity::getStudentId, reqModel.getStudentId())
                .eq(ConventionalPerformanceEntity::getSid, reqModel.getSchoolYear())
                .eq(ConventionalPerformanceEntity::getTerm, reqModel.getSemesterId()));
        if (CollectionUtils.isNotEmpty(list)) {
            List<StudentPerformanceTotalResModel> collect = list.stream().collect(Collectors.groupingBy(ConventionalPerformanceEntity::getType, Collectors.summingInt(ConventionalPerformanceEntity::getFrequency)))
                    .entrySet().stream().map(entry -> {
                        StudentPerformanceTotalResModel resModel = new StudentPerformanceTotalResModel();
                        resModel.setType(entry.getKey());
                        resModel.setNum(entry.getValue());
                        return resModel;
                    }).collect(Collectors.toList());
            // 添加不存在的
            Arrays.asList(ConventionalPerformanceTypeEnum.values()).forEach(typeEnum -> {
                boolean b = collect.stream().anyMatch(item -> item.getType() == typeEnum.getCode());
                if (!b) {
                    StudentPerformanceTotalResModel resModel = new StudentPerformanceTotalResModel();
                    resModel.setType(typeEnum.getCode());
                    resModel.setNum(0);
                    collect.add(resModel);
                }
            });
            return collect;
        }
        List<StudentPerformanceTotalResModel> collect = new ArrayList<>();
        Arrays.asList(ConventionalPerformanceTypeEnum.values()).forEach(typeEnum -> {
            StudentPerformanceTotalResModel resModel = new StudentPerformanceTotalResModel();
            resModel.setType(typeEnum.getCode());
            resModel.setNum(0);
            collect.add(resModel);
        });
        return collect;
    }
}