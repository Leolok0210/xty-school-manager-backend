package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.DepartmentScoreRuleEntity; // 修改实体类
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DepartmentScoreRuleDao extends BaseMapper<DepartmentScoreRuleEntity> {
}