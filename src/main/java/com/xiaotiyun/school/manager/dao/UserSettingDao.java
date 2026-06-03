package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.UserSettingEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户设置数据访问接口
 */
@Mapper
public interface UserSettingDao extends BaseMapper<UserSettingEntity> {
}
