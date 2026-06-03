package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.dto.StudentUsuallyPartakeCountDTO;
import com.xiaotiyun.school.manager.model.dto.StudentUsuallyScoreDTO;
import com.xiaotiyun.school.manager.model.dto.StudentUsuallyScoreDetailDTO;
import com.xiaotiyun.school.manager.model.entity.StudentUsuallyScoreEntity;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyScoreReqModel;
import com.xiaotiyun.school.manager.model.res.StudentPeriodScoreResponseModel;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyScoreResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentUsuallyScoreMapper extends BaseMapper<StudentUsuallyScoreEntity> {

    List<StudentUsuallyScoreDTO> scoreList(@Param("taskId") Long taskId);

    List<StudentUsuallyPartakeCountDTO> partakeCountList(@Param("taskIds") List<Long> taskIds);

    List<Long> partakeTaskList(@Param("studentInfo") String studentInfo);

    List<StudentUsuallyScoreDetailDTO> scoreDetailList(@Param("studentId") Long studentId, @Param("taskIds") List<Long> taskIds);

    /**
     * 获取学生平时成绩
     *
     * @param classId 班级ID
     * @param periodId 学段ID
     * @return 学生平时成绩列表
     */
    List<StudentPeriodScoreResponseModel> getStudentPeriodScores(@Param("classId") Long classId, @Param("periodId") Long periodId);

    /**
     * 获取学生平时成绩
     * @return 学生平时成绩列表
     */
    List<StudentUsuallyScoreResModel> getScoreListByStudent(@Param("reqModel") StudentUsuallyScoreReqModel reqModel);
}