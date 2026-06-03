package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.DeptEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门DAO接口
 */
@Mapper
public interface DeptDao extends BaseMapper<DeptEntity> {
}