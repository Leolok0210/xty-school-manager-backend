package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.StudentLeaveDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.StudentLeaveExportEnModel;
import com.xiaotiyun.school.manager.model.excel.StudentLeaveExportModel;
import com.xiaotiyun.school.manager.model.excel.StudentLeaveExportPtModel;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentLeaveServiceImpl extends ServiceImpl<StudentLeaveDao, StudentLeaveEntity> implements StudentLeaveService {
    private final ExportFileHandler exportFileHandler;
    private final StudentLeaveCourseService studentLeaveCourseService;
    private final SemesterService semesterService;
    private final SysFileRelevanceService sysFileRelevanceService;
    private final SysFileService sysFileService;
    private final LessonService lessonService;
    private final UserSchoolRelService userSchoolRelService;
    private final UserDeptRelService userDeptRelService;
    private final UserGroupService userGroupService;
    private final UserAuthHelper userAuthHelper;

    @Override
    public PageInfo<StudentLeavePageResModel> page(StudentLeavePageReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if (CollectionUtils.isEmpty(classIds)) {
                PageInfo<StudentLeavePageResModel> pageInfo = new PageInfo<>();
                pageInfo.setList(new ArrayList<>());
                return pageInfo;
            }
            reqModel.setClassIdList(classIds);
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<StudentLeavePageResModel> list = this.getBaseMapper().page(reqModel);
        // 补充关联数据
        List<Long> collect = list.stream().map(StudentLeavePageResModel::getId).collect(Collectors.toList());
        // 补充课节信息
        if (CollectionUtils.isNotEmpty(collect)) {
            List<StudentLeaveCourseEntity> leaveCourseList = studentLeaveCourseService.list(Wrappers.<StudentLeaveCourseEntity>lambdaQuery()
                    .in(StudentLeaveCourseEntity::getLeaveId, collect));
            if (CollectionUtils.isNotEmpty(leaveCourseList)) {
                List<LessonEntity> lessonEntities = lessonService.listByIds(leaveCourseList.stream().map(StudentLeaveCourseEntity::getCourseId).collect(Collectors.toList()));
                Map<Long, LessonEntity> lessonMap = lessonEntities.stream().collect(Collectors.toMap(LessonEntity::getId, Function.identity()));
                List<StudentLeaveCourseResModel> leaveCourseResList = leaveCourseList.stream().map(leave -> {
                    StudentLeaveCourseResModel leaveCourseEntity = new StudentLeaveCourseResModel();
                    BeanUtils.copyProperties(leave, leaveCourseEntity);
                    if (lessonMap.containsKey(leave.getCourseId())) {
                        leaveCourseEntity.setCourseName(lessonMap.get(leave.getCourseId()).getName());
                    }
                    return leaveCourseEntity;
                }).collect(Collectors.toList());
                Map<Long, List<StudentLeaveCourseResModel>> leaveCourseMap = leaveCourseResList.stream().collect(Collectors.groupingBy(StudentLeaveCourseResModel::getLeaveId));
                for (StudentLeavePageResModel model : list) {
                    model.setCourses(leaveCourseMap.get(model.getId()));
                }
            }
            // 补充图片信息
            List<SysFileRelevanceEntity> fileRelevanceList = sysFileRelevanceService.list(Wrappers.<SysFileRelevanceEntity>lambdaQuery()
                    .in(SysFileRelevanceEntity::getBusinessId, collect)
                    .eq(SysFileRelevanceEntity::getType, FileRelevanceTypeEnum.STUDENT_LEAVE.getType()));
            if (ObjectUtils.isNotEmpty(fileRelevanceList)) {
                List<SysFileEntity> sysFileEntities = sysFileService.listByIds(fileRelevanceList.stream().map(SysFileRelevanceEntity::getFileId).collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(sysFileEntities)) {
                    Map<Long, List<SysFileRelevanceEntity>> fileRelMap = fileRelevanceList.stream().collect(Collectors.groupingBy(SysFileRelevanceEntity::getBusinessId));
                    Map<Long, SysFileEntity> fileMap = sysFileEntities.stream().collect(Collectors.toMap(SysFileEntity::getId, Function.identity()));
                    for (StudentLeavePageResModel model : list) {
                        if (fileRelMap.containsKey(model.getId())) {
                            model.setImages(fileRelMap.get(model.getId()).stream().map(SysFileRelevanceEntity::getFileId).map(fileMap::get).map(a -> {
                                if (a != null) {
                                    StudentLeaveImageResModel imageResModel = new StudentLeaveImageResModel();
                                    imageResModel.setId(a.getId());
                                    imageResModel.setPath(a.getPath());
                                    return imageResModel;
                                }
                                return null;
                            }).collect(Collectors.toList()));
                        }
                    }
                }
            }
        }
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<StudentLeavePageResModel> teacherPage(StudentLeavePageReqModel reqModel) {
        // 获取当前用户
        UserEntity userEntity = (UserEntity) StpUtil.getSession().get("userInfo");
        if (userEntity == null) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        // 获取教师信息
        UserSchoolRelEntity userSchoolRelEntity = userSchoolRelService.getOne(Wrappers.<UserSchoolRelEntity>lambdaQuery()
                .eq(UserSchoolRelEntity::getUserId, userEntity.getId())
                .eq(UserSchoolRelEntity::getSchoolId, reqModel.getSchoolId()));
        if (userSchoolRelEntity == null) {
            throw new BusinessException(LanguageConstants.TEACHER_NOT_FOUND);
        }
        // 检查用户是否为学校管理员
        boolean isSchoolAdmin = false;
        if (StringUtils.isNotBlank(userSchoolRelEntity.getUserGroupIds())) {
            String[] userGroupIds = userSchoolRelEntity.getUserGroupIds().split(",");
            if (userGroupIds.length > 0) {
                List<Long> groupId = Arrays.stream(userGroupIds).map(Long::parseLong).collect(Collectors.toList());
                List<UserGroupEntity> userGroup = userGroupService.listByIds(groupId);
                // 判断是否学校管理员
                if (org.apache.commons.lang3.ObjectUtils.isNotEmpty(userGroup)) {
                    if (userGroup.stream().anyMatch(entity -> UserGroupTypeEnum.isSchoolAdmin(entity.getCode()))) {
                        isSchoolAdmin = true;
                    }
                }
            }
        }
        // 用户不是超管，并且不是学校管理员时，需要按照班级权限筛选结果
        if (userEntity.getUserType() != 2 && !isSchoolAdmin) {
            // 获取教师班级权限
            List<Long> classIds = userAuthHelper.getUserClassIds(userEntity.getId(), reqModel.getSchoolId());
            if (CollectionUtils.isEmpty(classIds)) {
                return new PageInfo<>(new ArrayList<>());
            }
            reqModel.setClassIds(classIds);
        }
        return new PageInfo<>(this.getBaseMapper().page(reqModel));
    }

    @Override
    public List<String> getImages(Long id) {
        List<SysFileRelevanceEntity> list = sysFileRelevanceService.list(Wrappers.<SysFileRelevanceEntity>lambdaQuery()
                .eq(SysFileRelevanceEntity::getBusinessId, id)
                .eq(SysFileRelevanceEntity::getType, FileTypeEnum.LEAVE.getType()));
        if (CollectionUtils.isNotEmpty(list)) {
            List<SysFileEntity> sysFileEntities = sysFileService.listByIds(list.stream().map(SysFileRelevanceEntity::getFileId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(sysFileEntities)) {
                return sysFileEntities.stream().map(SysFileEntity::getPath).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Transactional
    @Override
    public void save(StudentLeaveSaveAdminReqModel reqModel) {
        // 获取用户信息
        UserEntity userEntity = (UserEntity) StpUtil.getSession().get("userInfo");
        if (userEntity == null) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        saveStudentLeave(reqModel, userEntity.getId(), userEntity.getUsername(), 0);
    }

    @Transactional
    @Override
    public void save(StudentLeaveSaveReqModel reqModel) {
        // 获取用户信息
        UserEntity userEntity = (UserEntity) StpUtil.getSession().get("userInfo");
        if (userEntity == null) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        saveStudentLeave(reqModel, userEntity.getId(), userEntity.getUsername(), 0);
    }

    @Transactional
    @Override
    public void saveByStudent(StudentLeaveSaveReqModel reqModel) {
        // 获取用户信息
        StudentEntity student = (StudentEntity) StpUtil.getSession().get("student");
        if (student == null) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (currentLanguage.equals(SchoolLanguageEnum.ZH_MO.getCode())) {
            saveStudentLeave(reqModel, student.getId(), student.getChineseName(), 1);
        } else {
            saveStudentLeave(reqModel, student.getId(), student.getEnglishName(), 1);
        }
    }

    @Transactional
    @Override
    public void update(Long id, StudentLeaveUpdateAdminReqModel reqModel) {
        StudentLeaveEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
        // 课节详情记录多增少减
        List<StudentLeaveCourseEntity> oldList = studentLeaveCourseService.list(Wrappers.<StudentLeaveCourseEntity>lambdaQuery()
                .eq(StudentLeaveCourseEntity::getLeaveId, id));
        Map<Long, StudentLeaveCourseEntity> courseIdMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(oldList)) {
            courseIdMap = oldList.stream().collect(Collectors.toMap(StudentLeaveCourseEntity::getCourseId, Function.identity()));
        }
        List<StudentLeaveCourseEntity> saveList = new ArrayList<>();
        List<StudentLeaveCourseSaveReqModel> leaveCourseList = reqModel.getCourses();
        if (CollectionUtils.isNotEmpty(leaveCourseList)) {
            List<StudentLeaveCourseEntity> newList = leaveCourseList.stream().map(course -> {
                StudentLeaveCourseEntity leaveCourseEntity = new StudentLeaveCourseEntity();
                BeanUtils.copyProperties(course, leaveCourseEntity);
                return leaveCourseEntity;
            }).collect(Collectors.toList());
            for (StudentLeaveCourseEntity studentLeaveCourseEntity : newList) {
                if (!courseIdMap.containsKey(studentLeaveCourseEntity.getCourseId())) {
                    studentLeaveCourseEntity.setLeaveId(id);
                    saveList.add(studentLeaveCourseEntity);
                }
                courseIdMap.remove(studentLeaveCourseEntity.getCourseId());
            }
        }
        if (CollectionUtils.isNotEmpty(saveList)) {
            studentLeaveCourseService.saveBatch(saveList);
        }
        // 删除多余的课节详情记录
        if (!courseIdMap.isEmpty()) {
            studentLeaveCourseService.remove(Wrappers.<StudentLeaveCourseEntity>lambdaQuery()
                    .eq(StudentLeaveCourseEntity::getLeaveId, id)
                    .in(StudentLeaveCourseEntity::getCourseId, courseIdMap.keySet()));
        }
        // 图片信息更新
        sysFileRelevanceService.remove(Wrappers.<SysFileRelevanceEntity>lambdaQuery()
                .eq(SysFileRelevanceEntity::getBusinessId, id)
                .eq(SysFileRelevanceEntity::getType, FileTypeEnum.LEAVE.getType()));
        if (CollectionUtils.isNotEmpty(reqModel.getFileIds())) {
            sysFileRelevanceService.saveBatch(reqModel.getFileIds().stream().map(fileId -> {
                SysFileRelevanceEntity fileRelevanceEntity = new SysFileRelevanceEntity();
                fileRelevanceEntity.setFileId(fileId);
                fileRelevanceEntity.setType(FileRelevanceTypeEnum.STUDENT_LEAVE.getType());
                fileRelevanceEntity.setBusinessId(entity.getId());
                fileRelevanceEntity.setSchoolId(reqModel.getSchoolId());
                return fileRelevanceEntity;
            }).collect(Collectors.toList()));
        }
    }

    @Transactional
    @Override
    public void update(Long id, StudentLeaveSaveReqModel reqModel) {
        StudentLeaveEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
        // 课节详情记录多增少减
        List<StudentLeaveCourseEntity> oldList = studentLeaveCourseService.list(Wrappers.<StudentLeaveCourseEntity>lambdaQuery()
                .eq(StudentLeaveCourseEntity::getLeaveId, id));
        Map<Long, StudentLeaveCourseEntity> courseIdMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(oldList)) {
            courseIdMap = oldList.stream().collect(Collectors.toMap(StudentLeaveCourseEntity::getCourseId, Function.identity()));
        }
        List<StudentLeaveCourseSaveReqModel> leaveCourseList = reqModel.getCourses();
        List<StudentLeaveCourseEntity> newList = leaveCourseList.stream().map(course -> {
            StudentLeaveCourseEntity leaveCourseEntity = new StudentLeaveCourseEntity();
            BeanUtils.copyProperties(course, leaveCourseEntity);
            return leaveCourseEntity;
        }).collect(Collectors.toList());
        for (StudentLeaveCourseEntity studentLeaveCourseEntity : newList) {
            if (!courseIdMap.containsKey(studentLeaveCourseEntity.getCourseId())) {
                studentLeaveCourseEntity.setLeaveId(id);
                studentLeaveCourseService.save(studentLeaveCourseEntity);
            }
            courseIdMap.remove(studentLeaveCourseEntity.getCourseId());
        }
        for (Map.Entry<Long, StudentLeaveCourseEntity> entry : courseIdMap.entrySet()) {
            studentLeaveCourseService.removeById(entry.getValue().getId());
        }
        // 图片信息更新
        sysFileRelevanceService.remove(Wrappers.<SysFileRelevanceEntity>lambdaQuery()
                .eq(SysFileRelevanceEntity::getBusinessId, id)
                .eq(SysFileRelevanceEntity::getType, FileTypeEnum.LEAVE.getType()));
        if (CollectionUtils.isNotEmpty(reqModel.getFileIds())) {
            sysFileRelevanceService.saveBatch(reqModel.getFileIds().stream().map(fileId -> {
                SysFileRelevanceEntity fileRelevanceEntity = new SysFileRelevanceEntity();
                fileRelevanceEntity.setFileId(fileId);
                fileRelevanceEntity.setType(FileRelevanceTypeEnum.STUDENT_LEAVE.getType());
                fileRelevanceEntity.setBusinessId(entity.getId());
                fileRelevanceEntity.setSchoolId(reqModel.getSchoolId());
                return fileRelevanceEntity;
            }).collect(Collectors.toList()));
        }
    }

    @Transactional
    @Override
    public void delete(Long id) {
        StudentLeaveEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        this.removeById(id);
        studentLeaveCourseService.remove(Wrappers.<StudentLeaveCourseEntity>lambdaQuery()
                .eq(StudentLeaveCourseEntity::getLeaveId, id));
        sysFileRelevanceService.remove(Wrappers.<SysFileRelevanceEntity>lambdaQuery()
                .eq(SysFileRelevanceEntity::getBusinessId, id)
                .eq(SysFileRelevanceEntity::getType, FileTypeEnum.LEAVE.getType()));
    }

    @Override
    public String export(StudentLeavePageReqModel reqModel) {
        List<StudentLeavePageResModel> list = this.getBaseMapper().page(reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            String fileName = "学生请假缺席数据.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();

            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                fileName = "Student Leave Data.xlsx";
                List<StudentLeaveExportEnModel> exportEnModels = list.stream()
                        .map(resModel -> {
                            StudentLeaveExportEnModel exportModel = new StudentLeaveExportEnModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setClassName(resModel.getGradeName() + resModel.getClassName());
                            exportModel.setLeaveDate(resModel.getLeaveDate().toString());
                            exportModel.setLeaveType(formatLeaveType(resModel.getLeaveType(), SchoolLanguageEnum.EN_US));
                            exportModel.setSeatNo(resModel.getSeatNo() != null ? String.valueOf(resModel.getSeatNo()) : "");
                            exportModel.setPeriods(String.valueOf(resModel.getPeriods()));
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, StudentLeaveExportEnModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                fileName = "Dados de Ausência dos Alunos.xlsx";
                List<StudentLeaveExportPtModel> exportPtModels = list.stream()
                        .map(resModel -> {
                            StudentLeaveExportPtModel exportModel = new StudentLeaveExportPtModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setClassName(resModel.getGradeName() + resModel.getClassName());
                            exportModel.setLeaveDate(resModel.getLeaveDate().toString());
                            exportModel.setLeaveType(formatLeaveType(resModel.getLeaveType(), SchoolLanguageEnum.PT_PT));
                            exportModel.setSeatNo(resModel.getSeatNo() != null ? String.valueOf(resModel.getSeatNo()) : "");
                            exportModel.setPeriods(String.valueOf(resModel.getPeriods()));
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, StudentLeaveExportPtModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            } else {
                return exportFileHandler.doExportExcel(handleExportData(list), fileName, StudentLeaveExportModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            }
        }
        return null;
    }

    private void saveStudentLeave(StudentLeaveSaveAdminReqModel reqModel, Long id, String username, Integer registrantType) {
        if (CollectionUtils.isNotEmpty(reqModel.getStudentIds())) {
            List<StudentLeaveEntity> studentLeaves = new ArrayList<>();
            for (Long studentId : reqModel.getStudentIds()) {
                StudentLeaveEntity entity = BeanConvertUtil.convert(reqModel, StudentLeaveEntity.class);
                entity.setStudentId(studentId);
                entity.setRegistrantId(id);
                entity.setRegistrantName(username);
                entity.setRegistrantType(registrantType);
                studentLeaves.add(entity);
            }
            if (CollectionUtils.isNotEmpty(studentLeaves)) {
                this.saveBatch(studentLeaves);
                if (CollectionUtils.isNotEmpty(reqModel.getCourses()) || CollectionUtils.isNotEmpty(reqModel.getFileIds())) {
                    List<StudentLeaveCourseSaveReqModel> leaveCourseList = reqModel.getCourses();
                    List<Long> fileIds = reqModel.getFileIds();
                    List<SysFileRelevanceEntity> insertFileRelevanceList = new ArrayList<>();
                    List<StudentLeaveCourseEntity> insertLeaveCourseList = new ArrayList<>();
                    for (StudentLeaveEntity studentLeave : studentLeaves) {
                        if (CollectionUtils.isNotEmpty(fileIds)) {
                            insertFileRelevanceList.addAll(fileIds.stream().map(fileId -> {
                                SysFileRelevanceEntity fileRelevanceEntity = new SysFileRelevanceEntity();
                                fileRelevanceEntity.setFileId(fileId);
                                fileRelevanceEntity.setType(FileRelevanceTypeEnum.STUDENT_LEAVE.getType());
                                fileRelevanceEntity.setBusinessId(studentLeave.getId());
                                fileRelevanceEntity.setSchoolId(reqModel.getSchoolId());
                                return fileRelevanceEntity;
                            }).collect(Collectors.toList()));
                        }
                        if (CollectionUtils.isNotEmpty(leaveCourseList)) {
                            insertLeaveCourseList.addAll(leaveCourseList.stream().map(course -> {
                                StudentLeaveCourseEntity leaveCourseEntity = new StudentLeaveCourseEntity();
                                leaveCourseEntity.setCourseId(course.getCourseId());
                                leaveCourseEntity.setLeaveId(studentLeave.getId());
                                return leaveCourseEntity;
                            }).collect(Collectors.toList()));
                        }
                    }
                    if (CollectionUtils.isNotEmpty(insertFileRelevanceList)) {
                        sysFileRelevanceService.saveBatch(insertFileRelevanceList);
                    }
                    if (CollectionUtils.isNotEmpty(insertLeaveCourseList)) {
                        studentLeaveCourseService.saveBatch(insertLeaveCourseList);
                    }
                }
            }
        }
    }

    private void saveStudentLeave(StudentLeaveSaveReqModel reqModel, Long id, String username, Integer registrantType) {
        StudentLeaveEntity entity = BeanConvertUtil.convert(reqModel, StudentLeaveEntity.class);
        entity.setRegistrantId(id);
        entity.setRegistrantName(username);
        entity.setRegistrantType(registrantType);
        this.save(entity);
        // 记录课节信息
        if (entity.getId() != null) {
            List<StudentLeaveCourseSaveReqModel> leaveCourseList = reqModel.getCourses();
            List<StudentLeaveCourseEntity> collect = leaveCourseList.stream().map(course -> {
                StudentLeaveCourseEntity leaveCourseEntity = new StudentLeaveCourseEntity();
                leaveCourseEntity.setCourseId(course.getCourseId());
                leaveCourseEntity.setLeaveId(entity.getId());
                return leaveCourseEntity;
            }).collect(Collectors.toList());
            // 保存请假记录
            studentLeaveCourseService.saveBatch(collect);
        }
        // 关联所有文件
        if (CollectionUtils.isNotEmpty(reqModel.getFileIds())) {
            sysFileRelevanceService.saveBatch(reqModel.getFileIds().stream().map(fileId -> {
                SysFileRelevanceEntity fileRelevanceEntity = new SysFileRelevanceEntity();
                fileRelevanceEntity.setFileId(fileId);
                fileRelevanceEntity.setType(FileRelevanceTypeEnum.STUDENT_LEAVE.getType());
                fileRelevanceEntity.setBusinessId(entity.getId());
                fileRelevanceEntity.setSchoolId(reqModel.getSchoolId());
                return fileRelevanceEntity;
            }).collect(Collectors.toList()));
        }
    }

    private String formatLeaveType(Integer leaveType, SchoolLanguageEnum language) {
        if (leaveType == null) {
            return "";
        }

        StudentLeaveTypeEnum leaveTypeEnum = StudentLeaveTypeEnum.toEnum(leaveType);
        if (leaveTypeEnum == null) {
            return "";
        }

        if (language == SchoolLanguageEnum.EN_US) {
            switch (leaveTypeEnum) {
                case LEAVE:
                    return "Leave";
                case ABSENT:
                    return "Absence";
                case LATE:
                    return "Late";
                default:
                    return "";
            }
        } else if (language == SchoolLanguageEnum.PT_PT) {
            switch (leaveTypeEnum) {
                case LEAVE:
                    return "Licenças";
                case ABSENT:
                    return "Faltas";
                case LATE:
                    return "Atraso";
                default:
                    return "";
            }
        }

        return leaveTypeEnum.getValue();
    }

    private List<StudentLeaveExportModel> handleExportData(List<StudentLeavePageResModel> list) {
        List<StudentLeaveExportModel> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(resModel -> {
                StudentLeaveExportModel exportModel = new StudentLeaveExportModel();
                BeanUtils.copyProperties(resModel, exportModel);
                exportModel.setClassName(resModel.getGradeName() + resModel.getClassName());
                exportModel.setSeatNo(resModel.getSeatNo() != null ? String.valueOf(resModel.getSeatNo()) : "");
                exportModel.setLeaveDate(resModel.getLeaveDate().toString());
                StudentLeaveTypeEnum leaveTypeEnum = StudentLeaveTypeEnum.toEnum(resModel.getLeaveType());
                if (leaveTypeEnum != null) {
                    exportModel.setLeaveType(leaveTypeEnum.getValue());
                }
                exportModel.setPeriods(String.valueOf(resModel.getPeriods()));
                result.add(exportModel);
            });
        }
        return result;
    }

    @Override
    public List<StudentLeaveStatisticsResModel> getStudentLeaveStatistics(Long schoolId, Long classId, List<Long> studentIds) {
        if (schoolId == null) {
            throw new BusinessException(LanguageConstants.SCHOOL_ID_REQUIRED);
        }
        return this.baseMapper.selectStudentLeaveStatistics(schoolId, classId, studentIds);
    }

    @Override
    public List<StudentLeaveStatisticsResModel> getStudentLeaveCountBySemester(Long semesterId, Long classId) {
        // 查询学期时间
        SemesterEntity semester = semesterService.getById(semesterId);

        LocalDateTime semesterStart = semester.getStartTime();
        LocalDateTime semesterEnd = semester.getEndTime();

        // 使用学期时间过滤请假记录
        return this.getBaseMapper().selectStudentLeaveCountBySemester(semesterStart, semesterEnd, classId);
    }

    @Override
    public List<StudentPerformanceTotalResModel> getTotal(StudentPerformanceTotalReqModel reqModel) {
        List<StudentPerformanceTotalResModel> resModels = new ArrayList<>();
        // 获取学段信息
        SemesterEntity semester = semesterService.getById(reqModel.getSemesterId());
        if (semester == null) {
            resModels.add(StudentPerformanceTotalResModel.builder().type(4).num(0).build());
            resModels.add(StudentPerformanceTotalResModel.builder().type(6).num(0).build());
            resModels.add(StudentPerformanceTotalResModel.builder().type(7).num(0).build());
            return resModels;
        }
        List<StudentLeaveEntity> list = this.list(new LambdaQueryWrapper<StudentLeaveEntity>()
                .eq(StudentLeaveEntity::getSchoolId, reqModel.getSchoolId())
                .eq(StudentLeaveEntity::getStudentId, reqModel.getStudentId())
                .eq(StudentLeaveEntity::getSchoolYear, reqModel.getSchoolYear())
                .between(StudentLeaveEntity::getLeaveDate, semester.getStartTime(), semester.getEndTime()));
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(entity -> {
                resModels.add(StudentPerformanceTotalResModel.builder()
                        .type(getStudentPerformanceTotalResModelType(entity.getLeaveType()))
                        .num(1).build());
            });
            // 补充不存在的
            Arrays.asList(4, 6, 7).forEach(typeEnum -> {
                boolean b = resModels.stream().anyMatch(item -> item.getType() == typeEnum);
                if (!b) {
                    StudentPerformanceTotalResModel resModel = new StudentPerformanceTotalResModel();
                    resModel.setType(typeEnum);
                    resModel.setNum(0);
                    resModels.add(resModel);
                }
            });
            return resModels;
        }
        Arrays.asList(4, 6, 7).forEach(typeEnum -> {
            StudentPerformanceTotalResModel resModel = new StudentPerformanceTotalResModel();
            resModel.setType(typeEnum);
            resModel.setNum(0);
            resModels.add(resModel);
        });
        return resModels;
    }

    public int getStudentPerformanceTotalResModelType(int leaveType){
        switch (leaveType) {
            case 1:
                return 7;
            case 2:
                return 6;
            case 3:
                return 4;
            default:
                return 0;
        }
    }
} 