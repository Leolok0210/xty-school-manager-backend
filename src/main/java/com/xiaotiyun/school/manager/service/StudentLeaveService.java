package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.StudentLeaveEntity;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.StudentLeavePageResModel;
import com.xiaotiyun.school.manager.model.res.StudentLeaveStatisticsResModel;
import com.xiaotiyun.school.manager.model.res.StudentPerformanceTotalResModel;

import java.util.List;

public interface StudentLeaveService extends IService<StudentLeaveEntity> {
    PageInfo<StudentLeavePageResModel> page(StudentLeavePageReqModel reqModel);

    PageInfo<StudentLeavePageResModel> teacherPage(StudentLeavePageReqModel reqModel);

    List<String> getImages(Long id);

    void save(StudentLeaveSaveAdminReqModel reqModel);

    void save(StudentLeaveSaveReqModel reqModel);

    void saveByStudent(StudentLeaveSaveReqModel reqModel);

    void update(Long id, StudentLeaveUpdateAdminReqModel reqModel);

    void update(Long id, StudentLeaveSaveReqModel reqModel);

    void delete(Long id);

    String export(StudentLeavePageReqModel reqModel);

    /**
     * 统计学生请假信息
     *
     * @param schoolId   学校ID
     * @param classId    班级ID
     * @param studentIds 学生ID列表
     * @return 学生请假统计列表
     */
    List<StudentLeaveStatisticsResModel> getStudentLeaveStatistics(Long schoolId, Long classId, List<Long> studentIds);

    /**
     * 统计学生请假信息
     *
     * @param semesterId 学期ID
     * @param classId    班级ID
     * @return 学生请假统计列表
     */
    List<StudentLeaveStatisticsResModel> getStudentLeaveCountBySemester(Long semesterId, Long classId);

    List<StudentPerformanceTotalResModel> getTotal(StudentPerformanceTotalReqModel reqModel);
}