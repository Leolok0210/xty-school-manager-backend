package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.ActivityVolunteerLensonEntity;
import com.xiaotiyun.school.manager.model.res.ActivityStudentApplyReportVolunteerResModel;

import java.util.List;
import java.util.Map;

/**
 * 志愿课程表服务接口
 */
public interface ActivityVolunteerLensonService extends IService<ActivityVolunteerLensonEntity> {
    
    /**
     * 根据活动ID和学生ID查询志愿课程列表
     *
     * @return 志愿课程列表
     */
    List<ActivityStudentApplyReportVolunteerResModel> getVolunteerListByActivityAndStudent(Long applyId);

    /**
     * 根据活动ID和学生ID列表批量查询志愿课程列表
     *
     * @param activityId 活动ID
     * @param studentIds 学生ID列表
     * @return 学生ID -> 志愿课程列表的映射
     */
    Map<Long, List<ActivityStudentApplyReportVolunteerResModel>> getVolunteerListByActivityAndStudents(Long activityId, List<Long> studentIds);
} 