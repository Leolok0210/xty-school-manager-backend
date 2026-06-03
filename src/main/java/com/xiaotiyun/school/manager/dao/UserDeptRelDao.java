package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.UserDeptRelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户部门关联表 Mapper 接口
 */
@Mapper
public interface UserDeptRelDao extends BaseMapper<UserDeptRelEntity> {
}