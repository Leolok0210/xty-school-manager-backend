package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.StudentQualityScore;
import com.xiaotiyun.school.manager.model.req.StudentQualityScoreQueryReqModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentQualityScoreDao extends BaseMapper<StudentQualityScore> {

    List<StudentQualityScore> getStudentQualityScoreList(StudentQualityScoreQueryReqModel reqModel);
}