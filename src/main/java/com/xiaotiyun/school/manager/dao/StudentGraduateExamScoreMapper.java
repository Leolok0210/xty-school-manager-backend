package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.dto.StudentGraduateExamPartakeCountDTO;
import com.xiaotiyun.school.manager.model.dto.StudentGraduateExamScoreDTO;
import com.xiaotiyun.school.manager.model.dto.StudentGraduateExamScoreDetailDTO;
import com.xiaotiyun.school.manager.model.entity.StudentGraduateExamScoreEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentGraduateExamScoreMapper extends BaseMapper<StudentGraduateExamScoreEntity> {
    List<StudentGraduateExamScoreDTO> scoreList(@Param("taskId") Long taskId);

    List<StudentGraduateExamPartakeCountDTO> partakeCountList(@Param("taskIds") List<Long> taskIds);

    List<Long> partakeTaskList(@Param("studentInfo") String studentInfo);

    List<StudentGraduateExamScoreDetailDTO> scoreDetailList(@Param("studentId") Long studentId, @Param("taskIds") List<Long> taskIds);
} 