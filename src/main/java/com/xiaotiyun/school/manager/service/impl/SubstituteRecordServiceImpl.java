package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.enums.WeekEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.SubstituteRecordDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.model.entity.CourseScheduleEntity;
import com.xiaotiyun.school.manager.model.entity.LessonEntity;
import com.xiaotiyun.school.manager.model.entity.SubstituteRecordEntity;
import com.xiaotiyun.school.manager.model.excel.StudentExportEnModel;
import com.xiaotiyun.school.manager.model.excel.SubstituteRecordExportEnModel;
import com.xiaotiyun.school.manager.model.excel.SubstituteRecordExportModel;
import com.xiaotiyun.school.manager.model.excel.SubstituteRecordExportPtModel;
import com.xiaotiyun.school.manager.model.req.SubstituteDateSaveReqModel;
import com.xiaotiyun.school.manager.model.req.SubstitutePageReqModel;
import com.xiaotiyun.school.manager.model.req.SubstituteSaveReqModel;
import com.xiaotiyun.school.manager.model.req.SubstituteUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.SubstitutePageResModel;
import com.xiaotiyun.school.manager.service.CourseScheduleService;
import com.xiaotiyun.school.manager.service.LessonService;
import com.xiaotiyun.school.manager.service.SubstituteRecordService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubstituteRecordServiceImpl extends ServiceImpl<SubstituteRecordDao, SubstituteRecordEntity> implements SubstituteRecordService {
    @Resource
    private ExportFileHandler exportFileHandler;
    @Resource
    private CourseScheduleService courseScheduleService;
    @Resource
    private LessonService lessonService;

    @Override
    @Transactional
    public void add(Long schoolId, SubstituteSaveReqModel reqModel) {
        if (CollectionUtils.isNotEmpty(reqModel.getSubstituteDateList())) {
            List<SubstituteRecordEntity> saveList = new ArrayList<>();
            List<LocalDate> substituteDates = reqModel.getSubstituteDateList().stream().map(SubstituteDateSaveReqModel::getSubstituteDate).collect(Collectors.toList());
            List<Long> lessonIds = reqModel.getSubstituteDateList().stream().map(SubstituteDateSaveReqModel::getLessonIds).flatMap(List::stream).collect(Collectors.toList());
            Map<String, SubstituteRecordEntity> substituteMap = new HashMap<>();
            Map<LocalDate, List<CourseScheduleEntity>> courseMap = new HashMap<>();
            Map<String, CourseScheduleEntity> courseScheduleMap = new HashMap<>();
            Map<Long, LessonEntity> lessonMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(lessonIds)) {
                List<LessonEntity> lessons = lessonService.listByIds(lessonIds);
                if (CollectionUtils.isNotEmpty(lessons)) {
                    lessonMap = lessons.stream().collect(Collectors.toMap(LessonEntity::getId, lessonEntity -> lessonEntity));
                }
                if (CollectionUtils.isNotEmpty(substituteDates)) {
                    //获取代课老师代课信息
                    QueryWrapper<SubstituteRecordEntity> wrapper = new QueryWrapper<>();
                    wrapper.lambda().eq(SubstituteRecordEntity::getSchoolId, schoolId)
                            .in(SubstituteRecordEntity::getSubstituteDate, substituteDates)
                            .in(SubstituteRecordEntity::getLessonId, lessonIds);
                    List<SubstituteRecordEntity> substitutes = this.list(wrapper);
                    if (CollectionUtils.isNotEmpty(substitutes)) {
                        substituteMap = substitutes.stream().collect(Collectors.toMap(substituteRecordEntity -> substituteRecordEntity.getClassId() + "_" + substituteRecordEntity.getSubstituteDate().toString() + "_" + substituteRecordEntity.getLessonId(), substituteRecordEntity -> substituteRecordEntity));
                    }
                    //获取代课老师课表信息
                    QueryWrapper<CourseScheduleEntity> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda().eq(CourseScheduleEntity::getSchoolId, schoolId)
                            .in(CourseScheduleEntity::getLessonId, lessonIds)
                            .in(CourseScheduleEntity::getCourseDate, substituteDates);
                    List<CourseScheduleEntity> courses = courseScheduleService.list(queryWrapper);
                    if (CollectionUtils.isNotEmpty(courses)) {
                        courseScheduleMap = courses.stream().collect(Collectors.toMap(courseScheduleEntity -> courseScheduleEntity.getClassId() + "_" + courseScheduleEntity.getCourseDate().toString() + "_" + courseScheduleEntity.getLessonId(), courseScheduleEntity -> courseScheduleEntity));
                        List<CourseScheduleEntity> substituteTeacherCourses = courses.stream().filter(courseScheduleEntity -> courseScheduleEntity.getTeacherId().equals(reqModel.getSubstituteTeacherId())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(substituteTeacherCourses)) {
                            courseMap = substituteTeacherCourses.stream().collect(Collectors.groupingBy(CourseScheduleEntity::getCourseDate));
                        }
                    }
                }
            }
            for (SubstituteDateSaveReqModel substituteDate : reqModel.getSubstituteDateList()) {
                if (CollectionUtils.isNotEmpty(substituteDate.getLessonIds())) {
                    for (Long lessonId : substituteDate.getLessonIds()) {
                        LessonEntity lesson = lessonMap.get(lessonId);
                        //课节信息不存在，直接跳过
                        if (lesson == null) {
                            continue;
                        }
                        CourseScheduleEntity courseSchedule = courseScheduleMap.get(reqModel.getClassId() + "_" + substituteDate.getSubstituteDate().toString() + "_" + lessonId);
                        //课表信息不存在，直接跳过
                        if (courseSchedule == null) {
                            continue;
                        }
                        //课节已有其他老师代课，直接跳过
                        if (substituteMap.containsKey(reqModel.getClassId() + "_" + substituteDate.getSubstituteDate().toString() + "_" + lessonId)) {
                            continue;
                        }
                        List<CourseScheduleEntity> courseSchedules = courseMap.get(substituteDate.getSubstituteDate());
                        if (CollectionUtils.isNotEmpty(courseSchedules)) {
                            //代课老师当天有课，直接跳过
                            long count = courseSchedules.stream().filter(courseScheduleEntity -> courseScheduleEntity.getStartTime().isBefore(lesson.getEndTime()) && courseScheduleEntity.getEndTime().isAfter(lesson.getStartTime())).count();
                            if (count > 0) {
                                continue;
                            }
                        }
                        SubstituteRecordEntity entity = new SubstituteRecordEntity();
                        entity.setSchoolId(schoolId);
                        entity.setClassId(reqModel.getClassId());
                        entity.setSubjectId(reqModel.getSubjectId());
                        entity.setPeriodId(reqModel.getPeriodId());
                        entity.setOriginalTeacherId(reqModel.getOriginalTeacherId());
                        entity.setSubstituteTeacherId(reqModel.getSubstituteTeacherId());
                        entity.setSubstituteDate(substituteDate.getSubstituteDate());
                        entity.setCourseScheduleId(courseSchedule.getId());
                        entity.setLessonId(lessonId);
                        entity.setLessonName(lesson.getName());
                        entity.setRemark(reqModel.getRemark());
                        saveList.add(entity);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(saveList)) {
                this.saveBatch(saveList);
            }
        }
    }

    @Override
    @Transactional
    public void update(Long id, SubstituteUpdateReqModel reqModel) {
        SubstituteRecordEntity entity = getById(id);
        if (entity != null) {
            if (!entity.getSubstituteDate().isAfter(LocalDate.now())) {
                throw new BusinessException(LanguageConstants.SUBSTITUTE_UPDATE_FAIL);
            }
            if (!entity.getSubstituteTeacherId().equals(reqModel.getSubstituteTeacherId())) {
                //修改为其他老师时需要验证其他老师与当前代课是否冲突
                if (checkDuplicate(entity, reqModel.getSubstituteTeacherId())) {
                    throw new BusinessException(LanguageConstants.SUBSTITUTE_COURSE_TIME_OVERLAP);
                }
            }
            BeanUtils.copyProperties(reqModel, entity);
            this.updateById(entity);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SubstituteRecordEntity entity = getById(id);
        if (entity != null) {
            if (!entity.getSubstituteDate().isAfter(LocalDate.now())) {
                throw new BusinessException(LanguageConstants.SUBSTITUTE_DELETE_FAIL);
            }
            this.removeById(id);
        }
    }

    @Override
    public PageInfo<SubstitutePageResModel> page(Long schoolId, SubstitutePageReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<SubstitutePageResModel> list = this.getBaseMapper().page(schoolId, reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            //处理代课日期是周几
            list.forEach(resModel -> {
                resModel.setSubstituteType(resModel.getSubstituteDate().getDayOfWeek().getValue());
            });
        }
        return new PageInfo<>(list);
    }

    @Override
    public String export(Long schoolId, SubstitutePageReqModel reqModel) {
        List<SubstitutePageResModel> list = this.getBaseMapper().page(schoolId, reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            String fileName = "代課設定紀錄.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();
            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                List<SubstituteRecordExportPtModel> exportEnModels = list.stream()
                        .map(item -> {
                            SubstituteRecordExportPtModel resModel = new SubstituteRecordExportPtModel();
                            BeanUtils.copyProperties(item, resModel);
                            resModel.setSubstituteDate(item.getSubstituteDate().toString());
                            resModel.setSubstituteType(WeekEnum.getValue(item.getSubstituteDate().getDayOfWeek().getValue(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            resModel.setClassName(item.getGradeName() + item.getClassName());
                            return resModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, StudentExportEnModel.class, FileTypeEnum.EXPORT, schoolId);
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                List<SubstituteRecordExportEnModel> exportPtModels = list.stream()
                        .map(item -> {
                            SubstituteRecordExportEnModel resModel = new SubstituteRecordExportEnModel();
                            BeanUtils.copyProperties(item, resModel);
                            resModel.setSubstituteDate(item.getSubstituteDate().toString());
                            resModel.setSubstituteType(WeekEnum.getValue(item.getSubstituteDate().getDayOfWeek().getValue(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            resModel.setClassName(item.getGradeName() + item.getClassName());
                            return resModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, SubstituteRecordExportEnModel.class, FileTypeEnum.EXPORT, schoolId);
            } else {
                List<SubstituteRecordExportModel> exportMoModels = list.stream()
                        .map(item -> {
                            SubstituteRecordExportModel resModel = new SubstituteRecordExportModel();
                            BeanUtils.copyProperties(item, resModel);
                            resModel.setSubstituteDate(item.getSubstituteDate().toString());
                            resModel.setSubstituteType(WeekEnum.getValue(item.getSubstituteDate().getDayOfWeek().getValue(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            resModel.setClassName(item.getGradeName() + item.getClassName());
                            return resModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportMoModels, fileName, SubstituteRecordExportModel.class, FileTypeEnum.EXPORT, schoolId);
            }
        }
        return null;
    }

    private Boolean checkDuplicate(SubstituteRecordEntity entity, Long substituteTeacherId) {
        //查看代课课节信息
        LessonEntity lesson = lessonService.getById(entity.getLessonId());
        if (lesson == null) {
            throw new BusinessException(LanguageConstants.LESSON_NOT_FOUND);
        }
        //查看代课课节是否已经有其他老师代课
        QueryWrapper<SubstituteRecordEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SubstituteRecordEntity::getSchoolId, entity.getSchoolId())
                .eq(SubstituteRecordEntity::getSubstituteDate, entity.getSubstituteDate())
                .eq(SubstituteRecordEntity::getLessonId, entity.getLessonId())
                .ne(entity.getId() != null, SubstituteRecordEntity::getId, entity.getId());
        long substituteCount = this.count(wrapper);
        if (substituteCount > 0) {
            return true;
        }
        //查看代课老师代课的课节是否有课表安排
        QueryWrapper<CourseScheduleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CourseScheduleEntity::getSchoolId, entity.getSchoolId())
                .eq(CourseScheduleEntity::getTeacherId, substituteTeacherId)
                .eq(CourseScheduleEntity::getCourseDate, entity.getSubstituteDate());
        List<CourseScheduleEntity> courses = courseScheduleService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(courses)) {
            long count = courses.stream().filter(courseScheduleEntity -> courseScheduleEntity.getStartTime().isBefore(lesson.getEndTime()) && courseScheduleEntity.getEndTime().isAfter(lesson.getStartTime())).count();
            return count > 0;
        }
        return false;
    }
}