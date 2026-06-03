package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.CourseScheduleEntity;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.CourseScheduleClassListResModel;
import com.xiaotiyun.school.manager.model.res.CourseScheduleHomeClassListResModel;
import com.xiaotiyun.school.manager.model.res.CourseScheduleTeacherListResModel;

import java.util.List;

public interface CourseScheduleService extends IService<CourseScheduleEntity> {
    void add(Long schoolId, CourseScheduleSaveReqModel reqModel);

    void update(Long id, CourseScheduleUpdateReqModel reqModel);

    void delete(CourseScheduleDeleteReqModel reqModel);

    void copyToWeeks(Long schoolId, CourseScheduleCopyReqModel reqModel);

    List<CourseScheduleClassListResModel> classListByStudent(Long schoolId, CourseScheduleClassListReqModel reqModel);

    List<CourseScheduleClassListResModel> classList(Long schoolId, Long userId, CourseScheduleClassListReqModel reqModel);

    List<CourseScheduleClassListResModel> classQuery(Long schoolId, Long userId, CourseScheduleClassQueryReqModel reqModel);

    List<CourseScheduleHomeClassListResModel> homeClassList(Long schoolId, Long userId, CourseScheduleHomeClassListReqModel reqModel);

    List<CourseScheduleTeacherListResModel> teacherList(Long schoolId, CourseScheduleTeacherListReqModel reqModel);
}