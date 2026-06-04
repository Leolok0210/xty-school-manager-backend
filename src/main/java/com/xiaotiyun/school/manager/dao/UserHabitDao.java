package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.UserHabitEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserHabitDao extends BaseMapper<UserHabitEntity> {
}