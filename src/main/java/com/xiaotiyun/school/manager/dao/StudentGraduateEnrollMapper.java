package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.StudentGraduateEnrollEntity;
import com.xiaotiyun.school.manager.model.req.StudentGraduateEnrollPageReqModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateEnrollPageResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentGraduateEnrollMapper extends BaseMapper<StudentGraduateEnrollEntity> {

    List<StudentGraduateEnrollPageResModel> page(@Param("reqModel") StudentGraduateEnrollPageReqModel reqModel);
} 