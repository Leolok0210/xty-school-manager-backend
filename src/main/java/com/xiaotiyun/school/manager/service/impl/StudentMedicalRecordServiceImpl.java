package com.xiaotiyun.school.manager.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ImportTaskStatusEnum;
import com.xiaotiyun.school.manager.basic.enums.ImportTaskTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtils;
import com.xiaotiyun.school.manager.dao.StudentMedicalRecordDao;
import com.xiaotiyun.school.manager.listener.StudentMedicalRecordImportEnListener;
import com.xiaotiyun.school.manager.listener.StudentMedicalRecordImportPtListener;
import com.xiaotiyun.school.manager.listener.StudentMedicalRecordImportZhListener;
import com.xiaotiyun.school.manager.model.dto.ImportRecordSaveDTO;
import com.xiaotiyun.school.manager.model.entity.ImportRecordEntity;
import com.xiaotiyun.school.manager.model.entity.ImportTaskEntity;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.entity.StudentMedicalRecordEntity;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.MedicalRecordReqModel;
import com.xiaotiyun.school.manager.model.req.StudentMedicalRecordAddReqModel;
import com.xiaotiyun.school.manager.model.req.StudentMedicalRecordUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.MedicalRecordResModel;
import com.xiaotiyun.school.manager.service.ImportRecordService;
import com.xiaotiyun.school.manager.service.ImportTaskService;
import com.xiaotiyun.school.manager.service.StudentMedicalRecordService;
import com.xiaotiyun.school.manager.service.StudentService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 学生医护保健记录服务实现类
 */
@Service
public class StudentMedicalRecordServiceImpl extends ServiceImpl<StudentMedicalRecordDao, StudentMedicalRecordEntity> implements StudentMedicalRecordService {

    @Autowired
    private ImportRecordService importRecordService;
    @Autowired
    private ImportTaskService importTaskService;
    @Autowired
    private StudentService studentService;
    @Resource(name = "importExecutor")
    private ThreadPoolTaskExecutor importExecutor;

    private final LanguageUtil languageUtil;

    @Autowired
    public StudentMedicalRecordServiceImpl(LanguageUtil languageUtil) {
        this.languageUtil = languageUtil;
    }

    /**
     * 根据请求参数查询学生医护保健记录列表
     *
     * @param reqModel 请求参数对象
     * @return 包含学生医护保健记录列表的结果对象
     */
    @Override
    public Result<PageInfo<MedicalRecordResModel>> listStudentMedicalRecords(MedicalRecordReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<MedicalRecordResModel> list = this.getBaseMapper().page(reqModel);
        getChiefComplaintByLanguage(list);
        return Result.success(new PageInfo<>(list));
    }

    /**
     * 添加新的学生医护保健记录
     *
     * @param addEntity 学生医护保健记录实体对象
     * @param schoolId 学校ID
     * @return 操作结果对象
     */
    @Override
    public Result<String> addStudentMedicalRecord(StudentMedicalRecordAddReqModel addEntity, Long schoolId) {
        StudentMedicalRecordEntity entity = new StudentMedicalRecordEntity();
        BeanUtils.copyProperties(addEntity, entity);
        entity.setSchoolId(schoolId);
        entity.setDeleted(0L);
        save(entity);
        return Result.success();
    }

    /**
     * 更新学生医护保健记录
     *
     * @param updateEntity 学生医护保健记录实体对象
     * @return 操作结果对象
     */
    @Override
    public Result<String> updateStudentMedicalRecord(StudentMedicalRecordUpdateReqModel updateEntity) {
        StudentMedicalRecordEntity entity = new StudentMedicalRecordEntity();
        BeanUtils.copyProperties(updateEntity, entity);
        updateById(entity);
        return Result.success();
    }

    /**
     * 删除指定ID的学生医护保健记录
     *
     * @param id 学生医护保健记录ID
     * @return 操作结果对象
     */
    @Override
    public Result<String> deleteStudentMedicalRecord(Long id) {
        removeById(id);
        return Result.success();
    }

    @Override
    public ResponseEntity<byte[]> exportMedicalRecords(MedicalRecordReqModel reqModel) throws UnsupportedEncodingException {
        List<MedicalRecordResModel> resModels = this.getBaseMapper().page(reqModel);
        getChiefComplaintByLanguage(resModels);
        // 设置导出相应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        // 根据语言，导出不同的语言文档
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
            List<MedicalRecordExportEnModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        MedicalRecordExportEnModel exportModel = new MedicalRecordExportEnModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        exportModel.setGenderStr(resModel.getGender() == 1 ? "Male" : "Female");
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, MedicalRecordExportEnModel.class)
                    .sheet("Student Health Care Records")
                    .doWrite(exportEnModels);
            headers.setContentDispositionFormData("attachment", "Student Health Care Records_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } else if (currentLanguage.equals(SchoolLanguageEnum.ZH_MO.getCode())) {
            List<MedicalRecordExportModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        MedicalRecordExportModel exportModel = new MedicalRecordExportModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        exportModel.setGenderStr(resModel.getGender() == 1 ? "男" : "女");
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, MedicalRecordExportModel.class)
                    .sheet("学生医护保健记录")
                    .doWrite(exportEnModels);
            String encodedFileName = URLEncoder.encode("学生医护保健记录_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx", "UTF-8");
            headers.setContentDispositionFormData("attachment", encodedFileName);
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
            List<MedicalRecordExportPtModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        MedicalRecordExportPtModel exportModel = new MedicalRecordExportPtModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        exportModel.setGenderStr(resModel.getGender() == 1 ? "Masculino" : "Feminino");
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, MedicalRecordExportPtModel.class)
                    .sheet("Student care registros de saúde")
                    .doWrite(exportEnModels);
            headers.setContentDispositionFormData("attachment", "Student care registros de saúde_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        }
        return null;
    }

    private void getChiefComplaintByLanguage(List<MedicalRecordResModel> resModels) {
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        // 获取语言
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        // 国际化主诉表现
        resModels.forEach(resModel -> {
            if (ObjectUtils.isNotEmpty(resModel.getChiefComplaint())) {
                resModel.setChiefComplaintAll(LanguageUtils.getChiefComplaint(languageEnum, resModel) + "、其他：" + resModel.getChiefComplaint());
            } else {
                resModel.setChiefComplaintAll(LanguageUtils.getChiefComplaint(languageEnum, resModel));
            }
        });
    }

    /**
     * 医护保健记录批量导入方法
     *
     * @param file       导入的文件
     * @param schoolId   学校ID
     * @param schoolYear 学校年份
     * @return 导入任务ID
     */
    @Override
    public Long importMedicalRecords(MultipartFile file, Long schoolId, String schoolYear) {
        if (schoolId == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.NO_SCHOOL_FILE_CONTENT_EMPTY));
        }
        // 获取学校语言设置信息
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        // 读取Excel文件
        List<StudentMedicalRecordImportModel> list = readExcelData(file, languageEnum);
        if (CollectionUtils.isNotEmpty(list)) {
            // 创建导入任务
            ImportTaskEntity task = new ImportTaskEntity();
            task.setSchoolId(schoolId);
            task.setFileName(file.getOriginalFilename());
            task.setType(ImportTaskTypeEnum.STUDENT_MEDICAL_RECORD.getCode());
            task.setTotalCount(0);
            task.setSuccessCount(0);
            task.setFailCount(0);
            task.setStatus(ImportTaskStatusEnum.UNTREATED.getCode());
            importTaskService.save(task);

            CompletableFuture.runAsync(() -> {
                languageUtil.setLanguage(languageEnum.getCode());
                handleMedicalRecordImport(task, list, schoolId, schoolYear);
                LanguageUtil.clearLanguage();
            }, importExecutor).exceptionally(ex -> {
                // 打印异常信息
                log.error("异步导入医护保健记录任务发生异常", ex);
                return null;
            })
            .thenRun(LanguageUtil::clearLanguage);
            return task.getId();
        }
        return null;
    }

    private List<StudentMedicalRecordImportModel> readExcelData(MultipartFile file, SchoolLanguageEnum schoolLanguageEnum) {
        List<StudentMedicalRecordImportModel> result = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            switch (schoolLanguageEnum) {
                case ZH_MO:
                    StudentMedicalRecordImportZhListener importZhTwListener = new StudentMedicalRecordImportZhListener();
                    EasyExcel.read(inputStream, StudentMedicalRecordImportZhModel.class, importZhTwListener).sheet().headRowNumber(2).doReadSync();
                    List<StudentMedicalRecordImportZhModel> importZhTwModels = importZhTwListener.getDataList();
                    result = importZhTwModels.stream().map(item -> {
                        StudentMedicalRecordImportModel model = new StudentMedicalRecordImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case EN_US:
                    StudentMedicalRecordImportEnListener importEnUsListener = new StudentMedicalRecordImportEnListener();
                    EasyExcel.read(inputStream, StudentMedicalRecordImportEnModel.class, importEnUsListener).sheet().headRowNumber(2).doReadSync();
                    List<StudentMedicalRecordImportEnModel> importEnUsModels = importEnUsListener.getDataList();
                    result = importEnUsModels.stream().map(item -> {
                        StudentMedicalRecordImportModel model = new StudentMedicalRecordImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case PT_PT:
                    StudentMedicalRecordImportPtListener importPtListener = new StudentMedicalRecordImportPtListener();
                    EasyExcel.read(inputStream, StudentMedicalRecordImportPtModel.class, importPtListener).sheet().headRowNumber(2).doReadSync();
                    List<StudentMedicalRecordImportPtModel> importPtModels = importPtListener.getDataList();
                    result = importPtModels.stream().map(item -> {
                        StudentMedicalRecordImportModel model = new StudentMedicalRecordImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
            }
        } catch (IOException e) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.FILE_READ_ERROR));
        }
        return result;
    }

    private void handleMedicalRecordImport(ImportTaskEntity task, List<StudentMedicalRecordImportModel> list, Long schoolId, String schoolYear) {
        if (CollectionUtils.isNotEmpty(list)) {
            task.setTotalCount(list.size());
            task.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
            importTaskService.updateById(task);

            List<ImportRecordSaveDTO> importRecordSaveDTOS = new ArrayList<>();
            int successCount = 0;

            // 获取所有学生信息
            List<String> studentNumList = list.stream().map(StudentMedicalRecordImportModel::getStudentNumber).collect(Collectors.toList());
            List<StudentEntity> studentEntityList = studentService.list(Wrappers.<StudentEntity>lambdaQuery()
                    .eq(StudentEntity::getSchoolId, schoolId)
                    .in(StudentEntity::getStudentNo, studentNumList));
            Map<String, StudentEntity> studentEntityMap = studentEntityList.stream().collect(Collectors.toMap(StudentEntity::getStudentNo, a -> a));

            for (StudentMedicalRecordImportModel importModel : list) {
                if (!checkMedicalRecordImport(importModel, importRecordSaveDTOS, task.getId(), studentEntityMap)) {
                    continue;
                }
                try {
                    List<String> yesEnum = Arrays.asList("是","Yes","Sim");
                    StudentMedicalRecordEntity entity = new StudentMedicalRecordEntity();
                    BeanUtils.copyProperties(importModel, entity);
                    entity.setConsultationDate(importDateByModel(importModel.getConsultationDate(), importModel.getConsultationTime()));
                    entity.setSchoolId(schoolId);
                    entity.setDeleted(0L);
                    entity.setClassId(studentEntityMap.get(importModel.getStudentNumber()).getClassId());
                    entity.setStudentId(studentEntityMap.get(importModel.getStudentNumber()).getId());
                    entity.setFever(yesEnum.contains(importModel.getFever()));
                    entity.setCough(yesEnum.contains(importModel.getCough()));
                    entity.setRunnyNose(yesEnum.contains(importModel.getRunnyNose()));
                    entity.setSoreThroat(yesEnum.contains(importModel.getSoreThroat()));
                    entity.setDizziness(yesEnum.contains(importModel.getDizziness()));
                    entity.setHeadache(yesEnum.contains(importModel.getHeadache()));
                    entity.setNosebleed(yesEnum.contains(importModel.getNosebleed()));
                    entity.setNausea(yesEnum.contains(importModel.getNausea()));
                    entity.setAbdominalPain(yesEnum.contains(importModel.getAbdominalPain()));
                    entity.setVomitingCount(Integer.parseInt(importModel.getVomitingCount()));
                    entity.setDiarrheaCount(Integer.parseInt(importModel.getDiarrheaCount()));
                    entity.setSchoolYear(schoolYear);
                    save(entity);
                    successCount++;
                } catch (Exception e) {
                    ImportRecordSaveDTO dto = new ImportRecordSaveDTO();
                    dto.setTaskId(task.getId());
                    dto.setIncorrectLineno(String.valueOf(importModel.getExcelLineNo()));
                    dto.setIncorrectReason(e.getMessage());
                    importRecordSaveDTOS.add(dto);
                }
            }

            task.setSuccessCount(successCount);
            task.setFailCount(list.size() - successCount);
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            importTaskService.updateById(task);

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
    }

    private String importDateByModel(String consultationDate, String consultationTime) {
        String resultDate = "";
        if (consultationDate.contains("-")){
            resultDate = consultationDate;
        } else if (consultationDate.contains("/")){
            resultDate = DateUtil.parse(consultationDate, "yy/MM/dd").toString("yyyy-MM-dd");
        } else if (consultationDate.contains("年")){
            resultDate = DateUtil.parse(consultationDate, DatePattern.CHINESE_DATE_PATTERN).toString("yyyy-MM-dd");
        }
        if (StringUtils.isNotBlank(consultationTime) && consultationTime.length() == 8){
            resultDate += " " + consultationTime;
        } else if (StringUtils.isNotBlank(consultationTime) && consultationTime.length() == 5){
            resultDate += " " + consultationTime + ":00";
        }
        return resultDate;
    }

    private boolean checkMedicalRecordImport(StudentMedicalRecordImportModel importModel, List<ImportRecordSaveDTO> failureReasons, Long taskId, Map<String, StudentEntity> studentEntityMap) {
        String failReason = "";
        if (StringUtils.isBlank(importModel.getStudentName())) {
            failReason += languageUtil.getMessage(LanguageConstants.STUDENT_NAME_REQUIRED) + ";";
        }
        if (StringUtils.isBlank(importModel.getStudentNumber())) {
            failReason += languageUtil.getMessage(LanguageConstants.STUDENT_NO_REQUIRED) + ";";
        }
        StudentEntity studentEntity = studentEntityMap.get(importModel.getStudentNumber());
        if (studentEntity == null) {
            failReason += languageUtil.getMessage(LanguageConstants.STUDENT_NO_NOT_FOUND) + ";";
        } else if (!studentEntity.getChineseName().equals(importModel.getStudentName())) {
            failReason += languageUtil.getMessage(LanguageConstants.STUDENT_NAME_NOT_MATCH) + ";";
        }
        if (StringUtils.isBlank(importModel.getConsultationDate())) {
            failReason += languageUtil.getMessage(LanguageConstants.DATE_REQUIRED) + ";";
        } else if (importModel.getConsultationDate().contains("-")){
            try {
                DateUtil.parse(importModel.getConsultationDate(), DatePattern.NORM_DATE_PATTERN);
            } catch (Exception e) {
                failReason += languageUtil.getMessage(LanguageConstants.DATE_FORMAT_ERROR) + ";";
            }
        } else if (importModel.getConsultationDate().contains("/")){
            try {
                DateUtil.parse(importModel.getConsultationDate(), "yy/MM/dd");
            } catch (Exception e) {
                failReason += languageUtil.getMessage(LanguageConstants.DATE_FORMAT_ERROR) + ";";
            }
        } else if (importModel.getConsultationDate().contains("年")){
            try {
                DateUtil.parse(importModel.getConsultationDate(), DatePattern.CHINESE_DATE_PATTERN);
            } catch (Exception e) {
                failReason += languageUtil.getMessage(LanguageConstants.DATE_FORMAT_ERROR) + ";";
            }
        }
        if (StringUtils.isBlank(importModel.getConsultationTime())) {
            failReason += languageUtil.getMessage(LanguageConstants.TIME_REQUIRED) + ";";
        } else if (importModel.getConsultationTime().contains(":") &&
                importModel.getConsultationTime().length() == 8){
            try {
                DateUtil.parse(importModel.getConsultationTime(), DatePattern.NORM_TIME_PATTERN);
            } catch (Exception e) {
                failReason += languageUtil.getMessage(LanguageConstants.TIME_FORMAT_ERROR) + ";";
            }
        } else if (importModel.getConsultationTime().contains(":")){
            try {
                DateUtil.parse(importModel.getConsultationTime(), "HH:mm");
            } catch (Exception e) {
                failReason += languageUtil.getMessage(LanguageConstants.TIME_FORMAT_ERROR) + ";";
            }
        }
        if (StringUtils.isBlank(importModel.getTreatment())) {
            failReason += languageUtil.getMessage(LanguageConstants.PROCESS_CONTENT_NOT_EMPTY) + ";";
        }
        if (StringUtils.isBlank(importModel.getNotes())) {
            failReason += languageUtil.getMessage(LanguageConstants.REMARK_CONTENT_NOT_EMPTY) + ";";
        }
        if (StringUtils.isNotBlank(importModel.getTemperature())){
            try{
                Double.valueOf(importModel.getTemperature());
            } catch (NumberFormatException e){
                failReason += languageUtil.getMessage(LanguageConstants.FORMAT_ERROR_ONLY_ALLOW_NUMERIC_INPUT) + ";";
            }
        }
        List<String> yesEnum = Arrays.asList("是","Yes","Sim");
        if (StringUtils.isBlank(importModel.getFever()) && !yesEnum.contains(importModel.getFever())) {
            failReason += languageUtil.getMessage(LanguageConstants.EXISTING_SYMPTOM_PLEASE_INPUT_YES) + ";";
        }
        if (StringUtils.isBlank(importModel.getCough()) && !yesEnum.contains(importModel.getCough())) {
            failReason += languageUtil.getMessage(LanguageConstants.EXISTING_SYMPTOM_PLEASE_INPUT_YES) + ";";
        }
        if (StringUtils.isBlank(importModel.getRunnyNose()) && !yesEnum.contains(importModel.getRunnyNose())) {
            failReason += languageUtil.getMessage(LanguageConstants.EXISTING_SYMPTOM_PLEASE_INPUT_YES) + ";";
        }
        if (StringUtils.isBlank(importModel.getSoreThroat()) && !yesEnum.contains(importModel.getSoreThroat())) {
            failReason += languageUtil.getMessage(LanguageConstants.EXISTING_SYMPTOM_PLEASE_INPUT_YES) + ";";
        }
        if (StringUtils.isBlank(importModel.getDizziness()) && !yesEnum.contains(importModel.getDizziness())) {
            failReason += languageUtil.getMessage(LanguageConstants.EXISTING_SYMPTOM_PLEASE_INPUT_YES) + ";";
        }
        if (StringUtils.isBlank(importModel.getHeadache()) && !yesEnum.contains(importModel.getHeadache())) {
            failReason += languageUtil.getMessage(LanguageConstants.EXISTING_SYMPTOM_PLEASE_INPUT_YES) + ";";
        }
        if (StringUtils.isBlank(importModel.getNosebleed()) && !yesEnum.contains(importModel.getNosebleed())) {
            failReason += languageUtil.getMessage(LanguageConstants.EXISTING_SYMPTOM_PLEASE_INPUT_YES) + ";";
        }
        if (StringUtils.isBlank(importModel.getNausea()) && !yesEnum.contains(importModel.getNausea())) {
            failReason += languageUtil.getMessage(LanguageConstants.EXISTING_SYMPTOM_PLEASE_INPUT_YES) + ";";
        }
        if (StringUtils.isNotBlank(importModel.getVomitingCount())){
            try{
                Integer.valueOf(importModel.getVomitingCount());
            } catch (NumberFormatException e){
                failReason += languageUtil.getMessage(LanguageConstants.FORMAT_ERROR_ONLY_ALLOW_NUMERIC_INPUT) + ";";
            }
        }
        if (StringUtils.isBlank(importModel.getAbdominalPain()) && !yesEnum.contains(importModel.getAbdominalPain())) {
            failReason += languageUtil.getMessage(LanguageConstants.EXISTING_SYMPTOM_PLEASE_INPUT_YES) + ";";
        }
        if (StringUtils.isNotBlank(importModel.getDiarrheaCount())){
            try{
                Integer.valueOf(importModel.getDiarrheaCount());
            } catch (NumberFormatException e){
                failReason += languageUtil.getMessage(LanguageConstants.FORMAT_ERROR_ONLY_ALLOW_NUMERIC_INPUT) + ";";
            }
        }
        if (StringUtils.isNotBlank(failReason)) {
            ImportRecordSaveDTO failureReason = new ImportRecordSaveDTO();
            failureReason.setTaskId(taskId);
            failureReason.setIncorrectLineno(String.valueOf(importModel.getExcelLineNo()));
            failureReason.setIncorrectReason(failReason);
            failureReasons.add(failureReason);
            return false;
        }
        return true;
    }
}
