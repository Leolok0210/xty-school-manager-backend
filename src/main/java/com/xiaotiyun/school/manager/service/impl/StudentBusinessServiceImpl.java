package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.StudentBusinessDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.entity.StudentBusinessEntity;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.model.excel.StudentBusinessExportEnModel;
import com.xiaotiyun.school.manager.model.excel.StudentBusinessExportModel;
import com.xiaotiyun.school.manager.model.excel.StudentBusinessExportPtModel;
import com.xiaotiyun.school.manager.model.req.StudentBusinessPageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentBusinessSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentBusinessPageResModel;
import com.xiaotiyun.school.manager.service.GradeGroupService;
import com.xiaotiyun.school.manager.service.StudentBusinessService;
import com.xiaotiyun.school.manager.service.StudentService;
import com.xiaotiyun.school.manager.service.SysClassService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentBusinessServiceImpl extends ServiceImpl<StudentBusinessDao, StudentBusinessEntity> implements StudentBusinessService {
    private final StudentService studentService;
    private final SysClassService classService;
    private final GradeGroupService gradeGroupService;
    private final ExportFileHandler exportFileHandler;
    private final UserAuthHelper userAuthHelper;

    @Override
    public PageInfo<StudentBusinessPageResModel> page(Long schoolId, StudentBusinessPageReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), schoolId);
        List<Long> aClassIds = null;
        if (commonUser)
        {
            aClassIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), schoolId);
            if(CollectionUtils.isEmpty(aClassIds))
            {
                return new PageInfo<>();
            }
            reqModel.setClassIds(aClassIds);
        }
        LocalDateTime queryStartTime = reqModel.getStartDate() != null ? reqModel.getStartDate().atStartOfDay() : null;
        LocalDateTime queryEndTime = reqModel.getEndDate() != null ? reqModel.getEndDate().atTime(LocalTime.MAX).withNano(0) : null;
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        QueryWrapper<StudentBusinessEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StudentBusinessEntity::getSchoolId, schoolId)
                .eq(reqModel.getStudentId() != null && reqModel.getStudentId() > 0, StudentBusinessEntity::getStudentId, reqModel.getStudentId())
                .eq(reqModel.getClassId() != null && reqModel.getClassId() > 0, StudentBusinessEntity::getClassId, reqModel.getClassId())
                .eq(StringUtils.isNotBlank(reqModel.getSchoolYear()), StudentBusinessEntity::getSchoolYear, reqModel.getSchoolYear())
                .ge(queryStartTime != null, StudentBusinessEntity::getEndTime, queryStartTime)
                .le(queryEndTime != null, StudentBusinessEntity::getStartTime, queryEndTime)
                .in(reqModel.getClassIds() != null && !reqModel.getClassIds().isEmpty(), StudentBusinessEntity::getClassId, reqModel.getClassIds())
                .eq(StudentBusinessEntity::getDeleted, 0);
        List<StudentBusinessEntity> list = this.list(wrapper.lambda().orderByDesc(StudentBusinessEntity::getCreateTime));
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> studentIds = list.stream().map(StudentBusinessEntity::getStudentId).collect(Collectors.toList());
            Map<Long, StudentEntity> studentMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(studentIds)) {
                List<StudentEntity> studentEntities = studentService.listByIds(studentIds);
                if (CollectionUtils.isNotEmpty(studentEntities)) {
                    studentMap = studentEntities.stream().collect(Collectors.toMap(StudentEntity::getId, studentEntity -> studentEntity));
                }
            }
            List<Long> classIds = list.stream().map(StudentBusinessEntity::getClassId).collect(Collectors.toList());
            Map<Long, SysClass> classMap = new HashMap<>();
            Map<Long, String> gradeNameMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(classIds)) {
                List<SysClass> sysClasses = classService.listByIds(classIds);
                if (CollectionUtils.isNotEmpty(sysClasses)) {
                    classMap = sysClasses.stream().collect(Collectors.toMap(SysClass::getId, sysClass -> sysClass));
                    List<Long> gradeIds = sysClasses.stream().map(SysClass::getGradeGroup).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(gradeIds)) {
                        gradeNameMap = gradeGroupService.getNamesByIds(gradeIds);
                    }
                }
            }
            PageInfo<StudentBusinessEntity> pageInfo = new PageInfo<>(list);
            List<StudentBusinessPageResModel> resList = new ArrayList<>();
            for (StudentBusinessEntity entity : list) {
                StudentBusinessPageResModel resModel = new StudentBusinessPageResModel();
                BeanUtils.copyProperties(entity, resModel);
                StudentEntity student = studentMap.get(entity.getStudentId());
                if (student != null) {
                    resModel.setStudentName(student.getChineseName());
                }
                SysClass sysClass = classMap.get(entity.getClassId());
                if (sysClass != null) {
                    resModel.setClassName(sysClass.getClassName());
                    String gradeName = gradeNameMap.get(sysClass.getGradeGroup());
                    if (StringUtils.isNotBlank(gradeName)) {
                        resModel.setGradeId(sysClass.getGradeGroup());
                        resModel.setGradeName(gradeName);
                    }
                }
                resList.add(resModel);
            }
            PageInfo<StudentBusinessPageResModel> result = new PageInfo<>(resList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            return result;
        }
        return null;
    }

    @Override
    @Transactional
    public void save(Long schoolId, StudentBusinessSaveReqModel reqModel) {
        if (!DateUtils.isTimeValid(reqModel.getStartTime(), reqModel.getEndTime())) {
            throw new BusinessException(LanguageConstants.START_TIME_AFTER_END_TIME);
        }
        checkDuplicate(null, schoolId, reqModel);
        StudentBusinessEntity entity = BeanConvertUtil.convert(reqModel, StudentBusinessEntity.class);
        entity.setSchoolId(schoolId);
        this.save(entity);
    }

    @Override
    @Transactional
    public void update(Long id, StudentBusinessSaveReqModel reqModel) {
        if (!DateUtils.isTimeValid(reqModel.getStartTime(), reqModel.getEndTime())) {
            throw new BusinessException(LanguageConstants.START_TIME_AFTER_END_TIME);
        }
        StudentBusinessEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        checkDuplicate(id, entity.getSchoolId(), reqModel);
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
    }

    /**
     * 校验是否重复
     */
    private void checkDuplicate(Long id, Long schoolId, StudentBusinessSaveReqModel reqModel) {
        QueryWrapper<StudentBusinessEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StudentBusinessEntity::getSchoolId, schoolId)
                .eq(StudentBusinessEntity::getSchoolYear, reqModel.getSchoolYear())
                .eq(StudentBusinessEntity::getClassId, reqModel.getClassId())
                .eq(StudentBusinessEntity::getStudentId, reqModel.getStudentId())
                .le(StudentBusinessEntity::getStartTime, reqModel.getEndTime())
                .ge(StudentBusinessEntity::getEndTime, reqModel.getStartTime())
                .eq(StudentBusinessEntity::getDeleted, 0);
        if (id != null) {
            wrapper.lambda().ne(StudentBusinessEntity::getId, id);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(LanguageConstants.OFFICIAL_RECORD_EXISTS);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        StudentBusinessEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        this.removeById(id);
    }

    @Override
    public String export(Long schoolId, StudentBusinessPageReqModel reqModel) {
        LocalDateTime queryStartTime = reqModel.getStartDate() != null ? reqModel.getStartDate().atStartOfDay() : null;
        LocalDateTime queryEndTime = reqModel.getEndDate() != null ? reqModel.getEndDate().atTime(LocalTime.MAX).withNano(0) : null;
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        QueryWrapper<StudentBusinessEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StudentBusinessEntity::getSchoolId, schoolId)
                .eq(reqModel.getStudentId() != null && reqModel.getStudentId() > 0, StudentBusinessEntity::getStudentId, reqModel.getStudentId())
                .eq(reqModel.getClassId() != null && reqModel.getClassId() > 0, StudentBusinessEntity::getClassId, reqModel.getClassId())
                .eq(StringUtils.isNotBlank(reqModel.getSchoolYear()), StudentBusinessEntity::getSchoolYear, reqModel.getSchoolYear())
                .ge(queryStartTime != null, StudentBusinessEntity::getEndTime, queryStartTime)
                .le(queryEndTime != null, StudentBusinessEntity::getStartTime, queryEndTime)
                .eq(StudentBusinessEntity::getDeleted, 0);
        List<StudentBusinessEntity> list = this.list(wrapper.lambda().orderByDesc(StudentBusinessEntity::getCreateTime));
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> studentIds = list.stream().map(StudentBusinessEntity::getStudentId).collect(Collectors.toList());
            Map<Long, StudentEntity> studentMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(studentIds)) {
                List<StudentEntity> studentEntities = studentService.listByIds(studentIds);
                if (CollectionUtils.isNotEmpty(studentEntities)) {
                    studentMap = studentEntities.stream().collect(Collectors.toMap(StudentEntity::getId, studentEntity -> studentEntity));
                }
            }
            List<Long> classIds = list.stream().map(StudentBusinessEntity::getClassId).collect(Collectors.toList());
            Map<Long, SysClass> classMap = new HashMap<>();
            Map<Long, String> gradeNameMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(classIds)) {
                List<SysClass> sysClasses = classService.listByIds(classIds);
                if (CollectionUtils.isNotEmpty(sysClasses)) {
                    classMap = sysClasses.stream().collect(Collectors.toMap(SysClass::getId, sysClass -> sysClass));
                    List<Long> gradeIds = sysClasses.stream().map(SysClass::getGradeGroup).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(gradeIds)) {
                        gradeNameMap = gradeGroupService.getNamesByIds(gradeIds);
                    }
                }
            }
            List<StudentBusinessPageResModel> resList = new ArrayList<>();
            for (StudentBusinessEntity entity : list) {
                StudentBusinessPageResModel resModel = new StudentBusinessPageResModel();
                BeanUtils.copyProperties(entity, resModel);
                StudentEntity student = studentMap.get(entity.getStudentId());
                if (student != null) {
                    resModel.setStudentName(student.getChineseName());
                }
                SysClass sysClass = classMap.get(entity.getClassId());
                if (sysClass != null) {
                    resModel.setClassName(sysClass.getClassName());
                    String gradeName = gradeNameMap.get(sysClass.getGradeGroup());
                    if (StringUtils.isNotBlank(gradeName)) {
                        resModel.setGradeId(sysClass.getGradeGroup());
                        resModel.setGradeName(gradeName);
                    }
                }
                resList.add(resModel);
            }
            String fileName = "学生公务数据.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();
            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                List<StudentBusinessExportEnModel> exportEnModels = resList.stream()
                        .map(resModel -> {
                            StudentBusinessExportEnModel exportModel = new StudentBusinessExportEnModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setClassName(resModel.getGradeName() + resModel.getClassName());
                            exportModel.setStartTime(resModel.getStartTime().toString());
                            exportModel.setEndTime(resModel.getEndTime().toString());
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, StudentBusinessExportEnModel.class, FileTypeEnum.EXPORT, schoolId);
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                List<StudentBusinessExportPtModel> exportPtModels = resList.stream()
                        .map(resModel -> {
                            StudentBusinessExportPtModel exportModel = new StudentBusinessExportPtModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setClassName(resModel.getGradeName() + resModel.getClassName());
                            exportModel.setStartTime(resModel.getStartTime().toString());
                            exportModel.setEndTime(resModel.getEndTime().toString());
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, StudentBusinessExportPtModel.class, FileTypeEnum.EXPORT, schoolId);
            } else {
                List<StudentBusinessExportModel> exportPtModels = resList.stream()
                        .map(resModel -> {
                            StudentBusinessExportModel exportModel = new StudentBusinessExportModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setClassName(resModel.getGradeName() + resModel.getClassName());
                            exportModel.setStartTime(resModel.getStartTime().toString());
                            exportModel.setEndTime(resModel.getEndTime().toString());
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, StudentBusinessExportModel.class, FileTypeEnum.EXPORT, schoolId);
            }
        }
        return null;
    }
}