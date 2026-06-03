package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.CrossBorderStudentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 跨境学生登记DAO接口
 */
@Mapper
public interface CrossBorderStudentDao extends BaseMapper<CrossBorderStudentEntity> {
}