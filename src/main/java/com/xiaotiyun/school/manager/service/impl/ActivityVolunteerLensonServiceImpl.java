package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.ActivityVolunteerLensonDao;
import com.xiaotiyun.school.manager.model.entity.ActivityVolunteerLensonEntity;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityCoursesRecordEntity;
import com.xiaotiyun.school.manager.model.res.ActivityStudentApplyReportVolunteerResModel;
import com.xiaotiyun.school.manager.service.ActivityVolunteerLensonService;
import com.xiaotiyun.school.manager.service.LeisureActivityCoursesRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 志愿课程表服务实现类
 */
@Service
public class ActivityVolunteerLensonServiceImpl extends ServiceImpl<ActivityVolunteerLensonDao, ActivityVolunteerLensonEntity> implements ActivityVolunteerLensonService {

    @Resource
    private LeisureActivityCoursesRecordService leisureActivityCoursesRecordService;

    @Override
    public List<ActivityStudentApplyReportVolunteerResModel> getVolunteerListByActivityAndStudent(Long applyId) {
        if (applyId == null) {
            return new ArrayList<>();
        }

        // 查询志愿课程记录
        LambdaQueryWrapper<ActivityVolunteerLensonEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityVolunteerLensonEntity::getApplyId, applyId)
                .eq(ActivityVolunteerLensonEntity::getDeleted, 0L);

        List<ActivityVolunteerLensonEntity> volunteerList = this.list(queryWrapper);
        
        if (volunteerList.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取课程ID列表
        List<Long> lensonIds = new ArrayList<>();
        for (ActivityVolunteerLensonEntity volunteer : volunteerList) {
            lensonIds.add(volunteer.getLensonId());
        }

        // 查询课程信息
        Map<Long, String> lensonNameMap = new HashMap<>();
        if (!lensonIds.isEmpty()) {
            List<LeisureActivityCoursesRecordEntity> courses = leisureActivityCoursesRecordService.listByIds(lensonIds);
            for (LeisureActivityCoursesRecordEntity course : courses) {
                lensonNameMap.put(course.getId(), course.getName());
            }
        }

        // 组装返回数据
        List<ActivityStudentApplyReportVolunteerResModel> result = new ArrayList<>();
        for (ActivityVolunteerLensonEntity volunteer : volunteerList) {
            ActivityStudentApplyReportVolunteerResModel resModel = new ActivityStudentApplyReportVolunteerResModel();
            resModel.setLensonId(volunteer.getLensonId());
            resModel.setVolunteerType(volunteer.getVolunteerType());
            resModel.setLensonName(lensonNameMap.getOrDefault(volunteer.getLensonId(), ""));
            result.add(resModel);
        }

        return result;
    }

    @Override
    public Map<Long, List<ActivityStudentApplyReportVolunteerResModel>> getVolunteerListByActivityAndStudents(Long activityId, List<Long> studentIds) {
        if (activityId == null || studentIds == null || studentIds.isEmpty()) {
            return new HashMap<>();
        }

        // 批量查询志愿课程记录
        LambdaQueryWrapper<ActivityVolunteerLensonEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityVolunteerLensonEntity::getActivityId, activityId)
                .in(ActivityVolunteerLensonEntity::getStudentId, studentIds)
                .eq(ActivityVolunteerLensonEntity::getDeleted, 0L)
                .orderByAsc(ActivityVolunteerLensonEntity::getVolunteerType);

        List<ActivityVolunteerLensonEntity> volunteerList = this.list(queryWrapper);
        
        if (volunteerList.isEmpty()) {
            // 返回空列表映射
            Map<Long, List<ActivityStudentApplyReportVolunteerResModel>> result = new HashMap<>();
            for (Long studentId : studentIds) {
                result.put(studentId, new ArrayList<>());
            }
            return result;
        }

        // 获取课程ID列表
        List<Long> lensonIds = volunteerList.stream()
                .map(ActivityVolunteerLensonEntity::getLensonId)
                .distinct()
                .collect(Collectors.toList());

        // 查询课程信息
        Map<Long, String> lensonNameMap = new HashMap<>();
        if (!lensonIds.isEmpty()) {
            List<LeisureActivityCoursesRecordEntity> courses = leisureActivityCoursesRecordService.listByIds(lensonIds);
            for (LeisureActivityCoursesRecordEntity course : courses) {
                lensonNameMap.put(course.getId(), course.getName());
            }
        }

        // 按学生ID分组
        Map<Long, List<ActivityVolunteerLensonEntity>> studentVolunteerMap = volunteerList.stream()
                .collect(Collectors.groupingBy(ActivityVolunteerLensonEntity::getStudentId));

        // 组装返回数据
        Map<Long, List<ActivityStudentApplyReportVolunteerResModel>> result = new HashMap<>();
        
        // 为所有学生ID创建映射，确保没有志愿的学生也有空列表
        for (Long studentId : studentIds) {
            List<ActivityVolunteerLensonEntity> studentVolunteers = studentVolunteerMap.get(studentId);
            List<ActivityStudentApplyReportVolunteerResModel> volunteerResList = new ArrayList<>();
            
            if (studentVolunteers != null) {
                for (ActivityVolunteerLensonEntity volunteer : studentVolunteers) {
                    ActivityStudentApplyReportVolunteerResModel resModel = new ActivityStudentApplyReportVolunteerResModel();
                    resModel.setLensonId(volunteer.getLensonId());
                    resModel.setVolunteerType(volunteer.getVolunteerType());
                    resModel.setLensonName(lensonNameMap.getOrDefault(volunteer.getLensonId(), ""));
                    volunteerResList.add(resModel);
                }
            }
            
            result.put(studentId, volunteerResList);
        }

        return result;
    }
} 