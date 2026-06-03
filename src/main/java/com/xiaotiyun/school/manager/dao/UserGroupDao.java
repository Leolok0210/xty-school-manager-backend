package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.UserGroupEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserGroupDao extends BaseMapper<UserGroupEntity> {
    /**
     * 获取用户组关联的用户数量
     */
    Integer getUserCount(@Param("userGroupId") Long userGroupId);
} 