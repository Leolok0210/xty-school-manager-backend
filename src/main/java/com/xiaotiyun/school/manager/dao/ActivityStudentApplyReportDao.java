package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.dto.ActivityStudentApplyReportQueryDTO;
import com.xiaotiyun.school.manager.model.entity.ActivityStudentApplyReportEntity;
import com.xiaotiyun.school.manager.model.entity.ActivityVolunteerLensonEntity;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.ActivityStudentApplyAdmittedListResModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentApplyNotAdmittedListResModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentApplyNotRegisteredListResModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentApplyRegisteredListResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动报名表数据访问层
 */
@Mapper
public interface ActivityStudentApplyReportDao extends BaseMapper<ActivityStudentApplyReportEntity> {

    /**
     * 查询二次报名和没有二次报名但未匹配课程的学生
     *
     * @param schoolId   学校ID
     * @param department 学部
     * @param schoolYear 学年
     * @param reqModel   活动ID
     * @return 学生列表
     */
    List<ActivityStudentApplyReportQueryDTO> querySecondApplyAndUnmatchedStudents(
            @Param("schoolId") Long schoolId,
            @Param("department") Integer department,
            @Param("schoolYear") String schoolYear,
            @Param("reqModel") ActivityStudentApplyReportSecondListReqModel reqModel);

    /**
     * 查询活动二次报名总人数
     *
     * @param activityId 活动ID
     * @return 二次报名总人数
     */
    Integer countSecondApplyStudents(@Param("activityId") Long activityId);

    /**
     * 通过连表查询获取学生报名记录和志愿信息
     *
     * @param activityId 活动ID
     * @param applyType  报名类型
     * @return 学生志愿信息列表
     */
    List<ActivityVolunteerLensonEntity> selectStudentVolunteersByActivity(
            @Param("activityId") Long activityId,
            @Param("applyType") Integer applyType);

    List<ActivityStudentApplyAdmittedListResModel> admittedList(@Param("classIds") List<Long> classIds, @Param("reqModel") ActivityStudentApplyAdmittedListReqModel reqModel);

    List<ActivityStudentApplyNotAdmittedListResModel> notAdmittedList(@Param("classIds") List<Long> classIds, @Param("reqModel") ActivityStudentApplyNotAdmittedListReqModel reqModel);

    List<ActivityStudentApplyRegisteredListResModel> registeredList(@Param("classIds") List<Long> classIds, @Param("reqModel") ActivityStudentApplyRegisteredListReqModel reqModel);

    List<ActivityStudentApplyNotRegisteredListResModel> notRegisteredList(@Param("schoolId") Long schoolId, @Param("schoolYear") String schoolYear, @Param("department") Integer department,
                                                                          @Param("classIds") List<Long> classIds, @Param("reqModel") ActivityStudentApplyNotRegisteredListReqModel reqModel);
}