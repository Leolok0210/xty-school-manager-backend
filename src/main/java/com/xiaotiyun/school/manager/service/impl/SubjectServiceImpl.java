package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.xiaotiyun.school.manager.basic.enums.DepartmentEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtils;
import com.xiaotiyun.school.manager.dao.SubjectDao;
import com.xiaotiyun.school.manager.basic.enums.ImportTaskStatusEnum;
import com.xiaotiyun.school.manager.basic.enums.ImportTaskTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.listener.SubjectImportEnUsListener;
import com.xiaotiyun.school.manager.listener.SubjectImportListener;
import com.xiaotiyun.school.manager.listener.SubjectImportPtPtListener;
import com.xiaotiyun.school.manager.model.dto.ImportRecordSaveDTO;
import com.xiaotiyun.school.manager.model.entity.ImportTaskEntity;
import com.xiaotiyun.school.manager.model.entity.SchoolMajor;
import com.xiaotiyun.school.manager.model.entity.Subject;
import com.xiaotiyun.school.manager.model.entity.SubjectRelEntity;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.SubjectQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SubjectDetailResModel;
import com.xiaotiyun.school.manager.model.res.SubjectSimpleResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;

@Slf4j
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectDao, Subject> implements SubjectService {

    @Autowired
    private SubjectDao subjectDao;


    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private ImportRecordService importRecordService;

    @Resource(name = "importExecutor")
    private ThreadPoolTaskExecutor importExecutor;

    @Resource
    private LanguageUtil languageUtil;

    @Resource
    private SchoolMajorService schoolMajorService;
    @Resource
    private SubjectRelService subjectRelService;

    @Override
    public void createSubjects(List<Subject> subjects) {
        //校验subjectNumber不能重复，只要有一个重复就报错
        //使用批量查询
        List<Subject> data = subjectDao.selectList(new LambdaQueryWrapper<Subject>()
                .eq(Subject::getDeleted,0)
                .eq(Subject::getSchoolId,subjects.get(0).getSchoolId())
                .in(Subject::getSubjectNumber, subjects.stream().map(Subject::getSubjectNumber).collect(Collectors.toList())));
        if(!CollectionUtils.isEmpty(data)) {
            throw new BusinessException(LanguageConstants.SUBJECT_NUMBER_EXISTS);
        }

        //同一个学部下的"科目名称"不可重复
        //批量查询所有同名科目，避免N+1
        Set<String> subjectNames = subjects.stream().map(Subject::getSubjectName).collect(Collectors.toSet());
        Map<String, List<Subject>> existingByName = subjectDao.selectList(new LambdaQueryWrapper<Subject>()
                .eq(Subject::getDeleted, 0)
                .eq(Subject::getSchoolId, subjects.get(0).getSchoolId())
                .in(Subject::getSubjectName, subjectNames))
                .stream()
                .collect(Collectors.groupingBy(Subject::getSubjectName));
        for(Subject subject : subjects) {
            List<Subject> sameNameList = existingByName.get(subject.getSubjectName());
            if(!CollectionUtils.isEmpty(sameNameList)) {
                Set<String> currentScope = parseScope(subject.getScope());
                for(Subject exist : sameNameList) {
                    Set<String> existScope = parseScope(exist.getScope());
                    if(!Collections.disjoint(currentScope, existScope)) {
                        throw new BusinessException(LanguageConstants.SUBJECT_NAME_EXISTS);
                    }
                }
            }
        }
        subjects.forEach(subject -> {
            subject.setCreateTime(LocalDateTime.now());
            subject.setUpdateTime(LocalDateTime.now());
            subject.setDeleted(0L);
        });
        saveBatch(subjects);
    }

    private Set<String> parseScope(String scope) {
        List<Integer> array = JSON.parseArray(scope, Integer.class);
        return array.stream().map(String::valueOf).collect(Collectors.toSet());
    }

    @Override
    public void updateSubject(Subject subject) {
        if(subject.getId() == null)
            throw new BusinessException(LanguageConstants.SUBJECT_ID_REQUIRED);
//        Subject subject1 = getById(subject.getId());
        //校验subjectNumber不能重复
        if(subject.getSubjectNumber() != null) {
            List<Subject> data = subjectDao.selectList(new LambdaQueryWrapper<Subject>()
                    .eq(Subject::getDeleted,0)
                    .eq(Subject::getSchoolId,subject.getSchoolId())
                    .ne(Subject::getId,subject.getId())
                    .eq(Subject::getSubjectNumber, subject.getSubjectNumber()));
            if(!CollectionUtils.isEmpty(data))
                throw new BusinessException(LanguageConstants.SUBJECT_NUMBER_EXISTS);
        }
        //同一个学部下的"科目名称"不可重复
        if(subject.getSubjectName() != null) {
            List<Subject> sameNameList = subjectDao.selectList(new LambdaQueryWrapper<Subject>()
                    .eq(Subject::getDeleted,0)
                    .eq(Subject::getSchoolId,subject.getSchoolId())
                    .eq(Subject::getSubjectName, subject.getSubjectName()));
            if(!CollectionUtils.isEmpty(sameNameList)) {
                Set<String> currentScope = parseScope(subject.getScope());
                for(Subject exist : sameNameList) {
                    Set<String> existScope = parseScope(exist.getScope());
                    if(!Collections.disjoint(currentScope, existScope)) {
                        throw new BusinessException(LanguageConstants.SUBJECT_NAME_EXISTS);
                    }
                }
            }
        }
        subject.setUpdateTime(LocalDateTime.now());
        updateById(subject);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSubject(Long schoolId, Long id) {
        // 校验是否有关联的专业和级组
        long count1 = subjectRelService.count(new LambdaQueryWrapper<SubjectRelEntity>()
                .eq(SubjectRelEntity::getSchoolId,schoolId)
                .eq(SubjectRelEntity::getSubjectId,id));
        if(count1 > 0L) {
            throw new BusinessException(LanguageConstants.SUBJECT_HAS_RELATION);
        }
        List<SchoolMajor> schoolMajors = schoolMajorService.list(new LambdaQueryWrapper<SchoolMajor>()
                .eq(SchoolMajor::getSchoolId,schoolId));
        if(!CollectionUtils.isEmpty(schoolMajors)) {
            for (SchoolMajor schoolMajor : schoolMajors) {
                if (StringUtils.isNotBlank(schoolMajor.getMajorSubjects())) {
                    String[] split = schoolMajor.getMajorSubjects().split(",");
                    //String转int list
                    List<Long> ids = Arrays.stream(split).map(Long::parseLong).collect(Collectors.toList());
                    if (ids.contains(id)) {
                        throw new BusinessException(LanguageConstants.SUBJECT_HAS_RELATION);
                    }
                }
            }
        }
        //逻辑删除
        removeById(id);
    }

    @Override
    public Subject getSubjectById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public PageInfo<SubjectDetailResModel> getSubjectList(SubjectQueryReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(reqModel.getSubjectName()), Subject::getSubjectName, reqModel.getSubjectName())
                .eq(StringUtils.isNotBlank(reqModel.getSubjectNumber()), Subject::getSubjectNumber, reqModel.getSubjectNumber())
                .eq(Subject::getDeleted,0)
                .eq(reqModel.getSchoolId() != null, Subject::getSchoolId, reqModel.getSchoolId());
        wrapper.orderByDesc(Subject::getCreateTime);
        List<Subject> subjects = this.baseMapper.selectList(wrapper);
        PageInfo<Subject> pageInfo = new PageInfo<>(subjects);
        List<SubjectDetailResModel> subjectDetailResModels = pageInfo.getList().stream()
                .map(item ->{
                    SubjectDetailResModel resModel = new SubjectDetailResModel();
                    BeanUtils.copyProperties(item, resModel);
                    return resModel;
                }).collect(Collectors.toList());
        PageInfo<SubjectDetailResModel> subjectDetailResModelPageInfo = new PageInfo<>(subjectDetailResModels);
        subjectDetailResModelPageInfo.setTotal(pageInfo.getTotal());
        subjectDetailResModelPageInfo.setPages(pageInfo.getPages());
        subjectDetailResModelPageInfo.setList(subjectDetailResModels);
        return subjectDetailResModelPageInfo;
    }

    @Override
    public List<SubjectSimpleResModel> getSubjectsBySchoolAndDepartment(Long schoolId, Integer departmentId) {
        // 实现查询逻辑
        List<Subject> subjects = subjectDao.selectList(new LambdaQueryWrapper<Subject>()
                .eq(Subject::getDeleted,0)
                //scope范围 [1,2,3]'
                //查询符合条件的scope
                .apply("JSON_CONTAINS( scope, {0})",  String.valueOf(departmentId))
                .eq(Subject::getSchoolId,schoolId));
        return subjects.stream()
                .map(item ->{
                    SubjectSimpleResModel resModel = new SubjectSimpleResModel();
                    BeanUtils.copyProperties(item, resModel);
                    resModel.setName(item.getSubjectName());
                    return resModel;
                }).collect(Collectors.toList());
    }

    @Override
    public Long importSubject(MultipartFile file, Long schoolId) {
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
        List<SubjectImportModel> list = readExcelData(file, languageEnum);
        // 创建导入任务
        ImportTaskEntity task = new ImportTaskEntity();
        task.setSchoolId(schoolId);
        task.setFileName(file.getOriginalFilename());
        task.setType(ImportTaskTypeEnum.SUBJECT_INFO.getCode());
        task.setTotalCount(0);
        task.setSuccessCount(0);
        task.setFailCount(0);
        importTaskService.save(task);
        CompletableFuture.runAsync(() -> {
            languageUtil.setLanguage(languageEnum.getCode());
            log.info("当前使用的语言是:{}", LanguageUtil.getCurrentLanguage());
            handleSubjectImport(languageEnum,task, list, schoolId);
            LanguageUtil.clearLanguage();
        }, importExecutor).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("导入科目任务执行结束taskId=【{}】异常={}",task.getId(),ex);
            } else {
                log.info("导入科目完成，任务ID={}",task.getId());
            }
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            importTaskService.updateById(task);
        });
        return task.getId();
    }

    @Override
    public List<SubjectDetailResModel> getSubjects(List<Long> ids) {
        return subjectDao.selectBatchIds(ids).stream()
                .map(item ->{
                    SubjectDetailResModel resModel = new SubjectDetailResModel();
                    BeanUtils.copyProperties(item, resModel);
                    return resModel;
                }).collect(Collectors.toList());
    }

    @Override
    public List<SubjectDetailResModel> getSubjects(Long schoolId, String name) {
        return subjectDao.selectList(new LambdaQueryWrapper<Subject>()
                .eq(Subject::getDeleted,0)
                .eq(Subject::getSchoolId,schoolId)
                .like(StringUtils.isNotBlank(name), Subject::getSubjectName, name)).stream()
                .map(item ->{
                    SubjectDetailResModel resModel = new SubjectDetailResModel();
                    BeanUtils.copyProperties(item, resModel);
                    return resModel;
                }).collect(Collectors.toList());
    }

    private void handleSubjectImport( SchoolLanguageEnum languageEnum,ImportTaskEntity task, List<SubjectImportModel> list, Long schoolId) {
        task.setTotalCount(list.size());
        task.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
        importTaskService.updateById(task);
        List<ImportRecordSaveDTO> failureReason = new ArrayList<>();
        int successCount = 0;
        try {
            //获取验证map
            List<Subject> data = subjectDao.selectList(new LambdaQueryWrapper<Subject>()
                    .eq(Subject::getDeleted,0)
                    .eq(Subject::getSchoolId,schoolId)
                    .in(Subject::getSubjectName, list.stream().map(SubjectImportModel::getSubjectName).collect(Collectors.toList())));
            List<Subject> numberData = subjectDao.selectList(new LambdaQueryWrapper<Subject>()
                    .eq(Subject::getDeleted,0)
                    .eq(Subject::getSchoolId,schoolId)
                    .in(Subject::getSubjectNumber, list.stream().map(SubjectImportModel::getSubjectNumber).collect(Collectors.toList())));
            //根据编号和名称分组
            Map<String, List<Subject>> numberMap = numberData.stream().collect(Collectors.groupingBy(Subject::getSubjectNumber));
            Map<String, List<Subject>> nameMap = data.stream().collect(Collectors.groupingBy(Subject::getSubjectName));
            List<Subject> subjects = new ArrayList<>();
            for (SubjectImportModel subjectImportModel : list) {
                // 处理导入的数据
                // 数据校验
                if (!validateSubject(languageEnum,subjectImportModel, failureReason, task.getId(),numberMap, nameMap)) {
                    continue;
                }
                Subject subject = new Subject();
                subject.setSubjectName(subjectImportModel.getSubjectName());
                subject.setSubjectNumber(subjectImportModel.getSubjectNumber());
                subject.setSubjectEnglishName(subjectImportModel.getSubjectEnglishName());
                if (subjectImportModel.getUnit() != null)
                    subject.setUnit(Integer.parseInt(subjectImportModel.getUnit()));
                subject.setSchoolId(schoolId);
                subject.setDeleted(0L);
                subject.setCreateTime(LocalDateTime.now());
                subject.setUpdateTime(LocalDateTime.now());
                String[] split = subjectImportModel.getScope().split(",");
                List<Integer> scope = new ArrayList<>();
                for (String s : split) {
                    DepartmentEnum departmentEnum = LanguageUtils.getDepartmentEnum(languageEnum, s);
                    if (departmentEnum != null) {
                        scope.add(departmentEnum.getCode());
                    }
                }
                subject.setScope(JSON.toJSONString(scope));
                subjects.add(subject);
                successCount++;
            }
            saveBatch(subjects);
            //保存错误信息
            if(!CollectionUtils.isEmpty(failureReason)){
                importRecordService.save(failureReason);
            }
        }finally {
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            task.setFailCount(list.size() - successCount);
            task.setSuccessCount(successCount);
            importTaskService.updateById(task);
        }

    }

    private boolean validateSubject(SchoolLanguageEnum languageEnum, SubjectImportModel subjectImportModel, List<ImportRecordSaveDTO> failureReasons, Long taskId,
                                Map<String, List<Subject>> numberMap, Map<String, List<Subject>> nameMap) {
        boolean isValid = true;

        StringBuilder failureReason = new StringBuilder();
        // 科目编号校验
        String subjectCode = subjectImportModel.getSubjectNumber();
        if (subjectCode == null || subjectCode.isEmpty()) {
            failureReason.append(languageUtil.getMessage(LanguageConstants.SUBJECT_NUMBER_REQUIRED));
            isValid = false;
        } else if (!subjectCode.matches("[a-zA-Z0-9]+")) {
            failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.SUBJECT_NUMBER_FORMAT_ERROR), subjectCode));
            isValid = false;
        } else {
            if (numberMap.containsKey(subjectCode)) {
                failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.SUBJECT_NUMBER_EXISTS_ERROR), subjectCode));
                isValid = false;
            }
            numberMap.put(subjectCode, new ArrayList<>());
        }

        // 科目名称校验
        String subjectName = subjectImportModel.getSubjectName();
        if (subjectName == null || subjectName.isEmpty()) {
            failureReason.append(languageUtil.getMessage(LanguageConstants.SUBJECT_NAME_REQUIRED));
            isValid = false;
        } else {
            if (nameMap.containsKey(subjectName)) {
                List<Subject> subjects = nameMap.get(subjectName);
                Set<String> currentScope = parseScope(subjectImportModel.getScope());
                for(Subject exist : subjects) {
                    Set<String> existScope = parseScope(exist.getScope());
                    if(!Collections.disjoint(currentScope, existScope)) {
                        failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.SUBJECT_NAME_EXISTS_ERROR), subjectName));
                        isValid = false;
                        break;
                    }
                }
            }else {
                Subject subject = new Subject();
                subject.setSubjectName(subjectName);
                subject.setScope(subjectImportModel.getScope());
                nameMap.put(subjectName, Lists.newArrayList(subject));
            }
        }

        // 所属学部校验
        if(StringUtils.isBlank(subjectImportModel.getScope()))
        {
            failureReason.append(languageUtil.getMessage(LanguageConstants.DEPARTMENT_REQUIRED));
            isValid = false;
        }else{
            String[] department = subjectImportModel.getScope().split(",");
            //转desc list
            List<String> departmentList = LanguageUtils.getAllDepartmentDesc(languageEnum);
            if (department == null || department.length == 0) {
                failureReason.append(languageUtil.getMessage(LanguageConstants.DEPARTMENT_REQUIRED));
                isValid = false;
            } else {
                for (String dept : department) {
                    if (!departmentList.contains(dept)) {
                        failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.DEPARTMENT_FORMAT_ERROR), dept));
                        isValid = false;
                    }
                }
            }
        }


        //校验unit
        try {
            Integer.parseInt(subjectImportModel.getUnit());
        }catch (Exception e)
        {
            failureReason.append(languageUtil.getMessage(LanguageConstants.SUBJECT_UNIT_FORMAT_ERROR));
            isValid = false;
        }

        if (!isValid) {
            ImportRecordSaveDTO failureReasonDTO = new ImportRecordSaveDTO();
            failureReasonDTO.setTaskId(taskId);
            failureReasonDTO.setIncorrectLineno(String.valueOf(subjectImportModel.getRowIndex()));
            failureReasonDTO.setIncorrectReason(failureReason.toString());
            failureReasons.add(failureReasonDTO);
        }

        return isValid;
    }

    private List<SubjectImportModel> readExcelData(MultipartFile file, SchoolLanguageEnum schoolLanguageEnum) {
        List<SubjectImportModel> result = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            switch (schoolLanguageEnum) {
                case ZH_MO:
                    SubjectImportListener importZhCnListener = new SubjectImportListener();
                    EasyExcel.read(inputStream, SubjectImportZhModel.class, importZhCnListener).sheet().doRead();
                    List<SubjectImportZhModel> importZhCnModels = importZhCnListener.getDataList();
                    result = importZhCnModels.stream().map(item -> {
                        SubjectImportModel model = new SubjectImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case EN_US:
                    SubjectImportEnUsListener importEnUsListener = new SubjectImportEnUsListener();
                    EasyExcel.read(inputStream, SubjectImportEnUsModel.class, importEnUsListener).sheet().doRead();
                    List<SubjectImportEnUsModel> importEnUsModels = importEnUsListener.getDataList();
                    result = importEnUsModels.stream().map(item -> {
                        SubjectImportModel model = new SubjectImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case PT_PT:
                    SubjectImportPtPtListener importPtPtListener = new SubjectImportPtPtListener();
                    EasyExcel.read(inputStream, SubjectImportPtPtModel.class, importPtPtListener).sheet().doRead();
                    List<SubjectImportPtPtModel> importPtPtModels = importPtPtListener.getDataList();
                    result = importPtPtModels.stream().map(item -> {
                        SubjectImportModel model = new SubjectImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                default:
            }
        } catch (IOException e) {
            log.error("Excel文件读取失败", e);
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.FILE_READ_ERROR));
        }
        return result;
    }
}