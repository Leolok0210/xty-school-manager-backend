package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;

import com.xiaotiyun.school.manager.model.entity.TeachingSetting;
import com.xiaotiyun.school.manager.model.req.TeachingSettingQueryByRoleReqModel;
import com.xiaotiyun.school.manager.model.req.TeachingSettingQueryReqModel;
import com.xiaotiyun.school.manager.model.res.TeachingSettingDetailResModel;
import com.xiaotiyun.school.manager.model.res.TeachingSettingRoleResModel;

import java.util.List;

public interface TeachingSettingService extends IService<TeachingSetting> {
    void createTeachingSettings(List<TeachingSetting> teachingSettings);
    void updateTeachingSetting(TeachingSetting teachingSetting);
    void deleteTeachingSetting(Long id);
    TeachingSetting getTeachingSettingById(Long id);
    PageInfo<TeachingSettingDetailResModel> getTeachingSettings(TeachingSettingQueryReqModel reqModel);

    PageInfo<TeachingSettingRoleResModel> getTeachingSettingsByRole(TeachingSettingQueryByRoleReqModel reqModel);

    List<TeachingSetting> getTeachingSettingsBySchoolId(Long schoolId);
}