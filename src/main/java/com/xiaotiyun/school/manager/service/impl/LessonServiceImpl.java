package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.LessonDao;
import com.xiaotiyun.school.manager.model.entity.CourseScheduleEntity;
import com.xiaotiyun.school.manager.model.entity.LessonEntity;
import com.xiaotiyun.school.manager.model.entity.SubstituteRecordEntity;
import com.xiaotiyun.school.manager.model.req.LessonCopyReqModel;
import com.xiaotiyun.school.manager.model.req.LessonListReqModel;
import com.xiaotiyun.school.manager.model.req.LessonSaveReqModel;
import com.xiaotiyun.school.manager.model.res.LessonResModel;
import com.xiaotiyun.school.manager.service.CourseScheduleService;
import com.xiaotiyun.school.manager.service.LessonService;
import com.xiaotiyun.school.manager.service.SubstituteRecordService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonServiceImpl extends ServiceImpl<LessonDao, LessonEntity> implements LessonService {
    @Resource
    private CourseScheduleService courseScheduleService;
    @Resource
    private SubstituteRecordService substituteRecordService;

    @Override
    public List<Long> gradeList(Long schoolId) {
        QueryWrapper<LessonEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(LessonEntity::getSchoolId, schoolId);
        List<LessonEntity> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().map(LessonEntity::getGradeId).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public Long add(Long schoolId, LessonSaveReqModel reqModel) {
        LessonEntity entity = BeanConvertUtil.convert(reqModel, LessonEntity.class);
        entity.setSchoolId(schoolId);
        // 校验名称唯一性
        checkNameUnique(schoolId, reqModel, null);
        // 校验时间冲突
        checkTimeConflict(schoolId, reqModel, null);
        this.save(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, LessonSaveReqModel reqModel) {
        LessonEntity entity = getById(id);
        if (entity != null) {
            // 校验名称唯一性
            checkNameUnique(entity.getSchoolId(), reqModel, id);
            // 校验时间冲突
            checkTimeConflict(entity.getSchoolId(), reqModel, id);
            BeanUtils.copyProperties(reqModel, entity);
            this.updateById(entity);
            //更新相关的代课、课表信息
            QueryWrapper<SubstituteRecordEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(SubstituteRecordEntity::getSchoolId, entity.getSchoolId())
                    .eq(SubstituteRecordEntity::getLessonId, id)
                    .gt(SubstituteRecordEntity::getSubstituteDate, LocalDate.now());
            List<SubstituteRecordEntity> substitutes = substituteRecordService.list(wrapper);
            if (CollectionUtils.isNotEmpty(substitutes)) {
                substitutes.forEach(substitute -> {
                    substitute.setLessonName(entity.getName());
                });
                substituteRecordService.updateBatchById(substitutes);
            }
            QueryWrapper<CourseScheduleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(CourseScheduleEntity::getSchoolId, entity.getSchoolId())
                    .eq(CourseScheduleEntity::getLessonId, id)
                    .gt(CourseScheduleEntity::getCourseDate, LocalDate.now());
            List<CourseScheduleEntity> courses = courseScheduleService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(courses)) {
                courses.forEach(course -> {
                    course.setLessonName(entity.getName());
                    course.setStartTime(entity.getStartTime());
                    course.setEndTime(entity.getEndTime());
                });
                courseScheduleService.updateBatchById(courses);
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        LessonEntity entity = getById(id);
        if (entity != null) {
            this.removeById(id);
            //删除相关的代课、课表信息
            QueryWrapper<SubstituteRecordEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(SubstituteRecordEntity::getSchoolId, entity.getSchoolId())
                    .eq(SubstituteRecordEntity::getLessonId, id)
                    .gt(SubstituteRecordEntity::getSubstituteDate, LocalDate.now());
            substituteRecordService.remove(wrapper);
            QueryWrapper<CourseScheduleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(CourseScheduleEntity::getSchoolId, entity.getSchoolId())
                    .eq(CourseScheduleEntity::getLessonId, id)
                    .gt(CourseScheduleEntity::getCourseDate, LocalDate.now());
            courseScheduleService.remove(queryWrapper);
        }
    }

    @Override
    public List<LessonResModel> list(Long schoolId, LessonListReqModel reqModel) {
        QueryWrapper<LessonEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(LessonEntity::getGradeId, reqModel.getGradeId())
                .eq(LessonEntity::getSchoolId, schoolId)
                .orderByAsc(LessonEntity::getStartTime);
        List<LessonEntity> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return BeanConvertUtil.convertList(list, LessonResModel.class);
        }
        return null;
    }

    @Override
    @Transactional
    public void copy(Long schoolId, LessonCopyReqModel reqModel) {
        QueryWrapper<LessonEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(LessonEntity::getGradeId, reqModel.getGradeId())
                .eq(LessonEntity::getSchoolId, schoolId);
        List<LessonEntity> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            QueryWrapper<LessonEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(LessonEntity::getGradeId, reqModel.getCopyGradeIds())
                    .eq(LessonEntity::getSchoolId, schoolId);
            List<LessonEntity> oldList = this.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(oldList)) {
                throw new BusinessException(LanguageConstants.GRADE_LESSON_ALREADY_EXISTS);
            }
            reqModel.getCopyGradeIds().forEach(gradeId -> {
                // 复制新配置
                List<LessonEntity> copyLessons = list.stream()
                        .map(lesson -> {
                            LessonEntity copyLesson = new LessonEntity();
                            copyLesson.setSchoolId(schoolId);
                            copyLesson.setGradeId(gradeId);
                            copyLesson.setName(lesson.getName());
                            copyLesson.setStartTime(lesson.getStartTime());
                            copyLesson.setEndTime(lesson.getEndTime());
                            return copyLesson;
                        }).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(copyLessons)) {
                    this.saveBatch(copyLessons);
                }
            });
        }
    }

    private void checkNameUnique(Long schoolId, LessonSaveReqModel reqModel, Long id) {
        long count = lambdaQuery()
                .eq(LessonEntity::getSchoolId, schoolId)
                .eq(LessonEntity::getGradeId, reqModel.getGradeId())
                .eq(LessonEntity::getName, reqModel.getName())
                .ne(id != null, LessonEntity::getId, id)
                .count();
        if (count > 0) {
            throw new BusinessException(LanguageConstants.LESSON_NAME_DUPLICATE_IN_GRADE);
        }
    }

    private void checkTimeConflict(Long schoolId, LessonSaveReqModel reqModel, Long id) {
        boolean existsConflict = lambdaQuery()
                .eq(LessonEntity::getSchoolId, schoolId)
                .eq(LessonEntity::getGradeId, reqModel.getGradeId())
                .ne(id != null, LessonEntity::getId, id)
                .and(wrapper -> wrapper
                        .between(LessonEntity::getStartTime, reqModel.getStartTime(), reqModel.getEndTime())
                        .or()
                        .between(LessonEntity::getEndTime, reqModel.getStartTime(), reqModel.getEndTime())
                        .or(q -> q.le(LessonEntity::getStartTime, reqModel.getStartTime()).ge(LessonEntity::getEndTime, reqModel.getEndTime()))
                )
                .exists();

        if (existsConflict) {
            throw new BusinessException(LanguageConstants.LESSON_TIME_OVERLAP);
        }
    }
}