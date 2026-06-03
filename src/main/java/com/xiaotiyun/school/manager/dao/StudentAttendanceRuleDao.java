package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.StudentAttendanceRuleEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentAttendanceRuleDao extends BaseMapper<StudentAttendanceRuleEntity> {
}