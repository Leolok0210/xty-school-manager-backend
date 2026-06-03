package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.StudentLeaveEntity;
import com.xiaotiyun.school.manager.model.req.StudentLeavePageReqModel;
import com.xiaotiyun.school.manager.model.res.StudentLeavePageResModel;
import com.xiaotiyun.school.manager.model.res.StudentLeaveStatisticsResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StudentLeaveDao extends BaseMapper<StudentLeaveEntity> {
    List<StudentLeavePageResModel> page(@Param("reqModel") StudentLeavePageReqModel reqModel);

    /**
     * 查询学生请假统计
     *
     * @param schoolId 学校ID
     * @param classId 班级ID
     * @param studentIds 学生ID列表
     * @return 学生请假统计列表
     */
    List<StudentLeaveStatisticsResModel> selectStudentLeaveStatistics(@Param("schoolId") Long schoolId,
                                                                     @Param("classId") Long classId,
                                                                     @Param("studentIds") List<Long> studentIds);

    /**
     * 根据学段ID和班级ID统计学生请假信息
     *
     * @param semesterStart 开始时间
     * @param semesterEnd   结束时间
     * @param classId      班级ID
     * @return 学生请假统计列表
     */
    List<StudentLeaveStatisticsResModel> selectStudentLeaveCountBySemester(@Param("semesterStart") LocalDateTime semesterStart,
                                                                          @Param("semesterEnd") LocalDateTime semesterEnd,
                                                                          @Param("classId") Long classId);
}