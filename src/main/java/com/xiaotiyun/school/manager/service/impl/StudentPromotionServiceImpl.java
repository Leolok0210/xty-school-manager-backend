package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.enums.StudentPromotionTypeEnum;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.StudentPromotionDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.entity.GradeGroup;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.entity.StudentPromotionEntity;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.model.excel.StudentPromotionExportEnModel;
import com.xiaotiyun.school.manager.model.excel.StudentPromotionExportModel;
import com.xiaotiyun.school.manager.model.excel.StudentPromotionExportPtModel;
import com.xiaotiyun.school.manager.model.req.StudentPromotionPageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentPromotionSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentPromotionSaveStudentReqModel;
import com.xiaotiyun.school.manager.model.req.StudentPromotionUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentPromotionResModel;
import com.xiaotiyun.school.manager.service.GradeGroupService;
import com.xiaotiyun.school.manager.service.StudentPromotionService;
import com.xiaotiyun.school.manager.service.StudentService;
import com.xiaotiyun.school.manager.service.SysClassService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentPromotionServiceImpl extends ServiceImpl<StudentPromotionDao, StudentPromotionEntity> implements StudentPromotionService {
    @Resource
    private StudentService studentService;
    @Resource
    private SysClassService sysClassService;
    @Resource
    private GradeGroupService gradeGroupService;
    @Resource
    private ExportFileHandler exportFileHandler;

    @Resource
    private UserAuthHelper userAuthHelper;

    @Override
    public List<Long> studentList(Long schoolId, String schoolYear, Long classId) {
        QueryWrapper<StudentPromotionEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StudentPromotionEntity::getSchoolId, schoolId)
                .eq(StringUtils.isNotBlank(schoolYear), StudentPromotionEntity::getSchoolYear, schoolYear)
                .eq(classId != null && classId > 0, StudentPromotionEntity::getClassId, classId);
        List<StudentPromotionEntity> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().map(StudentPromotionEntity::getStudentId).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public PageInfo<StudentPromotionResModel> page(Long schoolId, StudentPromotionPageReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), schoolId);
        List<Long> aClassIds = null;
        if (commonUser)
        {
            aClassIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), schoolId);
            if (!CollectionUtils.isEmpty(aClassIds))
            {
                PageInfo<StudentPromotionResModel> pageInfo = new PageInfo<>();
                pageInfo.setList(new ArrayList<>());
                return pageInfo;
            }
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        QueryWrapper<StudentPromotionEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StudentPromotionEntity::getSchoolId, schoolId)
                .eq(StringUtils.isNotBlank(reqModel.getSchoolYear()), StudentPromotionEntity::getSchoolYear, reqModel.getSchoolYear())
                .eq(reqModel.getClassId() != null && reqModel.getClassId() > 0, StudentPromotionEntity::getClassId, reqModel.getClassId())
                .eq(reqModel.getStudentId() != null && reqModel.getStudentId() > 0, StudentPromotionEntity::getStudentId, reqModel.getStudentId())
                .in(aClassIds != null && !aClassIds.isEmpty(), StudentPromotionEntity::getClassId, aClassIds)
                .eq(reqModel.getPromotionType() != null && reqModel.getPromotionType() > 0, StudentPromotionEntity::getPromotionType, reqModel.getPromotionType());
        List<StudentPromotionEntity> list = this.list(wrapper.lambda().orderByDesc(StudentPromotionEntity::getId));
        if (CollectionUtils.isNotEmpty(list)) {
            //获取学生信息
            List<Long> studentIds = list.stream().map(StudentPromotionEntity::getStudentId).collect(Collectors.toList());
            Map<Long, StudentEntity> studentMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(studentIds)) {
                List<StudentEntity> students = studentService.listByIds(studentIds);
                if (CollectionUtils.isNotEmpty(students)) {
                    studentMap = students.stream().collect(Collectors.toMap(StudentEntity::getId, studentEntity -> studentEntity));
                }
            }
            //获取班级信息
            Map<Long, SysClass> classMap = new HashMap<>();
            Map<Long, GradeGroup> gradeMap = new HashMap<>();
            List<Long> classIds = list.stream().map(StudentPromotionEntity::getClassId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(classIds)) {
                List<SysClass> sysClasses = sysClassService.listByIds(classIds);
                if (CollectionUtils.isNotEmpty(sysClasses)) {
                    classMap = sysClasses.stream().collect(Collectors.toMap(SysClass::getId, sysClass -> sysClass));
                    List<Long> gradeIds = sysClasses.stream().map(SysClass::getGradeGroup).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(gradeIds)) {
                        List<GradeGroup> gradeGroups = gradeGroupService.listByIds(gradeIds);
                        if (CollectionUtils.isNotEmpty(gradeGroups)) {
                            gradeMap = gradeGroups.stream().collect(Collectors.toMap(GradeGroup::getId, gradeGroup -> gradeGroup));
                        }
                    }
                }
            }
            PageInfo<StudentPromotionEntity> pageInfo = new PageInfo<>(list);
            List<StudentPromotionResModel> resList = new ArrayList<>();
            for (StudentPromotionEntity entity : list) {
                StudentPromotionResModel resModel = new StudentPromotionResModel();
                BeanUtils.copyProperties(entity, resModel);
                StudentEntity student = studentMap.get(entity.getStudentId());
                if (student != null) {
                    resModel.setStudentName(student.getChineseName());
                }
                SysClass sysClass = classMap.get(entity.getClassId());
                if (sysClass != null) {
                    resModel.setClassName(sysClass.getClassName());
                    GradeGroup gradeGroup = gradeMap.get(sysClass.getGradeGroup());
                    if (gradeGroup != null) {
                        resModel.setGradeName(gradeGroup.getGradeGroupName());
                    }
                }
                resList.add(resModel);
            }
            PageInfo<StudentPromotionResModel> result = new PageInfo<>(resList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            return result;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Long schoolId, StudentPromotionSaveReqModel reqModel) {
        if (CollectionUtils.isNotEmpty(reqModel.getStudentInfos())) {
            List<Long> studentIds = reqModel.getStudentInfos().stream().map(StudentPromotionSaveStudentReqModel::getStudentId).collect(Collectors.toList());
            Map<Long, StudentEntity> studentMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(studentIds)) {
                List<StudentEntity> students = studentService.listByIds(studentIds);
                if (CollectionUtils.isNotEmpty(students)) {
                    studentMap = students.stream().collect(Collectors.toMap(StudentEntity::getId, studentEntity -> studentEntity));
                }
            }
            List<StudentPromotionEntity> saveList = new ArrayList<>();
            for (StudentPromotionSaveStudentReqModel studentInfo : reqModel.getStudentInfos()) {
                StudentPromotionEntity entity = new StudentPromotionEntity();
                BeanUtils.copyProperties(studentInfo, entity);
                entity.setSchoolId(schoolId);
                entity.setClassId(reqModel.getClassId());
                entity.setSchoolYear(reqModel.getSchoolYear());
                StudentEntity student = studentMap.get(studentInfo.getStudentId());
                if (student != null) {
                    entity.setSeatNo(student.getSeatNo());
                }
                saveList.add(entity);
            }
            this.saveBatch(saveList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, StudentPromotionUpdateReqModel reqModel) {
        StudentPromotionEntity entity = this.getById(id);
        if (entity != null) {
            BeanUtils.copyProperties(reqModel, entity);
            this.updateById(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        StudentPromotionEntity entity = this.getById(id);
        if (entity != null) {
            this.removeById(id);
        }
    }

    @Override
    public String export(Long schoolId, StudentPromotionPageReqModel reqModel) {
        QueryWrapper<StudentPromotionEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StudentPromotionEntity::getSchoolId, schoolId)
                .eq(StringUtils.isNotBlank(reqModel.getSchoolYear()), StudentPromotionEntity::getSchoolYear, reqModel.getSchoolYear())
                .eq(reqModel.getClassId() != null && reqModel.getClassId() > 0, StudentPromotionEntity::getClassId, reqModel.getClassId())
                .eq(reqModel.getStudentId() != null && reqModel.getStudentId() > 0, StudentPromotionEntity::getStudentId, reqModel.getStudentId())
                .eq(reqModel.getPromotionType() != null && reqModel.getPromotionType() > 0, StudentPromotionEntity::getPromotionType, reqModel.getPromotionType());
        List<StudentPromotionEntity> list = this.list(wrapper.lambda().orderByDesc(StudentPromotionEntity::getId));
        if (CollectionUtils.isNotEmpty(list)) {
            //获取学生信息
            List<Long> studentIds = list.stream().map(StudentPromotionEntity::getStudentId).collect(Collectors.toList());
            Map<Long, StudentEntity> studentMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(studentIds)) {
                List<StudentEntity> students = studentService.listByIds(studentIds);
                if (CollectionUtils.isNotEmpty(students)) {
                    studentMap = students.stream().collect(Collectors.toMap(StudentEntity::getId, studentEntity -> studentEntity));
                }
            }
            //获取班级信息
            Map<Long, SysClass> classMap = new HashMap<>();
            Map<Long, GradeGroup> gradeMap = new HashMap<>();
            List<Long> classIds = list.stream().map(StudentPromotionEntity::getClassId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(classIds)) {
                List<SysClass> sysClasses = sysClassService.listByIds(classIds);
                if (CollectionUtils.isNotEmpty(sysClasses)) {
                    classMap = sysClasses.stream().collect(Collectors.toMap(SysClass::getId, sysClass -> sysClass));
                    List<Long> gradeIds = sysClasses.stream().map(SysClass::getGradeGroup).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(gradeIds)) {
                        List<GradeGroup> gradeGroups = gradeGroupService.listByIds(gradeIds);
                        if (CollectionUtils.isNotEmpty(gradeGroups)) {
                            gradeMap = gradeGroups.stream().collect(Collectors.toMap(GradeGroup::getId, gradeGroup -> gradeGroup));
                        }
                    }
                }
            }
            List<StudentPromotionResModel> resList = new ArrayList<>();
            for (StudentPromotionEntity entity : list) {
                StudentPromotionResModel resModel = new StudentPromotionResModel();
                BeanUtils.copyProperties(entity, resModel);
                StudentEntity student = studentMap.get(entity.getStudentId());
                if (student != null) {
                    resModel.setStudentName(student.getChineseName());
                }
                SysClass sysClass = classMap.get(entity.getClassId());
                if (sysClass != null) {
                    resModel.setClassName(sysClass.getClassName());
                    GradeGroup gradeGroup = gradeMap.get(sysClass.getGradeGroup());
                    if (gradeGroup != null) {
                        resModel.setGradeName(gradeGroup.getGradeGroupName());
                    }
                }
                resList.add(resModel);
            }
            if (CollectionUtils.isNotEmpty(resList)) {
                String fileName = "义工服务信息导出.xlsx";
                String currentLanguage = LanguageUtil.getCurrentLanguage();
                if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                    List<StudentPromotionExportEnModel> exportEnModels = resList.stream()
                            .map(item -> {
                                StudentPromotionExportEnModel resModel = new StudentPromotionExportEnModel();
                                BeanUtils.copyProperties(item, resModel);
                                resModel.setClassName(item.getGradeName() + item.getClassName());
                                resModel.setSeatNo(item.getSeatNo() != null ? String.valueOf(item.getSeatNo()) : "");
                                resModel.setPromotionType(StudentPromotionTypeEnum.getValue(item.getPromotionType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                                return resModel;
                            }).collect(Collectors.toList());
                    return exportFileHandler.doExportExcel(exportEnModels, fileName, StudentPromotionExportEnModel.class, FileTypeEnum.EXPORT, schoolId);
                } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                    List<StudentPromotionExportPtModel> exportPtModels = resList.stream()
                            .map(item -> {
                                StudentPromotionExportPtModel resModel = new StudentPromotionExportPtModel();
                                BeanUtils.copyProperties(item, resModel);
                                resModel.setClassName(item.getGradeName() + item.getClassName());
                                resModel.setSeatNo(item.getSeatNo() != null ? String.valueOf(item.getSeatNo()) : "");
                                resModel.setPromotionType(StudentPromotionTypeEnum.getValue(item.getPromotionType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                                return resModel;
                            }).collect(Collectors.toList());
                    return exportFileHandler.doExportExcel(exportPtModels, fileName, StudentPromotionExportPtModel.class, FileTypeEnum.EXPORT, schoolId);
                } else {
                    List<StudentPromotionExportModel> exportMoModels = resList.stream()
                            .map(item -> {
                                StudentPromotionExportModel resModel = new StudentPromotionExportModel();
                                BeanUtils.copyProperties(item, resModel);
                                resModel.setClassName(item.getGradeName() + item.getClassName());
                                resModel.setSeatNo(item.getSeatNo() != null ? String.valueOf(item.getSeatNo()) : "");
                                resModel.setPromotionType(StudentPromotionTypeEnum.getValue(item.getPromotionType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                                return resModel;
                            }).collect(Collectors.toList());
                    return exportFileHandler.doExportExcel(exportMoModels, fileName, StudentPromotionExportModel.class, FileTypeEnum.EXPORT, schoolId);
                }
            }
        }
        return null;
    }
}