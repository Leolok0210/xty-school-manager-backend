package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.dto.StudentExamPartakeCountDTO;
import com.xiaotiyun.school.manager.model.dto.StudentExamScoreDTO;
import com.xiaotiyun.school.manager.model.dto.StudentExamScoreDetailDTO;
import com.xiaotiyun.school.manager.model.entity.StudentExamScoreEntity;
import com.xiaotiyun.school.manager.model.req.StudentExamScoreReqModel;
import com.xiaotiyun.school.manager.model.res.StudentExamScoreResModel;
import com.xiaotiyun.school.manager.model.res.StudentPeriodScoreResponseModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentExamScoreMapper extends BaseMapper<StudentExamScoreEntity> {
    List<StudentExamScoreDTO> scoreList(@Param("taskId") Long taskId);

    List<StudentExamPartakeCountDTO> partakeCountList(@Param("taskIds") List<Long> taskIds);

    List<Long> partakeTaskList(@Param("studentInfo") String studentInfo);

    List<StudentExamScoreDetailDTO> scoreDetailList(@Param("studentId") Long studentId, @Param("taskIds") List<Long> taskIds);

    /**
     * 获取班级学生考试成绩列表
     *
     * @param classId 班级ID
     * @param periodId 学期ID
     * @return 学生平时成绩列表
     */
    List<StudentPeriodScoreResponseModel> getStudentPeriodScores(@Param("classId") Long classId, @Param("periodId") Long periodId);

    /**
     * 获取学生考试成绩列表
     * @param reqModel 查询参数
     * @return 学生考试成绩列表
     */
    List<StudentExamScoreResModel> scoreListByStudent(@Param("reqModel") StudentExamScoreReqModel reqModel);
}