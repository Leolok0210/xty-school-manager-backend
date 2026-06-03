package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.ClassroomDao;
import com.xiaotiyun.school.manager.model.entity.ClassroomEntity;
import com.xiaotiyun.school.manager.model.entity.ClassroomTypeEntity;
import com.xiaotiyun.school.manager.model.entity.CourseScheduleEntity;
import com.xiaotiyun.school.manager.model.req.ClassroomPageReqModel;
import com.xiaotiyun.school.manager.model.req.ClassroomSaveReqModel;
import com.xiaotiyun.school.manager.model.req.ClassroomTypeReqModel;
import com.xiaotiyun.school.manager.model.res.ClassroomPageResModel;
import com.xiaotiyun.school.manager.model.res.ClassroomTypeResModel;
import com.xiaotiyun.school.manager.service.ClassroomService;
import com.xiaotiyun.school.manager.service.ClassroomTypeService;
import com.xiaotiyun.school.manager.service.CourseScheduleService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
public class ClassroomServiceImpl extends ServiceImpl<ClassroomDao, ClassroomEntity> implements ClassroomService {
    @Resource
    private ClassroomTypeService classroomTypeService;
    @Resource
    private CourseScheduleService courseScheduleService;

    @Override
    public Long addType(Long schoolId, ClassroomTypeReqModel reqModel) {
        return classroomTypeService.add(schoolId, reqModel);
    }

    @Override
    public void updateType(Long id, ClassroomTypeReqModel reqModel) {
        classroomTypeService.update(id, reqModel);
    }

    @Override
    public void deleteType(Long id) {
        // 检查是否有教室使用该类型
        long count = this.lambdaQuery()
                .eq(ClassroomEntity::getTypeId, id)
                .count();
        if (count > 0) {
            throw new BusinessException(LanguageConstants.CLASSROOM_TYPE_DELETE);
        }
        classroomTypeService.delete(id);
    }

    @Override
    public List<ClassroomTypeResModel> typeList(Long schoolId) {
        return classroomTypeService.list(schoolId);
    }

    @Override
    @Transactional
    public Long add(Long schoolId, ClassroomSaveReqModel reqModel) {
        ClassroomEntity entity = BeanConvertUtil.convert(reqModel, ClassroomEntity.class);
        entity.setSchoolId(schoolId);
        this.save(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, ClassroomSaveReqModel reqModel) {
        ClassroomEntity entity = getById(id);
        if (entity != null) {
            BeanUtils.copyProperties(reqModel, entity);
            this.updateById(entity);
            //更新相关的课表信息
            QueryWrapper<CourseScheduleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(CourseScheduleEntity::getSchoolId, entity.getSchoolId())
                    .eq(CourseScheduleEntity::getClassroomId, id)
                    .gt(CourseScheduleEntity::getCourseDate, LocalDate.now());
            List<CourseScheduleEntity> courses = courseScheduleService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(courses)) {
                courses.forEach(course -> {
                    course.setClassroomName(entity.getName());
                });
                courseScheduleService.updateBatchById(courses);
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ClassroomEntity entity = getById(id);
        if (entity != null) {
            //教室已添加到課表中，無法刪除
            this.removeById(id);
            //删除相关的课表教室信息
            QueryWrapper<CourseScheduleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(CourseScheduleEntity::getSchoolId, entity.getSchoolId())
                    .eq(CourseScheduleEntity::getClassroomId, id)
                    .gt(CourseScheduleEntity::getCourseDate, LocalDate.now());
            List<CourseScheduleEntity> courses = courseScheduleService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(courses)) {
                courses.forEach(course -> {
                    course.setClassroomId(null);
                    course.setClassroomName(null);
                });
                courseScheduleService.updateBatchById(courses);
            }
        }
    }

    @Override
    public PageInfo<ClassroomPageResModel> page(Long schoolId, ClassroomPageReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        QueryWrapper<ClassroomEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ClassroomEntity::getSchoolId, schoolId)
                .eq(reqModel.getTypeId() != null && reqModel.getTypeId() > 0, ClassroomEntity::getTypeId, reqModel.getTypeId())
                .like(StringUtils.isNotBlank(reqModel.getName()), ClassroomEntity::getName, reqModel.getName());
        List<ClassroomEntity> list = this.list(wrapper.lambda().orderByDesc(ClassroomEntity::getCreateTime));
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> typeIds = list.stream().map(ClassroomEntity::getTypeId).collect(Collectors.toList());
            List<ClassroomTypeEntity> classroomTypes = classroomTypeService.listByIds(typeIds);
            Map<Long, ClassroomTypeEntity> classroomTypeMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(classroomTypes)) {
                classroomTypeMap = classroomTypes.stream().collect(Collectors.toMap(ClassroomTypeEntity::getId, classroomTypeEntity -> classroomTypeEntity));
            }
            PageInfo<ClassroomEntity> pageInfo = new PageInfo<>(list);
            List<ClassroomPageResModel> resList = new ArrayList<>();
            for (ClassroomEntity entity : list) {
                ClassroomPageResModel resModel = new ClassroomPageResModel();
                BeanUtils.copyProperties(entity, resModel);
                ClassroomTypeEntity classroomType = classroomTypeMap.get(entity.getTypeId());
                if (classroomType != null) {
                    resModel.setTypeName(classroomType.getName());
                    resModel.setIsSystem(classroomType.getIsSystem());
                }
                resList.add(resModel);
            }
            PageInfo<ClassroomPageResModel> result = new PageInfo<>(resList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            return result;
        }
        return null;
    }
} 