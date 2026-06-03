package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.ActivityStudentReportEntity;
import com.xiaotiyun.school.manager.model.req.ActivityStudentReportQueryReqModel;
import com.xiaotiyun.school.manager.model.req.ActivityStudentReportListReqModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentReportQueryResModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentReportListResModel;
import com.xiaotiyun.school.manager.model.dto.ActivityStudentReportExportDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动已匹配表数据访问层
 */
@Mapper
public interface ActivityStudentReportDao extends BaseMapper<ActivityStudentReportEntity> {
    
    /**
     * 查询学生无选课情况列表
     *
     * @param reqModel 查询请求参数
     * @return 学生选课情况列表
     */
    List<ActivityStudentReportQueryResModel> selectStudentCourseList(@Param("req") ActivityStudentReportQueryReqModel reqModel);

    /**
     * 查询活动总人数
     *
     * @param schoolId 学校ID
     * @param schoolYear 学年
     * @param department 学部
     * @return 活动总人数
     */
    Integer selectActivityTotalStudentCount(@Param("schoolId") Long schoolId,
                                          @Param("schoolYear") String schoolYear,
                                          @Param("department") Integer department);

    /**
     * 查询课程总人数
     *
     * @param activityId 活动ID
     * @return 课程总人数
     */
    Integer selectCourseTotalStudentCount(@Param("activityId") Long activityId);

    /**
     * 查询活动匹配列表
     *
     * @param reqModel 查询请求参数
     * @return 活动匹配列表
     */
    List<ActivityStudentReportListResModel> selectActivityStudentReportList(@Param("req") ActivityStudentReportListReqModel reqModel);
    
    /**
     * 查询活动匹配导出数据
     *
     * @param activityId 活动ID
     * @return 活动匹配导出数据列表
     */
    List<ActivityStudentReportExportDTO> selectActivityStudentReportExportData(@Param("activityId") Long activityId);
} 