package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.TeachingSetting;
import com.xiaotiyun.school.manager.model.req.TeachingSettingQueryByRoleReqModel;
import com.xiaotiyun.school.manager.model.req.TeachingSettingQueryReqModel;
import com.xiaotiyun.school.manager.model.res.TeachingSettingDetailResModel;
import com.xiaotiyun.school.manager.model.res.TeachingSettingRoleResModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TeachingSettingDao extends BaseMapper<TeachingSetting> {

    List<TeachingSettingDetailResModel> getTeachingSettings(TeachingSettingQueryReqModel reqModel);

    List<TeachingSettingRoleResModel> getTeachingSettingsByRole(TeachingSettingQueryByRoleReqModel reqModel);

}