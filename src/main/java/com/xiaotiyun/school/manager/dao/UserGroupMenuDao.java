package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.UserGroupMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserGroupMenuDao extends BaseMapper<UserGroupMenuEntity> {
    /**
     * 批量插入
     */
    void insertBatch(@Param("list") List<UserGroupMenuEntity> list);
} 