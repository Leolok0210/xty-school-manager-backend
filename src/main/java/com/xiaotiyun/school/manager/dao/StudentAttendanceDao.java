package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.dto.StudentLateRecordDTO;
import com.xiaotiyun.school.manager.model.entity.StudentAttendanceEntity;
import com.xiaotiyun.school.manager.model.req.StudentAttendancePageReqModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendancePageResModel;
import com.xiaotiyun.school.manager.model.res.StudentLateCountResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StudentAttendanceDao extends BaseMapper<StudentAttendanceEntity> {
    List<StudentAttendancePageResModel> page(@Param("reqModel") StudentAttendancePageReqModel reqModel);


    /**
     * 查询学生时间范围内考勤统计
     *
     * @param studentId
     * @param status
     * @param startDate
     * @param endDate
     * @return
     */
    List<StudentLateCountResModel> selectStudentAttendanceCount(@Param("studentId") Long studentId,
                                                          @Param("status") Integer status,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);

    /**
     * 查询学生迟到统计
     *
     * @param schoolId   学校ID
     * @param classId    班级ID
     * @param studentIds 学生ID列表
     * @return 学生迟到统计列表
     */
    List<StudentLateCountResModel> selectStudentLateCount(@Param("schoolId") Long schoolId,
                                                          @Param("classId") Long classId,
                                                          @Param("studentIds") List<Long> studentIds);

    //countLateDays
    List<StudentLateRecordDTO> countLateDays(@Param("classId") Long classId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    List<StudentAttendanceEntity> getLateDays(@Param("classId") Long classId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
}