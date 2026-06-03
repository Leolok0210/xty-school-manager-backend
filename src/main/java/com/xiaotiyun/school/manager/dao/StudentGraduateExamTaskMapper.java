package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.StudentGraduateExamTaskEntity;
import com.xiaotiyun.school.manager.model.req.StudentGraduateExamPageReqModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateExamTaskPageResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentGraduateExamTaskMapper extends BaseMapper<StudentGraduateExamTaskEntity> {

    List<StudentGraduateExamTaskPageResModel> page(@Param("taskIds") List<Long> taskIds, @Param("reqModel") StudentGraduateExamPageReqModel reqModel);
} 